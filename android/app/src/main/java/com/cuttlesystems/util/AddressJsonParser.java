package com.cuttlesystems.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AddressJsonParser {
    private JSONObject parsedJson;

    public AddressJsonParser(String parsedJsonString) throws JSONException {
        parsedJson = new JSONObject(parsedJsonString);
    }

    public HashMap<String, ArrayList<UserAddress>> getAddresses() throws JSONException {

        HashMap<String, ArrayList<UserAddress>> addresses = new HashMap<>();
        Iterator <String> tabs = parsedJson.keys();
        for (Iterator<String> it = tabs; it.hasNext(); ) {
            String tab = it.next();
            JSONObject tabObject = parsedJson.getJSONObject(tab);
            String tabName = tabObject.getString("address_name");
            JSONArray balancesArray = tabObject.getJSONArray("addresses");
            ArrayList<UserAddress> balances = new ArrayList<UserAddress>();
            for (int i = 0; i < balancesArray.length(); i++){
                JSONObject balanceJson = balancesArray.getJSONObject(i);
                balances.add(new UserAddress(
                        balanceJson.getString("address"),
                        balanceJson.getString("balance")
                ));
            }
            addresses.put(tabName, balances);
        }

        return addresses;
    }
}

