package cz.vutbr.fit.maros.dip.service.impl;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.vutbr.fit.maros.dip.constants.ApiConstants;
import cz.vutbr.fit.maros.dip.exception.CustomException;
import cz.vutbr.fit.maros.dip.model.Player;
import cz.vutbr.fit.maros.dip.model.PlayerId;
import cz.vutbr.fit.maros.dip.service.PlayerService;
import java.io.BufferedReader;
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
}
