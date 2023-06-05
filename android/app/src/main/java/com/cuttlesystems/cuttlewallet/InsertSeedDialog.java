package com.cuttlesystems.cuttlewallet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cuttlesystems.cuttlewallet.R;

public class InsertSeedDialog extends AppCompatDialogFragment {
    public EditText editSeed;
    public InsertSeedDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        editSeed = view.findViewById(R.id.edit_seed);

        builder.setView(view)
                .setTitle("Insert your seed phrase")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String seed = editSeed.getText().toString();
                        try {
                            listener.applyTexts(seed);
                        } catch (Exception e) {
                            Log.e("Non handed error from InsertSeedDialog",e.getMessage());
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (InsertSeedDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement InsertSeedDialogListener");
        }
    }
}