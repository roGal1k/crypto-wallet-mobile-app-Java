package com.cuttlesystems.cuttlewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cuttlesystems.util.ApiMethodsProxy;
import com.cuttlesystems.cuttlewallet.R;

public class SettingsActivity extends AppCompatActivity {
    EditText oldPassword;
    EditText newPassword;
    EditText newPasswordConfirm;

    String tokenUser;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Bundle intentExtras = getIntent().getExtras();
        tokenUser = intentExtras.get("token").toString();
        key = intentExtras.get("key").toString();

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        newPasswordConfirm = findViewById(R.id.newPasswordConfirm);
    }

    public void onChangePassword(View view){
        String oldPasswordString = oldPassword.getText().toString();
        String newPasswordString = newPassword.getText().toString();
        String newPasswordConfirmString = newPasswordConfirm.getText().toString();

        Log.d(
                "changePassword",
                String.format(
                        "old pass %s new pass %s confirm %s",
                        oldPasswordString, newPasswordString, newPasswordConfirmString)
        );

        if (newPasswordString.equals(newPasswordConfirmString))
        {
            ApiMethodsProxy apiMethodsProxy = new ApiMethodsProxy(this);
            try {
                apiMethodsProxy.changePassword(tokenUser, oldPasswordString, newPasswordString);
                Toast.makeText(
                        this,
                        "Password successfully changed",
                        Toast.LENGTH_LONG
                ).show();
            } catch (Exception e) {
                Toast.makeText(
                        this,
                        e.toString(),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
        else
        {
            Toast.makeText(
                    this,
                    "Password confirmation not equal to password",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}