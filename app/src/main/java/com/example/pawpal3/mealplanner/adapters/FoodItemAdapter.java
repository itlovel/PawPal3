package com.example.pawpal3.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pawpal3.mealplanner.models.FoodItem;
import com.example.pawpal3.R;

import java.util.ArrayList;
import java.util.List;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder> {

    private List<FoodItem> foodItems;
    private Context context;
    private boolean selectMode = false;
    private List<FoodItem> selectedItems = new ArrayList<>();
    private OnFoodItemClickListener listener;

    public FoodItemAdapter(Context context, List<FoodItem> foodItems) {
        this.context = context;
        this.foodItems = foodItems;
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        notifyDataSetChanged();
    }

    public List<FoodItem> getSelectedItems() {
        return selectedItems;
    }

    public void setOnFoodItemClickListener(OnFoodItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
        FoodItem item = foodItems.get(position);

        holder.textName.setText(item.getName());
        holder.textCalories.setText(item.getCalories() + " kcal");

        // Load image with Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.error_food)
                .into(holder.imgFood);

        // Show checkbox in select mode
        if (selectMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(selectedItems.contains(item));
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

        // Handle click events
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodItemClick(item);
            }
        });

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedItems.contains(item)) {
                    selectedItems.add(item);
                }
            } else {
                selectedItems.remove(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public void updateData(List<FoodItem> newFoodItems) {
        this.foodItems = newFoodItems;
        notifyDataSetChanged();
    }

    public class FoodItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView textName;
        TextView textCalories;
        CheckBox checkBox;

        public FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.img_food);
            textName = itemView.findViewById(R.id.text_food_name);
            textCalories = itemView.findViewById(R.id.text_calories);
            checkBox = itemView.findViewById(R.id.checkbox_select);
        }
    }

    public interface OnFoodItemClickListener {
        void onFoodItemClick(FoodItem foodItem);
    }
}
