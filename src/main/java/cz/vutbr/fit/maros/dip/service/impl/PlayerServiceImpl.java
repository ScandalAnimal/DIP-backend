package cz.vutbr.fit.maros.dip.service.impl;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.vutbr.fit.maros.dip.constants.ApiConstants;
import cz.vutbr.fit.maros.dip.exception.CustomException;
import cz.vutbr.fit.maros.dip.model.BetterPlayers;
import cz.vutbr.fit.maros.dip.model.OptimizeRequest;
import cz.vutbr.fit.maros.dip.model.OptimizedSquad;
import cz.vutbr.fit.maros.dip.model.OptimizedSquads;
import cz.vutbr.fit.maros.dip.model.Player;
import cz.vutbr.fit.maros.dip.model.PlayerDetailData;
import cz.vutbr.fit.maros.dip.model.PlayerId;
import cz.vutbr.fit.maros.dip.model.PlayerInjuryData;
import cz.vutbr.fit.maros.dip.model.PlayerProjection;
import cz.vutbr.fit.maros.dip.model.PlayerStats;
import cz.vutbr.fit.maros.dip.model.PlayerTeam;
import cz.vutbr.fit.maros.dip.model.PredictedPointsStats;
import cz.vutbr.fit.maros.dip.model.TeamPlayer;
import cz.vutbr.fit.maros.dip.model.Technique;
import cz.vutbr.fit.maros.dip.service.PlayerService;
import cz.vutbr.fit.maros.dip.util.DatasetUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    public Player getById(Integer id) {
        return null;
    }

    public List<PlayerTeam> getAllPlayersTeams() {

        List<PlayerTeam> players = new ArrayList<>();
        String fileName = ApiConstants.BASE_URL + "players_raw.csv";

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String header = br.readLine();
            String[] keys = header.split(",");
            String line;
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");
                JSONObject jsonObject = new JSONObject();



                String[] selectedKeys = { "first_name", "second_name", "team" };
                List<String> keyList = Arrays.asList(selectedKeys);
                List<Object> valueList = new ArrayList<>();
                List<String> builtKeysList = new ArrayList<>();

                for (int i = 0; i < keys.length; i++) {
                    if (keyList.contains(keys[i])) {
                        valueList.add(values[i]);
                        builtKeysList.add(keys[i]);
                    }
                }


                for (int i = 0; i < builtKeysList.size(); i++) {
                    jsonObject.put(builtKeysList.get(i), valueList.get(i));
                }

                Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();
                PlayerTeam player = gson.fromJson(jsonObject.toString(), PlayerTeam.class);
                players.add(player);
            }

        } catch (IOException e) {
            throw new CustomException("Cannot read from file " + fileName + ".");
        }
        return players;
    }

    public List<Player> getAllPlayers() {

        List<Player> players = new ArrayList<>();
        String fileName = ApiConstants.BASE_URL + "players_raw.csv";

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String header = br.readLine();
            String[] keys = header.split(",");
            String line;
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");
                JSONObject jsonObject = new JSONObject();



                String[] selectedKeys = { "first_name", "second_name", "goals_scored", "assists", "total_points", "minutes", "goals_conceded",
                    "creativity", "influence", "threat", "bonus", "bps", "ict_index", "clean_sheets", "red_cards", "yellow_cards",
                    "selected_by_percent", "now_cost", "team", "team_code", "element_type" };
                List<String> keyList = Arrays.asList(selectedKeys);
                List<Object> valueList = new ArrayList<>();
                List<String> builtKeysList = new ArrayList<>();

                for (int i = 0; i < keys.length; i++) {
                    if (keyList.contains(keys[i])) {
                        valueList.add(values[i]);
                        builtKeysList.add(keys[i]);
                    }
                }


                for (int i = 0; i < builtKeysList.size(); i++) {
                    jsonObject.put(builtKeysList.get(i), valueList.get(i));
                }

                Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();
                Player player = gson.fromJson(jsonObject.toString(), Player.class);
                players.add(player);
            }

        } catch (IOException e) {
            throw new CustomException("Cannot read from file " + fileName + ".");
        }
        return players;
    }

    public List<PlayerId> getAllPlayersIds() {

        List<PlayerId> players = new ArrayList<>();
        String fileName = ApiConstants.BASE_URL + "player_idlist.csv";

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String header = br.readLine();
            String[] keys = header.split(",");
            String line;
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < keys.length; i++) {
                    jsonObject.put(keys[i], values[i]);
                }

                Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();
                PlayerId player = gson.fromJson(jsonObject.toString(), PlayerId.class);
                players.add(player);
            }

        } catch (IOException e) {
            throw new CustomException("Cannot read from file " + fileName + ".");
        }
        return players;
    }

    public List<PlayerProjection> getAllPlayersProjections(int id) {

        List<PlayerProjection> players = new ArrayList<>();
        String fileName = ApiConstants.DATASET_URL + "players/predictions/" +  id + "gw.csv";

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String header = br.readLine();
            String[] keys = header.split(",");
            String line;
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < keys.length; i++) {
                    if (keys[i].equals("predicted_points")) {
                        int rounded = 0;
                        if (!values[i].equals("?")) {
                            rounded = (int) Math.round(Double.parseDouble(values[i]));
                        }

                        jsonObject.put(keys[i], rounded);
                    } else {
                        jsonObject.put(keys[i], values[i]);
                    }
                }
                System.out.println(jsonObject.toJSONString());

                Gson gson = new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();
                PlayerProjection player = gson.fromJson(jsonObject.toString(), PlayerProjection.class);
                players.add(player);
            }

        } catch (IOException e) {
            throw new CustomException("Cannot read from file " + fileName + ".");
        }
        System.out.println(players);
        return players;
    }

    public List<PlayerDetailData> getAllPlayerData(String id) {

        String[] keys = {"total_points", "bps", "minutes", "own_goals", "goals_scored", "assists",
            "yellow_cards", "red_cards", "goals_conceded", "saves", "clean_sheets", "opponent_team" };

        List<PlayerDetailData> players = new ArrayList<>();

        List<String> currentSeasonPlayers = DatasetUtils.getCurrentSeasonPlayers();
        List<PlayerId> ids = getAllPlayersIds();
        String playerName = "";
        for (final PlayerId playerId : ids) {
            if (playerId.getId().equals(Long.parseLong(id))) {
                playerName = playerId.getFirstName() + "_" + playerId.getSecondName();
            }
        }

        for (int year = 1; year < 5; year++) {
            String path = DatasetUtils.getFilePath(year);
            File file = new File(path);
            String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

            if (directories != null) {
                for (final String directory : directories) {
                    int index = 1;
                    String newName = DatasetUtils.getNewName(directory);
                    if (playerName.equals(newName)) {
                        if (currentSeasonPlayers.contains(newName)) {
                            BufferedReader br;
                            try {
                                br = new BufferedReader(new FileReader(path + directory + "/gw.csv"));
                                String header = br.readLine();
                                Integer[] indexes = DatasetUtils.getIndexes(header, keys);
                                String filteredHeader = "season,gw_index,player_name," + DatasetUtils.filterLine(header, indexes);
                                String[] filteredKeysArray = filteredHeader.split(",");

                                String line;

                                while ((line = br.readLine()) != null) {

                                    String filteredLine = DatasetUtils.getYearLabel(year) + "," + index + "," + newName + "," + DatasetUtils
                                            .filterLine(line, indexes);
                                    index++;
                                    String[] filteredLineArray = filteredLine.split(",");

                                    JSONObject jsonObject = new JSONObject();
                                    for (int i = 0; i < filteredKeysArray.length; i++) {
                                        jsonObject.put(filteredKeysArray[i], filteredLineArray[i]);
                                    }
                                    Gson gson = new GsonBuilder()
                                            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                            .create();
                                    PlayerDetailData player = gson.fromJson(jsonObject.toString(), PlayerDetailData.class);
                                    players.add(player);
                                }

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
        return players;
    }

    public List<PlayerInjuryData> getAllPlayerInjuries() {

        List<PlayerInjuryData> players = new ArrayList<>();
        String fileName = ApiConstants.BASE_URL + "players_raw.csv";

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String header = br.readLine();
            String[] keys = header.split(",");
            String line;
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");
                JSONObject jsonObject = new JSONObject();

                String[] selectedKeys = { "first_name", "second_name", "chance_of_playing_next_round", "chance_of_playing_this_round",
                    "news", "news_added", "red_cards", "yellow_cards", "team_code", "web_name"};
                List<String> keyList = Arrays.asList(selectedKeys);
                List<Object> valueList = new ArrayList<>();
                List<String> builtKeysList = new ArrayList<>();

                for (int i = 0; i < keys.length; i++) {
                    if (keyList.contains(keys[i])) {
                        valueList.add(values[i]);
                        builtKeysList.add(keys[i]);
                    }
                }
                for (int i = 0; i < builtKeysList.size(); i++) {
                    if (builtKeysList.get(i).equals("news")) {
                        if (!valueList.get(i).equals("")) {
                            for (int j = 0; j < builtKeysList.size(); j++) {
                                jsonObject.put(builtKeysList.get(j), valueList.get(j));
                            }

                            Gson gson = new GsonBuilder()
                                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                    .create();
                            PlayerInjuryData player = gson.fromJson(jsonObject.toString(), PlayerInjuryData.class);
                            players.add(player);
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new CustomException("Cannot read from file " + fileName + ".");
        }
        return players;
    }

    public Integer getPlayerPrize(String playerName) {

        String fileName = ApiConstants.BASE_URL + "cleaned_players.csv";
        String[] keys = {"first_name", "second_name", "now_cost"};
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String header = br.readLine();
            Integer[] indexes = DatasetUtils.getIndexes(header, keys);
            String line;

            while ((line = br.readLine()) != null) {
                String filteredLine = DatasetUtils.filterLine(line, indexes);
                String[] split = filteredLine.split(",");
                String newName = split[0] + "_" + split[1];
                if (newName.equals(playerName)) {
                    return Integer.parseInt(split[2]);
                }
            }

        } catch (IOException e) {
            throw new CustomException("Cannot read from file " + fileName + ".");
        }
        return 0;
    }

    public Integer getPlayerPosition(String playerName) {

        String fileName = ApiConstants.BASE_URL + "players_raw.csv";
        String[] keys = {"first_name", "second_name", "element_type"};
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String header = br.readLine();
            Integer[] indexes = DatasetUtils.getIndexes(header, keys);
            String line;

            while ((line = br.readLine()) != null) {
                String filteredLine = DatasetUtils.filterLine(line, indexes);
                String[] split = filteredLine.split(",");
                String newName = split[0] + "_" + split[1];
                if (newName.equals(playerName)) {
                    return Integer.parseInt(split[2]);
                }
            }

        } catch (IOException e) {
            throw new CustomException("Cannot read from file " + fileName + ".");
        }
        return 0;
    }

    public OptimizedSquads getOptimizedSquads(OptimizeRequest optimizeRequest) {
        OptimizedSquads optimizedSquads = new OptimizedSquads();

        Long gameWeeks = optimizeRequest.getGameWeeks();
        Long transfers = optimizeRequest.getTransfers();


        transfers = 1L;
//        transfers = 2L;
//        transfers = 3L;
//        transfers = 4L;
//        transfers = 0L;

        System.out.println(optimizeRequest.getTechnique());
        // always need to be: 2 GK, 5 DEF, 5 MIDS, 3 FWDS
        // always max 100M
        // always max 3 players from team
        // based on params choose next step
        // number of transfers
        // selection technique
            // if max predicted points
                // sort players based on CPI and predicted points
                // ...
            // if total points so far
                // sort players based on CPI
                // ...
            // if form
                // sort players based on CPI-last 6
                // ...

        System.out.println("team:");
        System.out.println(Arrays.toString(optimizeRequest.getTeam()));

        List<PlayerStats> playerStats = getPlayerStats(gameWeeks);

        List<PlayerStats> allGK = filterBasedOnPosition(playerStats, 1);
        List<PlayerStats> allDF = filterBasedOnPosition(playerStats, 2);
        List<PlayerStats> allMD = filterBasedOnPosition(playerStats, 3);
        List<PlayerStats> allFW = filterBasedOnPosition(playerStats, 4);

        List<PlayerStats> currentTeam = getCurrentTeam(optimizeRequest, playerStats);

        List<PlayerStats> currentGK = filterBasedOnPosition(currentTeam, 1);
        List<PlayerStats> currentDF = filterBasedOnPosition(currentTeam, 2);
        List<PlayerStats> currentMD = filterBasedOnPosition(currentTeam, 3);
        List<PlayerStats> currentFW = filterBasedOnPosition(currentTeam, 4);

        Double originalBudget = countOriginalBudget(optimizeRequest.getTeam());

        List<PlayerStats> sortedGK = sortByTechnique(currentGK, optimizeRequest.getTechnique());
        List<PlayerStats> sortedDF = sortByTechnique(currentDF, optimizeRequest.getTechnique());
        List<PlayerStats> sortedMD = sortByTechnique(currentMD, optimizeRequest.getTechnique());
        List<PlayerStats> sortedFW = sortByTechnique(currentFW, optimizeRequest.getTechnique());

        System.out.println(sortedGK);
        System.out.println(sortedDF);
        System.out.println(sortedMD);
        System.out.println(sortedFW);

        List<BetterPlayers> options = new ArrayList<>();

        if (transfers.equals(1L)) {
            options.addAll(findBetterPlayers(sortedGK, allGK, optimizeRequest, originalBudget));
            options.addAll(findBetterPlayers(sortedDF, allDF, optimizeRequest, originalBudget));
            options.addAll(findBetterPlayers(sortedMD, allMD, optimizeRequest, originalBudget));
            options.addAll(findBetterPlayers(sortedFW, allFW, optimizeRequest, originalBudget));
        }
        System.out.println(options);

        List<BetterPlayers> bestOptions = findBestOptions(options, optimizeRequest.getTechnique(), currentTeam);

        System.out.println("sorted:");

        List<OptimizedSquad> squads = new ArrayList<>();
        for (final BetterPlayers bestOption : bestOptions) {
            List<PlayerStats> builtTeam = new ArrayList<>();
            for (final PlayerStats player : currentTeam) {
                if (!bestOption.getToRemove().contains(player)) {
                    builtTeam.add(player);
                }
            }
            builtTeam.addAll(bestOption.getToAdd());
            List<PlayerStats> sorted = builtTeam.stream().sorted(Comparator.comparing(a -> getValueByTechnique(a, optimizeRequest.getTechnique()))).collect(
                    Collectors.toList());
            System.out.println(sorted);
            String captainName = sorted.get(sorted.size() - 1).getPlayerName();
            String viceCaptainName = sorted.get(sorted.size() - 2).getPlayerName();
            Long captain = getPlayerIdByName(captainName);
            Long viceCaptain = getPlayerIdByName(viceCaptainName);
            OptimizedSquad squad = new OptimizedSquad();
            List<Long> teamIds = createTeam(sorted);
            squad.setTeam(teamIds);
            squad.setCaptain(captain);
            squad.setViceCaptain(viceCaptain);
            squads.add(squad);
            optimizedSquads.setSquads(squads);
        }

        return optimizedSquads;
    }

    private List<Long> createTeam(List<PlayerStats> team) {
        List<PlayerStats> createdTeam = new ArrayList<>();

        Collections.reverse(team);

        int[] values = {2,5,5,3};
        int[] counts = {0,0,0,0};

        for (int i = 0; i < 12; i++) {
            if (i == 0) {
                List<PlayerStats> pool = team.stream().filter(player -> player.getPosition() == 1).collect(Collectors.toList());
                createdTeam.add(pool.get(0));
                team.remove(pool.get(0));
                counts[0]++;
            } else if (i < 11) {
                List<PlayerStats> pool = team.stream().filter(player -> player.getPosition() != 1).collect(Collectors.toList());
                int index = 0;
                boolean found = false;
                do {
                    PlayerStats selected = pool.get(index);
                    int pos = selected.getPosition();
                    if (counts[pos - 1] < values[pos - 1]) {
                        createdTeam.add(selected);
                        team.remove(selected);
                        found = true;
                        counts[pos - 1]++;
                    }
                } while (!found);

            } else {
                createdTeam.addAll(team);
            }
        }
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println(createdTeam);

        return createdTeam.stream().map(player -> getPlayerIdByName(player.getPlayerName())).collect(Collectors.toList());
    }

    private Long getPlayerIdByName(String name) {
        List<PlayerId> playerIds = getAllPlayersIds();

        Long id = playerIds.stream().filter(player -> {
            String playerName = player.getFirstName() + "_" + player.getSecondName();
            return playerName.equals(name);
        }).map(PlayerId::getId).findFirst().orElse(null);
        return id != null ? id : null;
    }

    private List<BetterPlayers> findBestOptions(List<BetterPlayers> options, String technique, List<PlayerStats> currentTeam) {
        List<BetterPlayers> workingOptions = new ArrayList<>(options);
        List<BetterPlayers> bestOptions = new ArrayList<>();
        int count = 3;

        for (int i = 0; i < count; i++) {

            double bestDiff = 0.0;
            int bestDiffIndex = 0;

            for (int i1 = 0; i1 < workingOptions.size(); i1++) {
                boolean isCorrectNumberOfPlayersFromTeams = isCorrectNumberOfPlayersFromTeams(currentTeam, workingOptions.get(i1));
                if (isCorrectNumberOfPlayersFromTeams) {
                    final BetterPlayers option = workingOptions.get(i1);
                    Double before = 0.0;
                    List<PlayerStats> toRemove = option.getToRemove();
                    for (final PlayerStats playerStats : toRemove) {
                        before += getValueByTechnique(playerStats, technique);
                    }
                    Double after = 0.0;
                    List<PlayerStats> toAdd = option.getToAdd();
                    for (final PlayerStats playerStats : toAdd) {
                        after += getValueByTechnique(playerStats, technique);
                    }

                    double difference = after - before;
                    if (difference > bestDiff) {
                        bestDiff = difference;
                        bestDiffIndex = i1;
                    }
                }
            }
            bestOptions.add(workingOptions.get(bestDiffIndex));
            workingOptions.remove(bestDiffIndex);
        }

        System.out.println("BEST OPTIONS: ");
        System.out.println(bestOptions);
        return bestOptions;
    }

    private boolean isCorrectNumberOfPlayersFromTeams(List<PlayerStats> currentTeam, BetterPlayers options) {

        List<PlayerTeam> playerTeams = getAllPlayersTeams();
        System.out.println("9999999999999999999999999999999999");
        List<PlayerStats> builtTeam = new ArrayList<>();
        for (final PlayerStats playerStats : currentTeam) {
            if (options.getToRemove().contains(playerStats)) {
                System.out.println("toRemove");
            } else {
                builtTeam.add(playerStats);
            }
            System.out.println(playerStats.getPlayerName());
            System.out.println(findTeam(playerTeams, playerStats.getPlayerName()));
        }
        System.out.println("////////////////");
        for (final PlayerStats playerStats : options.getToRemove()) {
            System.out.println(playerStats.getPlayerName());
            System.out.println(findTeam(playerTeams, playerStats.getPlayerName()));
        }
        System.out.println("////////////////////");
        for (final PlayerStats playerStats : options.getToAdd()) {
            builtTeam.add(playerStats);
            System.out.println(playerStats.getPlayerName());
            System.out.println(findTeam(playerTeams, playerStats.getPlayerName()));
        }

        Map<String, Integer> countMap = new HashMap<>();
        for (PlayerStats item: builtTeam) {
            String team = findTeam(playerTeams, item.getPlayerName());
            if (countMap.containsKey(team)) {
                countMap.put(team, countMap.get(team) + 1);
            } else {
                countMap.put(team, 1);
            }
        }

        for (Map.Entry<String,Integer> entry : countMap.entrySet()) {
            if (entry.getValue() > 3) {
                return false;
            }
        }

        return true;
    }

    private String findTeam(List<PlayerTeam> teams, String playerName) {
        return teams.stream().filter(playerTeam -> {
            String name = playerTeam.getFirstName() + "_" + playerTeam.getSecondName();
            return name.equals(playerName);
        }).map(PlayerTeam::getTeam).findFirst().orElse("");
    }

    private List<BetterPlayers> findBetterPlayers(List<PlayerStats> currentTeam, List<PlayerStats> otherPlayers, OptimizeRequest optimizeRequest, Double originalBudget) {
        PlayerStats selected = currentTeam.get(0);
        List<PlayerStats> toRemove = new ArrayList<>();
        toRemove.add(selected);

        String selectedPlayerName = selected.getPlayerName();
        double selectedPlayerSellingPrice = 0.0;
        for (final TeamPlayer teamPlayer : optimizeRequest.getTeam()) {
            if (teamPlayer.getPlayerName().equals(selectedPlayerName)) {
                selectedPlayerSellingPrice = teamPlayer.getSellingPrice() / 10.0;
            }
        }
        Double selectedPlayerValue = getValueByTechnique(selected, optimizeRequest.getTechnique());
        double recalculatedBudget = Math.round((originalBudget + selectedPlayerSellingPrice) * 10.0) / 10.0;

        System.out.println(otherPlayers);
        System.out.println("original budget: " + originalBudget);
        System.out.println("selling: " + selectedPlayerSellingPrice);
        System.out.println("budget: " + recalculatedBudget);
        List<PlayerStats> betterPlayers = otherPlayers.stream()
                .filter(player -> {
                    Double value = getValueByTechnique(player, optimizeRequest.getTechnique());
                    return value > 0.0;
                })
                .filter(player -> recalculatedBudget >= player.getCost())
                .filter(player -> {
                    Double value = getValueByTechnique(player, optimizeRequest.getTechnique());
                    return value > selectedPlayerValue;
                })
                .collect(Collectors.toList());
        List<PlayerStats> sortedBetterPlayers = sortByTechnique(betterPlayers, optimizeRequest.getTechnique());
        System.out.println("////////////////////////");
        System.out.println(selected);
        System.out.println(sortedBetterPlayers);

        List<BetterPlayers> options = new ArrayList<>();
        List<PlayerStats> best3 = sortedBetterPlayers.subList(Math.max(sortedBetterPlayers.size() - 3, 0), sortedBetterPlayers.size());

        for (int i = 0; i < 3; i++) {
            List<PlayerStats> list = new ArrayList<>();
            list.add(best3.get(i));
            options.add(new BetterPlayers(toRemove, list));
        }
        return options;
    }

    private Double getValueByTechnique(PlayerStats player, String technique) {
        if (Technique.MAX.label.equals(technique)) {
            return player.getPredictedPoints();
        } else if (Technique.TOTAL.label.equals(technique)) {
            return player.getCostPointIndex();
        } else if (Technique.FORM.label.equals(technique)) {
            return player.getCostPointIndexLast6();
        }
        return 0.0;
    }

    private Double countOriginalBudget(TeamPlayer[] team) {
        double cost = 0.0;
        for (final TeamPlayer teamPlayer : team) {
            System.out.println(teamPlayer.getPurchasePrice());
            cost += teamPlayer.getPurchasePrice() / 10.0;
        }
        return Math.round((100.0 - cost) * 10.0) / 10.0;
    }

    private List<PlayerStats> sortByTechnique(List<PlayerStats> list, String technique) {
        List<PlayerStats> tmpList = new ArrayList<>(list);
        if (Technique.MAX.label.equals(technique)) {
            tmpList.sort(Comparator.comparing(PlayerStats::getPredictedPoints));
        } else if (Technique.TOTAL.label.equals(technique)) {
            tmpList.sort(Comparator.comparing(PlayerStats::getCostPointIndex));
        } else if (Technique.FORM.label.equals(technique)) {
            tmpList.sort(Comparator.comparing(PlayerStats::getCostPointIndexLast6));
        }
        return tmpList;
    }

    private List<PlayerStats> getCurrentTeam(OptimizeRequest optimizeRequest, List<PlayerStats> playerStats) {
        List<String> teamPlayers = Arrays.stream(optimizeRequest.getTeam()).map(TeamPlayer::getPlayerName).collect(Collectors.toList());
        return playerStats.stream().filter(player -> teamPlayers.contains(player.getPlayerName())).collect(Collectors.toList());
    }

    private List<PlayerStats> getPlayerStats(Long gameWeeks) {

        List<PredictedPointsStats> predictedPointsStats = getPredictedPointsStats(gameWeeks);
        String[] keys = {"player_name","total_points","cost", "position", "cost_point_index", "cost_point_index_last_6"};
        List<PlayerStats> playerStats = new ArrayList<>();

        String basePath = "dataset/players/stats/stats.csv";
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(basePath));
            String header = br.readLine();
            Integer[] indexes = DatasetUtils.getIndexes(header, keys);
            String line;
            while ((line = br.readLine()) != null) {
                String filteredLine = DatasetUtils.filterLine(line, indexes);
                String[] split = filteredLine.split(",");
                String name = split[0];
                Double points = predictedPointsStats.stream().filter(stat -> stat.getPlayerName().equals(name)).findFirst().map(
                        PredictedPointsStats::getPredictedPoints).orElse(0.0);
                playerStats.add(new PlayerStats(split[0], Double.parseDouble(split[1]), Double.parseDouble(split[2]), Integer.parseInt(split[3]), Double.parseDouble(split[4]), Double.parseDouble(split[5]), points));
            }

        } catch (FileNotFoundException e) {
            throw new CustomException("File " + basePath + " does not exist, please generate file first.");
        } catch (IOException e) {
            throw new CustomException("Cannot read from file " + basePath + ".");
        }
        return playerStats;
    }

    private List<PredictedPointsStats> getPredictedPointsStats(Long gameWeeks) {
        String[] predictedPointsKeys = {"player_name","predicted_points"};
        List<PredictedPointsStats> predictedPointsStats = new ArrayList<>();

        String basePath = "dataset/players/predictions/" + gameWeeks + "gw.csv";
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(basePath));
            String header = br.readLine();
            Integer[] indexes = DatasetUtils.getIndexes(header, predictedPointsKeys);
            String line;
            while ((line = br.readLine()) != null) {
                String filteredLine = DatasetUtils.filterLine(line, indexes);
                String[] split = filteredLine.split(",");
                Double points = split[1].equals("?") ? 0.0 : Double.parseDouble(split[1]);
                predictedPointsStats.add(new PredictedPointsStats(split[0], points));
            }

        } catch (FileNotFoundException e) {
            throw new CustomException("File " + basePath + " does not exist, please generate file first.");
        } catch (IOException e) {
            throw new CustomException("Cannot read from file " + basePath + ".");
        }
        return predictedPointsStats;
    }

    private List<PlayerStats> filterBasedOnPosition(List<PlayerStats> stats, Integer position) {
        return stats.stream().filter(player -> player.getPosition().equals(position)).collect(Collectors.toList());
    }

}
