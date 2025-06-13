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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpal3.mealplanner.adapters.FoodItemAdapter;
import com.example.pawpal3.mealplanner.base.BaseActivity;
import com.example.pawpal3.mealplanner.models.CartItem;
import com.example.pawpal3.mealplanner.models.FoodItem;
import com.example.pawpal3.mealplanner.utils.CartManager;
import com.example.pawpal3.mealplanner.utils.FirebaseManager;
import com.example.pawpal3.R;

import java.util.ArrayList;
import java.util.List;

public class FoodSelectionActivity extends BaseActivity {

    private ImageButton btnBack;
    private EditText etSearch;
    private RecyclerView recyclerFoodItems;
    private Button btnNext;
    private Button btnBack2;
    private ProgressBar progressBar;

    private FoodItemAdapter adapter;
    private List<FoodItem> allFoodItems = new ArrayList<>();
    private List<FoodItem> filteredFoodItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_selection);

        initViews();
        setupRecyclerView();
        loadFoodItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure navigation bar shows the correct selection
        navigationView.setSelectedItemId(R.id.navigation_meal);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        etSearch = findViewById(R.id.et_search);
        recyclerFoodItems = findViewById(R.id.recycler_food_items);
        btnNext = findViewById(R.id.btn_next);
        btnBack2 = findViewById(R.id.btn_back2);
        progressBar = findViewById(R.id.progress_bar);

        btnBack.setOnClickListener(v -> finish());

        btnBack2.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            if (adapter.getSelectedItems().isEmpty()) {
                Toast.makeText(this, "Please select at least one food item", Toast.LENGTH_SHORT).show();
            } else {
                // Add selected items to cart
                for (FoodItem item : adapter.getSelectedItems()) {
                    CartManager.getInstance().addItem(item);
                }

                Intent intent = new Intent(FoodSelectionActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoodItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new FoodItemAdapter(this, filteredFoodItems);
        adapter.setSelectMode(true);
        recyclerFoodItems.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerFoodItems.setAdapter(adapter);

        adapter.setOnFoodItemClickListener(foodItem -> {
            // Toggle selection on item click
            if (adapter.getSelectedItems().contains(foodItem)) {
                adapter.getSelectedItems().remove(foodItem);
            } else {
                adapter.getSelectedItems().add(foodItem);
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void loadFoodItems() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseManager.getAllFoodItems(new FirebaseManager.OnFoodItemsLoadedListener() {
            @Override
            public void onFoodItemsLoaded(List<FoodItem> foodItems) {
                allFoodItems = foodItems;
                filteredFoodItems = new ArrayList<>(allFoodItems);
                adapter.updateData(filteredFoodItems);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFoodItemsLoadFailed(String errorMessage) {
                Toast.makeText(FoodSelectionActivity.this,
                        "Failed to load food items: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void filterFoodItems(String query) {
        filteredFoodItems.clear();

        if (query.isEmpty()) {
            filteredFoodItems.addAll(allFoodItems);
        } else {
            String lowerCaseQuery = query.toLowerCase();

            for (FoodItem item : allFoodItems) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery) ||
                        item.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredFoodItems.add(item);
                }
            }
        }

        adapter.updateData(filteredFoodItems);
    }
}