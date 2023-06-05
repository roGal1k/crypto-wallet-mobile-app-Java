package com.cuttlesystems.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CoinsJsonParser {
    private JSONArray parsedJson;

    public CoinsJsonParser(JSONArray parsedJsonString) throws JSONException {
        parsedJson = parsedJsonString;
    }

    public ArrayList<Coins> getCoins() throws JSONException {
        ArrayList<Coins> coins = new ArrayList<Coins>();
        for (int i = 0; i < parsedJson.length(); i++){
            JSONObject coinsJson = parsedJson.getJSONObject(i);
            coins.add(new Coins(
                    coinsJson.getString("id"),
                    coinsJson.getString("full_name"),
                    coinsJson.getString("short_name"),
                    coinsJson.getString("icon"),
                    coinsJson.getString("is_active")
            ));
        }
        return coins;
    }
}