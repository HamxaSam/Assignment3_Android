package com.griffith.assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by 42900 on 14/04/2017 for Assignment3.
 */

public class LocationDBOpenHelper extends SQLiteOpenHelper {
    private static String TAG = "LOC_OPENHELPER";
    private static String TABLE = "location";

    private static final String create_table = "create table if not exists " + TABLE + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "moment TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "latitude DOUBLE, " +
            "longitude DOUBLE, " +
            "distance FLOAT" +
            ")";
    private static final String drop_table = "drop table " + TABLE;

    public LocationDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetDB(db);
    }

    public void resetDB(SQLiteDatabase db){
        db.execSQL(drop_table);
        db.execSQL(create_table);
    }

    public void addLocation(SQLiteDatabase db, Location location){
        ContentValues cv = new ContentValues();
        cv.put("moment", System.currentTimeMillis());
        cv.put("latitude", location.getLatitude());
        cv.put("longitude", location.getLongitude());
        CustomLocation cl = getLastLocation(db);
        if (cl != null){
            float distance = cl.distanceTo(new CustomLocation(location));
            cv.put("distance", distance);
        }
        db.insert(TABLE, null, cv);
        Log.v(TAG, "Location added");
    }

    public CustomLocation getLastLocation(SQLiteDatabase db){
        Cursor cursor = db.query(false, TABLE, null, null,  null, null, null, "id DESC", "1");
        CustomLocation l = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            l = new CustomLocation(cursor.getDouble(2), cursor.getDouble(3));
        }
        cursor.close();
        return l;
    }

    //for debug
    public ArrayList<CustomLocation> getAllLocations(SQLiteDatabase db){
        Cursor cursor = db.query(false, TABLE, null, null,  null, null, null, "id DESC", null);
        ArrayList<CustomLocation> l = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++){
                l.add(new CustomLocation(new Timestamp(cursor.getLong(1)), cursor.getDouble(2), cursor.getDouble(3), cursor.getFloat(4)));
                Log.d(TAG, cursor.getInt(0) + "=> " + cursor.getLong(1));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return l;
    }
}
