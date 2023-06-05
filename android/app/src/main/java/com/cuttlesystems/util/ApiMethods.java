package com.cuttlesystems.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class ApiMethods{
    private Exception exception;
// -------------------------------------------------------------------------------------------------
    private JSONArray answerCoins;
    private JSONObject answerBalance;
    private JSONObject answerSeed;
    private JSONObject addressesUser;
    private JSONObject infoTransaction;
// -------------------------------------------------------------------------------------------------
    private final String URL;
    private String token;
    private String keyUser;
    private String taxSize;
// -------------------------------------------------------------------------------------------------
    private TaxCoin taxCoin;
// -------------------------------------------------------------------------------------------------
    private final Configurator config;
// -------------------------------------------------------------------------------------------------
    static final String TEST_NET = "https://wallet.csexchange.kz/api/";
    static final String MAIN_NET = "https://master-wallet.csexchange.kz/api/";
// -------------------------------------------------------------------------------------------------
    private static final int BALANCES_TIMEOUT_SEC = 90;
    private static final int ADDRESSES_TIMEOUT_SEC = 60;
    private static final int TRANSACTION_SEND_TIMEOUT = 60;
// -------------------------------------------------------------------------------------------------

    public ApiMethods(Context context) {
        config = new Configurator(context);
        switch (config.getCountNetwork()){
            case (0):
                this.URL = TEST_NET;
                break;
            case(1):
                this.URL = MAIN_NET;
                break;
            default:
                this.URL = config.getListNet().get(config.getCountNetwork());
                break;
        }
    }


    public void loginUserPost(String userName, String password) {
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient();

            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            JSONObject userData = new JSONObject();
            userData.put("username", userName);
            userData.put("password", password);

            RequestBody body = RequestBody.create(userData.toString(), JSON);

            Request request = new Request.Builder()
                    .url(this.URL + "auth/token/login/")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Response not created because: " +
                        response.code() + " " + response.message());
            }
            System.out.println("Server: " + response.header("Server"));
            String jsonString = response.body().string();
            JSONObject json = new JSONObject(jsonString);
            token = json.getString("auth_token");
            System.out.println(token);
        }
        catch (Exception ex)
        {
            exception = ex;
        }
    }

    public void createNewUserPost(String userName, String password, String email){
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient();

            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            //ToDo: don't used SPICE and used normal creating json
            JSONObject userData = new JSONObject();
            userData.put("username", userName);
            userData.put("password", password);
            userData.put("email", email);

            RequestBody body = RequestBody.create(userData.toString(), JSON);

            Request request = new Request.Builder()
                    .url(this.URL + "users/")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Response not created because: " +
                        response.code() + " " + response.message());
            }
            System.out.println("Server: " + response.header("Server"));
        }
        catch (Exception ex)
        {
            exception = ex;
        }
    }

    public void getTaxTransaction(String id,
            String token, String key,
            String feeLevel, String recipientAddress, String amount){
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(2400000, TimeUnit.MILLISECONDS)
                    .build();

            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            //ToDo: don't used SPICE and used normal creating json
            JSONObject userData = new JSONObject();
            userData.put("fee_level", feeLevel);
            userData.put("recipient_address", recipientAddress);
            userData.put("amount", amount);

            RequestBody body = RequestBody.create(userData.toString(), JSON);

            Request request = new Request.Builder()
                    .url(this.URL + "fee_estimate/" + id + "/")
                    .post(body)
                    .header("Authorization","Token "+ token)
                    .header("key", key)

                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Response not created because: " +
                        response.code() + " " + response.message());
            }
            ResponseBody responseBody = response.body();

            String jsonString = responseBody.string();
            JSONObject jsonObject = new JSONObject(jsonString);
            taxSize = jsonObject.getString("fee_amount");

            taxCoin = (new TaxCoinsJsonParser(
                    jsonObject.getJSONObject("commission_coin")).getCoin());
            System.out.println(taxSize);
            System.out.println(taxCoin.getShortName());
        }
        catch (Exception ex) {
            exception = ex;
        }
    }

    public void getBalancesPost(String tokenUser, String keyUser)
    {
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(BALANCES_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(this.URL + "crypto_balance/")
                    .get()
                    .header("Authorization","Token "+tokenUser)
                    .header("key", keyUser)
                    .build();

            Response response = client.newCall(request).execute();
            String responseBodyString = response.body().string();
            if (!response.isSuccessful()) {
                if (response.code() == 400) {

                    JSONObject errorResponse = new JSONObject(responseBodyString);
                    String errorMessage = errorResponse.getString("message");

                    throw new ApiMessageException("Send error: " + errorMessage);
                }
                else {
                    throw new CriticalErrorException(
                            "Server error code (" + String.valueOf(response.code()) + ") "
                                    + " details: " + responseBodyString);
                }
            }
            System.out.println("Server: " + response.header("Server"));
            answerBalance = new JSONObject(responseBodyString);
        }
        catch (Exception ex) {
            exception = ex;
            System.out.println(ex.getMessage());
        }
    }

    public void changePassword(String tokenUser, String currentPassword, String newPassword)
    {
        exception = null;
        try{
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(180000, TimeUnit.MILLISECONDS)
                    .build();

            JSONObject bodyJson = new JSONObject();
            bodyJson.put("current_password", currentPassword);
            bodyJson.put("new_password", newPassword);

            MediaType json = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(bodyJson.toString(), json);

            Request request = new Request.Builder()
                    .url(this.URL + "users/set_password/")
                    .post(body)
                    .header("Authorization","Token " + tokenUser)
                    //.header("key", keyUser)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String bodyText;
                    if (response.body() != null)
                    {
                        bodyText = response.body().string();
                    }
                    else
                    {
                        bodyText = "Unknown error";
                    }
                    throw new IOException("Can not change password: " +
                            bodyText);
                }
            }
        }
        catch (Exception ex) {
            exception = ex;
            System.out.println(ex.getMessage());
        }
    }

    /*
    public void getCurrenciesPost( String tokenUser)
    {
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL_CUTTLE_WALLET + "currencies/")
                    .get()
                    .header("Token", tokenUser)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Response not created because: " +
                        response.code() + " " + response.message());
            }
            System.out.println("Server: " + response.header("Server"));

            String jsonData = response.body().string();
            JSONObject Jobject = new JSONObject(jsonData);
            JSONArray Jarray = Jobject.getJSONArray("employees");

            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject object= Jarray.getJSONObject(i);
            }

            System.out.println(answerBalance);
        }
        catch (Exception ex) {
            exception = ex;
        }
    }
    */

    @SuppressLint("LongLogTag")
    public void getSeedPhrasesPost(String tokenUser, String key)
    {
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(this.URL + "seed_phrases/")
                    .get()
                    .header("Authorization","Token "+ tokenUser)
                    .header("key", key)
                    .build();
            Response response = client.newCall(request).execute();
            ResponseBody rb= response.body();
            if (!response.isSuccessful()) {
                Log.e("Response not created because: ",
                        response.code() + " " + response.message());
                answerSeed = null;
            }
            String jsonString =rb.string();
            answerSeed = new JSONObject(jsonString);
        }
        catch (Exception ex) {
            config.setSeedState(false);
            answerSeed = null;
            exception = ex;
        }
    }

    public void deleteSeedPhrasePost(String tokenUser, String id)
    {
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(this.URL + "seed_phrases/"+id+'/')
                    .delete()
                    .header("Authorization","Token "+ tokenUser)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Response not created because: " +
                        response.code() + " " + response.message());
            }
            config.setSeedState(false);
            System.out.println("Server: " +  response.body().string());
        }
        catch (Exception ex) {
            exception = ex;
        }
    }

    public void getKeyPost(String token, String password)
    {
        keyUser = null;
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(this.URL + "get_encryption_key/")
                    .get()
                    .header("Authorization","Token "+ token)
                    .header("password", password)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new ApiMessageException("Response not created because: " +
                        response.code() + " " + response.message());
            }
            System.out.println("Server: " + response.header("Server"));

            String jsonString = response.body().string();
            JSONObject json = new JSONObject(jsonString);
            keyUser = json.getString("key");

            System.out.println(keyUser);
        }
        catch (Exception ex) {
            exception = ex;
        }
    }

    public void getCoinsPost(String token)
    {
        exception = null;
        try{//
            System.out.println("Server: " + "start getting all coins");
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(300000, TimeUnit.MILLISECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(this.URL + "active_coins/")
                    .get()
                    .header("Authorization","Token "+ token)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Response not created because: " +
                        response.code() + " " + response.message());
            }
            System.out.println("Server: " + response.header("Server"));

            ResponseBody responseBody = response.body();
            String jsonString = responseBody.string();
            //ToDo: Поставить определенную валюту считываемую из ui добавить getBalance тот

            answerCoins = new JSONArray(jsonString);

            System.out.println(answerCoins);
        }
        catch (Exception ex) {
            exception = ex;
        }
    }

    public void updateCoinsPost(String token, String state, String id)
    {
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient();

            //ToDo: don't used SPICE and used normal creating json
            JSONObject stateBody = new JSONObject();
            stateBody.put("is_active", state);
            stateBody.put("coin_id", id);

            RequestBody body = RequestBody.create(stateBody.toString(),
                    MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(this.URL + "active_coins/")
                    .patch(body)
                    .header("Authorization","Token "+ token)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Response not created because: " +
                        response.code() + " " + response.message());
            }
            System.out.println("Server: " + response.header("Server"));
        }
        catch (Exception ex) {
            exception = ex;
        }
    }

    public void getAddressPost(String token, String key, String id)
    {
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(ADDRESSES_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(this.URL + "wallet_address/"+id+"/")
                    .get()
                    .header("Authorization","Token "+ token)
                    .header("key", key)
                    .build();

            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (!response.isSuccessful()) {
                throw new IOException("Response not created because: " +
                        response.code() + " " + response.message() + " body: " + responseBody.string());
            }
            System.out.println("Server: " + response.header("Server"));

            String jsonString = responseBody.string();
            addressesUser = new JSONObject(jsonString);
            System.out.println(addressesUser);
        }
        catch (Exception ex) {
            addressesUser = null;
            exception = ex;
        }
    }

    public void setUserSeedPhrasePost(String token,String key,String seed_phrase)
    {
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            JSONObject jsonBodyRequest = new JSONObject();
            jsonBodyRequest.put("seed_phrase",  seed_phrase);
            RequestBody requestBody = RequestBody.create(jsonBodyRequest.toString(), JSON);

            Request request = new Request.Builder()
                    .url(this.URL + "seed_phrases/")
                    .post(requestBody)
                    .header("Authorization","Token "+ token)
                    .header("key", key)
                    .build();

            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (!response.isSuccessful()) {
                throw new IOException("Response not created because: " +
                        response.code() + " " + response.message() + " body: " + responseBody.string());
            }
            System.out.println("Server: " + response.header("Server"));
            config.setSeedState(true);
            System.out.println(responseBody.string());
        }
        catch (Exception ex) {
            config.setSeedState(false);
            exception = ex;
        }
    }

    public void createSeedPhrasePost(String token, String key, ArrayList <Integer> entropy)
    {
        exception = null;
        try{//
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            JSONObject jsonBodyRequest = new JSONObject();
            jsonBodyRequest.put("entropy",  entropy.toString());
            RequestBody requestBody = RequestBody.create(jsonBodyRequest.toString(), JSON);

            Request request = new Request.Builder()
                    .url(this.URL + "seed_phrases/")
                    .post(requestBody)
                    .header("Authorization","Token "+ token)
                    .header("key", key)
                    .build();

            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (!response.isSuccessful()) {
                throw new IOException("Response not created because: " +
                        response.code() + " " + response.message() + " body: " + responseBody.string());
            }
            System.out.println("Server: " + response.header("Server"));
            config.setSeedState(true);
            System.out.println(responseBody.string());
        }
        catch (Exception ex) {
            config.setSeedState(false);
            exception = ex;
        }
    }

    public void transactionPost(String price, String address, String key, String token, String id)
    {
        exception = null;
        infoTransaction = null;
        try{//
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(TRANSACTION_SEND_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            JSONObject jsonBodyRequest = new JSONObject();
            jsonBodyRequest.put("recipient_address", address);
            jsonBodyRequest.put("amount", price);
            RequestBody requestBody = RequestBody.create(jsonBodyRequest.toString(), JSON);

            Request request = new Request.Builder()
                    .url(this.URL + "transaction/" + id + "/")
                    .post(requestBody)
                    .header("Authorization","Token "+ token)
                    .header("key", key)
                    .build();

            Response response = client.newCall(request).execute();
            String responseBodyString = response.body().string();
            if (!response.isSuccessful()) {
                if (response.code() == 400) {

                    JSONObject errorResponse = new JSONObject(responseBodyString);
                    String errorMessage = errorResponse.getString("message");

                    throw new ApiMessageException("Send error: " + errorMessage);
                }
                else {
                    throw new CriticalErrorException(
                            "Server error code (" + String.valueOf(response.code()) + ") "
                                    + " details: " + responseBodyString);
                }
            }
            System.out.println("Server: " + response.header("Server"));

            infoTransaction = new JSONObject(responseBodyString);
        }
        catch (Exception ex) {
            exception = ex;
        }
    }

    public String getTaxSize() { return taxSize;}
    public TaxCoin getTaxCoin() { return taxCoin;}
    public JSONObject getInfoTransaction() { return infoTransaction;}

    public JSONObject getAddressesUsers(){
        return addressesUser;
    }

    public JSONObject getBalancesPost(){
        return answerBalance;
    }

    public JSONArray getCoins(){
        return answerCoins;
    }

    public String getTokenUser(){
        return token;
    }

    public JSONObject getSeedPhrasesUser(){
        return answerSeed;
    }

    public String getKeyUsers(){
        return keyUser;
    }

    public Exception getException() {
        return exception;
    }
}