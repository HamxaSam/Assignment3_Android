package com.griffith.assignment3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
    @override
    onRequestPermissionResult

 */
public class MainActivity extends AppCompatActivity {
    private LocationManager lm;
    private LocationDBOpenHelper locationDBOpenHelper;
    private SQLiteDatabase sqLiteDatabase;

    private static final int REQUEST_LOCATION = 1;

    private TextView total_distance;
    private TextView current_speed;
    private TextView average_speed;
    private Chronometer chronometer;
    private Button button_start_stop;

    private LocationListener locationListener;

    private Boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationDBOpenHelper = new LocationDBOpenHelper(this, "gps_location_assgnmt3.db", null, 1);
        sqLiteDatabase = locationDBOpenHelper.getWritableDatabase();

        total_distance = (TextView)findViewById(R.id.total_distance);
        current_speed = (TextView)findViewById(R.id.current_speed);
        average_speed = (TextView)findViewById(R.id.average_speed);
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        button_start_stop = (Button)findViewById(R.id.button_stop_start);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        chronometer.stop();

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer cArg) {
                long t = SystemClock.elapsedRealtime() - cArg.getBase();
                cArg.setText(DateFormat.format("mm:ss", t));
            }
        });

        button_start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started){
                    locationDBOpenHelper.resetDB(sqLiteDatabase);
                    current_speed.setText(String.format("%.2f", 0.0f));
                    total_distance.setText(String.format("%.2f", 0.0f));
                    average_speed.setText(String.format("%.2f", 0.0f));
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    addLocationListener();
                    chronometer.start();
                    started = true;
                    button_start_stop.setText(R.string.button_stop_txt);
                }
                else {
                    chronometer.stop();
                    started = false;
                    button_start_stop.setText(R.string.button_start_txt);
                    if (locationListener != null) {
                        lm.removeUpdates(locationListener);
                        locationListener = null;
                    }
                    Intent intent = new Intent(MainActivity.this, Summary.class);
                    intent.putExtra("TOTALDISTANCE", String.format("%.2f", locationDBOpenHelper.getTotal_distance() / 1000));
                    intent.putExtra("AVGSPEED", String.format("%.2f", locationDBOpenHelper.getAvgSpeed(sqLiteDatabase) * 3.6f));
                    intent.putExtra("TOTALTIME", DateFormat.format("mm:ss", locationDBOpenHelper.getTotalTime(sqLiteDatabase) * 1000));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addLocationListener();
                }
            }
        }
    }

    private void addLocationListener() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }
        locationListener = new
                LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //Log.d("GPS", location.toString());
                        locationDBOpenHelper.addLocation(sqLiteDatabase, location);
                        current_speed.setText(String.format("%.2f", locationDBOpenHelper.getCurrentSpeed(sqLiteDatabase) * 3.6f));
                        total_distance.setText(String.format("%.2f", locationDBOpenHelper.getTotal_distance() / 1000));
                        average_speed.setText(String.format("%.2f", locationDBOpenHelper.getAvgSpeed(sqLiteDatabase) * 3.6f));
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
//                            if (LocationManager.GPS_PROVIDER.equals(provider)) {
//                                Log.d("OK", "BAD VALUES");
//                            }
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        if (LocationManager.GPS_PROVIDER.equals(provider)) {
                            try {
                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                                    return;
                                }
                                Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (l != null) {
                                    locationDBOpenHelper.addLocation(sqLiteDatabase, l);
                                    current_speed.setText(String.format("%.2f", locationDBOpenHelper.getCurrentSpeed(sqLiteDatabase) * 3.6f));
                                    total_distance.setText(String.format("%.2f", locationDBOpenHelper.getTotal_distance() / 1000));
                                    average_speed.setText(String.format("%.2f", locationDBOpenHelper.getAvgSpeed(sqLiteDatabase) * 3.6f));
                                }
                            }catch (Exception e) {
                            }
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                };

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5.0f, locationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 5.0f, locationListener);
    }
}
