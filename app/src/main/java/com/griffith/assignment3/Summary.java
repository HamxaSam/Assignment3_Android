package com.griffith.assignment3;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.LinkedList;

public class Summary extends AppCompatActivity {
    private LinkedList<Float> test_list = new LinkedList<Float>();
    private GraphBar gb;
    private LocationDBOpenHelper locationDBOpenHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        locationDBOpenHelper = new LocationDBOpenHelper(this, "gps_location_assgnmt3.db", null, 1);
        sqLiteDatabase = locationDBOpenHelper.getWritableDatabase();

        ArrayList<CustomLocation> list = locationDBOpenHelper.getAllCheckpoints(sqLiteDatabase);
        for (CustomLocation c : list)
            System.out.println(c);
        gb = (GraphBar)findViewById(R.id.graph_bar);


        test_list.add(7*60f);
        test_list.add(6*60f);
        test_list.add(9*60f);
        test_list.add(4*60f);
        test_list.add(7*60f);

        gb.post(new Runnable() {
            @Override
            public void run() {
                gb.setValues(test_list);
            }
        });
    }
}
