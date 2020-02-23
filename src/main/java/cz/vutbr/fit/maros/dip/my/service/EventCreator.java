package cz.vutbr.fit.maros.dip.my.service;

import cz.vutbr.fit.maros.dip.my.exception.CustomException;
import cz.vutbr.fit.maros.dip.my.util.StringUtils;
import java.io.IOException;
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

@Component
public class EventCreator {

    private static final Logger LOG = LoggerFactory.getLogger(EventCreator.class);

    private final FileService fileService;

    public EventCreator(FileService fileService) {
        this.fileService = fileService;
    }

    //    @Scheduled(cron = "0 0 12 * * ?")
    @Scheduled(fixedRate = 10000)
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

}
