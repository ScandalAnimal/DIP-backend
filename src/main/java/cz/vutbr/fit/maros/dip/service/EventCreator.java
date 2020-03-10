package cz.vutbr.fit.maros.dip.service;

import cz.vutbr.fit.maros.dip.exception.CustomException;
import cz.vutbr.fit.maros.dip.util.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@SuppressWarnings("CheckStyle")
@Component
public class EventCreator {

    private static final Logger LOG = LoggerFactory.getLogger(EventCreator.class);

    private final FileService fileService;

    public EventCreator(FileService fileService) {
        this.fileService = fileService;
    }

    //    @Scheduled(cron = "0 0 12 * * ?")
    @SuppressWarnings("checkstyle:CommentsIndentation")
    @Scheduled(fixedRate = 100000)
    public void getFPLData() {

        String apiUrl = "https://fantasy.premierleague.com/api/bootstrap-static/";
        Connection.Response response;
        try {
            response = Jsoup.connect(apiUrl)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:73.0) Gecko/20100101 Firefox/73.0")
                    .timeout(10000)
                    .execute();

            int statusCode = response.statusCode();
            if (HttpStatus.OK.value() == statusCode) {
                Document doc = Jsoup.connect(apiUrl).ignoreContentType(true).get();
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(doc.text());
                fileService.writeRawObjectToFile(json);

                JSONArray players = (JSONArray) json.get("elements");
                String playerStatKeys = getPlayerStatKeys(players);
                String playerStatValues = getPlayerStatValues(players);
                fileService.writeRawPlayerDataToFile(playerStatKeys, playerStatValues);

                JSONArray events = (JSONArray) json.get("events");
                Long gameWeekNumber = getGameWeekNumber(events);
                System.out.println(gameWeekNumber);

                getCleanedPlayerStatValues(players);


            } else {
                throw new CustomException("Connection to FPL Api failed. Couldn't download data. Error code: " + statusCode + ".");
            }
        } catch (IOException e) {
            throw new CustomException("Connection to FPL Api failed. Couldn't download data.");
        } catch (ParseException e) {
            throw new CustomException("Invalid format of fetched data from the FPL Api. Couldn't download data.");
        }

    }

    private String getPlayerStatKeys(JSONArray json) {
        JSONObject firstPlayer = (JSONObject) json.get(0);
        return StringUtils.stringifyJSONObject(firstPlayer.keySet().toString());
    }

    private String getPlayerStatValues(JSONArray json) {
        StringBuilder result = new StringBuilder();

        for (Object o : json) {
            JSONObject player = (JSONObject) o;
            result.append(StringUtils.stringifyJSONObject(player.values().toString()));
        }

        return result.toString();
    }

    private Long getGameWeekNumber(JSONArray json) {
        for (final Object o : json) {
            JSONObject event = (JSONObject) o;
            if ((Boolean) event.get("is_current")) {
                return (Long) event.get("id");
            }
        }
        return 0L;
    }

    private String getCleanedPlayerStatKeys(JSONObject player, List<String> keyList) {
        StringBuilder resultKeys = new StringBuilder();
        List<String> keyStatList = new ArrayList<>();

        Iterator keyIterator = player.keySet().iterator();
        Iterator valueIterator = player.values().iterator();
        while (keyIterator.hasNext()) {
            String key = (String) keyIterator.next();
            if (keyList.contains(key)) {
                if (valueIterator.hasNext()) {
                    keyStatList.add(key);
                }
            } else {
                if (valueIterator.hasNext()) {
                    valueIterator.next();
                }
            }
        }
        resultKeys.append(StringUtils.stringifyJSONObject(keyStatList.toString()));
        return resultKeys.toString();
    }

    private void getCleanedPlayerStatValues(JSONArray json) {
        String[] keys = { "first_name", "second_name", "goals_scored", "assists", "total_points", "minutes", "goals_conceded",
                "creativity", "influence", "threat", "bonus", "bps", "ict_index", "clean_sheets", "red_cards", "yellow_cards",
                "selected_by_percent", "now_cost" };
        List<String> keyList = Arrays.asList(keys);
        List<Object> valueList = new ArrayList<>();

        StringBuilder resultValues = new StringBuilder();

        JSONObject firstPlayer = (JSONObject) json.get(0);
        String keyStats = getCleanedPlayerStatKeys(firstPlayer, keyList);

        for (Object o : json) {
            JSONObject player = (JSONObject) o;

            Iterator keyIterator = player.keySet().iterator();
            Iterator valueIterator = player.values().iterator();
            while (keyIterator.hasNext()) {
                String key = (String) keyIterator.next();
                if (keyList.contains(key)) {
                    if (valueIterator.hasNext()) {
                        valueList.add(valueIterator.next());
                    }
                } else {
                    if (valueIterator.hasNext()) {
                        valueIterator.next();
                    }
                }
            }
            resultValues.append(StringUtils.stringifyJSONObject(valueList.toString()));
            valueList.clear();
        }

        fileService.writeCleanedPlayerDataToFile(keyStats, resultValues.toString());
    }

}
