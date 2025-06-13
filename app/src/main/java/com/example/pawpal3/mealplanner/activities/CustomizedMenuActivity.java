package com.example.pawpal3.mealplanner.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpal3.mealplanner.adapters.CustomizedMenuAdapter;
import com.example.pawpal3.mealplanner.base.BaseActivity;
import com.example.pawpal3.mealplanner.models.CartItem;
import com.example.pawpal3.mealplanner.models.CustomMenu;
import com.example.pawpal3.mealplanner.models.FoodItem;
import com.example.pawpal3.mealplanner.utils.CartManager;
import com.example.pawpal3.mealplanner.utils.FirebaseManager;
import com.example.pawpal3.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomizedMenuActivity extends BaseActivity {

    private ImageButton btnBack;
    private TextView tvTitle;
    private EditText etDate;
    private RecyclerView recyclerCustomizedMenu;
    private Button btnFinish;

    private CustomizedMenuAdapter adapter;
    private Date selectedDate = new Date();
    private Map<String, FoodItem> selectedMeals = new HashMap<>();
    private List<Map.Entry<String, FoodItem>> menuEntries = new ArrayList<>();

    // Available meal categories
    private static final String[] MEAL_CATEGORIES = {
            "Sarapan", "Snack", "Makan siang", "Snack", "Makan malam"
    };

    // Track next available category index
    private int nextCategoryIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customized_menu);

        initViews();
        setupRecyclerView();
        loadSelectedCartItems();
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
        etDate = findViewById(R.id.et_date);
        recyclerCustomizedMenu = findViewById(R.id.recycler_customized_menu);
        btnFinish = findViewById(R.id.btn_finish);

        tvTitle.setText("Customized menu");

        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        etDate.setText(sdf.format(selectedDate));

        btnBack.setOnClickListener(v -> finish());

        etDate.setOnClickListener(v -> showDatePicker());

        btnFinish.setOnClickListener(v -> {
            saveCustomMenu();
            finish(); // Return to CustomMenu activity
        });
    }

    private void setupRecyclerView() {
        adapter = new CustomizedMenuAdapter(this, menuEntries);
        recyclerCustomizedMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerCustomizedMenu.setAdapter(adapter);

        adapter.setOnCustomizedMenuItemListener((category, position) -> {
            // Remove the item from the map and list
            selectedMeals.remove(category);
            menuEntries.remove(position);
            adapter.notifyDataSetChanged();
        });
    }

    private void loadSelectedCartItems() {
        List<CartItem> selectedCartItems = CartManager.getInstance().getSelectedCartItems();

        for (CartItem cartItem : selectedCartItems) {
            for (int i = 0; i < cartItem.getQuantity(); i++) {
                if (nextCategoryIndex < MEAL_CATEGORIES.length) {
                    String category = MEAL_CATEGORIES[nextCategoryIndex];
                    selectedMeals.put(category, cartItem.getFoodItem());
                    nextCategoryIndex++;
                }
            }
        }

        updateMenuEntries();
    }

    private void updateMenuEntries() {
        menuEntries.clear();
        for (Map.Entry<String, FoodItem> entry : selectedMeals.entrySet()) {
            menuEntries.add(entry);
        }
        adapter.updateData(menuEntries);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        c.setTime(selectedDate);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year1, monthOfYear, dayOfMonth);
                    selectedDate = newDate.getTime();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                    etDate.setText(sdf.format(selectedDate));
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void saveCustomMenu() {
        if (selectedMeals.isEmpty()) {
            Toast.makeText(this, "Please add at least one meal to your menu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a custom menu object
        CustomMenu customMenu = new CustomMenu();
        customMenu.setDate(selectedDate);

        for (Map.Entry<String, FoodItem> entry : selectedMeals.entrySet()) {
            customMenu.addMeal(entry.getKey(), entry.getValue());
        }

        // Save to Firebase
        FirebaseManager.saveCustomMenu(customMenu, new FirebaseManager.OnCustomMenuSavedListener() {
            @Override
            public void onCustomMenuSaved(CustomMenu menu) {
                Toast.makeText(CustomizedMenuActivity.this, "Menu saved successfully!", Toast.LENGTH_SHORT).show();

                // Clear cart selected items
                CartManager.getInstance().clearSelectedItems();

                // Return to previous screen
                finish();
            }

            @Override
            public void onCustomMenuSaveFailed(String errorMessage) {
                Toast.makeText(CustomizedMenuActivity.this,
                        "Failed to save menu: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}