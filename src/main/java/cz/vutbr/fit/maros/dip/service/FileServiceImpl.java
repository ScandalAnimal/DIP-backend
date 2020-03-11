package cz.vutbr.fit.maros.dip.service;

import cz.vutbr.fit.maros.dip.exception.CustomException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class FileServiceImpl implements FileService {

    public void writeRawObjectToFile(JSONObject data) {
        FileWriter file;

        try {
            file = new FileWriter("data/raw.json");
            file.write(data.toJSONString());
            file.flush();
            file.close();
            System.out.println("Finished writing to raw.json.");
        } catch (IOException e) {
            throw new CustomException("Couldn't create file for writing data.");
        }

    }

    public void writeRawPlayerDataToFile(String playerStatKeys, String playerStatValues) {
        FileWriter file;
        Path path = Paths.get("data/players.csv");

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new CustomException("Couldn't delete file for writing data.");
        }

        try {
            file = new FileWriter("data/players.csv", true);
            file.write(playerStatKeys);
            file.write(playerStatValues);
            file.flush();
            file.close();
            System.out.println("Finished writing to players.csv.");
        } catch (IOException e) {
            throw new CustomException("Couldn't create file for writing data.");
        }

    }

    public void writeCleanedPlayerDataToFile(String playerStatKeys, String playerStatValues) {
        FileWriter file;
        Path path = Paths.get("data/cleaned_players.csv");

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new CustomException("Couldn't delete file for writing data.");
        }

        try {
            file = new FileWriter("data/cleaned_players.csv", true);
            file.write(playerStatKeys);
            file.write(playerStatValues);
            file.flush();
            file.close();
            System.out.println("Finished writing to cleaned_players.csv.");
        } catch (IOException e) {
            throw new CustomException("Couldn't create file for writing data.");
        }

    }

    public void writeFixturesToFile(String fixtureKeys, String fixtureValues) {
        FileWriter file;
        Path path = Paths.get("data/fixtures.csv");

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new CustomException("Couldn't delete file for writing data.");
        }

        try {
            file = new FileWriter("data/fixtures.csv", true);
            file.write(fixtureKeys);
            file.write(fixtureValues);
            file.flush();
            file.close();
            System.out.println("Finished writing to fixtures.csv.");
        } catch (IOException e) {
            throw new CustomException("Couldn't create file for writing data.");
        }

    }

}
