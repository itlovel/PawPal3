package com.example.pawpal3.mealplanner.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pawpal3.MainActivity;
import com.example.pawpal3.R;
import com.example.pawpal3.mealplanner.activities.MealPlannerActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        FrameLayout container = findViewById(R.id.activity_container);
        getLayoutInflater().inflate(layoutResID, container, true);

        // Set the current selected navigation item
        updateNavigationBarSelection();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // Check if we're already on the selected screen to avoid unnecessary navigation
        if (isCurrentNavigationItem(itemId)) {
            return true;
        }

        if (itemId == R.id.navigation_home) {
            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        } else if (itemId == R.id.navigation_adopt) {
            // Start PetsActivity
            // startActivity(new Intent(this, PetsActivity.class));
            return true;
        } else if (itemId == R.id.navigation_katalog) {
            // If this is the meal planner section
            startActivity(new Intent(this, MealPlannerActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        } else if (itemId == R.id.navigation_grooming) {
            // Start FavoriteActivity
            // startActivity(new Intent(this, FavoriteActivity.class));
            return true;
        } else if (itemId == R.id.navigation_meal) {
            // Start ProfileActivity
            // startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }

        return false;
    }

    /**
     * Checks if the selected navigation item corresponds to the current activity
     */
    private boolean isCurrentNavigationItem(int itemId) {
        // Use Class comparison instead of instanceof to avoid casting issues
        Class<?> currentClass = this.getClass();

        if (currentClass.equals(MainActivity.class) && itemId == R.id.navigation_home) {
            return true;
        } else if (currentClass.equals(MealPlannerActivity.class) && itemId == R.id.navigation_meal) {
            return true;
        }

        return false;
    }

    /**
     * Updates the selected item in the bottom navigation bar based on current activity
     */
    private void updateNavigationBarSelection() {
        // Use Class comparison instead of instanceof to avoid casting issues
        Class<?> currentClass = this.getClass();

        if (currentClass.equals(MainActivity.class)) {
            navigationView.setSelectedItemId(R.id.navigation_home);
        } else if (currentClass.equals(MealPlannerActivity.class)) {
            navigationView.setSelectedItemId(R.id.navigation_meal);
        }
        // For other activities that are part of the meal planner flow, set calendar as selected
        else if (currentClass.getPackage().getName().contains("mealplanner")) {
            navigationView.setSelectedItemId(R.id.navigation_meal);
        }
    }

    /**
     * Method to hide the bottom navigation if needed in certain screens
     */
    protected void hideBottomNavigation() {
        if (navigationView != null) {
            navigationView.setVisibility(View.GONE);
        }
    }

    /**
     * Method to show the bottom navigation
     */
    protected void showBottomNavigation() {
        if (navigationView != null) {
            navigationView.setVisibility(View.VISIBLE);
        }
    }
}