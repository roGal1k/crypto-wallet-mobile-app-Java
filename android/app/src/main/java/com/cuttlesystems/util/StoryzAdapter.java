package com.cuttlesystems.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cuttlesystems.cuttlewallet.R;

import java.util.ArrayList;

public class StoryzAdapter extends RecyclerView.Adapter<StoryzViewHolder>{

    ArrayList<Drawable> pixmapStoryzList = new ArrayList<Drawable>();
    Context context;
    SelectedListenerStory listener;
    @SuppressLint("UseCompatLoadingForDrawables")
    public StoryzAdapter(Context context, SelectedListenerStory listener) {
        this.context = context;
        this.listener = listener;

        pixmapStoryzList.add(context.getResources().getDrawable(R.drawable.story_1));
        pixmapStoryzList.add(context.getResources().getDrawable(R.drawable.story_2));
        pixmapStoryzList.add(context.getResources().getDrawable(R.drawable.story_3));
        pixmapStoryzList.add(context.getResources().getDrawable(R.drawable.story_4));
        pixmapStoryzList.add(context.getResources().getDrawable(R.drawable.story_5));
        pixmapStoryzList.add(context.getResources().getDrawable(R.drawable.story_6));
        pixmapStoryzList.add(context.getResources().getDrawable(R.drawable.story_7));
    }

    @NonNull
    @Override
    public StoryzViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_storyz_table,
                parent, false);
        return new StoryzViewHolder(context,view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryzViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        holder.storyzImg.setForeground(pixmapStoryzList.get(position));

        holder.storyzImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{//
                    listener.onStoryItemClick(position);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pixmapStoryzList.size();
    }

    public void notifyDataSetChanged(int adapterPosition) {
    }
}

class StoryzViewHolder extends RecyclerView.ViewHolder{

    ImageView storyzImg;
    Context context;
    private StoryzAdapter adapter;

    @SuppressLint("NotifyDataSetChanged")
    public StoryzViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        storyzImg = itemView.findViewById(R.id.storyzItem);
        itemView.findViewById(R.id.storyzItem).setOnClickListener(view -> {

        });
    }

    public StoryzViewHolder linkAdapter(StoryzAdapter adapter)
    {
        this.adapter = adapter;
        return this;
    }
}