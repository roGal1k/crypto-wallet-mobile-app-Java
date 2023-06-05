package com.cuttlesystems.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cuttlesystems.cuttlewallet.R;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AssetsAdapter extends RecyclerView.Adapter<AssetsViewHolder>{

    ArrayList<Coins> assets;
// -------------------------------------------------------------------------------------------------
    Context context;
// -------------------------------------------------------------------------------------------------
    SelectedListenerAsset listener;
// -------------------------------------------------------------------------------------------------
    public AssetsAdapter(Context context, ArrayList<Parcelable> assets,
                         SelectedListenerAsset listener) {
        this.assets = convertToUserClassList(assets);
        this.context = context;
        this.listener = listener;
    }

    public void insertAll(ArrayList<Coins> assets)
    {
        this.assets.addAll(assets);
        this.notifyDataSetChanged();
    }

    public ArrayList<Coins> convertToUserClassList(ArrayList<Parcelable> parcelables) {
        ArrayList<Coins> userClasses = new ArrayList<>();
        for (Parcelable parcelable : parcelables) {
            userClasses.add((Coins) parcelable);
        }
        return userClasses;
    }

    public void clearAll() {
        this.assets.clear();
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assets_table,
                parent, false);
        return new AssetsViewHolder(context,view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetsViewHolder holder, int position) {

        int index= holder.getAdapterPosition();

        holder.coinCode.setText(assets.get(index).getShortName().toString());
        holder.coinName.setText(assets.get(index).getFullName().toString());
        String imageUrl = assets.get(index).getIcon().toString();
        Picasso.get()
                .load(imageUrl)
                .into(holder.coinIcon);

        if("true".equals(assets.get(index).getState()))
        {
            //stateTest = true;
            holder.stateCoin.setText("ON");
            holder.stateCoin.setTextColor(Color.GREEN);
            holder.state = "true";
            //holder.switcher.setChecked(true);
        }
        else {
            holder.state = "false";
            holder.stateCoin.setText("OFF");
            holder.stateCoin.setTextColor(Color.RED);
        }

        //holder.switcher.setOnClickListener(new View.OnClickListener() {
        holder.stateCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {//
                    String state = assets.get(index).getState();


                    if (state.equals(holder.state)) {
                        if ("true".equals(state)) {
                            holder.state = "false";
                            holder.stateCoin.setText("OFF");
                            holder.stateCoin.setTextColor(Color.RED);
                        } else {
                            holder.state = "true";
                            holder.stateCoin.setText("ON");
                            holder.stateCoin.setTextColor(Color.GREEN);
                        }
                    }
                    else
                    {
                        if ("true".equals(state)) {
                            holder.state = "true";
                            holder.stateCoin.setText("ON");
                            holder.stateCoin.setTextColor(Color.GREEN);
                        } else {
                            holder.state = "false";
                            holder.stateCoin.setText("OFF");
                            holder.stateCoin.setTextColor(Color.RED);
                        }
                    }

                    listener.onMyItemClick(assets.get(holder.getAdapterPosition()).getId(),holder.state);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return assets.size();
    }

    public void notifyDataSetChanged(int adapterPosition) {
    }
}

class AssetsViewHolder extends RecyclerView.ViewHolder{

    TextView coinCode;
    TextView coinName;
    ImageView coinIcon;
    Context context;
    //Switch switcher;
    TextView stateCoin;

    String state;
    private AssetsAdapter adapter;

    @SuppressLint("NotifyDataSetChanged")
    public AssetsViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.state = "false";
        coinCode = itemView.findViewById(R.id.coin_code);
        coinName = itemView.findViewById(R.id.coin_name);
        coinIcon = itemView.findViewById(R.id.coinIcon);
        stateCoin = itemView.findViewById(R.id.stateCoin);
        //switcher = itemView.findViewById(R.id.switch_asset);
    }

    public AssetsViewHolder linkAdapter(AssetsAdapter adapter)
    {
        this.adapter = adapter;
        return this;
    }
}