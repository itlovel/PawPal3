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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.pawpal3.R;
import com.example.pawpal3.mealplanner.base.BaseActivity;

public class MealPlannerActivity extends BaseActivity {

    private CardView cardFoodRecommendation;
    private CardView cardCustomMenu;
    private Button btnChoose;
    private TextView tvTitle;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_planner);

        initViews();
        setupListeners();
    }

    private void initViews() {
        cardFoodRecommendation = findViewById(R.id.card_food_recommendation);
        cardCustomMenu = findViewById(R.id.card_custom_menu);
        btnChoose = findViewById(R.id.btn_choose);
        tvTitle = findViewById(R.id.tv_title);
        tvDescription = findViewById(R.id.tv_description);

        // Set title and description
        tvTitle.setText("Pet Meal Planner");
        tvDescription.setText("Pet Meal Planner merupakan fitur aplikasi Paw Pal yang dapat membantu pengguna memantau makanan hewan peliharaannya melalui maintenance pola makan");
    }

    private void setupListeners() {
        cardFoodRecommendation.setOnClickListener(v -> {
            Intent intent = new Intent(MealPlannerActivity.this, FoodRecommendationActivity.class);
            startActivity(intent);
        });

        cardCustomMenu.setOnClickListener(v -> {
            Intent intent = new Intent(MealPlannerActivity.this, YourCustomMenuActivity.class);
            startActivity(intent);
        });

        btnChoose.setOnClickListener(v -> {
            Intent intent = new Intent(MealPlannerActivity.this, YourCustomMenuActivity.class);
            startActivity(intent);
        });
    }
}