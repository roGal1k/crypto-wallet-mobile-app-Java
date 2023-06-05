package com.cuttlesystems.cuttlewallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cuttlesystems.util.ApiMethodsProxy;
import com.cuttlesystems.util.AssetsAdapter;
import com.cuttlesystems.util.Coins;
import com.cuttlesystems.util.CoinsJsonParser;
import com.cuttlesystems.util.SelectedListenerAsset;

import org.json.JSONException;

import java.util.ArrayList;

public class AssetsActivity extends AppCompatActivity {

    String token;
// -------------------------------------------------------------------------------------------------
    ApiMethodsProxy apiMethodsProxy;
// -------------------------------------------------------------------------------------------------
    public static class OptionsResponse {
        public String token;
        public String state;
        public String id;
        private OptionsResponse(String token, String state, String id)
        {
            this.token = token;
            this.state = state;
            this.id = id;
        }
    }
    ArrayList<Coins> allCoins;

// -------------------------------------------------------------------------------------------------
    RecyclerView recyclerView;
// -------------------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    class AssetsAsync extends android.os.AsyncTask<OptionsResponse, Void, Void> {

        @SuppressLint("LongLogTag")
        @Override
        protected Void doInBackground(OptionsResponse ... params) {
            try {
                apiMethodsProxy = new ApiMethodsProxy(AssetsActivity.this);
                apiMethodsProxy.updateCoinsPost(params[0].token, params[0].state, params[0].id);
                apiMethodsProxy.getCoinsPost(token);
                CoinsJsonParser parser = new CoinsJsonParser(apiMethodsProxy.getCoins());
                allCoins = parser.getCoins();
            } catch (RuntimeException e) {
                Log.e("Runtime exception in ShareBalanceActivity",
                        "Runtime in apiMethodsProxy.getAddressPost(tokenUser, keyUser, idCoin): " + e.getMessage());
            }  catch (Exception ex) {
                Log.e("Critical Error in ShareBalanceActivity", ex.getMessage());
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void unused) {
            updateList();
        }
    }

    class AssetsCoinAsync extends android.os.AsyncTask<Void, Void, Void> {

        @SuppressLint("LongLogTag")
        protected Void doInBackground(Void... params) {
            try {
                apiMethodsProxy.getCoinsPost(token);
                CoinsJsonParser parser = new CoinsJsonParser(apiMethodsProxy.getCoins());
                allCoins = parser.getCoins();
            } catch (RuntimeException e) {
                Log.e("Runtime exception in HomeFragment",
                        "Runtime in apiMethodsProxy.getCoinsPost(tokenUser): " + e.getMessage());
            } catch (JSONException e) {
                Log.e("Runtime exception in HomeFragment",
                        "JSON not parsed in parser.getCoins(): " + e.getMessage());
            } catch (Exception ex) {
                Log.e("Critical Error in HomeFragment", ex.getMessage());
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void unused) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets);
        apiMethodsProxy = new ApiMethodsProxy(AssetsActivity.this);

        token = getIntent().getExtras().getString("Token");

        ArrayList<Parcelable> assets = getIntent().getParcelableArrayListExtra("Coins");

        recyclerView = findViewById(R.id.table_assets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SelectedListenerAsset listener = new SelectedListenerAsset() {
            @Override
            public void onMyItemClick(String id, String state){
                new AssetsAsync().execute(new OptionsResponse(token, state, id));
            }
        };
        AssetsAdapter adapter = new AssetsAdapter(this,assets,listener);
        recyclerView.setAdapter(adapter);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 100);
        initCustomization();
        getSupportActionBar().hide();
        findViewById(R.id.closeAsset).setBackgroundResource(R.drawable.close_icon);
    }

    public void closeAssetSlot(View v) {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void updateList()
    {
        AssetsAdapter adapter = (AssetsAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.clearAll();
            if (allCoins.size()!=0) adapter.insertAll(allCoins);
        }
    }

    private void initCustomization()
    {
        SearchView svAssets = this.findViewById(R.id.searchAssets);
        svAssets.setQueryHint("Search Here");
    }
}