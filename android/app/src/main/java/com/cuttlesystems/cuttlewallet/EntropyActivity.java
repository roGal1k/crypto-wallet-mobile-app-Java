package com.cuttlesystems.cuttlewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cuttlesystems.util.ApiMethodsProxy;
import com.cuttlesystems.util.SeedPhrase;
import com.cuttlesystems.util.SeedPhraseJsonParser;
import com.cuttlesystems.cuttlewallet.R;

import org.json.JSONException;

import java.util.ArrayList;

public class EntropyActivity extends AppCompatActivity {

    final int SMALL_SIZE_ENTROPY = 128;
    final int MEDIUM_SIZE_ENTROPY = 192;
    final int HIGH_SIZE_ENTROPY = 256;
    final int PERFECT_SIZE_ENTROPY = 526;
// -------------------------------------------------------------------------------------------------
    final int PERCENT_FULL = 100;

    ArrayList <Integer> entropy = new ArrayList<>();
    String token;
    String key;
    ProgressBar progressBar;
    TextView entropyInfoWidget;

    int prevX = 0;
    int prevY = 0;

    boolean isResetToNullForSmall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entropy);
        token = getIntent().getExtras().get("token").toString();
        key = getIntent().getExtras().get("key").toString();
        getSupportActionBar().hide();
        progressBar = findViewById(R.id.progress_bar_entropy);
        progressBar.setVisibility(View.VISIBLE);
        entropyInfoWidget = findViewById(R.id.entropyInfo);
        String text = "<font color=#979797>To safely form a seed phrase, " +
                "you must press the screen for </font>";
        entropyInfoWidget.setText(Html.fromHtml(text));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getX();
                int y = (int) event.getY();
                int sizeOfEntropy = entropy.size();
                if (x != prevX && y != prevY) {
                    if (sizeOfEntropy < SMALL_SIZE_ENTROPY) {
                        progressBar.setProgress((int) (sizeOfEntropy * PERCENT_FULL / SMALL_SIZE_ENTROPY));
                        initSmallSet();
                    }
                    if (sizeOfEntropy > SMALL_SIZE_ENTROPY && sizeOfEntropy < MEDIUM_SIZE_ENTROPY) {
                        progressBar.setProgress((int) (sizeOfEntropy * PERCENT_FULL / MEDIUM_SIZE_ENTROPY));
                        if (sizeOfEntropy == MEDIUM_SIZE_ENTROPY - 1)
                            progressBar.setProgress(0);
                        initMediumSet();
                    }
                    if (sizeOfEntropy > MEDIUM_SIZE_ENTROPY && sizeOfEntropy < HIGH_SIZE_ENTROPY) {
                        progressBar.setProgress((int) (sizeOfEntropy * PERCENT_FULL / HIGH_SIZE_ENTROPY));
                        if (sizeOfEntropy == HIGH_SIZE_ENTROPY - 1)
                            progressBar.setProgress(0);
                        initHighSet();
                    }
                    if (sizeOfEntropy > HIGH_SIZE_ENTROPY && sizeOfEntropy < PERFECT_SIZE_ENTROPY) {
                        progressBar.setProgress((int) (sizeOfEntropy * PERCENT_FULL / PERFECT_SIZE_ENTROPY));
                        if (sizeOfEntropy == PERFECT_SIZE_ENTROPY - 1)
                            progressBar.setVisibility(View.GONE);
                        initPerfectSet();
                    }
                    entropy.add(x);
                    entropy.add(y);
                }
                prevX = x;
                prevY = y;
                break;
            case MotionEvent.ACTION_UP:
                break;
            }
        return true;
    }

    public void createNewSeedPhrases() throws Exception {
        ApiMethodsProxy apiMethodsProxy = new ApiMethodsProxy(EntropyActivity.this);
        apiMethodsProxy.createSeedPhrasePost(token,key,entropy);
    }

    @SuppressLint("LongLogTag")
    public void createSeedPhraseSlot(View v){
        try{//
            createNewSeedPhrases();
        }
        catch (InterruptedException ex)
        {
            Log.e("Error in entropy", "(Thread has interrupted) " + ex.getMessage());
        }
        catch (Exception e) {
            Log.e("Undressed error from EntropyActivity", e.getMessage());
        }
        finally {
            Intent intent = new Intent(this, SeedActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("key", key);
            ApiMethodsProxy apiMethodsProxy = new ApiMethodsProxy(EntropyActivity.this);
            try {
                apiMethodsProxy.getSeedPhrasesPost(token,key);
                ArrayList<SeedPhrase> seedPhrases = new SeedPhraseJsonParser(
                        apiMethodsProxy.getSeedPhrasesUser()).getSeed();
                intent.putParcelableArrayListExtra("seeds", seedPhrases);
            } catch (RuntimeException e) {
                Log.e("Error from entropyActivity", "(Server not response) " + e.getMessage());
            } catch (JSONException e) {
                Log.e("Error from entropyActivity", "(JSon from response " +
                        "server not must parsed) "+ e.getMessage());
            } catch (Exception e) {
                Log.e("Undressed error from EntropyActivity",e.getMessage());
            }
            finish();
            startActivity(intent);
        }
    }


    private void initSmallSet()
    {
        progressBar.setVisibility(View.VISIBLE);
        TextView entropyInfoWidget = findViewById(R.id.entropyInfo);
        String text = "<font color=#979797 size=\"7\">To safely generate a seed phrase, swipe \n" +
                "Progress small set formed:</font>" +
                "<font color=#979797>" +
                String.valueOf((int)(entropy.size()*100/SMALL_SIZE_ENTROPY)) + "%</font>";
        entropyInfoWidget.setText(Html.fromHtml(text));
    }

    private void initMediumSet()
    {
        progressBar.setVisibility(View.VISIBLE);
        TextView entropyInfoWidget = findViewById(R.id.entropyInfo);
        String text = "<font color=#979797 size=\"7\">To safely generate a seed phrase, swipe \n" +
                "Progress normal set formed:</font>" +
                "<font color=#E7DDF2>" +
                String.valueOf((int)(entropy.size()*100/MEDIUM_SIZE_ENTROPY)) + "%</font>";
        entropyInfoWidget.setText(Html.fromHtml(text));
    }

    private void initHighSet()
    {
        progressBar.setVisibility(View.VISIBLE);
        TextView entropyInfoWidget = findViewById(R.id.entropyInfo);
        String text = "<font color=#979797>To safely generate a seed phrase, swipe \n" +
                "Progress normal set formed:</font>" +
                "<font color=#5F32AA>" +
                String.valueOf((int)(entropy.size()*100/HIGH_SIZE_ENTROPY)) + "%</font>";
        entropyInfoWidget.setText(Html.fromHtml(text));
    }

    private void initPerfectSet()
    {
        progressBar.setVisibility(View.VISIBLE);
        TextView entropyInfoWidget = findViewById(R.id.entropyInfo);
        String text = "<font color=#979797>To safely generate a seed phrase, swipe \n" +
                "Progress perfect set formed:</font>" +
                "<font color=#83D39D>" +
                String.valueOf((int)(entropy.size()*100/PERFECT_SIZE_ENTROPY)) + "%</font>";
        entropyInfoWidget.setText(Html.fromHtml(text));
    }
}