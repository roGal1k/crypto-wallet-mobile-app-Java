package com.cuttlesystems.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ApiMethodsProxy {
    private ApiMethods apiMethods;

    public ApiMethodsProxy(Context context)
    {
        apiMethods = new ApiMethods(context);
    }

    //------------------------------------------------------------------------------------------

    public void loginUserPost(String userName, String password) throws Exception {
        runInThread(() -> apiMethods.loginUserPost(userName, password));
    }

    public void changePassword(String tokenUser, String currentPassword, String newPassword)
            throws Exception
    {
        runInThread(() -> apiMethods.changePassword(tokenUser, currentPassword, newPassword));
    }

    public void createSeedPhrasePost(String token, String key, ArrayList <Integer>entropy)
            throws Exception {
        runInThread(() -> apiMethods.createSeedPhrasePost(token, key, entropy));
    }

    public void setUserSeedPhrasePost(String token, String key, String seed_phrase) throws Exception {
        runInThread(() -> apiMethods.setUserSeedPhrasePost(token, key, seed_phrase));
    }

    public void deleteSeedPhrasePost(String token, String id) throws Exception {
        runInThread(() -> apiMethods.deleteSeedPhrasePost(token, id));
    }

    public void createNewUser(String userName, String password, String email) throws Exception {
        runInThread(() -> apiMethods.createNewUserPost(userName, password, email));
    }

    public void getBalancesPost(String tokenUser, String keyUser) throws Exception {
        runInThread(() -> apiMethods.getBalancesPost(tokenUser, keyUser));
    }

    public void getCoinsPost(String tokenUser) throws Exception {
        runInThread(() -> apiMethods.getCoinsPost(tokenUser));
    }

    public void getSeedPhrasesPost(String tokenUser, String key) throws Exception {
        runInThread(() -> apiMethods.getSeedPhrasesPost(tokenUser, key));
    }

    public void getKeyPost(String token, String password) throws Exception {
        runInThread(() -> apiMethods.getKeyPost(token, password));
    }

    public void getAddressPost(String token, String key, String id) throws Exception {
        runInThread(() -> apiMethods.getAddressPost(token, key, id));
    }

    public void updateCoinsPost(String token, String state, String id) throws Exception {
        runInThread(() -> apiMethods.updateCoinsPost(token, state, id));
    }

    public void transactionPost(String price, String address, String key, String token, String id)
            throws Exception {
        runInThread(() -> apiMethods.transactionPost(price, address, key, token, id));
    }

    public void getTaxTransaction(String id,
                                  String token, String key,
                                  String feeLevel, String recipientAddress, String amount)
            throws Exception {
        runInThread(() -> apiMethods.getTaxTransaction(id, token, key, feeLevel,
                recipientAddress, amount));
    }

    //------------------------------------------------------------------------------------------

    private void runInThread(Runnable code) throws Exception {
        Thread thread = new Thread(code);
        thread.start();
        thread.join();
        Exception exception = apiMethods.getException();
        if (exception != null){
            throw exception;
        }
    }

    //------------------------------------------------------------------------------------------

    public String getToken(){
        return apiMethods.getTokenUser();
    }

    public Exception getException(){
        return apiMethods.getException();
    }

    public JSONObject getBalances(){
        return apiMethods.getBalancesPost();
    }

    public JSONArray getCoins(){
        return apiMethods.getCoins();
    }

    public JSONObject getSeedPhrasesUser(){
        return apiMethods.getSeedPhrasesUser();
    }

    public JSONObject getInfoTransaction() { return apiMethods.getInfoTransaction();}

    public String getKeyUser(){
        return apiMethods.getKeyUsers();
    }

    public String getTaxSize() { return apiMethods.getTaxSize(); }

    public TaxCoin getTaxCoin() { return apiMethods.getTaxCoin(); }
    public JSONObject getAddressesUser() {return apiMethods.getAddressesUsers();}
}
