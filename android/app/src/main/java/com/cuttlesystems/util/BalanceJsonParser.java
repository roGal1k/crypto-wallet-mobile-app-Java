package com.cuttlesystems.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BalanceJsonParser {
    private JSONObject parsedJson;

    public BalanceJsonParser(JSONObject parsedJsonString) throws JSONException {
        parsedJson = parsedJsonString;
    }

    public ArrayList<UserBalance> getBalances() throws JSONException {
        ArrayList<UserBalance> balances = new ArrayList<UserBalance>();
        JSONArray coinsJsonArray = parsedJson.getJSONArray("coins");
        for (int i = 0; i < coinsJsonArray.length(); i++){
            JSONObject balanceJson = coinsJsonArray.getJSONObject(i);
            balances.add(new UserBalance(
                    balanceJson.getString("id"),
                    balanceJson.getString("balance"),
                    balanceJson.getString("full_name"),
                    balanceJson.getString("short_name"),
                    balanceJson.getString("icon")
                    ));
        }
        return balances;
    }

    public TotalBalance getTotal() throws JSONException {
        TotalBalance total = null;
        JSONObject totalDict = parsedJson.getJSONObject("total");
        total = new TotalBalance(
                totalDict.getString("BTC"),
                totalDict.getString("USD"),
                totalDict.getString("KZT")
        );
        return total;
    }
}