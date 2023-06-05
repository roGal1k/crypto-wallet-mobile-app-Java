package com.cuttlesystems.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.cuttlesystems.cuttlewallet.R;

public class LoadingAlert {

    private Activity activity;
    private AlertDialog dialog;
    
    public LoadingAlert(Activity activity){
        this.activity = activity;
    }

    public void startAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity,
                0);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading_layout, null));

        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    public void closeAlertDialog(){
        dialog.dismiss();
    }

}
