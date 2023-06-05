package com.cuttlesystems.cuttlewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cuttlesystems.util.ApiMethodsProxy;
import com.cuttlesystems.util.SeedPhrase;

import java.io.IOException;
import java.util.ArrayList;

public class SeedActivity extends AppCompatActivity implements InsertSeedDialogListener {

    String key;
    String token;
    String seed;
    String seedId;

// -------------------------------------------------------------------------------------------------
    boolean seedState;
// -------------------------------------------------------------------------------------------------
    Button removeOrCreateSeedPhrase;
    Button copyOrInsertSeedPhrase;
// -------------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed);
        getSupportActionBar().hide();
        key = getIntent().getExtras().get("key").toString();
        token =  getIntent().getExtras().get("token").toString();
        removeOrCreateSeedPhrase = findViewById(R.id.left_button_seed);
        copyOrInsertSeedPhrase = findViewById(R.id.right_button_seed);
        //toDo:: change when add method for many seedsPhrases
        try {
            ArrayList<SeedPhrase> seedList = convertToUserClassList(
                    getIntent().getParcelableArrayListExtra("seeds"));
            updateState(true);
            seed = seedList.get(0).getSeed();
            seedId = seedList.get(0).getId();
            TextView seedText = (TextView) findViewById(R.id.seedPhrases);
            seedText.setText(seed);
            setSeedIsCreated();
        }
        catch (Exception ex)
        {
            updateState(false);
            Log.e("ERROR SeedActivity", "SeedsPhrases is not created");
            setSeedNotCreated();
            initEmptySeed();
        }
    }

    public ArrayList<SeedPhrase> convertToUserClassList(ArrayList<Parcelable> parcelables) {
        ArrayList<SeedPhrase> userClasses = new ArrayList<>();
        for (Parcelable parcelable : parcelables) {
            userClasses.add((SeedPhrase) parcelable);
        }
        return userClasses;
    }

    public void closeSeedActivitySlot(View v){
        Intent returnIntent = new Intent();
        if(seedState)
            setResult(Activity.RESULT_OK, returnIntent);
        else setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    public void createSeedPhrasesNowSlot(View v)
    {
        Intent intent = new Intent(this, EntropyActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("token", token);
        intent.putExtra("seedId", seedId);
        startActivity(intent);
        finish();
    }

    public void copyInfoInBufferSlot(View v)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        TextView seedText = (TextView) findViewById(R.id.seedPhrases);
        if(!seedText.getText().toString().isEmpty()) {
            ClipData clip = ClipData.newPlainText("Seed phrases",seedText.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copy", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.e("ERROR SEED", "SeedsPhrases is null");
            initEmptySeed();
        }
    }

    @SuppressLint("SetTextI18n")
    public void setSeedNotCreated()
    {
        removeOrCreateSeedPhrase.setText("Create seed phrase");
        removeOrCreateSeedPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    createSeedPhrasesNowSlot(v);
            }});

        copyOrInsertSeedPhrase.setText("Insert seed phrase");
        copyOrInsertSeedPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertCustomSeedSlot(v);
            }});
    }

    @SuppressLint("SetTextI18n")
    public void setSeedIsCreated()
    {
        TextView labelSeed = findViewById(R.id.labelSeed);
        labelSeed.setText("");
        TextView seedText = findViewById(R.id.seedPhrases);
        seedText.setVisibility(View.VISIBLE);
        seedText.setText(seed);
        removeOrCreateSeedPhrase.setText("Remove seed phrase");
        removeOrCreateSeedPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    removeSeedPhrase();
                } catch (RuntimeException e) {
                    Log.e("Error seedActivity", "Server isn't responses");
                }
                catch (Exception e) {
                    Log.e("Error seedActivity", "Seed phrase don't created");
                }
            }});

        copyOrInsertSeedPhrase.setText("Copy seed phrase");
        copyOrInsertSeedPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyInfoInBufferSlot(v);
            }});
    }

    @SuppressLint("SetTextI18n")
    public void initEmptySeed()
    {
        TextView labelSeed = findViewById(R.id.labelSeed);
        labelSeed.setText("Seed phrase has not been created yet");
        TextView seedText =findViewById(R.id.seedPhrases);
        seedText.setVisibility(View.INVISIBLE);
    }

    public void insertCustomSeedSlot(View v)
    {
        InsertSeedDialog exampleDialog = new InsertSeedDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void removeSeedPhrase(){
        ApiMethodsProxy apiMethodsProxy = new ApiMethodsProxy(SeedActivity.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("After deleting a seed phrase, it will not be possible to restore it." +
                        "Please save the seed phrase so you don't lose access to your current wallets."+
                        "Delete seed phrase?")
                .setTitle("Dangerous action")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            apiMethodsProxy.deleteSeedPhrasePost(token,seedId);
                            updateState(false);
                        }
                        catch (IOException e) {
                            Log.e("Error from SeedActivity", "Seed phrases hasn't been remove");
                        } catch (RuntimeException e) {
                            Log.e("Error from SeedActivity", "Server not response");
                        } catch (Exception e) {
                            Log.e("Error from SeedActivity", "Unexpected error: " +
                                    e.getMessage());
                        }
                        finally {
                            setSeedNotCreated();
                            initEmptySeed();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateState(true);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void applyTexts(String seed){
        ApiMethodsProxy apiMethodsProxy = new ApiMethodsProxy(SeedActivity.this);
        this.seed = seed;
        String[] wordsChecked = seed.split("\\s+");
        int numWords = wordsChecked.length;
        Log.d("Checked length users seed phrase", String.valueOf(numWords));
        if (numWords==24) {
            try {
                apiMethodsProxy.setUserSeedPhrasePost(token,key,seed);
                updateState(true);
            } catch (Exception e) {
                this.seed = null;
                updateState(false);
                Log.e("Error from SeedActivity", e.getMessage());
            }
        } else {
            this.seed = null;
            updateState(false);
            Toast.makeText(this,
                    "The seed phrase must be correct!",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void updateState(boolean state) {
        seedState = state;
        if (state) { //seed's created
            setSeedIsCreated();
        } else { //seed isn't create
            initEmptySeed();
            setSeedNotCreated();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

}