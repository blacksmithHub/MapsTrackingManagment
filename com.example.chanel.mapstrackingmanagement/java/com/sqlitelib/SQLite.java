package com.sqlitelib;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;
import android.util.Log;
import com.example.chanel.mapstrackingmanagement.BuildConfig;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SQLite {
    static String SQL = BuildConfig.FLAVOR;

    public static void Fitlicense() {
        Calendar thatDay = Calendar.getInstance();
        thatDay.set(5, 5);
        thatDay.set(2, 7);
        thatDay.set(1, 2015);
        thatDay.set(11, 0);
        thatDay.set(12, 0);
        thatDay.set(13, 0);
        thatDay.set(14, 0);
        Calendar today = Calendar.getInstance();
        today.set(11, 0);
        today.set(12, 0);
        today.set(13, 0);
        today.set(14, 0);
        if ((thatDay.getTimeInMillis() - today.getTimeInMillis()) / 86400000 < 0) {
            Process.killProcess(Process.myPid());
            System.exit(0);
        }
    }

    public static void FITCreateTable(String DatabaseName, Activity Act, String TableName, String fields) {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        String formattedDate = new SimpleDateFormat("dd-MMM-yyyy").format(c.getTime());
        SQLiteDatabase db = new DataBaseHelper(Act, DatabaseName, 2).getWritableDatabase();
        SQL = "create table " + TableName + "(" + fields + ");";
        try {
            Log.d("SQL", SQL);
            db.execSQL(SQL);
        } catch (Exception e) {
        }
    }

    public static void FITInsert(String DatabaseName, Activity Act, String TableName, String column, String Value) {
        SQLiteDatabase db = new DataBaseHelper(Act, DatabaseName, 2).getWritableDatabase();
        SQL = "Insert into " + TableName + "(" + column + ") VALUES (" + Value + ");";
        try {
            Log.d("SQL", SQL);
            db.execSQL(SQL);
        } catch (Exception e) {
        }
    }

    public static void FITDropTable(String DatabaseName, Activity Act, String TableName) {
        SQLiteDatabase db = new DataBaseHelper(Act, DatabaseName, 2).getWritableDatabase();
        SQL = "DROP TABLE " + TableName + ";";
        try {
            Log.d("SQL", SQL);
            db.execSQL(SQL);
        } catch (Exception e) {
        }
    }

    public static void FITDeleteTable(String DatabaseName, Activity Act, String TableName, String condition) {
        SQLiteDatabase db = new DataBaseHelper(Act, DatabaseName, 2).getWritableDatabase();
        if (condition.equals(BuildConfig.FLAVOR)) {
            SQL = "DELETE FROM " + TableName + ";";
        } else {
            SQL = "DELETE FROM " + TableName + " where=" + condition + ";";
        }
        try {
            Log.d("SQL", SQL);
            db.execSQL(SQL);
        } catch (Exception e) {
        }
    }
}
