package com.example.pawpal3.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pawpal3.mealplanner.models.CustomMenu;
import com.example.pawpal3.mealplanner.models.FoodItem;
import com.example.pawpal3.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomMenuAdapter extends RecyclerView.Adapter<CustomMenuAdapter.CustomMenuViewHolder> {

    private List<Map.Entry<String, FoodItem>> menuItems;
    private Context context;
    private boolean editMode = false;
    private OnCustomMenuItemListener listener;

    public CustomMenuAdapter(Context context, CustomMenu customMenu) {
        this.context = context;
        this.menuItems = customMenu.getMealsAsList();
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        notifyDataSetChanged();
    }

    public void setOnCustomMenuItemListener(OnCustomMenuItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_custom_menu, parent, false);
        return new CustomMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomMenuViewHolder holder, int position) {
        Map.Entry<String, FoodItem> entry = menuItems.get(position);
        String category = entry.getKey();
        FoodItem item = entry.getValue();

        holder.textCategory.setText(category);
        holder.textName.setText(item.getName());
        holder.textDescription.setText(item.getDescription());
        holder.textCalories.setText(item.getCalories() + " kcal");

        // Load image with Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.error_food)
                .into(holder.imgFood);

        // Show checkbox in edit mode
        if (editMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null && isChecked) {
                listener.onMenuItemSelected(category, item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public void updateData(CustomMenu customMenu) {
        this.menuItems = customMenu.getMealsAsList();
        notifyDataSetChanged();
    }

    public class CustomMenuViewHolder extends RecyclerView.ViewHolder {
        TextView textCategory;
        ImageView imgFood;
        TextView textName;
        TextView textDescription;
        TextView textCalories;
        CheckBox checkBox;

        public CustomMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.text_menu_category);
            imgFood = itemView.findViewById(R.id.img_menu_food);
            textName = itemView.findViewById(R.id.text_menu_food_name);
            textDescription = itemView.findViewById(R.id.text_menu_description);
            textCalories = itemView.findViewById(R.id.text_menu_calories);
            checkBox = itemView.findViewById(R.id.checkbox_menu_select);
        }
    }

    public interface OnCustomMenuItemListener {
        void onMenuItemSelected(String category, FoodItem foodItem, int position);
    }
}
