package com.example.league95.maplocationdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    //request permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            //check for permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Of course we need to check permission explicitly again.
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,
                            0, 0, locationListener);
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //We're going to write most of the code in onMapReady
        //Initiate location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Then the location listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Now everytime location changes, change the location on the map!
                LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                //before adding the new marker, clear the map
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(sydney).title("Markerrrr"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                //We can also do a reverse geo location
                //i.e get the address from coordinates
                //Locale is simply a format for the address.
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                //Then we create a list of addresses
                try {
                    //We only need one result.
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);
                    //check to see if we got something back
                    if (addressList != null && addressList.size() > 0) {
                        Address add = addressList.get(0);
                        String text = "";
                        if (add.getSubThoroughfare() != null) {
                            text += add.getSubThoroughfare() + ", ";
                        }
                        if (add.getThoroughfare() != null) {
                            text += add.getThoroughfare() + ", ";
                        }
                        if (add.getLocality() != null) {
                            text += add.getLocality() + ", ";
                        }
                        if (add.getCountryName() != null){
                            text += add.getCountryName() + ".";
                        }
                        Toast.makeText(MapsActivity.this, text, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    System.out.println("No such address!");
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            //just get the location without asking!
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER
                    , 0, 0, locationListener);
        } else {
            //We need to ask for permission other wise.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Then ask for permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                //We already have permission and just get the location..
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,
                        0, 0, locationListener);

                //Get the user's location as soon as the app is launched!
                Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                //Now use that location to define the location at the start
                LatLng sydney = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                //before adding the new marker, clear the map
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(sydney).title("last location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        }


    }
}
