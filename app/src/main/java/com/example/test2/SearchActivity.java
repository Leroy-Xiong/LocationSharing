package com.example.test2;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button buttonBack, buttonConfirm;
    private GoogleMap mMap;
    private SearchView mapSearchView;
    private Address address;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        setContentView(R.layout.activity_search);

        mapSearchView = findViewById(R.id.mapSearch);
        buttonBack = findViewById(R.id.btn_back);
        buttonConfirm = findViewById(R.id.btn_confirm);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null) {
                    Geocoder geocoder = new Geocoder(SearchActivity.this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
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
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
                    }
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
                if (address != null) {
                    LatLng markerPosition = marker.getPosition();

                    Log.d(TAG, String.valueOf("Hahahah Latitude: " + markerPosition.latitude + " Longitude: " + markerPosition.longitude));

                    Intent intent = new Intent(getApplicationContext(), LocationShareActivity.class);
                    intent.putExtra("address", markerPosition);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SearchActivity.this, "You need to search first", Toast.LENGTH_SHORT).show();
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
}