package com.example.test2;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocationShareActivity extends AppCompatActivity {

    Button buttonLogout, buttonSubmit, buttonBack, buttonToMap, buttonUseCurrentLoc;
    EditText editTextName, editTextLatitude, editTextLongitude;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    TextView buttonSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_share);

        buttonLogout = findViewById(R.id.btn_logout);
        buttonSubmit = findViewById(R.id.btn_submit);
//        buttonBack = findViewById(R.id.btn_back);
        buttonSearch = findViewById(R.id.btn_search);
        buttonToMap = findViewById(R.id.btn_toMap);
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

        LatLng address = getIntent().getParcelableExtra("address");
        if (address != null) {
            double latitude = address.latitude;
            double longitude = address.longitude;

            editTextLatitude.setText(String.format("%.6f", latitude)); // 格式化为6位小数
            editTextLongitude.setText(String.format("%.6f", longitude));

            // 现在您可以使用传递过来的latitude和longitude进行相关操作...
            Log.d(TAG, String.format("Received Latitude: %.6f, Longitude: %.6f", latitude, longitude));
        } else {
            Log.w(TAG, "Failed to receive address from previous activity.");
        }


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String latitudeStr = String.valueOf(editTextLatitude.getText());
                String longitudeStr = String.valueOf(editTextLongitude.getText());
                String nameStr = String.valueOf(editTextName.getText());

                if (TextUtils.isEmpty(nameStr)){
                    Toast.makeText(LocationShareActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(latitudeStr)){
                    Toast.makeText(LocationShareActivity.this, "Enter latitude", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(longitudeStr)){
                    Toast.makeText(LocationShareActivity.this, "Enter longitude", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                                        Toast.makeText(LocationShareActivity.this, "Your locationSearch was successfully submitted!", Toast.LENGTH_SHORT).show();
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

//        buttonBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), LocationChoiceActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        buttonToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                finish();
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