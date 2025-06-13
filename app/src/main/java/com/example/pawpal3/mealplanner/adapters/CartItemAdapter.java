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
import com.example.pawpal3.mealplanner.models.CartItem;
import com.example.pawpal3.R;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private List<CartItem> cartItems;
    private Context context;
    private OnCartItemChangeListener listener;

    public CartItemAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    public void setOnCartItemChangeListener(OnCartItemChangeListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.textName.setText(item.getFoodItem().getName());
        holder.textCalories.setText(item.getFoodItem().getCalories() + " kcal");
        holder.textQuantity.setText(String.valueOf(item.getQuantity()));
        holder.checkBox.setChecked(item.isSelected());

        // Load image with Glide
        Glide.with(context)
                .load(item.getFoodItem().getImageUrl())
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.error_food)
                .into(holder.imgFood);

        // Handle click events
        holder.btnIncrease.setOnClickListener(v -> {
            item.incrementQuantity();
            notifyItemChanged(position);
            if (listener != null) {
                listener.onQuantityChanged(item, position);
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                item.decrementQuantity();
                notifyItemChanged(position);
                if (listener != null) {
                    listener.onQuantityChanged(item, position);

                    // Remove item if quantity becomes zero
                    if (item.getQuantity() == 0) {
                        cartItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartItems.size());
                    }
                }
            }
        });

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (listener != null) {
                listener.onSelectionChanged(item, position, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateData(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView textName;
        TextView textCalories;
        TextView textQuantity;
        ImageButton btnIncrease;
        ImageButton btnDecrease;
        CheckBox checkBox;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.img_cart_food);
            textName = itemView.findViewById(R.id.text_cart_food_name);
            textCalories = itemView.findViewById(R.id.text_cart_calories);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            checkBox = itemView.findViewById(R.id.checkbox_cart_select);
        }
    }

    public interface OnCartItemChangeListener {
        void onQuantityChanged(CartItem cartItem, int position);
        void onSelectionChanged(CartItem cartItem, int position, boolean isSelected);
    }
}
