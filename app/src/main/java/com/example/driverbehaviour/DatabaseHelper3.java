package com.example.driverbehaviour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper3 extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "gps_data.db";
    public static final String TABLE_NAME = "gps_table";
    public static final String COL = "ID";
    public static final String COL_1 = "time";
    public static final String COL_2 = "latitude";
    public static final String COL_3 = "longitude";
    public static final String COL_4 = "altitude";
    public static final String COL_5 = "speed";

    public DatabaseHelper3(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT ,time LONG ,latitude DOUBLE,longitude DOUBLE,altitude DOUBLE,speed FLOAT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(long time,double latitude,double longitude,double altitude,float speed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,time);
        contentValues.put(COL_2,latitude);
        contentValues.put(COL_3,longitude);
        contentValues.put(COL_4,altitude);
        contentValues.put(COL_5,speed);
        long result = db.insert(TABLE_NAME, null  ,contentValues);
        if(result == -1){
            //   System.out.println("FALSE ");
            return false;
        }

        else{
            //   System.out.println("TRUE ");
            return true;
        }

    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }


    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }

}
