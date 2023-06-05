package com.cuttlesystems.cuttlewallet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cuttlesystems.cuttlewallet.R;

public class AddNewNet extends AppCompatDialogFragment {

    AddNewNetDialogListener listener;
    public EditText editNet;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_add_net, null);


        editNet = view.findViewById(R.id.edit_add_net);
        editNet.setHint("Enter address new net");

        builder.setView(view)
                .setTitle("Add new net")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = editNet.getText().toString();
                        try {
                            listener.applyTexts(text);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AddNewNetDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement AddNewDialogListener");
        }
    }
}