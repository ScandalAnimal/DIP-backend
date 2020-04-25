package cz.vutbr.fit.maros.dip.service.impl;

import cz.vutbr.fit.maros.dip.constants.ApiConstants;
import cz.vutbr.fit.maros.dip.exception.CustomException;
import cz.vutbr.fit.maros.dip.model.Fixture;
import cz.vutbr.fit.maros.dip.service.DatasetService;
import cz.vutbr.fit.maros.dip.service.FileService;
import cz.vutbr.fit.maros.dip.service.TeamService;
import cz.vutbr.fit.maros.dip.util.DatasetUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.timeseries.WekaForecaster;
import weka.core.Instances;

@Service
public class DatasetServiceImpl implements DatasetService {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetServiceImpl.class);
    private final FileService fileService;
    private final TeamService teamService;

    public DatasetServiceImpl(FileService fileService, TeamService teamService) {
        this.fileService = fileService;
        this.teamService = teamService;
    }

    public int initializeDataset() {

        String[] keys = {"total_points", "bps", "creativity", "threat", "influence", "ict_index", "minutes", "value",
            "goals_scored", "assists", "yellow_cards", "red_cards", "goals_conceded", "saves", "was_home", "opponent_team" };

        String url = "players/";

        List<String> currentSeasonPlayers = DatasetUtils.getCurrentSeasonPlayers();
        List<Fixture> remainingFixtures = getRemainingFixtures();

        for (int year = 1; year < 5; year++) {

            String path = DatasetUtils.getFilePath(year);
            File file = new File(path);
            String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

            if (directories != null) {
                for (final String directory : directories) {
                    String newName = DatasetUtils.getNewName(directory);
                    if (currentSeasonPlayers.contains(newName)) {
                        StringBuilder sb = new StringBuilder();
                        BufferedReader br;
                        try {
                            br = new BufferedReader(new FileReader(path + directory + "/gw.csv"));
                            String header = br.readLine();
                            Integer[] indexes = DatasetUtils.getIndexes(header, keys);
                            String filteredHeader = "gw_index," + DatasetUtils.filterLine(header, indexes);
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
                                index = Integer.parseInt(split[0]) + 1;
                            } else {
                                index = 1;
                            }

                            while ((line = br.readLine()) != null) {

                                String filteredLine = index + "," + DatasetUtils.filterLine(line, indexes);
                                sb.append(filteredLine).append("\n");
                                index++;
                            }

                            if (year == 4) {
                                String[] names = DatasetUtils.getNewName(directory).split("_");
                                Integer teamId = teamService.getTeamByPlayerName(names[0], names[1]);
                                List<Fixture> teamFixtures = filterFixtures(teamId, remainingFixtures);
                                for (Fixture fixture : teamFixtures) {
                                    String template = filteredHeader;
                                    StringBuilder sb2 = new StringBuilder();
                                    String[] split = template.split(",");
                                    sb2.append(index).append(",");
                                    for (final String s : split) {
                                        if (Objects.equals(s, "opponent_team")) {
                                            Integer home = fixture.getHomeTeam();
                                            Integer away = fixture.getAwayTeam();

                                            if (home.equals(teamId)) {
                                                sb2.append(away).append("\n");
                                            } else {
                                                sb2.append(home).append("\n");
                                            }
                                        } else if (!Objects.equals(s, "gw_index")) {
                                            sb2.append("?,");
                                        }
                                    }
                                    sb.append(sb2.toString());
                                    index++;
                                }
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
        return 0;
    }

    public int divideDatasets() {

        String basePath = "dataset/players/";
        final int MAX_LAG = 6;
        try (Stream<Path> walk = Files.walk(Paths.get(basePath), 1)) {

            List<String> result = walk
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::toString).collect(Collectors.toList());

            for (String playerFile : result) {
                Path path = Paths.get(playerFile);
                String playerName = path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf("."));
                String filePath = basePath + playerName + ".csv";
                String trainPath = basePath + "train/" + playerName + ".csv";
                String primePath = basePath + "prime/" + playerName + ".csv";
                String futurePath = basePath + "future/" + playerName + ".csv";
                String trainBasePath = basePath + "train/" + playerName;
                String primeBasePath = basePath + "prime/" + playerName;
                String futureBasePath = basePath + "future/" + playerName;

                try {
                    Files.deleteIfExists(Paths.get(trainPath));
                    Files.deleteIfExists(Paths.get(primePath));
                    Files.deleteIfExists(Paths.get(futurePath));
                } catch (IOException e) {
                    throw new CustomException("Couldn't delete file for writing data.", e);
                }

                List<String> lines = new ArrayList<>();
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(filePath));
                    String header = br.readLine();
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }

                    List<String> trainingSet = new ArrayList<>();
                    List<String> primingSet = new ArrayList<>();
                    List<String> futureSet = new ArrayList<>();

                    // fake data to make same arff headers
                    trainingSet.add("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,False,0\n");
                    trainingSet.add("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,True,0\n");
                    primingSet.add("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,False,0\n");
                    primingSet.add("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,True,0\n");
                    futureSet.add("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,False,0\n");
                    futureSet.add("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,True,0\n");

                    lines.forEach(elem -> {
                        String[] split = elem.split(",");
                        if (!Objects.equals(split[1], "?")) {
                            trainingSet.add(elem);
                        } else {
                            futureSet.add(elem);
                        }
                    });
                    int primingSize = trainingSet.size() - MAX_LAG;
                    primingSet.addAll(trainingSet.subList((primingSize) < 0 ? trainingSet.size() : primingSize, trainingSet.size()));
                    StringBuilder trainingSetSb = new StringBuilder();
                    trainingSet.forEach(item -> trainingSetSb.append(item).append("\n"));
                    StringBuilder primingSetSb = new StringBuilder();
                    primingSet.forEach(item -> primingSetSb.append(item).append("\n"));
                    StringBuilder futureSetSb = new StringBuilder();
                    futureSet.forEach(item -> futureSetSb.append(item).append("\n"));

                    fileService.createCsv(header, trainingSetSb.toString(), trainPath);
                    fileService.createCsv(header, primingSetSb.toString(), primePath);
                    fileService.createCsv(header, futureSetSb.toString(), futurePath);
                    fileService.csvToArff(trainBasePath);
                    fileService.csvToArff(primeBasePath);
                    fileService.csvToArff(futureBasePath);

                } catch (IOException e) {
                    throw new CustomException("Cannot read from file " + filePath + ".");
                }
            }

        } catch (IOException e) {
            throw new CustomException("Cannot read files from directory " + basePath + ".");
        }

        return 0;
    }

    public int makeAllPredictions() {

        String basePath = "dataset/players/";
        final int MAX_LAG = 6;
        final int PREDICTIONS = 3;
        StringBuilder stringBuilder = new StringBuilder();
        String keys = "player_name,predicted_points";

        List<String> result;
        try (Stream<Path> walk = Files.walk(Paths.get(basePath), 1)) {
            result = walk
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new CustomException("Cannot read files from directory " + basePath + ".");
        }


        for (String playerFile : result) {
            Path path = Paths.get(playerFile);
            String playerName = path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf("."));
            System.out.println(playerName);

            String trainPath = basePath + "train/" + playerName + ".csv";
            String primePath = basePath + "prime/" + playerName + ".csv";
            String futurePath = basePath + "future/" + playerName + ".csv";
            String trainBasePath = basePath + "train/" + playerName;
            String primeBasePath = basePath + "prime/" + playerName;
            String futureBasePath = basePath + "future/" + playerName;

            try {
                File trainFile = new File(trainBasePath + ".arff");
                File primeFile = new File(primeBasePath + ".arff");
                File futureFile = new File(futureBasePath + ".arff");
                Instances trainData = new Instances(new BufferedReader(new FileReader(trainFile)));
                Instances primeData = new Instances(new BufferedReader(new FileReader(primeFile)));
                Instances futureData = new Instances(new BufferedReader(new FileReader(futureFile)));

                // it's important to iterate from last to first, because when we remove
                // an instance, the rest shifts by one position.
                for (int i = futureData.numInstances() - 1; i >= 0; i--) {
                    if (i <= 1) {
                        trainData.delete(i);
                        primeData.delete(i);
                        futureData.delete(i);
                    }
                }

                if (trainData.numInstances() <= 6) {
                    stringBuilder.append(playerName).append(",").append("?").append("\n");
                } else {
                    WekaForecaster forecaster = new WekaForecaster();
                    forecaster.setFieldsToForecast("total_points");
                    forecaster.setBaseForecaster(new MultilayerPerceptron());
                    forecaster.getTSLagMaker().setTimeStampField("gw_index");
                    forecaster.getTSLagMaker().setMinLag(1);
                    forecaster.getTSLagMaker().setMaxLag(MAX_LAG);
                    forecaster.getTSLagMaker().setRemoveLeadingInstancesWithUnknownLagValues(true);
                    forecaster.setOverlayFields("opponent_team");

                    forecaster.buildForecaster(trainData, System.out);

                    forecaster.primeForecaster(primeData);

                    List<List<NumericPrediction>> forecast = forecaster.forecast(PREDICTIONS, futureData, System.out);

                    for (int i = 0; i < PREDICTIONS; i++) {
                        List<NumericPrediction> predsAtStep = forecast.get(i);
                        for (NumericPrediction predForTarget : predsAtStep) {
                            System.out.println("`" + predForTarget.predicted() + "`");
                            stringBuilder.append(playerName).append(",").append(Math.floor(predForTarget.predicted() * 100) / 100).append("\n");
                        }
                    }
                }


            } catch (IOException e) {
                throw new CustomException("Cannot read from one of the files: " + trainPath + ", " + primePath + ", " + futurePath + ".", e);
            } catch (Exception e) {
                throw new CustomException("Cannot perform forecast", e);
            }
        }

        fileService.createCsv(keys, stringBuilder.toString(), basePath + "predictions/3gw.csv");

        return 0;
    }

    private List<Fixture> getRemainingFixtures() {
        String path = "data/2019-20/fixtures.csv";
        File file = new File(path);

        List<Fixture> fixtures = new ArrayList<>();
        BufferedReader br;

        String[] keys = {"event", "finished", "team_h", "team_a", "team_h_difficulty", "team_a_difficulty" };

        try {
            br = new BufferedReader(new FileReader(file));
            String header = br.readLine();
            Integer[] indexes = DatasetUtils.getIndexes(header, keys);
            String line;

            while ((line = br.readLine()) != null) {

                String filteredLine = DatasetUtils.filterLine(line, indexes);
                String[] split = filteredLine.split(",");
                if (!Objects.equals(split[0], "") && Objects.equals(split[1], "False")) {
                    final Fixture fixture = new Fixture(
                            Double.parseDouble(split[0]),
                            Boolean.parseBoolean(split[1]),
                            Integer.parseInt(split[2]),
                            Integer.parseInt(split[3]),
                            Integer.parseInt(split[4]),
                            Integer.parseInt(split[5])
                    );
                    fixtures.add(fixture);
                }
            }

        } catch (FileNotFoundException e) {
            throw new CustomException("File " + path + " does not exist, please generate fixtures file first.");
        } catch (IOException e) {
            throw new CustomException("Cannot read from file " + path + ".");
        }
        return fixtures;
    }

    private List<Fixture> filterFixtures(Integer teamId, List<Fixture> remainingFixtures) {
        List<Fixture> fixtureList = new ArrayList<>();

        for (Fixture fixture : remainingFixtures) {
            if (fixture.getHomeTeam() == teamId || fixture.getAwayTeam() == teamId) {
                fixtureList.add(fixture);
            }
        }
        return fixtureList;
    }

}