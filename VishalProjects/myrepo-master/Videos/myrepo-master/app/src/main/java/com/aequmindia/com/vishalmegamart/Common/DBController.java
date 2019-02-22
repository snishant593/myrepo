package com.aequmindia.com.vishalmegamart.Common;

/**
 * Created by nishant on 18/5/18.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.aequmindia.com.vishalmegamart.mvvm.model.Datamodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DBController extends SQLiteOpenHelper {
 private static final String LOGCAT = null;

 public DBController(Context applicationcontext) {
     super(applicationcontext, "Vishal.db", null, 1);  // creating DATABASE
     Log.d(LOGCAT, "Created");
 }

 @Override
 public void onCreate(SQLiteDatabase database) {
     String query;
     query = "CREATE TABLE IF NOT EXISTS retail_prod (Store_Nm TEXT,Prod_Desc TEXT,Invoice_No INTEGER,HU_No VARCHAR(50) PRIMARY KEY,HU_Status TEXT,Final_Status TEXT)";
     database.execSQL(query);
     query = "CREATE TABLE IF NOT EXISTS retail_hu_excess (Store_Nm TEXT,HU_No TEXT PRIMARY KEY,HU_Status TEXT)";
     database.execSQL(query);
     query = "CREATE TABLE IF NOT EXISTS retail_gate (GATE_EXIT_NO TEXT PRIMARY KEY, KM_READING TEXT,SEAL_NO_1 TEXT,IS_BROKEN_1 TEXT,SEAL_NO_2 TEXT,IS_BROKEN_2 TEXT,DATE TEXT ,TIME TEXT)";
     database.execSQL(query);
     query =  "CREATE TABLE IF NOT EXISTS retail_filename (File_Nm TEXT PRIMARY KEY )";
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

    public ArrayList<Datamodel>getalldatainvoice() {
        ArrayList<Datamodel> invoicenolist = new ArrayList<Datamodel>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("select DISTINCT Invoice_No ,Store_Nm from retail_prod", null);
            if (cursor.moveToFirst()) {
                do {
                    Datamodel datamodel=new Datamodel();
                    datamodel.setInvoiceno(cursor.getString(cursor.getColumnIndex("Invoice_No")));
                   datamodel.setStorename(cursor.getString(cursor.getColumnIndex("Store_Nm")));

                    invoicenolist.add(datamodel);
                } while (cursor.moveToNext());

            }


        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }

        return invoicenolist;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String Phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        /*String[] params = new String[1];
        params[0] = Phone + "%";*/
        String getQty=null;
        String Query = ("select HU_No from retail_prod where HU_No='"+Phone+"' AND HU_Status = 'short'");
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            Log.e("InDatabase","InDatabase");
            return false;
        }
            cursor.close();
        Log.e("Notindatabase","Notindatabase");
            return true;
    }


    public ArrayList<String> getScanData(){
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT BARCODE FROM retail_physical_scanning",null);
        if (cur.moveToFirst()) {
            do {

                arraylist.add(cur.getString(cur.getColumnIndex("BARCODE")));

            } while (cur.moveToNext());
        }

        return arraylist;

    }





    public void updateExistinghustatus(String HuNo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


            contentValues.put("HU_Status","Recieved");
            db.update("retail_prod", contentValues, "HU_No = ? ", new String[]{String.valueOf(HuNo)});

    }

    public void Hustatusinvoice(String invoiceNo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put("HU_Status","short");
        db.update("retail_prod", contentValues, "Invoice_No= ? ", new String[]{String.valueOf(invoiceNo)});

    }

    public void Hustatusinvoiceremove(String invoiceNo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put("HU_Status","");
        db.update("retail_prod", contentValues, "Invoice_No= ? ", new String[]{String.valueOf(invoiceNo)});

    }
    public void Insertgateentry(String gateentry,String Kmreading,String SEAL1,String BROKEN1, String SEAL2, String BROKEN2)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("GATE_EXIT_NO", gateentry);
        contentValues.put("KM_READING ",Kmreading);
         contentValues.put("SEAL_NO_1",SEAL1);
        contentValues.put("IS_BROKEN_1", BROKEN1);
        contentValues.put("SEAL_NO_2", SEAL2);
        contentValues.put("IS_BROKEN_2", BROKEN2);
        contentValues.put("DATE",date());
        contentValues.put("TIME",Time());
        db.insert("retail_gate", null, contentValues);

        Log.e("############","gateinserted");

    }





    public void newhustatus(String HuNo,String Storename)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Hu_No", HuNo);
        contentValues.put("Store_Nm",Storename);
        contentValues.put("Hu_Status", "Recieved");
        db.insert("retail_hu_excess", null, contentValues);

        Log.e("############","NewDataInserted");

    }


    public ArrayList<String> getRecievedhuNo()
    {
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT HU_No FROM retail_prod where HU_Status = 'Recieved'",null);
        if (cur.moveToFirst()) {
            do {

                arraylist.add(cur.getString(cur.getColumnIndex("HU_No")));

            } while (cur.moveToNext());
        }

        return arraylist;

    }


    public ArrayList<huscan> gethualreadyscanned(String Huno)
    {
        ArrayList<huscan> arraylist = new ArrayList<huscan>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select HU_Status from(SELECT HU_Status FROM retail_prod where HU_No='"+Huno+"' AND HU_Status = 'Recieved' union SELECT HU_Status FROM retail_hu_excess where HU_No='"+Huno+"' AND HU_Status = 'Recieved')",null);
        if (cur.moveToFirst()) {
            do {

                huscan husc = new huscan();

                husc.setHuscan(cur.getString(cur.getColumnIndex("HU_Status")));


                 arraylist.add(husc);


            } while (cur.moveToNext());
        }

        return arraylist;

    }



    public ArrayList<String> getExcessData(){
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT HU_No FROM retail_hu_excess",null);
        if (cur.moveToFirst()) {
            do {

                arraylist.add(cur.getString(cur.getColumnIndex("HU_No")));

            } while (cur.moveToNext());
        }

        return arraylist;

    }

    public ArrayList<String> getShortData(){
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT HU_No FROM retail_prod where HU_Status = 'short'",null);
        if (cur.moveToFirst()) {
            do {

                arraylist.add(cur.getString(cur.getColumnIndex("HU_No")));

            } while (cur.moveToNext());
        }

        return arraylist;

    }

    public ArrayList<String> getShortconitnueData(){
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT HU_No FROM retail_prod where HU_Status = 'Recieved'",null);
        if (cur.moveToFirst()) {
            do {

                arraylist.add(cur.getString(cur.getColumnIndex("HU_No")));

            } while (cur.moveToNext());
        }

        return arraylist;

    }

    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM retail_prod where HU_Status = 'Recieved'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getExcessReceivesCount() {
        String countQuery = "SELECT  * FROM retail_hu_excess where HU_Status = 'Recieved'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    public ArrayList<String> getconitnueDataexcesstable(){
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT HU_No FROM retail_hu_excess where HU_Status = 'Recieved'",null);
        if (cur.moveToFirst()) {
            do {

                arraylist.add(cur.getString(cur.getColumnIndex("HU_No")));

            } while (cur.moveToNext());
        }

        return arraylist;

    }




    public Cursor raw() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT Store_Nm,rtrim(Hu_No,'@') As Hu_No FROM retail_prod where HU_Status = 'Recieved'",null);

        return res;
    }

    public Cursor Reciveduser() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select Hu_No from(SELECT rtrim(Hu_No,'@') As Hu_No FROM retail_hu_excess union SELECT rtrim(Hu_No,'@') As Hu_No FROM retail_prod where HU_Status = 'Recieved')",null);

        return res;
    }

    public Cursor Recivedusercipher() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT  Hu_No FROM retail_prod where HU_Status = 'Recieved' union SELECT  Hu_No FROM retail_hu_excess where HU_Status = 'Recieved' ",null);

        return res;
    }


    public Cursor second() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT Store_Nm,rtrim(Hu_No,'@') As Hu_No FROM retail_hu_excess",null);
        return res;
    }

    public Cursor shortdata() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT Store_Nm,rtrim(Hu_No,'@') As Hu_No,Invoice_No FROM retail_prod where HU_Status = 'short'",null);
        return res;
    }

    public Cursor gatedata() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * from retail_gate",null);
        return res;
    }


    public Cursor mGetTotalSave()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mHuNo = db.rawQuery("SELECT HU_No FROM retail_prod",null);
        return mHuNo;
    }

    public Cursor mGetInvoiceNo()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mInvoiceNo = db.rawQuery("SELECT distinct Invoice_No FROM retail_prod",null);
        return mInvoiceNo;
    }


    public void deleteFirstRow()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("retail_prod" , null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            String rowId = cursor.getString(cursor.getColumnIndex("HU_No"));

            db.delete("retail_prod" , "HU_No = ? ",  new String[]{rowId});
            Log.e("############",rowId);
        }
        db.close();
    }


    public Cursor mGetTotalHucount(String InvoiceNo)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mTotalTriId = db.rawQuery("SELECT COUNT(*)HU_No FROM retail_prod where Invoice_No ='"+InvoiceNo+"'",null);
        return mTotalTriId;
    }



    public String date() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Log.e("Date", formattedDate);
        return formattedDate;


    }



    public String Time() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Log.e("Time", formattedDate);
        return formattedDate;


    }


    public void insertfile(String filename) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("File_Nm", filename);

        db.insert("retail_filename", null, contentValues);

        Log.e("############","filedatainserted" + "" + filename);

    }

    public ArrayList<String> getfilename() {
        ArrayList<String> arraylist = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT File_Nm FROM retail_filename",null);
        if (cur.moveToFirst()) {
            do {

                arraylist.add(cur.getString(cur.getColumnIndex("File_Nm")));

            } while (cur.moveToNext());
        }

        return arraylist;
    }
}
