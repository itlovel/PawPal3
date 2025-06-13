package com.example.pawpal3.mealplanner.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pawpal3.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpal3.mealplanner.adapters.CartItemAdapter;
import com.example.pawpal3.mealplanner.base.BaseActivity;
import com.example.pawpal3.mealplanner.models.CartItem;
import com.example.pawpal3.mealplanner.utils.CartManager;
import com.example.pawpal3.R;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity {

    private ImageButton btnBack;
    private TextView tvTitle;
    private RecyclerView recyclerCart;
    private Button btnCustomize;

    private CartItemAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private List<CartItem> selectedCartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        setupRecyclerView();
        loadCartItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure navigation bar shows the correct selection
        navigationView.setSelectedItemId(R.id.navigation_meal);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_title);
        recyclerCart = findViewById(R.id.recycler_cart);
        btnCustomize = findViewById(R.id.btn_customize);

        tvTitle.setText("Keranjang");

        btnBack.setOnClickListener(v -> finish());

        btnCustomize.setOnClickListener(v -> {
            if (selectedCartItems.isEmpty()) {
                Toast.makeText(this, "Please select at least one item", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass selected items to customized menu activity
            CartManager.getInstance().setSelectedCartItems(selectedCartItems);

            Intent intent = new Intent(CartActivity.this, CustomizedMenuActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new CartItemAdapter(this, cartItems);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerCart.setAdapter(adapter);

        adapter.setOnCartItemChangeListener(new CartItemAdapter.OnCartItemChangeListener() {
            @Override
            public void onQuantityChanged(CartItem cartItem, int position) {
                // Update cart manager
                CartManager.getInstance().updateItem(cartItem.getFoodItem(), cartItem.getQuantity());

                // Remove from selected items if quantity is zero
                if (cartItem.getQuantity() == 0) {
                    selectedCartItems.remove(cartItem);
                }
            }

            @Override
            public void onSelectionChanged(CartItem cartItem, int position, boolean isSelected) {
                if (isSelected) {
                    if (!selectedCartItems.contains(cartItem)) {
                        selectedCartItems.add(cartItem);
                    }
                } else {
                    selectedCartItems.remove(cartItem);
                }
            }
        });
    }

    private void loadCartItems() {
        cartItems = CartManager.getInstance().getCartItems();
        adapter.updateData(cartItems);

        // Reset selected items
        selectedCartItems.clear();
    }
}