package com.cuttlesystems.cuttlewallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cuttlesystems.util.ApiMethodsProxy;
import com.cuttlesystems.util.Coins;
import com.cuttlesystems.util.SeedPhrase;
import com.cuttlesystems.util.SeedPhraseJsonParser;
import com.cuttlesystems.util.SelectedListener;
import com.cuttlesystems.util.SelectedListenerStory;
import com.cuttlesystems.util.Storyz;
import com.cuttlesystems.util.TotalBalance;
import com.cuttlesystems.util.UserBalance;
import com.cuttlesystems.cuttlewallet.R;
import com.cuttlesystems.cuttlewallet.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SelectedListener,
        SelectedListenerStory, HomeFragment.DataTransferListener {
    BottomNavigationView bottomNavigationView;

// -------------------------------------------------------------------------------------------------
    private String tokenUser;
    private String keyUser;
// -------------------------------------------------------------------------------------------------
    ArrayList<Coins> allCoins;
    ApiMethodsProxy apiMethodsProxy;
    ArrayList<SeedPhrase> seedPhrases = new ArrayList<>();
// -------------------------------------------------------------------------------------------------
    NaviGate naviGate;
    TotalBalance total;

    @SuppressLint("SetTextI18n")
    public void totalClickedSlot(View v)
    {
        TextView totalView = findViewById(R.id.totalBalance);
        if (total!=null) {
            if (totalView.getText().toString().contains("BTC"))
                totalView.setText(total.getBalanceKZT() + " KZT");
            else if (totalView.getText().toString().contains("KZT"))
                totalView.setText(total.getBalanceUSD() + " USD");
            else totalView.setText(total.getBalanceBTC() + " BTC");
        }
    }
    @SuppressLint({"LongLogTag", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiMethodsProxy = new ApiMethodsProxy(MainActivity.this);

        naviGate = new NaviGate(this.getSupportFragmentManager());
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(naviGate);


        tokenUser = getIntent().getExtras().get("token").toString();
        keyUser = getIntent().getExtras().get("key").toString();

        Bundle bundle = new Bundle();
        bundle.putString("key", keyUser);
        bundle.putString("token", tokenUser);
        naviGate.getHomeFragment().setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, naviGate.getHomeFragment())
                .commit();
        naviGate.goHomeFragment();

        getSupportActionBar().hide();

    }

    @Override
    public void onDataTransfer(ArrayList<Coins> data) {
        allCoins = data;
    }

    private void loadEmptyBalances(){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("BalancesUser", new ArrayList<UserBalance>());
        naviGate.getHomeFragment().setArguments(bundle);
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
    }

    public void showSeedSlot(View v){

        Intent intent = new Intent(naviGate.getHomeFragment().getContext(), SeedActivity.class);
        intent.putExtra("key", keyUser);
        intent.putExtra("token", tokenUser);
        try {
            apiMethodsProxy.getSeedPhrasesPost(tokenUser,keyUser);
        } catch (Exception e) {
            Log.e("Error from MainActivity", e.getMessage() );
        }
        JSONObject seeds = apiMethodsProxy.getSeedPhrasesUser();
        SeedPhraseJsonParser seedParser = null;
        try {
            seedParser = new SeedPhraseJsonParser(seeds);
        } catch (JSONException e) {
            Log.e("Error from MainActivity", "Json with seeds is not created" + e.getMessage());
        }
        try {
            seedPhrases = seedParser.getSeed();
        } catch (JSONException e) {
            Log.e("Error from MainActivity", "Json with seeds is not created " +
                    "\nand not created ArrayList SeedPhrases " + e.getMessage() );
            seedPhrases = new ArrayList<>();
        }
        intent.putParcelableArrayListExtra("seeds", seedPhrases);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onMyItemClick(String name, String value, String id, String code, String icon){

        Intent intent = new Intent(naviGate.getHomeFragment().getContext(),
                ShareBalanceActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("balance",value);
        bundle.putString("name",name);
        bundle.putString("id",id);
        bundle.putString("token", tokenUser);
        bundle.putString("key", keyUser);
        bundle.putString("icon", icon);
        bundle.putString("code", code);
        bundle.putString("image", keyUser);

        intent.putExtra("mapAddresses", bundle);

        startActivityForResult(intent, 3);
    }

    public void openSettingsClicked(View view){
        Log.d("debug", "Open settings");
        String key = getIntent().getExtras().get("key").toString();

        Intent intent = new Intent(
                naviGate.getHomeFragment().getContext(),
                SettingsActivity.class);

        intent.putExtra("key", key);
        intent.putExtra("token", tokenUser);
        startActivity(intent);
    }

    @Override
    public void onStoryItemClick(int count){
        Intent intent = new Intent(naviGate.getHomeFragment().getContext(),
                StorysActivity.class);
        intent.putExtra("item", count);
        startActivity(intent);
    }

    public void showAddAssetSlot(View v){
        Intent intent = new Intent(naviGate.getHomeFragment().getContext(), AssetsActivity.class);
        intent.putExtra("Token", tokenUser);
        intent.putParcelableArrayListExtra("Coins", allCoins);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case (1):
            if (resultCode == RESULT_OK) {
                naviGate.getHomeFragment().updateAll();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d( "Main Activity", "Not available error in assetActivity" );
            }
            break;
            case (2):
                if (resultCode == RESULT_OK) {
                    naviGate.getHomeFragment().updateAll();
                } else if (resultCode == RESULT_CANCELED) {
                    seedPhrases = new ArrayList<>();
                    Log.d( "Main Activity", "Not available error in assetActivity" );
                }
                apiMethodsProxy = new ApiMethodsProxy(this);
                naviGate.getHomeFragment().updateAll();
                break;
            case (3):
                if (resultCode == RESULT_OK) {
                    naviGate.getHomeFragment().updateAll();
                } else if (resultCode == RESULT_CANCELED) {
                }
                break;
        }
    }

    @Override
    public void updateRecyclerView(ArrayList<UserBalance> balances) {
        HomeFragment fragment = naviGate.getHomeFragment();
        fragment.updateRecyclerView(balances);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}