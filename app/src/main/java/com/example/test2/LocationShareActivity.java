package com.example.test2;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocationShareActivity extends AppCompatActivity {

    Button buttonLogout, buttonSubmit;
    TextInputEditText editTextName, editTextLatitude, editTextLongitude;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_share);

        buttonLogout = findViewById(R.id.btn_logout);
        buttonSubmit = findViewById(R.id.btn_submit);
        editTextLatitude = findViewById(R.id.text_latitude);
        editTextLongitude = findViewById(R.id.text_longitude);
        editTextName = findViewById(R.id.your_name);

        mAuth = FirebaseAuth.getInstance();

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Confirm Logout");
                builder.setMessage("Are you sure you want to log out?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String latitudeStr = String.valueOf(editTextLatitude.getText());
                String longitudeStr = String.valueOf(editTextLongitude.getText());
                String nameStr = String.valueOf(editTextName.getText());

                // check whether the latitude and longitude are valid
                try {
                    double latitude = Double.parseDouble(latitudeStr);
                    double longitude = Double.parseDouble(longitudeStr);

                    // 检查经纬度范围
                    if (isValidLatitude(latitude) && isValidLongitude(longitude)) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("latitude", latitude);
                        user.put("longitude", longitude);
                        user.put("name", nameStr);

                        // save the latitude and longitude to Cloud Firebase
                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                        db.collection("users")
                                .document(userId) // 使用用户ID作为Document ID
                                .update(user) // 使用set方法将数据写入指定Document
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + userId);
                                        Toast.makeText(LocationShareActivity.this, "Your location was successfully submitted!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });

                        // jump to MapActivity
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LocationShareActivity.this,
                                "Invalid latitude or longitude. Please enter a value within the valid range.",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(LocationShareActivity.this,
                            "Invalid input. Please enter a valid numeric value for latitude and longitude.",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    // Determine if latitude and longitude are within reasonable limits
    private boolean isValidLatitude(double latitude) {
        return -90 <= latitude && latitude <= 90;
    }

    private boolean isValidLongitude(double longitude) {
        return -180 <= longitude && longitude <= 180;
    }
}