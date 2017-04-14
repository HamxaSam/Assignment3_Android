package com.griffith.assignment3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private LocationManager lm;
    private LocationDBOpenHelper locationDBOpenHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationDBOpenHelper = new LocationDBOpenHelper(this, "gps_location_assignment3.db", null, 1);
        sqLiteDatabase = locationDBOpenHelper.getWritableDatabase();

//        Intent intent = new Intent(this, ServiceGPS.class);
//        startService(intent);

        locationDBOpenHelper.resetDB(sqLiteDatabase);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        addLocationListener();
    }

    private void addLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMIT", "TA PAS LES PERMISSIONS");
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5.0f, new
                LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d("GPS", location.toString());
                        locationDBOpenHelper.addLocation(sqLiteDatabase, location);
                        ArrayList<CustomLocation> cl = locationDBOpenHelper.getAllLocations(sqLiteDatabase);
                        for (CustomLocation c : cl){
                            Log.d("GPS", c.toString());
                        }
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        if (provider == LocationManager.GPS_PROVIDER) {
                            Log.d("OK", "BAD VALUES");
                        }
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        if (provider == LocationManager.GPS_PROVIDER) {
                            try {
                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    Log.d("PERMIT", "TA TOUJOURS PAS LES PERMISSIONS");
                                    return;
                                }
                                Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (l != null) {
                                    Log.d("LAT", "????Latitude: " + l.getLatitude());
                                    Log.d("LONG", "????Longitude: " + l.getLongitude());
                                }
                            }catch (Exception e) {
                                Log.e("OULA", "ERROR", e);
                            }
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle
                            extras) {
                    }
                });
    }
}
