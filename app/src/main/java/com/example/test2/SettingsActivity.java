package com.example.test2;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    Button buttonBack, buttonChangeLoc;
    Switch aSwitch, switchSatellite;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean satellite_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        buttonBack = findViewById(R.id.btn_back);
        buttonChangeLoc = findViewById(R.id.btn_changeLoc);
        aSwitch = findViewById(R.id.sw_show_location);
        switchSatellite = findViewById(R.id.sw_satellite_map);

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

        Intent intent = getIntent();
        boolean satellite = intent.getBooleanExtra("satellite", false);
        switchSatellite.setChecked(satellite);
        switchSatellite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                satellite_map = isChecked;
                Log.d(TAG, "onCheckedChanged: " + satellite_map);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean show = true;
                if (!aSwitch.isChecked()) {
                    show = false;
                }

                Map<String, Object> user = new HashMap<>();
                user.put("show", show);
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

                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("satellite_map", satellite_map);
                Log.d(TAG, "onClick: " + satellite_map);
                startActivity(intent);
                finish();

            }
        });

        buttonChangeLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationShareActivity.class);
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