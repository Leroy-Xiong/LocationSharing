package com.example.test2;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button buttonConfirm, buttonGetCurrentLoc;
    private GoogleMap mMap;
    private SearchView mapSearchView;
    private Address address;
    Marker marker;
    private TextView textViewDrag;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    String locationSearch;
    SupportMapFragment mapFragment;
    TextView buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        setContentView(R.layout.activity_search);

        mapSearchView = findViewById(R.id.mapSearch);
        buttonBack = findViewById(R.id.btn_back);
        buttonConfirm = findViewById(R.id.btn_confirm);
        buttonGetCurrentLoc = findViewById(R.id.btn_getCurrentLoc);
        textViewDrag = findViewById(R.id.textDrag);

        textViewDrag.setVisibility(View.GONE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get a handle to the fragment and register the callback.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                locationSearch = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if (locationSearch != null) {
                    Geocoder geocoder = new Geocoder(SearchActivity.this);

                    try {
                        addressList = geocoder.getFromLocationName(locationSearch, 1);
                    } catch (IOException e) {
                        Toast.makeText(SearchActivity.this, "Unable to connect to geocoding service, please try again later", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    if (addressList == null || addressList.isEmpty()) {
                        Toast.makeText(SearchActivity.this, "Failed to find a matching address!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onQueryTextSubmit: Toast");
                    } else {
                        address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.clear();
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(locationSearch));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }

                    // Show "Drag" text view
                    // 创建一个Alpha动画，从完全透明到完全不透明（即淡入效果）
                    ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(textViewDrag, "alpha", 0f, 1f);
                    fadeInAnimator.setDuration(500); // 毫秒
                    fadeInAnimator.start();

                    fadeInAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            textViewDrag.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationShareActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (marker != null) {
                    LatLng markerPosition = marker.getPosition();

                    Log.d(TAG, String.valueOf("Latitude: " + markerPosition.latitude + " Longitude: " + markerPosition.longitude));

                    Intent intent = new Intent(getApplicationContext(), LocationShareActivity.class);
                    intent.putExtra("address", markerPosition);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SearchActivity.this, "You need to chose a location first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonGetCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLastLocation();

                // Show "Drag" text view
                // 创建一个Alpha动画，从完全透明到完全不透明（即淡入效果）
                ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(textViewDrag, "alpha", 0f, 1f);
                fadeInAnimator.setDuration(500); // 毫秒
                fadeInAnimator.start();

                fadeInAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        textViewDrag.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }
        });

    }

    private void getLastLocation() {

        Log.d(TAG, "getLastLocation: test");
        
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            Log.d(TAG, "getLastLocation: test2");
            return;
        }
        Log.d(TAG, "getLastLocation: test3");
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "getLastLocation: test4");
                if (location != null) {
                    Log.d(TAG, "getLastLocation: test5");
                    mMap.clear();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(locationSearch));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
            }
        });


    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}