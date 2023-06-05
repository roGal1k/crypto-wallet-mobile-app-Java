package com.cuttlesystems.cuttlewallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cuttlesystems.util.AddressAdapter;
import com.cuttlesystems.util.AddressJsonParser;
import com.cuttlesystems.util.ApiMethodsProxy;
import com.cuttlesystems.util.SelectedListenerAddress;
import com.cuttlesystems.util.UserAddress;
import com.cuttlesystems.cuttlewallet.R;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ShareBalanceActivity extends Activity {

    ApiMethodsProxy apiMethodsProxy;
// -------------------------------------------------------------------------------------------------
    HashMap<String, ArrayList<UserAddress>> addresses = new HashMap<>();
// -------------------------------------------------------------------------------------------------
    Boolean stateFinish = false;
// -------------------------------------------------------------------------------------------------
    TextView header;
    TextView balanceName;
    TextView balanceValue;
    TextView loadingAddresses;
    ImageView iconWidget;
    RecyclerView tableAddresses;
    TabLayout tablet;
    LinearLayout sendSet;
// -------------------------------------------------------------------------------------------------
    String idCoin;
    String tokenUser;
    String keyUser;
    String nameCoin;
    String balanceCoin;
    String icon;
    String code;

// -------------------------------------------------------------------------------------------------

    class ShareBalanceAsync extends android.os.AsyncTask<Void, Void, Void> {

        Boolean loadingState= false;
        Handler handler = new Handler();
        Runnable updateTextRunnable;

        @SuppressLint("LongLogTag")
        @Override
        protected Void doInBackground(Void... params) {
            try {
                apiMethodsProxy.getAddressPost(tokenUser, keyUser, idCoin);
                AddressJsonParser parser = new AddressJsonParser(
                        apiMethodsProxy.getAddressesUser().toString());
                addresses = parser.getAddresses();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initBitTableAddressPreview();
                    }
                });
            } catch (RuntimeException e) {
                Log.e("Runtime exception in ShareBalanceActivity",
                        "Runtime in apiMethodsProxy.getAddressPost(tokenUser, keyUser, idCoin): " + e.getMessage());
            } catch (JSONException e) {
                Log.e("Runtime exception in ShareBalanceActivity",
                        "JSON not parsed in parser.getAddresses(): " + e.getMessage());
            } catch (Exception ex) {
                Log.e("Critical Error in ShareBalanceActivity", ex.getMessage());
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            loadingState = true;
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
                    loadingAddresses.setVisibility(View.VISIBLE);
                    loadingAddresses.setText(sb.toString());
                    count = (count + 1) % (ellipsesCount + 1);
                    handler.postDelayed(this, delayInMillis);
                }
            };

            handler.post(updateTextRunnable);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            loadingState = false;
            handler.removeCallbacks(updateTextRunnable);

            loadingAddresses.setVisibility(View.GONE);
        }
    }

// -------------------------------------------------------------------------------------------------
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_balance);

        Bundle bundle = getIntent().getBundleExtra("mapAddresses");

        apiMethodsProxy = new ApiMethodsProxy(ShareBalanceActivity.this);

        findViewById(R.id.closeShare).setBackgroundResource(R.drawable.close_icon);

        header = findViewById(R.id.titleShare);
        balanceName = findViewById(R.id.NameBalance);
        balanceValue = findViewById(R.id.ValueBalance);
        tablet = findViewById(R.id.tabLayout);
        sendSet = findViewById(R.id.send_tank);
        tableAddresses = findViewById(R.id.address_table_widget);
        loadingAddresses = findViewById(R.id.loading_addresses_share_activity);
        iconWidget = findViewById(R.id.IconCoinShare);

        tablet.setVisibility(View.GONE);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        tableAddresses.setLayoutManager(layoutManager);

        idCoin = bundle.getString("id");
        nameCoin = bundle.getString("name");
        icon = bundle.getString("icon");
        code = bundle.getString("code");
        tokenUser = bundle.getString("token");
        keyUser = bundle.getString("key");
        balanceCoin = bundle.getString("balance");

        header.setText(nameCoin);
        balanceName.setText(nameCoin);
        balanceValue.setText((String) balanceCoin + " " + code);


        Picasso.get().load(icon).into(iconWidget);

        new ShareBalanceAsync().execute();

        sendSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toDo:: Add new activity for SendingCoins
                startSendingCoinActivity(v);
            }
        });
        tablet.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                RecyclerView recycler = findViewById(R.id.address_table_widget);
                AddressAdapter adapter = (AddressAdapter) recycler.getAdapter();
                if (adapter != null) adapter.clearAll();
                adapter.insertAll(new ArrayList<Parcelable>(
                        Objects.requireNonNull(addresses.get(
                                addresses.keySet().toArray()[tab.getPosition()].toString()))
                        )
                );
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                RecyclerView recycler = findViewById(R.id.address_table_widget);
                AddressAdapter adapter = (AddressAdapter) recycler.getAdapter();
                if (adapter != null) adapter.clearAll();
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initBitTableAddressPreview()
    {
        if(!addresses.isEmpty())
        {
            if(addresses.size()!=1)
            {
                tablet.setVisibility(View.VISIBLE);
            }
            else {
                tablet.setVisibility(View.GONE);
            }
            RecyclerView recyclerView = findViewById(R.id.address_table_widget);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ArrayList<Parcelable> test = new ArrayList<Parcelable>(
                    Objects.requireNonNull(
                            addresses.get(addresses.keySet().toArray()[0].toString())));

            SelectedListenerAddress listener = new SelectedListenerAddress() {
                @Override
                public void onMyItemClick(String address){
                    ClipboardManager clipboard =
                            (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    if(!address.isEmpty()) {
                        ClipData clip = ClipData.newPlainText("address:",address);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getBaseContext(), "Copy address:",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.e("ERROR:", "This coin not have address");
                    }
                }

                @SuppressLint("LongLogTag")
                @Override
                public void copyQr(Bitmap qr){
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    File fileCash = new File(getExternalCacheDir() +"/"+"userImage"+".png");

                    Intent intent;
                    try
                    {
                        FileOutputStream outFile = new FileOutputStream(fileCash);
                        qr.compress(Bitmap.CompressFormat.JPEG,100, outFile);
                        outFile.flush();
                        outFile.close();
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileCash));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, "Share image:"));
                    } catch (FileNotFoundException e) {
                        Log.e("Error from ShareBalanceActivity", e.getMessage());
                    } catch (IOException e) {
                        Log.e("Error from ShareBalanceActivity", e.getMessage());
                    }

                }
            };
            AddressAdapter adapter =
                    new AddressAdapter(this, test,(SelectedListenerAddress) listener);
            recyclerView.setAdapter(adapter);
        }
        else {
            Toast.makeText(this, "Sorry!" +
                    " At the moment we do not support working with this coin.", Toast.LENGTH_SHORT).show();
        }
    }

    public void startSendingCoinActivity(View v)
    {
        Intent intent = new Intent(this, SendActivity.class);
        intent.putExtra("token",    tokenUser);
        intent.putExtra("key",      keyUser);
        intent.putExtra("idCoin",   idCoin);
        intent.putExtra("nameCoin", nameCoin);
        intent.putExtra("codeCoin", code);

        startActivity(intent);
    }

    public void closeShareSlot(View v) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            stateFinish = true;
            setResult(Activity.RESULT_OK);
            finish();
        } else if (resultCode == RESULT_CANCELED) {
            setResult(Activity.RESULT_CANCELED);
            stateFinish = false;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }


}
