package com.cuttlesystems.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cuttlesystems.cuttlewallet.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceViewHolder>{

    ArrayList<UserBalance> items;
    SelectedListener listener;
    Context context;

    public BalanceAdapter(Context context, ArrayList<Parcelable> items, SelectedListener listener) {
        this.items = convertToUserClassList(items);
        this.listener = listener;
        this.context = context;
    }

    public ArrayList<UserBalance> convertToUserClassList(ArrayList<Parcelable> parcelables) {
        ArrayList<UserBalance> userClasses = new ArrayList<>();
        for (Parcelable parcelable : parcelables) {
            userClasses.add((UserBalance) parcelable);
        }
        return userClasses;
    }

    public void updateAll(ArrayList<UserBalance> items)
    {
        clearAll();
        this.items.addAll(items);
        this.notifyDataSetChanged();
    }
    public void clearAll()
    {
        this.items.clear();
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_balance_table,
                parent, false);
        return new BalanceViewHolder(context,view).linkAdapter(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        holder.balanceName.setText(items.get(position).getName());
        holder.balanceTitle.setText(items.get(position).getAlphabeticCode());
        holder.balanceVar.setText(items.get(position).getBalance()+
                items.get(position).getAlphabeticCode());

        Picasso.get()
            .load(items.get(position).getIcon())
            .into(holder.coinIcon);

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{//
                    listener.onMyItemClick(items.get(position).getName(),
                            items.get(position).getBalance(),items.get(position).getId(),
                            items.get(position).getAlphabeticCode(),items.get(position).getIcon());
                } catch (Exception e) {
                    Log.e("Critical error",e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

class BalanceViewHolder extends RecyclerView.ViewHolder{

    TextView balanceTitle;
    TextView balanceName;
    TextView balanceVar;
    TextView button;
    ImageView coinIcon;
    Context context;
    private BalanceAdapter adapter;

    @SuppressLint("NotifyDataSetChanged")
    public BalanceViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        balanceVar = itemView.findViewById(R.id.balance_value);
        balanceName = itemView.findViewById(R.id.balance_name);
        balanceTitle = itemView.findViewById(R.id.balance_title);
        button = itemView.findViewById(R.id.openin);
        coinIcon = itemView.findViewById(R.id.coinIcon);
    }
    
    public BalanceViewHolder linkAdapter(BalanceAdapter adapter)
    {
        this.adapter = adapter;
        return this;
    }
}