package com.cuttlesystems.cuttlewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import com.cuttlesystems.util.ApiMethodsProxy;
import com.cuttlesystems.util.Configurator;
import com.cuttlesystems.cuttlewallet.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class RegistrateActivity extends AppCompatActivity {

    private ApiMethodsProxy apiMethodsProxy;
// -------------------------------------------------------------------------------------------------
    private EditText userName;
    private EditText password;
    private EditText password2;
    private EditText email;
    private Spinner mSpinner;
// -------------------------------------------------------------------------------------------------
    Configurator config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrate);
        getSupportActionBar().hide();

        apiMethodsProxy = new ApiMethodsProxy(RegistrateActivity.this);

        userName = findViewById(R.id.userNameReg);
        password = findViewById(R.id.passwordReg);
        password2 = findViewById(R.id.passwordReg2);
        email = findViewById(R.id.emailReg);


        config = new Configurator(RegistrateActivity.this);

        List<String> items = config.getListNet();


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, items);
        mSpinner = findViewById(R.id.spinner_registrate);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        Button addNewNet = (Button) findViewById(R.id.add_net_registrate);

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
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        String text = "<font color=#979797>Do you already have an account?</font> " +
                "<font color=#1C1A18>Login account</font>";
        TextView tvCreateAccount= findViewById(R.id.loginAccount);
        tvCreateAccount.setText(Html.fromHtml(text));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void signUpClicked(View v){
        try {//
            loadRegistrate();
        }
        catch (InterruptedException e) {
            Toast.makeText(this,"Critical error: " +
                    e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this,
                    "User " + ((EditText)findViewById(R.id.userNameLog)).getText()
                            +" with insert password not founded",
                    Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            Toast.makeText(this,"Unexpected error: " +
                    e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void goToLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("LongLogTag")
    private void loadRegistrate() throws Exception {
        //toDO: remove if not used more
        if (userName.getText().length() != 0) {
            if (password.getText().length() != 0) {
                if (email.getText().length() != 0) {
                    if (password2.getText().length() != 0) {
                        if (password.getText().toString().equals(password2.getText().toString())) {
                            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                                apiMethodsProxy = new ApiMethodsProxy(RegistrateActivity.this);
                                apiMethodsProxy.createNewUser(userName.getText().toString(),
                                        password.getText().toString(), email.getText().toString());
                                Toast.makeText(this,
                                        "Congratulations. " +userName.getText()+", created new account!",
                                        Toast.LENGTH_SHORT).show();
                                startMainActivity();
                            }
                            else {
                                Log.d("Error from RegistrateActivity",
                                        "Users e-mail not validate");
                                Toast.makeText(this,
                                        "E-mail format not followed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Log.d("Error from RegistrateActivity",
                                    "User's password mismatch");
                            Toast.makeText(this,
                                    "Password mismatch", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Log.d("Error from RegistrateActivity",
                                "Field repeat password are empty");
                        Toast.makeText(this,
                                "Please enter password again",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d("Error from RegistrateActivity",
                            "Field e-mail are empty");
                    Toast.makeText(this,
                            "Please enter e-mail in field",
                            Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Log.d("Error from RegistrateActivity",
                        "Field password are empty");
                Toast.makeText(this,
                        "Please enter password in field",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Log.d("Error from RegistrateActivity",
                    "Field username are empty");
            Toast.makeText(this,
                    "Please enter username in field",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void addNewNetSlot(View view){
        AddNewNet exampleDialog = new AddNewNet();
        exampleDialog.show(getSupportFragmentManager(), "AddNet dialog");
    }
}