package com.shopma.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PointsRetraitActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 200;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_retrait);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);


        addPickupMarkers();


        LatLng morocco = new LatLng(31.7917, -7.0926);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(morocco, 5.5f));


        checkLocationPermission();
    }

    private void addPickupMarkers() {
        // --- Casablanca ---
        LatLng casablanca = new LatLng(33.5731, -7.5898);
        mMap.addMarker(new MarkerOptions()
                .position(casablanca)
                .title("ShopMa - Casablanca Centre")
                .snippet("Boulevard Mohammed V, Casablanca 20000")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        // --- Rabat ---
        LatLng rabat = new LatLng(34.0209, -6.8416);
        mMap.addMarker(new MarkerOptions()
                .position(rabat)
                .title("ShopMa - Rabat Agdal")
                .snippet("Avenue de France, Agdal, Rabat 10080")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        // --- Marrakech ---
        LatLng marrakech = new LatLng(31.6295, -7.9811);
        mMap.addMarker(new MarkerOptions()
                .position(marrakech)
                .title("ShopMa - Marrakech Guéliz")
                .snippet("Avenue Mohammed VI, Guéliz, Marrakech 40000")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        // --- Fès ---
        LatLng fes = new LatLng(34.0331, -5.0003);
        mMap.addMarker(new MarkerOptions()
                .position(fes)
                .title("ShopMa - Fès Ville Nouvelle")
                .snippet("Avenue Hassan II, Fès 30000")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        // --- Tanger ---
        LatLng tanger = new LatLng(35.7595, -5.8340);
        mMap.addMarker(new MarkerOptions()
                .position(tanger)
                .title("ShopMa - Tanger Médina")
                .snippet("Place du Grand Socco, Tanger 90000")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        // --- Agadir ---
        LatLng agadir = new LatLng(30.4278, -9.5981);
        mMap.addMarker(new MarkerOptions()
                .position(agadir)
                .title("ShopMa - Agadir Centre")
                .snippet("Boulevard Hassan II, Agadir 80000")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null &&
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, R.string.location_permission_denied,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
