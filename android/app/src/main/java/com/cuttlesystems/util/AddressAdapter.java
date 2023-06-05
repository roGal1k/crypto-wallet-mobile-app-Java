package com.cuttlesystems.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cuttlesystems.cuttlewallet.R;
import com.google.zxing.WriterException;

import java.util.ArrayList;


public class AddressAdapter extends RecyclerView.Adapter<AdressViewHolder>{

    ArrayList<UserAddress> addresses;
    Context context;
    SelectedListenerAddress listener;

    public AddressAdapter(Context context, ArrayList<Parcelable> addresses,
                          SelectedListenerAddress listener) {

        this.addresses = convertToUserClassList(addresses);
        this.context = context;
        this.listener = listener;
    }

    public void insertAll(ArrayList<Parcelable> addresses)
    {
        this.addresses.addAll(convertToUserClassList(addresses));
        this.notifyDataSetChanged();
    }

    public ArrayList<UserAddress> convertToUserClassList(ArrayList<Parcelable> parcelables) {
        ArrayList<UserAddress> userClasses = new ArrayList<>();
        for (Parcelable parcelable : parcelables) {
            userClasses.add((UserAddress) parcelable);
        }
        return userClasses;
    }

    public void clearAll() {
        this.addresses.clear();
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_table,
                parent, false);
        return new AdressViewHolder(context,view).linkAdapter(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdressViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        holder.address.setText(addresses.get(position).getAddress());
        holder.balances.setText(addresses.get(position).getBalance());

        String textToEncode = addresses.get(position).getAddress();

        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{//
                    listener.onMyItemClick(addresses.get(position).getAddress());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        holder.copy.setImageResource(R.drawable.copy_icon);
        holder.share.setImageResource(R.drawable.share_icon);
        holder.copy.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.share.setScaleType(ImageView.ScaleType.FIT_CENTER);

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(textToEncode, 1200);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            holder.qr.setImageBitmap(bitmap);

            holder.share.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onClick(View v) {
                    try{//
                        listener.copyQr(bitmap);
                    } catch (Exception e) {
                        Log.e("Error from addressAdapter", e.getMessage().toString());
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

}

class AdressViewHolder extends RecyclerView.ViewHolder{

    TextView address;
    TextView balances;
    Context context;
    ImageView qr;
    ImageButton copy;
    ImageButton share;
    private AddressAdapter adapter;

    @SuppressLint("NotifyDataSetChanged")
    public AdressViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        balances = itemView.findViewById(R.id.address_aa);
        address = itemView.findViewById(R.id.balance_aa);
        copy = itemView.findViewById(R.id.copy_address);
        share = itemView.findViewById(R.id.shareQR);

        qr = itemView.findViewById(R.id.qrcode_address);
    }

    public AdressViewHolder linkAdapter(AddressAdapter adapter)
    {
        this.adapter = adapter;
        return this;
    }
}