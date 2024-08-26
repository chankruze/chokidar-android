package com.geekofia.chokidar.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geekofia.chokidar.R;
import com.geekofia.chokidar.models.SafetyFeatureCardItem;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SafetyFeatureCardAdapter extends RecyclerView.Adapter<SafetyFeatureCardAdapter.CardViewHolder> {

    private final Context context;
    private final List<SafetyFeatureCardItem> safetyFeatureCardItemList;

    public SafetyFeatureCardAdapter(Context context, List<SafetyFeatureCardItem> safetyFeatureCardItemList) {
        this.context = context;
        this.safetyFeatureCardItemList = safetyFeatureCardItemList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.safety_feature_card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        SafetyFeatureCardItem safetyFeatureCardItem = safetyFeatureCardItemList.get(position);
        holder.cardTitle.setText(safetyFeatureCardItem.getTitle());
        holder.cardDescription.setText(safetyFeatureCardItem.getDescription());
        holder.cardIcon.setImageResource(safetyFeatureCardItem.getImageResId());

        holder.cardView.setOnClickListener(v -> {
            if (safetyFeatureCardItem.getActivityClass() != null) {
                Intent intent = new Intent(context, safetyFeatureCardItem.getActivityClass());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, safetyFeatureCardItem.getTitle() + " has no action implemented", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return safetyFeatureCardItemList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        ImageView cardIcon;
        TextView cardTitle;
        TextView cardDescription;

        CardViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            cardIcon = itemView.findViewById(R.id.card_icon);
            cardTitle = itemView.findViewById(R.id.card_title);
            cardDescription = itemView.findViewById(R.id.card_description);
        }
    }
}
