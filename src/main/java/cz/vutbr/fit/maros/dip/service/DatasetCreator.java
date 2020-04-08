package cz.vutbr.fit.maros.dip.service;

import cz.vutbr.fit.maros.dip.exception.CustomException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

        String[] keys = { "assists", "bonus", "bps", "clean_sheets", "creativity", "element", "fixture",
            "goals_conceded", "goals_scored", "ict_index", "influence", "kickoff_time", "minutes", "opponent_team",
            "own_goals", "penalties_missed", "penalties_saved", "red_cards", "round", "saves", "selected",
            "team_a_score", "team_h_score", "threat", "total_points", "transfers_balance", "transfers_in",
            "transfers_out", "value", "was_home", "yellow_cards" };

        String url = "players/";

        for (int year = 1; year < 5; year++) {

            String path = getFilePath(year);
            File file = new File(path);
            String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

            if (directories != null) {
                for (final String directory : directories) {
                    String newName = directory;
                    long occurrences = directory.chars().filter(ch -> ch == '_').count();
                    if (occurrences > 1) {
                        int sepPos = directory.lastIndexOf("_");
                        if (sepPos != -1) {
                            newName = directory.substring(0, sepPos);
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new FileReader(path + directory + "/gw.csv"));
                        String header = br.readLine();
                        String filteredHeader = filterHeader(header, keys);
                        Integer[] indexes = getIndexes(header, keys);
                        String line;
                        while ((line = br.readLine()) != null) {

                            String filteredLine = filterLine(line, indexes);
                            sb.append(filteredLine).append("\n");
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

    private String filterHeader(String header, String[] keys) {

        StringJoiner joiner = new StringJoiner(",");

        List<String> keyList = Arrays.asList(keys);
        String[] splitted = header.split(",");
        for (final String s : splitted) {
            if (keyList.contains(s)) {
                joiner.add(s);
            }
        }
        return joiner.toString();
    }

    private String filterLine(String line, Integer[] indexes) {

        StringJoiner joiner = new StringJoiner(",");

        String[] splitted = line.split(",");

        for (final Integer index : indexes) {
            joiner.add(splitted[index]);
        }
        return joiner.toString();
    }

    private Integer[] getIndexes(String header, String[] keys) {

        String[] splitted = header.split(",");
        Integer[] indexes = new Integer[keys.length];
        int x = 0;

        List<String> keyList = Arrays.asList(keys);
        for (int i = 0; i < splitted.length; i++) {
            final String s = splitted[i];
            if (keyList.contains(s)) {
                indexes[x] = i;
                x++;
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

}