package com.griffith.assignment3;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

public class Summary extends AppCompatActivity {
    private LinkedList<Float> speed_list = new LinkedList<Float>();
    private GraphBar gb;
    private LocationDBOpenHelper locationDBOpenHelper;
    private SQLiteDatabase sqLiteDatabase;

    private TextView total_distance;
    private TextView average_speed;
    private TextView total_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        locationDBOpenHelper = new LocationDBOpenHelper(this, "gps_location_assgnmt3.db", null, 1);
        sqLiteDatabase = locationDBOpenHelper.getWritableDatabase();

        total_distance = (TextView)findViewById(R.id.total_distance_sum);
        average_speed = (TextView)findViewById(R.id.average_speed_sum);
        total_time = (TextView)findViewById(R.id.total_time_sum);


        ArrayList<CustomLocation> list = locationDBOpenHelper.getAllCheckpoints(sqLiteDatabase);
        for (int i = 0 ; i < list.size(); ++i) {
            if (i > 0){
                speed_list.add((float)(list.get(i).getTimeBetween(list.get(i -1 ))));
            }
        }
        gb = (GraphBar)findViewById(R.id.graph_bar);

        String total_distance_v;
        String average_speed_v;
        String total_time_v;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                total_distance_v = null;
                average_speed_v = null;
                total_time_v = null;
            } else {
                total_distance_v = extras.getString("TOTALDISTANCE");
                average_speed_v = extras.getString("AVGSPEED");
                total_time_v = extras.getString("TOTALTIME");
            }
        } else {
            total_distance_v= (String) savedInstanceState.getSerializable("TOTALDISTANCE");
            average_speed_v= (String) savedInstanceState.getSerializable("AVGSPEED");
            total_time_v= (String) savedInstanceState.getSerializable("TOTALTIME");
        }

        total_distance.setText(total_distance_v);
        average_speed.setText(average_speed_v);
        total_time.setText(total_time_v);
        gb.post(new Runnable() {
            @Override
            public void run() {
                gb.setValues(speed_list);
            }
        });
    }
}
