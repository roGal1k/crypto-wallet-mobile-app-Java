package com.cuttlesystems.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SeedPhraseJsonParser {
    private JSONObject parsedJson;

    public SeedPhraseJsonParser(JSONObject parsedJsonString) throws JSONException {
        parsedJson = parsedJsonString;
    }

    public ArrayList<SeedPhrase> getSeed() throws JSONException {
        ArrayList <SeedPhrase> seedList = new ArrayList<>();
        seedList.add(
                new SeedPhrase(
                        parsedJson.getString("seed_phrase"),
                        parsedJson.getString("id"))
        );
        return seedList;
    }
}