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

    public void writeDataToCsv(String keys, String values, String filename) {
        FileWriter file;
        String filePath = "data/" + filename;
        Path path = Paths.get(filePath);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new CustomException("Couldn't delete file for writing data.");
        }

        try {
            file = new FileWriter(filePath, true);
            file.write(keys);
            file.write(values);
            file.flush();
            file.close();
            System.out.println("Finished writing to " + filename);
        } catch (IOException e) {
            throw new CustomException("Couldn't create file for writing data.");
        }

    }
}
