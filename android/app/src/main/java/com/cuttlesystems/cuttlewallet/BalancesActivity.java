package com.cuttlesystems.cuttlewallet;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cuttlesystems.cuttlewallet.R;

import java.util.ArrayList;

public class BalancesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balances);
    }

    public void closeBalancesClicked(View v) {
        finish();
    }

    public ArrayList<String> initBalances(){
        return (ArrayList<String>) getIntent().getStringArrayListExtra("BalancesUser");
    }

    public ArrayList<String> initNames(){
        return (ArrayList<String>) getIntent().getStringArrayListExtra("BalancesName");
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //count = Integer.parseInt(intent.getStringExtra("shareBalance"));
            StartShareBalanceActivity(intent.getStringExtra("shareBalance"));
        }
    };

    private void StartShareBalanceActivity(String count){
        Intent intentBalance = new Intent(this, ShareBalanceActivity.class);
        intentBalance.putExtra("count", count);
        startActivity(intentBalance);
    }

    public void addBalances(){}
}