package com.vishalcilpher.nishant.cipherliquidation;

/**
 * Created by nishant on 18/5/18.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DBController extends SQLiteOpenHelper {
 private static final String LOGCAT = null;

 public DBController(Context applicationcontext) {
     super(applicationcontext, "Vishal.db", null, 1);  // creating DATABASE
     Log.d(LOGCAT, "Created");
 }

 @Override
 public void onCreate(SQLiteDatabase database) {
     String query;
     query = "CREATE TABLE IF NOT EXISTS retail_prod (BARCODE VARCHAR(50) PRIMARY KEY,OFFERS TEXT)";
     database.execSQL(query);

     Log.e("@@@@@@@@",query);
 }


 @Override
 public void onUpgrade(SQLiteDatabase database, int version_old,
                       int current_version) {
     String query;
     query = "DROP TABLE IF EXISTS retail_prod";
     database.execSQL(query);
     onCreate(database);
 }



    public ArrayList<String> gethualreadyscanned(String Barcode)
    {
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select OFFERS from retail_prod where BARCODE ='"+Barcode+"'",null);
        if (cur.moveToFirst()) {
            do {


                arraylist.add(cur.getString(cur.getColumnIndex("OFFERS")));


            } while (cur.moveToNext());

        }
        return arraylist;

    }







}
