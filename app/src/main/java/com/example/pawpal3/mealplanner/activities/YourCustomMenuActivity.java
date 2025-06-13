package com.example.pawpal3.mealplanner.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pawpal3.R;

import android.app.DatePickerDialog;
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

import com.example.pawpal3.mealplanner.adapters.CustomMenuAdapter;
import com.example.pawpal3.mealplanner.base.BaseActivity;
import com.example.pawpal3.mealplanner.models.CustomMenu;
import com.example.pawpal3.mealplanner.models.FoodItem;
import com.example.pawpal3.mealplanner.utils.FirebaseManager;
import com.example.pawpal3.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class YourCustomMenuActivity extends BaseActivity {

    private ImageButton btnBack;
    private ImageButton btnCart;
    private ImageButton btnCustom;
    private ImageButton btnAddMenu;
    private TextView tvDayOfWeek;
    private ImageButton btnCalendar;
    private ImageButton btnEdit;
    private RecyclerView recyclerCustomMenu;
    private Button btnAdd;
    private Button btnRemove;

    private CustomMenuAdapter adapter;
    private CustomMenu customMenu;
    private Date selectedDate = new Date();
    private boolean isEditMode = false;
    private List<String> selectedCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_custom_menu);

        initViews();
        setupRecyclerView();
        loadCustomMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomMenu(); // Reload menu when returning from other activities
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnCart = findViewById(R.id.btn_cart);
        btnCustom = findViewById(R.id.btn_custom);
        btnAddMenu = findViewById(R.id.btn_add_menu);
        tvDayOfWeek = findViewById(R.id.tv_day_of_week);
        btnCalendar = findViewById(R.id.btn_calendar);
        btnEdit = findViewById(R.id.btn_edit);
        recyclerCustomMenu = findViewById(R.id.recycler_custom_menu);
        btnAdd = findViewById(R.id.btn_add);
        btnRemove = findViewById(R.id.btn_remove);

        // Set day of week
        updateDayOfWeek();

        // Hide edit buttons initially
        btnAdd.setVisibility(View.GONE);
        btnRemove.setVisibility(View.GONE);

        // Setup click listeners
        btnBack.setOnClickListener(v -> finish());

        btnCalendar.setOnClickListener(v -> showDatePicker());

        btnEdit.setOnClickListener(v -> toggleEditMode());

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(YourCustomMenuActivity.this, CartActivity.class);
            startActivity(intent);
        });

        btnCustom.setOnClickListener(v -> {
            Intent intent = new Intent(YourCustomMenuActivity.this, FoodSelectionActivity.class);
            startActivity(intent);
        });

        btnAddMenu.setOnClickListener(v -> {
            Intent intent = new Intent(YourCustomMenuActivity.this, MakeYourOwnMenuActivity.class);
            startActivity(intent);
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(YourCustomMenuActivity.this, FoodSelectionActivity.class);
            startActivity(intent);
        });

        btnRemove.setOnClickListener(v -> removeSelectedItems());
    }

    private void setupRecyclerView() {
        adapter = new CustomMenuAdapter(this, new CustomMenu());
        recyclerCustomMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerCustomMenu.setAdapter(adapter);

        adapter.setOnCustomMenuItemListener(new CustomMenuAdapter.OnCustomMenuItemListener() {
            @Override
            public void onMenuItemSelected(String category, FoodItem foodItem, int position) {
                if (isEditMode) {
                    if (!selectedCategories.contains(category)) {
                        selectedCategories.add(category);
                    } else {
                        selectedCategories.remove(category);
                    }
                }
            }
        });
    }

    private void loadCustomMenu() {
        FirebaseManager.getCustomMenuForDate(selectedDate, new FirebaseManager.OnCustomMenuLoadedListener() {
            @Override
            public void onCustomMenuLoaded(CustomMenu menu) {
                customMenu = menu;
                adapter.updateData(customMenu);

                // Clear selected categories when loading new menu
                selectedCategories.clear();
            }

            @Override
            public void onCustomMenuLoadFailed(String errorMessage) {
                Toast.makeText(YourCustomMenuActivity.this,
                        "Failed to load custom menu: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });
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
                    updateDayOfWeek();
                    loadCustomMenu();
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void updateDayOfWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("id", "ID"));
        String dayOfWeek = sdf.format(selectedDate);
        tvDayOfWeek.setText(dayOfWeek);
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        adapter.setEditMode(isEditMode);

        if (isEditMode) {
            btnAdd.setVisibility(View.VISIBLE);
            btnRemove.setVisibility(View.VISIBLE);
        } else {
            btnAdd.setVisibility(View.GONE);
            btnRemove.setVisibility(View.GONE);
            selectedCategories.clear();
        }
    }

    private void removeSelectedItems() {
        if (selectedCategories.isEmpty()) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String category : selectedCategories) {
            customMenu.removeMeal(category);
        }

        // Save updated menu
        FirebaseManager.saveCustomMenu(customMenu, new FirebaseManager.OnCustomMenuSavedListener() {
            @Override
            public void onCustomMenuSaved(CustomMenu menu) {
                adapter.updateData(customMenu);
                selectedCategories.clear();

                // Show success message
                Toast.makeText(YourCustomMenuActivity.this, "Menu Successfully Removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCustomMenuSaveFailed(String errorMessage) {
                Toast.makeText(YourCustomMenuActivity.this,
                        "Failed to update menu: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}