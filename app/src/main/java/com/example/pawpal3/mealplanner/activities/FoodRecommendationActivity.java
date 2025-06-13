package com.example.pawpal3.mealplanner.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pawpal3.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpal3.mealplanner.adapters.RecommendationAdapter;
import com.example.pawpal3.mealplanner.base.BaseActivity;
import com.example.pawpal3.mealplanner.models.FoodItem;
import com.example.pawpal3.mealplanner.utils.FirebaseManager;
import com.example.pawpal3.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodRecommendationActivity extends BaseActivity {

    private ImageButton btnBack;
    private TextView tvDayOfWeek;
    private RecyclerView recyclerRecommendations;
    private ProgressBar progressBar;

    private RecommendationAdapter adapter;
    private List<FoodItem> recommendedFoodItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_recommendation);

        initViews();
        setupRecyclerView();
        loadRecommendedFoodItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure navigation bar shows the correct selection
        navigationView.setSelectedItemId(R.id.navigation_meal);
    }


    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvDayOfWeek = findViewById(R.id.tv_day_of_week);
        recyclerRecommendations = findViewById(R.id.recycler_recommendations);
        progressBar = findViewById(R.id.progress_bar);

        // Set day of week
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("id", "ID"));
        String dayOfWeek = sdf.format(calendar.getTime());
        tvDayOfWeek.setText(dayOfWeek);

        // Setup back button
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new RecommendationAdapter(this);
        recyclerRecommendations.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecommendations.setAdapter(adapter);
    }

    private void loadRecommendedFoodItems() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseManager.getRecommendedFoodItems(new FirebaseManager.OnFoodItemsLoadedListener() {
            @Override
            public void onFoodItemsLoaded(List<FoodItem> foodItems) {
                recommendedFoodItems = foodItems;
                processRecommendedItems();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFoodItemsLoadFailed(String errorMessage) {
                Toast.makeText(FoodRecommendationActivity.this,
                        "Failed to load recommendations: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void processRecommendedItems() {
        // Group items by category
        Map<String, List<FoodItem>> itemsByCategory = new HashMap<>();

        // Define the categories in order
        List<String> categories = new ArrayList<>();
        categories.add("Sarapan");
        categories.add("Snack");
        categories.add("Makan siang");
        categories.add("Snack");
        categories.add("Makan malam");

        // Initialize the map with empty lists
        for (String category : categories) {
            itemsByCategory.put(category, new ArrayList<>());
        }

        // Group items by category
        for (FoodItem item : recommendedFoodItems) {
            String category = item.getCategory();
            if (itemsByCategory.containsKey(category)) {
                itemsByCategory.get(category).add(item);
            }
        }

        // Set the data to the adapter
        adapter.setCategories(categories);
        adapter.setItemsByCategory(itemsByCategory);
    }
}