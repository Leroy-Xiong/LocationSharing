package com.example.test2;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    Button buttonBack, buttonSave;
    EditText editTextLatitude, editTextLongitude;
    Switch aSwitch;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        buttonBack = findViewById(R.id.btn_back);
        buttonSave = findViewById(R.id.btn_save);
        aSwitch = findViewById(R.id.sw_show_location);
        editTextLatitude = findViewById(R.id.text_latitude);
        editTextLongitude = findViewById(R.id.text_longitude);

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data =  document.getData();
                        aSwitch.setChecked((Boolean) data.get("show"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean show = true;
                if (!aSwitch.isChecked()) {
                    show = false;
                }

                String latitudeStr = String.valueOf(editTextLatitude.getText());
                String longitudeStr = String.valueOf(editTextLongitude.getText());

                // check whether the latitude and longitude are valid
                try {
                    double latitude = Double.parseDouble(latitudeStr);
                    double longitude = Double.parseDouble(longitudeStr);

                    // 检查经纬度范围
                    if (isValidLatitude(latitude) && isValidLongitude(longitude)) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("latitude", latitude);
                        user.put("longitude", longitude);
                        user.put("show", show);

                        // save the latitude and longitude to Cloud Firebase
                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                        db.collection("users")
                                .document(userId) // 使用用户ID作为Document ID
                                .update(user) // 使用set方法将数据写入指定Document
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + userId);
                                        Toast.makeText(SettingsActivity.this, "Settings saved!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SettingsActivity.this,
                                "Invalid latitude or longitude. Please enter a value within the valid range.",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(SettingsActivity.this,
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