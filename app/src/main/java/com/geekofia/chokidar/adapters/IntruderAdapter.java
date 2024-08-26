package com.geekofia.chokidar.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geekofia.chokidar.R;
import com.geekofia.chokidar.activities.FullScreenImageActivity;
import com.geekofia.chokidar.models.Intruder;

import java.util.List;

public class IntruderAdapter extends RecyclerView.Adapter<IntruderAdapter.IntruderViewHolder> {

    private final List<Intruder> intruderList;
    private final Context context;

    public IntruderAdapter(Context context, List<Intruder> intruderList) {
        this.context = context;
        this.intruderList = intruderList;
    }

    @NonNull
    @Override
    public IntruderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_intruder, parent, false);
        return new IntruderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntruderViewHolder holder, int position) {
        Intruder intruder = intruderList.get(position);
        holder.alertTypeTextView.setText(intruder.getAlertType());
        Glide.with(context)
                .load(intruder.getImageFilePath())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullScreenImageActivity.class);
            intent.putExtra(FullScreenImageActivity.EXTRA_IMAGE_PATH, intruder.getImageFilePath());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return intruderList.size();
    }

    static class IntruderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView alertTypeTextView;

        public IntruderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewIntruder);
            alertTypeTextView = itemView.findViewById(R.id.textViewAlertType);
        }
    }
}
