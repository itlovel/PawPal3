package com.example.pawpal3.mealplanner.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pawpal3.mealplanner.base.BaseActivity;
import com.example.pawpal3.mealplanner.models.FoodItem;
import com.example.pawpal3.mealplanner.utils.FirebaseManager;
import com.example.pawpal3.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class MakeYourOwnMenuActivity extends BaseActivity {

    private ImageButton btnBack;
    private TextView tvTitle;
    private EditText etName;
    private EditText etCalories;
    private Spinner spinnerCategory;
    private EditText etDescription;
    private ImageView imgFood;
    private View layoutUpload;
    private Button btnConfirm;
    private ProgressBar progressBar;

    private Uri imageUri;
    private String[] categories = {"Sarapan", "Snack", "Makan Siang", "Makan Malam"};

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_your_own_menu);

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imgFood.setImageURI(imageUri);
                        imgFood.setVisibility(View.VISIBLE);
                    }
                }
        );

        initViews();
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
        etName = findViewById(R.id.et_food_name);
        etCalories = findViewById(R.id.et_calories);
        spinnerCategory = findViewById(R.id.spinner_category);
        etDescription = findViewById(R.id.et_food_description);
        imgFood = findViewById(R.id.img_food);
        layoutUpload = findViewById(R.id.layout_upload);
        btnConfirm = findViewById(R.id.btn_confirm);
        progressBar = findViewById(R.id.progress_bar);

        tvTitle.setText("Make Your Own Menu");

        // Setup spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        layoutUpload.setOnClickListener(v -> openImagePicker());

        btnConfirm.setOnClickListener(v -> {
            if (validateInputs()) {
                uploadFoodWithImage();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private boolean validateInputs() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String caloriesStr = etCalories.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            return false;
        }

        if (description.isEmpty()) {
            etDescription.setError("Description is required");
            return false;
        }

        if (caloriesStr.isEmpty()) {
            etCalories.setError("Calories is required");
            return false;
        }

        try {
            Integer.parseInt(caloriesStr);
        } catch (NumberFormatException e) {
            etCalories.setError("Invalid calorie value");
            return false;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void uploadFoodWithImage() {
        progressBar.setVisibility(View.VISIBLE);
        btnConfirm.setEnabled(false);

        // Upload image to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imageRef = storageRef.child("food_images/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Create food item and save to Firestore
                        createFoodItem(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnConfirm.setEnabled(true);
                    Toast.makeText(MakeYourOwnMenuActivity.this,
                            "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createFoodItem(String imageUrl) {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        int calories = Integer.parseInt(etCalories.getText().toString().trim());
        String category = spinnerCategory.getSelectedItem().toString();

        FoodItem foodItem = new FoodItem(null, name, description, imageUrl, calories, category);

        FirebaseManager.addCustomFoodItem(foodItem, new FirebaseManager.OnFoodItemSavedListener() {
            @Override
            public void onFoodItemSaved(FoodItem item) {
                progressBar.setVisibility(View.GONE);
                showSuccessDialog();
            }

            @Override
            public void onFoodItemSaveFailed(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                btnConfirm.setEnabled(true);
                Toast.makeText(MakeYourOwnMenuActivity.this,
                        "Failed to create menu item: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog() {
        Dialog successDialog = new Dialog(this);
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setContentView(R.layout.dialog_success);
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successDialog.setCancelable(false);

        TextView tvMessage = successDialog.findViewById(R.id.tv_success_message);
        tvMessage.setText("Your Menu Successfully Added");

        successDialog.show();

        // Automatically dismiss after a delay and finish activity
        new android.os.Handler().postDelayed(() -> {
            successDialog.dismiss();
            finish();
        }, 2000);
    }
}