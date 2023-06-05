package com.cuttlesystems.cuttlewallet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.cuttlesystems.util.ApiMessageException;
import com.cuttlesystems.util.ApiMethodsProxy;
import com.cuttlesystems.util.CoinsJsonParser;
import com.cuttlesystems.util.LoadingAlert;
import com.cuttlesystems.util.TaxSize;
import com.cuttlesystems.cuttlewallet.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SendActivity extends AppCompatActivity {

// -------------------------------------------------------------------------------------------------
    private ApiMethodsProxy apiMethodsProxy;
// -------------------------------------------------------------------------------------------------
    private ArrayList <String> taxTransaction = new ArrayList<>();
// -------------------------------------------------------------------------------------------------
    private ImageButton scanButton;
    private RadioGroup mRadioGroup;
    private EditText valueWidget;
    private EditText addressWidget;
    private TextView taxSizeWidget;
    private TextView codeWidget;
// -------------------------------------------------------------------------------------------------
    private Context context;
// -------------------------------------------------------------------------------------------------
    LoadingAlert loadingAlert;
// -------------------------------------------------------------------------------------------------
    private String code;
// -------------------------------------------------------------------------------------------------
    private Intent intent;
// -------------------------------------------------------------------------------------------------
    class AllTaxAsync extends android.os.AsyncTask<String, Void, String>
    {
        Handler handler = new Handler();
        Runnable updateTextRunnable;
        boolean stateLoading = false;

        @SuppressLint({"LongLogTag"})
        @Override
        protected String doInBackground(String... arrayLists) {
            try {
                //toDo::optimized
                taxTransaction.clear();
                apiMethodsProxy.getTaxTransaction(
                        arrayLists[0],
                        arrayLists[1],
                        arrayLists[2],
                        TaxSize.LOW,
                        arrayLists[3],
                        arrayLists[4]);
                setTaxesFromApi();

                apiMethodsProxy.getTaxTransaction(
                        arrayLists[0],
                        arrayLists[1],
                        arrayLists[2],
                        TaxSize.MEDIUM,
                        arrayLists[3],
                        arrayLists[4]);
                setTaxesFromApi();

                apiMethodsProxy.getTaxTransaction(
                        arrayLists[0],
                        arrayLists[1],
                        arrayLists[2],
                        TaxSize.HIGH,
                        arrayLists[3],
                        arrayLists[4]);
                setTaxesFromApi();
                Log.d("Taxes", taxTransaction.toString());
            }
            catch (Exception e) {
                Log.e("Error from sendActivity", "doInBackground: Critical unaccepted Error"
                        + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            setStateClickableWidget(stateLoading);
            Toast.makeText(context,"Calculating taxes, please wait", Toast.LENGTH_SHORT).show();
            stateLoading = true;
            final String originalText = "Loading";
            final int ellipsesCount = 3;
            final int delayInMillis = 500;

            updateTextRunnable = new Runnable() {
                int count = 0;

                @Override
                public void run() {
                    StringBuilder sb = new StringBuilder(originalText);
                    for (int i = 0; i < count; i++) {
                        sb.append('.');
                    }
                    taxSizeWidget.setText(sb.toString());
                    count = (count + 1) % (ellipsesCount + 1);
                    handler.postDelayed(this, delayInMillis);
                }
            };

            handler.post(updateTextRunnable);
        }

            @SuppressLint({"LongLogTag", "SetTextI18n"})
        @Override
        protected void onPostExecute(String s) {
            stateLoading = false;
            handler.removeCallbacks(updateTextRunnable);
            setStateClickableWidget(true);
            if(!taxTransaction.isEmpty())
            {
                switch (mRadioGroup.getCheckedRadioButtonId()){
                    case(R.id.lowTax):
                        taxSizeWidget.setText(taxTransaction.get(0));
                        break;
                    case(R.id.mediumTax):
                        taxSizeWidget.setText(taxTransaction.get(1));
                        break;
                    case(R.id.highTax):
                        taxSizeWidget.setText(taxTransaction.get(2));
                        break;
                }
            }
            else {
                String text = "<font color=#979797>Server is not response. </font>" +
                        "<font color=#1C1A18>Calculate now</font>";
                taxSizeWidget.setText(Html.fromHtml(text));
                taxSizeWidget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pressOnCalculateSlot(v);
                    }
                });
            }
        }
    }
// -------------------------------------------------------------------------------------------------

    class TransferAsync extends android.os.AsyncTask<Void, Void, Void> {

        @SuppressLint("LongLogTag")
        @Override
        protected Void doInBackground(Void... params) {
            String token= getIntent().getExtras().get("token").toString();
            String key= getIntent().getExtras().get("key").toString();
            String id= getIntent().getExtras().get("idCoin").toString();
            String value = valueWidget.getText().toString();
            String address = addressWidget.getText().toString();

            if (!address.isEmpty())
            {
                try {
                    apiMethodsProxy.transactionPost(value,address,key,token,id);
                    JSONObject JsonResponse = apiMethodsProxy.getInfoTransaction();
                    intent.putExtra("Id", JsonResponse.getString("txid"));
                    intent.putExtra("Link", JsonResponse.getString("link"));
                    intent.putExtra("state", true);
                    Toast.makeText(context,
                            "The transaction has been sent and is being processed",
                            Toast.LENGTH_SHORT).show();
                }
                catch (Exception ex)
                {
                    Log.e("Error in SendActivity:", ex.getMessage());
                    intent.putExtra("message", ex.getMessage());
                    intent.putExtra("state", false);
                }
            }
            else {
                Toast.makeText(context,
                        "Please insert address of the recipient",
                        Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            loadingAlert.startAlertDialog();
        }

        @Override
        protected void onPostExecute(Void unused) {
            loadingAlert.closeAlertDialog();
            startActivity(intent);
            finish();
        }
    }
// -------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadingAlert = new LoadingAlert(SendActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        getSupportActionBar().hide();

        context = this;

        apiMethodsProxy = new ApiMethodsProxy(SendActivity.this);
        intent = new Intent(this, InfoTransactionActivity.class);

        String name = getIntent().getExtras().get("nameCoin").toString();
        TextView nameActivity = findViewById(R.id.name_activity_send);
        nameActivity.setText(name);

        valueWidget = findViewById(R.id.value);
        mRadioGroup = findViewById(R.id.radioGroup);
        scanButton = findViewById(R.id.scan_button);
        addressWidget = findViewById(R.id.address);
        taxSizeWidget = findViewById(R.id.taxSize);
        codeWidget = findViewById(R.id.code_send_activity);

        code = getIntent().getExtras().getString("codeCoin");

        valueWidget.setHint("Amount");
        addressWidget.setHint("Address");
        codeWidget.setText(code);

        taxSizeWidget.setText("");
        scanButton.setOnClickListener(view -> startScanning());

        String taxText = "<font color=#979797>More information about the terms of the " +
                "offer and the size of the commission can be</font>" +
                "<font color=#1C1A18> found here</font>" + "<font color=#979797>.</font>";
        String anyQuestionText = "<font color=#979797>Any questions?</font>" +
                "<font color=#1C1A18>Ask them here</font>" + "<font color=#979797>.</font>";

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(taxTransaction.size()!=0) {
                    if (taxTransaction.get(0).isEmpty()&&
                            taxTransaction.get(1).isEmpty()&&
                            taxTransaction.get(2).isEmpty())
                    {
                        taxSizeWidget.setOnClickListener(null);
                    }else {
                        switch (checkedId) {
                            case (R.id.lowTax):
                                taxSizeWidget.setText(taxTransaction.get(0));
                                break;
                            case (R.id.mediumTax):
                                taxSizeWidget.setText(taxTransaction.get(1));
                                break;
                            case (R.id.highTax):
                                taxSizeWidget.setText(taxTransaction.get(2));
                                break;
                        }
                    }
                }
            }
        });


    //toDo:: review
        valueWidget.addTextChangedListener(new SendActivity.TextWatcher(valueWidget) {});
        addressWidget.addTextChangedListener(new SendActivity.TextWatcher(addressWidget) {});

        TextView tax= findViewById(R.id.taxInfoSend);
        tax.setTextSize(14);
        tax.setText(Html.fromHtml(taxText));
        TextView anyQuestion= findViewById(R.id.questionInfoText);
        anyQuestion.setTextSize(14);
        anyQuestion.setText(Html.fromHtml(anyQuestionText));
    }

    private void setTaxesFromApi()
    {
        if (apiMethodsProxy.getTaxSize().equals("null")) taxTransaction.add("— "
                +apiMethodsProxy.getTaxCoin().getShortName()) ;
        else taxTransaction.add(apiMethodsProxy.getTaxSize() +
                " " +apiMethodsProxy.getTaxCoin().getShortName());
    }

    public void pressOnCalculateSlot(View v)
    {
        String token= getIntent().getExtras().get("token").toString();
        String key= getIntent().getExtras().get("key").toString();
        String id= getIntent().getExtras().get("idCoin").toString();

        String address = addressWidget.getText().toString();

        String value = valueWidget.getText().toString();

        if (value.isEmpty()) value = "";
        if (address.isEmpty()) address = "";

        new AllTaxAsync().execute(id, token, key, address, value);
        taxSizeWidget.setOnClickListener(null);
    }

    public void sendingCoin(View v){
        addressWidget = findViewById(R.id.address);
        Log.d("Send activity",valueWidget.getText().toString());
        new TransferAsync().execute();
    }

    public void closeActivitySlot(View v)
    {
        finish();
    }

    private void startScanning() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureExtendsActivity.class);
        scanLauncher.launch(options);
    }

    ActivityResultLauncher <ScanOptions> scanLauncher = registerForActivityResult(new ScanContract(), result->
    {
       if(result.getContents() !=null)
       {
           AlertDialog.Builder builder = new AlertDialog.Builder(SendActivity.this);
           builder.setTitle("Result").setMessage(result.getContents())
                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
               }
           });
           EditText addressWidget = findViewById(R.id.address);
           addressWidget.setText(result.getContents());
       }

    });

    private void setStateClickableWidget(boolean state)
    {
        valueWidget.setEnabled(state);
        mRadioGroup.setEnabled(state);
        scanButton.setEnabled(state);
        addressWidget.setEnabled(state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private class TextWatcher implements android.text.TextWatcher {
        private String beforeText;
        private EditText widget;

        TextWatcher(EditText widget)
        {
            this.widget = widget;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            beforeText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void afterTextChanged(Editable s) {
            String param = widget.getText().toString();
            if (!param.equals(beforeText))
            {
                taxSizeWidget.setText("Calculate");
                taxSizeWidget.setTextColor(Color.BLACK);
                taxSizeWidget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pressOnCalculateSlot(v);
                    }
                });
            }
        }
    }

}