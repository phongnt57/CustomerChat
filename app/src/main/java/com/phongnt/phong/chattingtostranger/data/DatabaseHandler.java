package com.phongnt.phong.chattingtostranger.data;

/**
 * Created by phong on 9/27/2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_LOGIN = "login";
    private static final String KEY_ID = "id";
    private static final String KEY_PASS = "password";
    private static final String KEY_USERNAME = "username";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USERNAME + " TEXT,"
                + KEY_PASS + " TEXT" + ")";


        db.execSQL(CREATE_LOGIN_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);


        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String token) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, name); // Name
        values.put(KEY_PASS, token); // token
        //values.put(KEY_UID, uid); // Email
        //values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }
    public boolean resetLogin()
    {
        SQLiteDatabase da = this.getWritableDatabase();
        int doneDelete = 0;
        doneDelete = da.delete(TABLE_LOGIN, null , null);
        Log.w("delete timetable", Integer.toString(doneDelete));
        return doneDelete > 0;

    }
    public User getUser()
    {

        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN ;
        String token = "";
        String username;
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            username = cursor.getString(1);
            token = cursor.getString(2);
            user.setUser(username);
            user.setPass(token);

        }
        cursor.close();
        db.close();
        return user;

    }

    public int getLoginCount()
    {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;

    }

}
