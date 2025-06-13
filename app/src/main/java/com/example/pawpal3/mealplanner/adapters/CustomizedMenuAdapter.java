package com.example.pawpal3.mealplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pawpal3.mealplanner.models.FoodItem;
import com.example.pawpal3.R;

import java.util.List;
import java.util.Map;

public class CustomizedMenuAdapter extends RecyclerView.Adapter<CustomizedMenuAdapter.CustomizedMenuViewHolder> {

    private List<Map.Entry<String, FoodItem>> menuItems;
    private Context context;
    private OnCustomizedMenuItemListener listener;

    public CustomizedMenuAdapter(Context context, List<Map.Entry<String, FoodItem>> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    public void setOnCustomizedMenuItemListener(OnCustomizedMenuItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomizedMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customized_menu, parent, false);
        return new CustomizedMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomizedMenuViewHolder holder, int position) {
        Map.Entry<String, FoodItem> entry = menuItems.get(position);
        String category = entry.getKey();
        FoodItem item = entry.getValue();

        holder.textCategory.setText(category);
        holder.textName.setText(item.getName());
        holder.textCalories.setText(item.getCalories() + " kcal");

        // Load image with Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.error_food)
                .into(holder.imgFood);

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(category, position);
                menuItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, menuItems.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public void updateData(List<Map.Entry<String, FoodItem>> newMenuItems) {
        this.menuItems = newMenuItems;
        notifyDataSetChanged();
    }

    public class CustomizedMenuViewHolder extends RecyclerView.ViewHolder {
        TextView textCategory;
        ImageView imgFood;
        TextView textName;
        TextView textCalories;
        ImageButton btnRemove;

        public CustomizedMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.text_customized_category);
            imgFood = itemView.findViewById(R.id.img_customized_food);
            textName = itemView.findViewById(R.id.text_customized_food_name);
            textCalories = itemView.findViewById(R.id.text_customized_calories);
            btnRemove = itemView.findViewById(R.id.btn_remove_item);
        }
    }

    public interface OnCustomizedMenuItemListener {
        void onRemoveItem(String category, int position);
    }
}
