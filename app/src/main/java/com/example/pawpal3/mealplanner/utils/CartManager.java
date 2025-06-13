package com.example.pawpal3.mealplanner.utils;

import com.example.pawpal3.mealplanner.models.CartItem;
import com.example.pawpal3.mealplanner.models.FoodItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;

    private Map<String, CartItem> cartItemsMap;
    private List<CartItem> selectedCartItems;

    private CartManager() {
        cartItemsMap = new HashMap<>();
        selectedCartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(FoodItem foodItem) {
        String itemId = foodItem.getId();
        if (cartItemsMap.containsKey(itemId)) {
            // Item already in cart, increment quantity
            CartItem existingItem = cartItemsMap.get(itemId);
            existingItem.incrementQuantity();
        } else {
            // Add new item to cart with quantity 1
            CartItem newItem = new CartItem(foodItem, 1);
            cartItemsMap.put(itemId, newItem);
        }
    }

    public void updateItem(FoodItem foodItem, int quantity) {
        String itemId = foodItem.getId();
        if (quantity <= 0) {
            // Remove item if quantity is zero or negative
            cartItemsMap.remove(itemId);
        } else {
            // Update quantity
            if (cartItemsMap.containsKey(itemId)) {
                CartItem existingItem = cartItemsMap.get(itemId);
                existingItem.setQuantity(quantity);
            } else {
                CartItem newItem = new CartItem(foodItem, quantity);
                cartItemsMap.put(itemId, newItem);
            }
        }
    }

    public void removeItem(String itemId) {
        cartItemsMap.remove(itemId);
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItemsMap.values());
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : cartItemsMap.values()) {
            count += item.getQuantity();
        }
        return count;
    }

    public void setSelectedCartItems(List<CartItem> selectedItems) {
        this.selectedCartItems = new ArrayList<>(selectedItems);
    }

    public List<CartItem> getSelectedCartItems() {
        return selectedCartItems;
    }

    public void clearSelectedItems() {
        selectedCartItems.clear();
    }

    public void clearCart() {
        cartItemsMap.clear();
        selectedCartItems.clear();
    }
}
