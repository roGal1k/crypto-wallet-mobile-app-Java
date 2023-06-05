package com.cuttlesystems.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Configurator {

    Context context;
    Boolean seedState = false;
    int countNetwork = 0;
    ArrayList<String> network = new ArrayList<>();
    static final String NAME_FILE_CONFIG = "config.txt";

    @SuppressLint("LongLogTag")
    public Configurator(Context context)
    {
        File internalStorageDir = context.getFilesDir();
        File configTXT = new File(internalStorageDir, NAME_FILE_CONFIG);

        this.context = context;

        try {
            initConfig(configTXT);
        } catch (Exception e) {
            try {
                InputStream inputStream = context.getAssets().open("config.json");
                OutputStream outputStream = new FileOutputStream(configTXT);

                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();
                initConfig(configTXT);
                // File copied successfully
            } catch (IOException ex) {
                Log.e("Error from created config.txt", ex.getMessage());
                // Error occurred while copying the file
            } catch (JSONException ex) {
                Log.e("Error from init config.txt", ex.getMessage());
            }
        }
    }

    public void setFileConfig(){

    }

    public boolean getSeedState(){
        return seedState;
    }

    public ArrayList<String> getListNet(){
        return network;
    }

    public int getCountNetwork(){
        return countNetwork;
    }

    public void setSeedState(boolean seedState){
        this.seedState= seedState;
        try {
            saveConfigFile();
        } catch (JSONException e) {
            Log.e("Error from save config", "Error from working with json "+ e.getMessage());
        } catch (IOException e) {
            Log.e("Error from save config", e.getMessage());
        }
    }

    public void setListNet(ArrayList<String> netList){
        this.network = netList;
    }

    public void setCountNetwork(int countNetwork){
        this.countNetwork = countNetwork;
    }

    public static String loadJSONFromAsset(Context context, File file){
        String json = null;
        try {
            InputStream is =  new FileInputStream(file);
            //context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void initConfig(File configTXT) throws JSONException {
        String jsonString = loadJSONFromAsset(context, configTXT);
        JSONObject jsonObject = new JSONObject(jsonString);

        String seed = jsonObject.getString("is_seed");
        this.seedState = Boolean.parseBoolean(seed);

        String countNetwork = jsonObject.getString("count_network");
        this.countNetwork = Integer.parseInt(countNetwork);

        JSONArray jsonArray = new JSONArray(jsonObject.getString("all_net"));

        ArrayList<String> networks = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            String item = jsonArray.getString(i);
            networks.add(item);
        }
        this.network = networks;
    }

    public void saveConfigFile() throws JSONException, IOException {
        JSONObject json = new JSONObject();
        json.put("is_seed", this.seedState);
        json.put("count_network", this.countNetwork);
        JSONArray jsonArray = new JSONArray();

        for (String item : this.network) {
            jsonArray.put(item);
        }
        json.put("all_net", jsonArray);
        String jsonString = json.toString();
        File dir = new File(context.getFilesDir().toString());
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, NAME_FILE_CONFIG);

        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(jsonString.getBytes());
        outputStream.close();
    }
}
