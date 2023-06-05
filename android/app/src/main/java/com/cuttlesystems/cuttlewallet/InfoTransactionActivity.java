package com.cuttlesystems.cuttlewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cuttlesystems.cuttlewallet.R;

public class InfoTransactionActivity extends AppCompatActivity {

    ImageButton closeActivity;
    ImageView   imageStateTransaction;
    TextView    titleActivity;
    TextView    titleStateTransaction;
    TextView    globalTitleStateTransaction;
    TextView    idTransaction;
    TextView    titleInfoResultTransaction;
    TextView    infoResultTransaction;
// -------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_transaction);
        getSupportActionBar().hide();

        titleActivity = findViewById(R.id.title_result_transaction_activity);
        closeActivity = findViewById(R.id.close_result_transaction);
        imageStateTransaction = findViewById(R.id.image_result_transaction);
        titleStateTransaction = findViewById(R.id.title_text_result_transaction);
        globalTitleStateTransaction = findViewById(R.id.title_info_window_result_transaction);
        idTransaction = findViewById(R.id.identifier_result_transaction);
        titleInfoResultTransaction = findViewById(R.id.title_info_result_transaction);
        infoResultTransaction = findViewById(R.id.info_result_transaction);

        initStateTransaction(getIntent().getExtras().getBoolean("state"));
    }

    @SuppressLint("SetTextI18n")
    public void initStateTransaction(boolean state)
    {
        if(state)
        {
            imageStateTransaction.setImageResource(R.drawable.completed_transaction_img);
            globalTitleStateTransaction.setText("Transaction sent to the blockchain");
            titleStateTransaction.setText("Transaction ID");
            idTransaction.setVisibility(View.VISIBLE);
            idTransaction.setText(getIntent().getExtras().getString("Id"));
            titleInfoResultTransaction.setText("Transaction link");
            SpannableString spannableString =
                    new SpannableString(getIntent().getExtras().getString("Link"));
            URLSpan urlSpan =
                    new URLSpan(getIntent().getExtras().getString("Link"));

            spannableString.setSpan(urlSpan, 0,
                    getIntent().getExtras().getString("Link").length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            infoResultTransaction.setText(spannableString);
            infoResultTransaction.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else
        {
            imageStateTransaction.setImageResource(R.drawable.error_transaction_img);
            globalTitleStateTransaction.setText("Error sending transaction");
            titleStateTransaction.setText("Failed to send transaction");
            idTransaction.setOnClickListener(null);
            idTransaction.setVisibility(View.GONE);
            titleInfoResultTransaction.setText("An error occurred while sending the transaction:");
            infoResultTransaction.setText(getIntent().getExtras().getString("message"));
        }
    }

    public void closeInfoTransactionActivityClicked(View v) {
        finish();
    }

    public void copyIdTransactionClicked(View v)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if(!idTransaction.getText().toString().isEmpty()) {
            ClipData clip = ClipData.newPlainText("Information about transaction",
                    idTransaction.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copy", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.e("ERROR InfoTransaction", "SeedsPhrases is null");
        }
    }
}