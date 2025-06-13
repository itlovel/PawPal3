package com.example.pawpal3.mealplanner.utils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pawpal3.mealplanner.models.CartItem;
import com.example.pawpal3.mealplanner.models.CustomMenu;
import com.example.pawpal3.mealplanner.models.FoodItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Get current user ID
    public static String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // Get recommended food items
    public static void getRecommendedFoodItems(final OnFoodItemsLoadedListener listener) {
        db.collection("food_items")
                .whereEqualTo("recommended", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<FoodItem> foodItems = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FoodItem item = document.toObject(FoodItem.class);
                            foodItems.add(item);
                        }
                        listener.onFoodItemsLoaded(foodItems);
                    } else {
                        Log.w(TAG, "Error getting recommended food items", task.getException());
                        listener.onFoodItemsLoadFailed(task.getException().getMessage());
                    }
                });
    }

    // Get all food items
    public static void getAllFoodItems(final OnFoodItemsLoadedListener listener) {
        db.collection("food_items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<FoodItem> foodItems = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FoodItem item = document.toObject(FoodItem.class);
                            foodItems.add(item);
                        }
                        listener.onFoodItemsLoaded(foodItems);
                    } else {
                        Log.w(TAG, "Error getting all food items", task.getException());
                        listener.onFoodItemsLoadFailed(task.getException().getMessage());
                    }
                });
    }

    // Get custom menu for specific date
    public static void getCustomMenuForDate(Date date, final OnCustomMenuLoadedListener listener) {
        String userId = getCurrentUserId();
        if (userId == null) {
            listener.onCustomMenuLoadFailed("User not logged in");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(date);

        db.collection("custom_menus")
                .whereEqualTo("userId", userId)
                .whereEqualTo("dateString", dateString)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            CustomMenu customMenu = document.toObject(CustomMenu.class);
                            listener.onCustomMenuLoaded(customMenu);
                        } else {
                            // No menu for this date
                            listener.onCustomMenuLoaded(new CustomMenu(null, userId, date));
                        }
                    } else {
                        Log.w(TAG, "Error getting custom menu", task.getException());
                        listener.onCustomMenuLoadFailed(task.getException().getMessage());
                    }
                });
    }

    // Save custom menu
    public static void saveCustomMenu(CustomMenu customMenu, final OnCustomMenuSavedListener listener) {
        String userId = getCurrentUserId();
        if (userId == null) {
            listener.onCustomMenuSaveFailed("User not logged in");
            return;
        }

        customMenu.setUserId(userId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(customMenu.getDate());

        Map<String, Object> menuData = new HashMap<>();
        menuData.put("userId", userId);
        menuData.put("dateString", dateString);
        menuData.put("date", customMenu.getDate());

        Map<String, Map<String, Object>> mealsByCategoryMap = new HashMap<>();
        for (Map.Entry<String, FoodItem> entry : customMenu.getMealsByCategory().entrySet()) {
            Map<String, Object> foodItemMap = new HashMap<>();
            FoodItem item = entry.getValue();
            foodItemMap.put("id", item.getId());
            foodItemMap.put("name", item.getName());
            foodItemMap.put("description", item.getDescription());
            foodItemMap.put("imageUrl", item.getImageUrl());
            foodItemMap.put("calories", item.getCalories());
            foodItemMap.put("category", item.getCategory());

            mealsByCategoryMap.put(entry.getKey(), foodItemMap);
        }

        menuData.put("mealsByCategory", mealsByCategoryMap);

        if (customMenu.getId() == null) {
            // Create new menu
            db.collection("custom_menus")
                    .add(menuData)
                    .addOnSuccessListener(documentReference -> {
                        customMenu.setId(documentReference.getId());
                        listener.onCustomMenuSaved(customMenu);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding custom menu", e);
                        listener.onCustomMenuSaveFailed(e.getMessage());
                    });
        } else {
            // Update existing menu
            db.collection("custom_menus")
                    .document(customMenu.getId())
                    .set(menuData)
                    .addOnSuccessListener(aVoid -> {
                        listener.onCustomMenuSaved(customMenu);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error updating custom menu", e);
                        listener.onCustomMenuSaveFailed(e.getMessage());
                    });
        }
    }

    // Add custom food item
    public static void addCustomFoodItem(FoodItem foodItem, final OnFoodItemSavedListener listener) {
        String userId = getCurrentUserId();
        if (userId == null) {
            listener.onFoodItemSaveFailed("User not logged in");
            return;
        }

        Map<String, Object> foodData = new HashMap<>();
        foodData.put("name", foodItem.getName());
        foodData.put("description", foodItem.getDescription());
        foodData.put("imageUrl", foodItem.getImageUrl());
        foodData.put("calories", foodItem.getCalories());
        foodData.put("category", foodItem.getCategory());
        foodData.put("createdBy", userId);
        foodData.put("custom", true);

        db.collection("food_items")
                .add(foodData)
                .addOnSuccessListener(documentReference -> {
                    foodItem.setId(documentReference.getId());
                    listener.onFoodItemSaved(foodItem);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding custom food item", e);
                    listener.onFoodItemSaveFailed(e.getMessage());
                });
    }

    // Interfaces for callbacks
    public interface OnFoodItemsLoadedListener {
        void onFoodItemsLoaded(List<FoodItem> foodItems);
        void onFoodItemsLoadFailed(String errorMessage);
    }

    public interface OnCustomMenuLoadedListener {
        void onCustomMenuLoaded(CustomMenu customMenu);
        void onCustomMenuLoadFailed(String errorMessage);
    }

    public interface OnCustomMenuSavedListener {
        void onCustomMenuSaved(CustomMenu customMenu);
        void onCustomMenuSaveFailed(String errorMessage);
    }

    public interface OnFoodItemSavedListener {
        void onFoodItemSaved(FoodItem foodItem);
        void onFoodItemSaveFailed(String errorMessage);
    }
}
