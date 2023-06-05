package com.cuttlesystems.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class TaxCoinsJsonParser {
    private JSONObject parsedJson;

    public TaxCoinsJsonParser(JSONObject parsedJsonString) throws JSONException {
        parsedJson = parsedJsonString;
    }

    public TaxCoin getCoin() throws JSONException {
        TaxCoin coin = new TaxCoin(
                parsedJson.getString("id"),
                parsedJson.getString("full_name"),
                parsedJson.getString("short_name"),
                parsedJson.getString("icon")
        );
        return coin;
    }
}