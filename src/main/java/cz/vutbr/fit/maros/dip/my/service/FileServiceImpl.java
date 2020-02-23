package cz.vutbr.fit.maros.dip.my.service;

import cz.vutbr.fit.maros.dip.my.exception.CustomException;
import java.io.FileWriter;
import java.io.IOException;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class FileServiceImpl implements FileService {

    public void writeRawDataToFile(JSONObject data) {
        FileWriter file;

        try {
            file = new FileWriter("data/raw.json");
            file.write(data.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            throw new CustomException("Couldn't create file for writing data.");
        }

    }
}
