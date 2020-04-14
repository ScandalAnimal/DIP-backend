package cz.vutbr.fit.maros.dip.service;

import cz.vutbr.fit.maros.dip.constants.ApiConstants;
import cz.vutbr.fit.maros.dip.exception.CustomException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DatasetCreator {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetCreator.class);
    private final FileService fileService;

    public DatasetCreator(FileService fileService) {
        this.fileService = fileService;
    }

    //    @Scheduled(cron = "0 0 12 * * ?")
    @Scheduled(fixedRate = 1000000000)
    public void createDataset() {

        String[] keys = {"total_points", "bps", "creativity", "threat", "influence", "ict_index", "minutes", "value",
            "goals_scored", "assists", "yellow_cards", "red_cards", "goals_conceded", "saves", "was_home", "opponent_team" };

        String url = "players/";

        List<String> currentSeasonPlayers = getCurrentSeasonPlayers();

        for (int year = 1; year < 5; year++) {

            String path = getFilePath(year);
            File file = new File(path);
            String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

            if (directories != null) {
                for (final String directory : directories) {
                    String newName = getNewName(directory);
                    if (currentSeasonPlayers.contains(newName)) {
                        StringBuilder sb = new StringBuilder();
                        BufferedReader br;
                        try {
                            br = new BufferedReader(new FileReader(path + directory + "/gw.csv"));
                            String header = br.readLine();
                            Integer[] indexes = getIndexes(header, keys);
                            String filteredHeader = "gw_index," + filterLine(header, indexes);
                            String line;

                            int index;
                            File existingFile = new File(ApiConstants.DATASET_URL + url + newName + ".csv");
                            if (existingFile.exists()) {
                                BufferedReader br2 = new BufferedReader(new FileReader(existingFile));
                                String lastLine = "";
                                String currentLine = "";
                                while ((currentLine = br2.readLine()) != null) {
                                    lastLine = currentLine;
                                }
                                String[] split = lastLine.split(",");
                                index = Integer.parseInt(split[0]);
                            } else {
                                index = 1;
                            }

                            while ((line = br.readLine()) != null) {

                                String filteredLine = index + "," + filterLine(line, indexes);
                                sb.append(filteredLine).append("\n");
                                index++;
                            }
                            fileService.appendDataToDataset(filteredHeader, sb.toString(), url + newName + ".csv");

                        } catch (FileNotFoundException e) {
                            throw new CustomException("File " + path + directory + " does not exist, please generate gw file first.");
                        } catch (IOException e) {
                            throw new CustomException("Cannot read from file " + path + directory + ".");
                        }
                    }
                }
            }
        }

    }

    private String filterLine(String line, Integer[] indexes) {

        StringJoiner joiner = new StringJoiner(",");

        String[] split = line.split(",");

        for (final Integer index : indexes) {
            joiner.add(split[index]);
        }
        return joiner.toString();
    }

    private Integer[] getIndexes(String header, String[] keys) {

        String[] split = header.split(",");
        Integer[] indexes = new Integer[keys.length];

        List<String> keyList = Arrays.asList(keys);
        for (int i = 0; i < split.length; i++) {
            final String s = split[i];
            int pos = keyList.indexOf(s);
            if (pos >= 0) {
                indexes[pos] = i;
            }
        }
        return indexes;
    }

    private String getFilePath(Integer year) {
        String dir2016 = "data/2016-17/players/";
        String dir2017 = "data/2017-18/players/";
        String dir2018 = "data/2018-19/players/";
        String dir2019 = "data/2019-20/players/";

        if (year == 1) {
            return dir2016;
        } else if (year == 2) {
            return dir2017;
        } else if (year == 3) {
            return dir2018;
        } else if (year == 4) {
            return dir2019;
        }
        return "";
    }

    private List<String> getCurrentSeasonPlayers() {
        String path = getFilePath(4);
        File file = new File(path);
        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

        List<String> players = new ArrayList<>();
        if (directories != null) {
            for (final String directory : directories) {
                String newName = getNewName(directory);
                players.add(newName);
            }
        }
        return players;
    }

    private String getNewName(String oldName) {
        String newName = oldName;
        long occurrences = oldName.chars().filter(ch -> ch == '_').count();
        if (occurrences > 1) {
            int sepPos = oldName.lastIndexOf("_");
            if (sepPos != -1) {
                newName = oldName.substring(0, sepPos);
            }
        }
        return newName;
    }

}