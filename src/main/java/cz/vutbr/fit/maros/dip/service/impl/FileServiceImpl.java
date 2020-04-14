package cz.vutbr.fit.maros.dip.service.impl;

import cz.vutbr.fit.maros.dip.constants.ApiConstants;
import cz.vutbr.fit.maros.dip.exception.CustomException;
import cz.vutbr.fit.maros.dip.service.FileService;
import java.io.File;
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
        String filePath = ApiConstants.BASE_URL + "raw.json";
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        Path path = Paths.get(filePath);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new CustomException("Couldn't delete file for writing data.");
        }
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(data.toJSONString());
            writer.flush();
            writer.close();
            System.out.println("Finished writing to raw.json.");
        } catch (IOException e) {
            throw new CustomException("Couldn't create file for writing data.");
        }

    }

    public void writeDataToCsv(String keys, String values, String filename) {
        String filePath = ApiConstants.BASE_URL + filename;

        File file = new File(filePath);
        file.getParentFile().mkdirs();
        Path path = Paths.get(filePath);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new CustomException("Couldn't delete file for writing data.");
        }

        try {
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(keys);
            writer.write(values);
            writer.flush();
            writer.close();
            System.out.println("Finished writing to " + filePath);
        } catch (IOException e) {
            throw new CustomException("Couldn't create file for writing data.");
        }

    }

    public void appendDataToCsv(String keys, String values, String filename, boolean addKeys) {
        String filePath = ApiConstants.BASE_URL + filename;

        File file = new File(filePath);
        file.getParentFile().mkdirs();
        Path path = Paths.get(filePath);

        try {
            FileWriter writer = new FileWriter(filePath, true);
            if (addKeys) {
                writer.write(keys);
            }
            writer.write(values);
            writer.flush();
            writer.close();
            System.out.println("Finished writing to " + filePath);
        } catch (IOException e) {
            throw new CustomException("Couldn't create file for writing data.");
        }

    }

    public void appendDataToDataset(String keys, String values, String filename) {
        String filePath = ApiConstants.DATASET_URL + filename;

        File file = new File(filePath);
        file.getParentFile().mkdirs();
        Path path = Paths.get(filePath);

        try {
            FileWriter writer = new FileWriter(filePath, true);
            if (file.length() == 0) {
                writer.write(keys + '\n');
            }
            writer.write(values);
            writer.flush();
            writer.close();
            System.out.println("Finished writing to " + filePath);
        } catch (IOException e) {
            throw new CustomException("Couldn't create file for writing data.");
        }

    }
}
