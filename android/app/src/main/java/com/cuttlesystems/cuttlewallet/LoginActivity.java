package com.cuttlesystems.cuttlewallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cuttlesystems.util.ApiMethodsProxy;
import com.cuttlesystems.util.CoinsJsonParser;
import com.cuttlesystems.util.Configurator;
import com.cuttlesystems.util.LoadingAlert;
import com.cuttlesystems.cuttlewallet.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements AddNewNetDialogListener{

    private String tokenUser;
    private String keyUser;
// -------------------------------------------------------------------------------------------------
    private Spinner mSpinner;
    LoadingAlert loadingAlert;
    EditText userName;
    EditText password;
    Configurator config;
    Context context;
// -------------------------------------------------------------------------------------------------
    private ApiMethodsProxy apiMethodsProxy;

    class LoginAsync extends android.os.AsyncTask<String, Void, Boolean>
    {
        @SuppressLint("LongLogTag")
        @Override
        protected Boolean doInBackground(String... arrayLists) {
            try{
                /*!!! endured in new activity like Sber/Raif/VTb/others or add progressbar !!!*/
                apiMethodsProxy = new ApiMethodsProxy(LoginActivity.this);
                apiMethodsProxy.loginUserPost(arrayLists[0].toString(),
                        arrayLists[1].toString());

                tokenUser = apiMethodsProxy.getToken();
                apiMethodsProxy.getKeyPost(tokenUser,arrayLists[1].toString());
                keyUser = apiMethodsProxy.getKeyUser();
                return true;
            }
            catch (Exception ex) {
                Log.e("test async in main activity",ex.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if(s){
                Toast.makeText(context, "Welcome back, " + userName.getText(),
                        Toast.LENGTH_SHORT).show();
                loadingAlert.closeAlertDialog();
                startMainActivity();
            } else {
                loadingAlert.closeAlertDialog();
                Toast.makeText(context, "User not founded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadingAlert = new LoadingAlert(LoginActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        context = this;
        userName = findViewById(R.id.userNameLog);
        password = findViewById(R.id.passwordLog);
        String text = "<font color=#979797>Still no account?</font> " +
                "<font color=#1C1A18>Create an account</font>";
        TextView tvCreateAccount= findViewById(R.id.createAccount);
        tvCreateAccount.setText(Html.fromHtml(text));

        config = new Configurator(LoginActivity.this);

        List<String> items = config.getListNet();


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, items);
        mSpinner = findViewById(R.id.spinner_login);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        Button addNewNet = (Button) findViewById(R.id.add_net_login);

        addNewNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewNetSlot(v);
            }});

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                config.setCountNetwork(position);
                try {
                    config.saveConfigFile();
                } catch (JSONException e) {
                    Log.e("Exception from LoginActivity",
                            "Not working config.json:\n" + e.getMessage());
                } catch (IOException e) {
                    Log.e("Exception from LoginActivity",e.getMessage());
                }
                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
// -------------------------------------------------------------------------------------------------

    @Override
    protected void onStart(){
        super.onStart();
    }

    public void signInClicked(View v) throws JSONException {
        try{//
            loadingAlert.startAlertDialog();
            loadLoginning();
        }
        catch (IOException e) {
            System.out.println("Error connection: " + e);
            Toast.makeText(this,
                    "User " + ((EditText)findViewById(R.id.userNameLog)).getText()
                            +" with insert password not founded",
                    Toast.LENGTH_SHORT).show();
        }
        catch (InterruptedException e) {
            Toast.makeText(this,"Critical error: " +
                    e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            Toast.makeText(this,"Unexpected error: " +
                    e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void createAccountClicked(View v){
        Intent intent = new Intent(this, RegistrateActivity.class);
        startActivity(intent);
    }

    private void loadLoginning() throws Exception {
        if (userName.getText().length() != 0 && password.getText().length() != 0)
        {
            new LoginAsync().execute(userName.getText().toString(),
                    password.getText().toString());
        }
        else{
            Log.e("Error", "Information fields are empty");
            Toast.makeText(this,
                    "Information fields are empty" + userName.getText(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void addNewNetSlot(View view){
        AddNewNet exampleDialog = new AddNewNet();
        exampleDialog.show(getSupportFragmentManager(), "AddNet dialog");
    }

    @Override
    public void applyTexts(String text) {
        if(!text.isEmpty())
        {
            ArrayList <String> listNetwork = config.getListNet();
            listNetwork.add(text);
            config.setListNet(listNetwork);
            try {
                config.saveConfigFile();
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) mSpinner.getAdapter();
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error from LoginAct",
                        "NetworkList isEmpty()" + e.getMessage());
            } catch (IOException e) {
                Log.e("Error from LoginAct",
                        "Critical error - not find or create config files" + e.getMessage());
            }
        }
    }

    @SuppressLint("LongLogTag")
    public void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("token", tokenUser);
        intent.putExtra("key", keyUser);
        startActivity(intent);
        finish();
    }
}