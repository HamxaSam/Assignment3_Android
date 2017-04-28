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
import java.util.List;

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
            "distance FLOAT, " +
            "kmdone BOOLEAN" +
            ")";
    private static final String drop_table = "drop table " + TABLE;

    private float total_distance = 0.0f;

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
        float tmp_distance = total_distance;
        ContentValues cv = new ContentValues();
        cv.put("moment", System.currentTimeMillis());
        cv.put("latitude", location.getLatitude());
        cv.put("longitude", location.getLongitude());
        CustomLocation cl = getLastLocation(db);
        if (cl != null){
            float distance = cl.distanceTo(new CustomLocation(location));
            cv.put("distance", distance);
            total_distance += distance;
        }
        if ((((int)total_distance % 1000 < (int)tmp_distance % 1000))) {
            cv.put("kmdone", true);
        } else {
            cv.put("kmdone", false);
        }
        db.insert(TABLE, null, cv);
        //Log.v(TAG, "Location added");
    }

    public CustomLocation getLastLocation(SQLiteDatabase db){
        Cursor cursor = db.query(false, TABLE, null, null,  null, null, null, "id DESC", "1");
        CustomLocation l = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            l = new CustomLocation(new Timestamp(cursor.getLong(1)), cursor.getDouble(2), cursor.getDouble(3), cursor.getFloat(4));
        }
        cursor.close();
        return l;
    }

    public CustomLocation getFirstLocation(SQLiteDatabase db){
        Cursor cursor = db.query(false, TABLE, null, null,  null, null, null, "id ASC", "1");
        CustomLocation l = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            l = new CustomLocation(new Timestamp(cursor.getLong(1)), cursor.getDouble(2), cursor.getDouble(3), cursor.getFloat(4));
        }
        cursor.close();
        return l;
    }
    public ArrayList<CustomLocation> getTwoLastLocations(SQLiteDatabase db){
        Cursor cursor = db.query(false, TABLE, null, null,  null, null, null, "id DESC", "2");
        ArrayList<CustomLocation> l = new ArrayList<>();
        if (cursor.getCount() == 2) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++){
                l.add(new CustomLocation(new Timestamp(cursor.getLong(1)), cursor.getDouble(2), cursor.getDouble(3), cursor.getFloat(4)));
                cursor.moveToNext();
            }
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
                cursor.moveToNext();
            }
        }
        cursor.close();
        return l;
    }

//    public float getTotalDistance(SQLiteDatabase db){
//        String[] columns = { "SUM(distance)" };
//        Cursor cursor = db.query(false, TABLE, columns, null,  null, null, null, null, null);
//        float distance = 0f;
//        if (cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            distance = cursor.getFloat(0);
//        }
//        cursor.close();
//        return distance;
//    }

    public float getCurrentSpeed(SQLiteDatabase db){
        ArrayList<CustomLocation> twoLastLocation = getTwoLastLocations(db);
        float speed = 0.0f;
        if (twoLastLocation.size() == 2){
            Long timebtw = twoLastLocation.get(0).getTimeBetween(twoLastLocation.get(1));
            float dist = twoLastLocation.get(0).distanceTo(twoLastLocation.get(1));
            try{
                speed = dist / timebtw;
            } catch (Exception e){
                Log.e(TAG, "ERROR", e);
            }
        }
        return speed;
    }

    public float getAvgSpeed(SQLiteDatabase db){
        CustomLocation first = getFirstLocation(db);
        CustomLocation last = getLastLocation(db);
        Long timebtw = first.getTimeBetween(last);
        float dist = total_distance;
        float speed = 0.0f;
        try{
            speed = dist / timebtw;
        } catch (Exception e){
            Log.e(TAG, "ERROR", e);
        }
        return speed;
    }

    public ArrayList<CustomLocation> getAllCheckpoints(SQLiteDatabase db){
        String[] wherearg = new String[]{ "true" };
        Cursor cursor = db.query(false, TABLE, null, "kmdone = ?", wherearg , null, null, "id DESC", null);
        ArrayList<CustomLocation> l = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++){
                l.add(new CustomLocation(new Timestamp(cursor.getLong(1)), cursor.getDouble(2), cursor.getDouble(3), cursor.getFloat(4)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return l;
    }

    public float getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(float total_distance) {
        this.total_distance = total_distance;
    }
}
