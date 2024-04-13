package com.example.test2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LocationShareActivity extends AppCompatActivity {

    Button buttonLogout, buttonSubmit;
    TextInputEditText editTextLatitude, editTextLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_share);

        buttonLogout = findViewById(R.id.btn_logout);
        buttonSubmit = findViewById(R.id.btn_submit);
        editTextLatitude = findViewById(R.id.text_latitude);
        editTextLongitude = findViewById(R.id.text_longitude);



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

                String latitude, longitude;
                latitude = String.valueOf(editTextLatitude.getText());
                longitude = String.valueOf(editTextLongitude.getText());

                Log.d("MyTag", "Latitude: " + latitude + ", Longitude: " + longitude);
                if (latitude != null && longitude != null) {
                    Toast.makeText(getApplicationContext(), "Latitude: " + latitude + ", Longitude: " + longitude,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve latitude and longitude",
                            Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }
}