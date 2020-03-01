package cz.vutbr.fit.maros.dip.service;

import org.json.simple.JSONObject;

public interface FileService {

    void writeRawObjectToFile(JSONObject data);

    void writeRawPlayerDataToFile(String playerStatKeys, String playerStatValues);
}

