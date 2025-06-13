package com.example.pawpal3.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pawpal3.mealplanner.models.FoodItem;
import com.example.pawpal3.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> {

    private Context context;
    private List<String> categories = new ArrayList<>();
    private Map<String, List<FoodItem>> itemsByCategory = new HashMap<>();

    public RecommendationAdapter(Context context) {
        this.context = context;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setItemsByCategory(Map<String, List<FoodItem>> itemsByCategory) {
        this.itemsByCategory = itemsByCategory;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommendation, parent, false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        String category = categories.get(position);
        List<FoodItem> items = itemsByCategory.get(category);

        holder.textCategory.setText(category);

        if (items != null && !items.isEmpty()) {
            FoodItem item = items.get(0); // Get first item for the category

            holder.textName.setText(item.getName());
            holder.textDescription.setText(item.getDescription());
            holder.textCalories.setText(item.getCalories() + " kcal");

            // Load image with Glide
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.error_food)
                    .into(holder.imgFood);
        } else {
            // No items for this category
            holder.textName.setText("No recommendation available");
            holder.textDescription.setText("");
            holder.textCalories.setText("0 kcal");
            holder.imgFood.setImageResource(R.drawable.placeholder_food);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class RecommendationViewHolder extends RecyclerView.ViewHolder {
        TextView textCategory;
        ImageView imgFood;
        TextView textName;
        TextView textDescription;
        TextView textCalories;

        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.text_recommendation_category);
            imgFood = itemView.findViewById(R.id.img_recommendation_food);
            textName = itemView.findViewById(R.id.text_recommendation_food_name);
            textDescription = itemView.findViewById(R.id.text_recommendation_description);
            textCalories = itemView.findViewById(R.id.text_recommendation_calories);
        }
    }
}