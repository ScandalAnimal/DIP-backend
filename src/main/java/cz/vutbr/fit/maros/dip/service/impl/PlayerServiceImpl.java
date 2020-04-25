package cz.vutbr.fit.maros.dip.service.impl;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.vutbr.fit.maros.dip.constants.ApiConstants;
import cz.vutbr.fit.maros.dip.exception.CustomException;
import cz.vutbr.fit.maros.dip.model.Player;
import cz.vutbr.fit.maros.dip.model.PlayerDetailData;
import cz.vutbr.fit.maros.dip.model.PlayerId;
import cz.vutbr.fit.maros.dip.model.PlayerProjection;
import cz.vutbr.fit.maros.dip.service.PlayerService;
import cz.vutbr.fit.maros.dip.util.DatasetUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    public Player getById(Integer id) {
        return null;
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

    public List<PlayerDetailData> getAllPlayerData(String year) {

        String[] keys = {"total_points", "bps", "minutes", "own_goals", "goals_scored", "assists",
            "yellow_cards", "red_cards", "goals_conceded", "saves", "clean_sheets", "opponent_team" };

        List<PlayerDetailData> players = new ArrayList<>();

        List<String> currentSeasonPlayers = DatasetUtils.getCurrentSeasonPlayers();

        int formattedYear = Integer.parseInt(year);

        String path = DatasetUtils.getFilePath(formattedYear);
        File file = new File(path);
        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

        if (directories != null) {
            for (final String directory : directories) {
                int index = 1;
                String newName = DatasetUtils.getNewName(directory);
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

                            String filteredLine = DatasetUtils.getYearLabel(formattedYear) + "," + index + "," + newName + "," + DatasetUtils.filterLine(line, indexes);
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
        return players;
    }
}
