package com.example.pawpal3.mealplanner.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomMenu {
    private String id;
    private String userId;
    private Date date;
    private Map<String, FoodItem> mealsByCategory; // e.g., "Breakfast" -> FoodItem

    public CustomMenu() {
        mealsByCategory = new HashMap<>();
    }

    public CustomMenu(String id, String userId, Date date) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.mealsByCategory = new HashMap<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String, FoodItem> getMealsByCategory() {
        return mealsByCategory;
    }

    public void setMealsByCategory(Map<String, FoodItem> mealsByCategory) {
        this.mealsByCategory = mealsByCategory;
    }

    public void addMeal(String category, FoodItem foodItem) {
        mealsByCategory.put(category, foodItem);
    }

    public void removeMeal(String category) {
        mealsByCategory.remove(category);
    }

    public List<Map.Entry<String, FoodItem>> getMealsAsList() {
        return new ArrayList<>(mealsByCategory.entrySet());
    }
}