package com.aequmindia.com.vishalmegamart.mvvm.View.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aequmindia.com.vishalmegamart.Common.Config;
import com.aequmindia.com.vishalmegamart.Common.DBController;
import com.aequmindia.com.vishalmegamart.Common.huscan;
import com.aequmindia.com.vishalmegamart.R;
import com.aequmindia.com.vishalmegamart.mvvm.model.Datamodel;
import com.cipherlab.barcode.ReaderManager;
import com.cipherlab.barcodebase.ReaderCallback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class continueoldmobile extends AppCompatActivity {


    String  userTypedString;
    AutoCompleteTextView showScanResult;
    ArrayList<huscan> gunlist;
    DBController dbController = new DBController(this);
    String storename,filename;
    ArrayList<Datamodel> InvoiceList;
    private Button Save, AddhunoButton, ExportCsv,Exit,Clear;
    private EditText Addhuno;
    Toolbar toolbar;
    TextView hucount,textcount;
    int count;
    ArrayList<String> checkrecieved = new ArrayList<>();
    ArrayList<String> filedata = new ArrayList<>();
    ArrayList<String> checkexcess = new ArrayList<>();
    private TextWatcher barcodetextwatcher;
    public static final String BARCODE_STRING_PREFIX = "@";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continueoldmobile);

        showScanResult = (AutoCompleteTextView) findViewById(R.id.barcode_value);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Save = (Button) findViewById(R.id.save);
        AddhunoButton = (Button) findViewById(R.id.button_huno);
        Addhuno = (EditText) findViewById(R.id.addhuno);
        ExportCsv = (Button) findViewById(R.id.exportcsv);
        Exit = (Button) findViewById(R.id.read_barcode);
        hucount = (TextView) findViewById(R.id.textView9);
        textcount = (TextView)findViewById(R.id.textView5);


        InvoiceList = dbController.getalldatainvoice();
        count  = dbController.getProfilesCount() + dbController.getExcessReceivesCount();
        textcount.setText(String.valueOf(count));
        //dbController.rawcipher();
        Log.e("fffffffff","" + count);
        storename = InvoiceList.get(0).getStorename();

        Log.d("filename", filedata.toString());
        userTypedString = showScanResult.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Config.SHARED_PREF_USERNAME, "");
        filename = sharedPreferences.getString(Config.SHARED_PREF_FILE, "");
        hucount.setText(username);





    barcodetextwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        // oninput
        @Override
        public void afterTextChanged(Editable s) {

            if (showScanResult.isPerformingCompletion())
            {
                return;
            }
            userTypedString = showScanResult.getText().toString();

            if (userTypedString.endsWith(BARCODE_STRING_PREFIX))
            {

                gunlist = dbController.gethualreadyscanned(userTypedString);
                if (gunlist.size() >=1)
                {

                    showScanResult.setText("");
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,150);
                    Toast.makeText(continueoldmobile.this, "Hu Already Scanned", Toast.LENGTH_SHORT).show();
                }
                else {

                    checkToDb(userTypedString);
                }


            }
        }
    };

        showScanResult.addTextChangedListener(barcodetextwatcher);

        ExportCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                    new ExportRecievedCSV().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    new ExportExcessCSV().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    new ExportShortCSV().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    new ExportRecievedUserCSV().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } else {

                    new ExportRecievedCSV().execute();

                }
                Save.setVisibility(View.VISIBLE);
                ExportCsv.setVisibility(View.INVISIBLE);
                hucount.setText("0");
                textcount.setText("0");

            }
        });

        AddhunoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if (Addhuno.getText().toString().matches("")) {
                    Toast.makeText(continueoldmobile.this, "Please Add Hu No", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkToDbfornewhu(Addhuno.getText().toString());



            }
        });

        Exit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Exitbuttondialog();
            }
        });

//        Clear.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//
//                showScanResult.setText("");
//
//
//
//            }
//        });
//



        Save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                checkexcess =dbController.getExcessData();
                checkrecieved =dbController.getRecievedhuNo();

                if (checkexcess.size()== 0 && checkrecieved.size()==0)
                {
                    Toast.makeText(continueoldmobile.this, "No Data To Save", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    Savebuttondialog();
                }

            }
        });




    }

    @Override
    public void onBackPressed() {

        Exitbuttondialog();

    }

//    public void checkduplicatevalue(String hu)
//    {
////        for (String value:list) {
////            if (value.matches(hu))
////            {
////                Toast.makeText(this, "Hu Already Scanned", Toast.LENGTH_SHORT).show();
////            }
////            else
////            {
////
////
////               // Toast.makeText(this, "HU added"+hu, Toast.LENGTH_SHORT).show();
////              //  list.add(hu);
////            }
////        }
//
//
//        Iterator<String> iterator = gunlist.iterator();
//
//        while (iterator.hasNext()){
//            String value = iterator.next();
//
//            if(value.equals(hu)) {
//                // list.remove(hu);
//                Toast.makeText(this, "Hu Already Scanned", Toast.LENGTH_SHORT).show();
//            }
//            else{
//                //checkToDb(hu);
//                templist.add(hu);
//                System.out.println(hu);
//            }
//        }
//        for(String val:templist){
//            if(!gunlist.contains(val)){
//                gunlist.add(val);
//            }
//        }
////        Log.e("listttt",list.toString());
//       /* for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
//            String string = iterator.next();
//            if (string.matches(hu))
//            {
//                // Remove the current element from the iterator and the list.
//                iterator.remove();
//            }
//            else
//            {
//                list.add(string);
//            }
//*/
////            if (hu.matches(dup))
////            {
//
//        // list.remove(hu);
//        // Duplicatedialog();
//        // Toast.makeText(this, "Hu Already Scanned", Toast.LENGTH_SHORT).show();
//        //Log.e("add", "list");
//
//    }
//
//


    public void Savebuttondialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("You Can't Do the changes once the file is saved");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to save it");

        // Setting Icon to Dialog
        //     alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {



//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//
//                        new Databasesave().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//                    } else {
//
//                        new Databasesave().execute();
//                    }

                    ExportCsv.setVisibility(View.VISIBLE);
                    Save.setVisibility(View.INVISIBLE);

                    Toast.makeText(continueoldmobile.this, "Saved Successfully", Toast.LENGTH_SHORT).show();



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void Exitbuttondialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Exit!!");

        // Setting Dialog Message
        alertDialog.setMessage("Are you Sure you want to Exit");

        // Setting Icon to Dialog
        //     alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {

                    clearfield();
                    Intent intent = new Intent(getApplicationContext(),NavigationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public void checkToDb(String userTypedString) {

        Boolean check;
        check = dbController.CheckIsDataAlreadyInDBorNot(userTypedString);

        if (check == true)
        {

            dbController.updateExistinghustatus(userTypedString);
            showScanResult.setText("");
            textcount.setText(String.valueOf(count++));

            Toast.makeText(this, userTypedString.replace("@",""), Toast.LENGTH_SHORT).show();



            Log.e("Nishant", "Exist" + gunlist);

        } else {

            clearbuttondialog();
            Log.e("Nishant", "DontExist");
        }
    }



//    public void addListToDb() {
//        gunlist.add("1047421716@");
//        for (String obj : gunlist)
//        {
//            boolean included = false;
//            Cursor mTriID = dbController.mGetTotalSave();
//            Log.e("addlist","gunlist" + gunlist + "" + gunlist.size());
//
//
//
//            //  result = String.valueOf(mTriID.getInt(mTriID.getColumnIndex("HU_No")));
//            while (mTriID.moveToNext()) {
//
//                // int id = rows.getInt(0);
//                String text1 = String.valueOf(mTriID.getLong(mTriID.getColumnIndex("HU_No")));
//                //  String text2 = rows.getString(2);
//                if (obj.equals(text1)) {
//                    dbController.updateExistinghustatus(obj);
//                    included = true;
//                    Log.e("HuNoRecieved", obj);
//                    break;
//                }
//            }
//            mTriID.moveToPosition(-1);
//            if (!included) {
//                //    dbController.newhustatus(obj);
//                Log.e("NewHuNO", obj);
//
//
//            }
//        }
//
////        Toast.makeText(this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
//
//
//    }

    public void checkToDbfornewhu(String userTypedString) {

        Boolean check;
        check = dbController.CheckIsDataAlreadyInDBorNot(userTypedString);

        if (check == true) {

            dbController.updateExistinghustatus(userTypedString);
            Toast.makeText(this, "New HuNo Added Successfully", Toast.LENGTH_SHORT).show();
            Addhuno.setText("");
            textcount.setText(String.valueOf(count++));

            Log.e("AddnewHu", "Exist");
        } else {
            Addhunobuttondialog();
            Log.e("Addnewhu", "DontExist");
        }
    }

    public void Addhunobuttondialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("This Hu is not in your upload file");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to Save it");

        // Setting Icon to Dialog
        //     alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {

                    dbController.newhustatus(Addhuno.getText().toString(), storename);

                    Toast.makeText(continueoldmobile.this, "New HuNo Added Successfully", Toast.LENGTH_SHORT).show();
                    Addhuno.setText("");
                    textcount.setText(String.valueOf(count++));



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }

        });

        // Showing Alert Message
        alertDialog.show();
    }



    public void clearbuttondialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("This Hu is not in your upload file");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to Save it");

        // Setting Icon to Dialog
        //     alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {

                    dbController.newhustatus(showScanResult.getText().toString(), storename);
                    showScanResult.setText("");
                    textcount.setText(String.valueOf(count++));

                    Log.e("Barcodedialog", userTypedString);
                    //   Toast.makeText(Gunactivity.this, showScanResult.getText().toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public  void clearfield()
    {
        dbController =  new DBController(this);
        SQLiteDatabase db = dbController.getWritableDatabase();
        String tableName = "retail_prod";
        String table2 = "retail_hu_excess";
        String table3 = "retail_gate";
        db.execSQL("delete from " + tableName);
        db.execSQL("delete from " + table2);
        db.execSQL("delete from " + table3);

        Log.e("Data","Deleted");
        //  db.execSQL("delete from " + tableName2);

    }


//    public class Databasesave extends AsyncTask<String, Void, Boolean> {
//
//
//        private final ProgressDialog dialog = new ProgressDialog(Gunactivity.this);
//        DBController dbhelper;
//
//        @Override
//        protected void onPreExecute() {
//            this.dialog.setMessage("Please Wait");
//            this.dialog.show();
//            dbhelper = new DBController(Gunactivity.this);
//        }
//
//        protected Boolean doInBackground(final String... args) {
//            try {
//               // addListToDb();
//
//                return true;
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                return false;
//            }
//        }
//
//        protected void onPostExecute(final Boolean success) {
//            if (this.dialog.isShowing()) {
//                this.dialog.dismiss();
//            }
//            if (success)
//            {
//
//                Toast.makeText(Gunactivity.this, "Saved successful!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(Gunactivity.this, "UnSaved successful!", Toast.LENGTH_SHORT).show();
//
//            }
//        }
//    }


public class ExportRecievedCSV extends AsyncTask<String, Void, Boolean> {

    private final ProgressDialog dialog = new ProgressDialog(continueoldmobile.this);
    ArrayList<String> recieved = new ArrayList<>();
    DBController dbhelper;

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Exporting Csv Files");
        this.dialog.show();
        dbhelper = new DBController(continueoldmobile.this);
    }

    protected Boolean doInBackground(final String... args) {
        recieved = dbhelper.getRecievedhuNo();
        if (recieved.size() == 0) {
            Log.e("recieved", "" + recieved);
            return false;
        } else {
            File exportDir = new File(Environment.getExternalStorageDirectory(), "/VishalGrc/");
            Log.e("Nishant", exportDir.getAbsolutePath());
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, storename.concat("_").concat(datetime().concat("_").concat(filename)));
            try {

                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                Cursor curCSV = dbhelper.raw();
                csvWrite.writeNext(curCSV.getColumnNames());
                while (curCSV.moveToNext()) {
                    String arrStr[] = null;
                    String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                    for (int i = 0; i < curCSV.getColumnNames().length; i++) {
                        mySecondStringArray[i] = curCSV.getString(i);
                    }
                    csvWrite.writeNext(mySecondStringArray);
                }
                csvWrite.close();
                curCSV.close();
                MediaScannerConnection.scanFile (getApplicationContext(), new String[] {file.toString()}, null, null);

                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    protected void onPostExecute(final Boolean success) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
        if (success) {
            Toast.makeText(continueoldmobile.this, "Export successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(continueoldmobile.this, "No Hu Recieved", Toast.LENGTH_SHORT).show();
        }
    }
}


public class ExportExcessCSV extends AsyncTask<String, Void, Boolean> {
    private final ProgressDialog dialog = new ProgressDialog(continueoldmobile.this);
    ArrayList<String> excess = new ArrayList<>();
    DBController dbhelper;

    @Override
    protected void onPreExecute() {

        this.dialog.setMessage("Exporting CSV Files");
        this.dialog.show();
        dbhelper = new DBController(continueoldmobile.this);
    }

    protected Boolean doInBackground(final String... args) {
        excess = dbhelper.getExcessData();
        if (excess.size() == 0) {
            Log.e("excessdata", "" + excess);
            return false;
        } else {

            File exportDir = new File(Environment.getExternalStorageDirectory(), "/VishalGrc/");
            Log.e("Nishant", exportDir.getAbsolutePath());
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, storename.concat("_").concat("EXCESS").concat("_").concat(datetime().concat("_").concat(filename)));
            try {

                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                Cursor curCSV = dbhelper.second();
                csvWrite.writeNext(curCSV.getColumnNames());
                while (curCSV.moveToNext()) {
                    String arrStr[] = null;
                    String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                    for (int i = 0; i < curCSV.getColumnNames().length; i++) {
                        mySecondStringArray[i] = curCSV.getString(i);
                    }
                    csvWrite.writeNext(mySecondStringArray);
                }
                csvWrite.close();
                curCSV.close();
                MediaScannerConnection.scanFile (getApplicationContext(), new String[] {file.toString()}, null, null);

                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    protected void onPostExecute(final Boolean success) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
        if (success) {
            Toast.makeText(continueoldmobile.this, " ExcessFile Export successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(continueoldmobile.this, "No Excess File", Toast.LENGTH_SHORT).show();
        }
    }


}


public class ExportShortCSV extends AsyncTask<String, Void, Boolean> {
    private final ProgressDialog dialog = new ProgressDialog(continueoldmobile.this);
    ArrayList<String> shortdata = new ArrayList<>();
    DBController dbhelper;

    @Override
    protected void onPreExecute() {

        this.dialog.setMessage("Exporting CSV Files");
        this.dialog.show();
        dbhelper = new DBController(continueoldmobile.this);
    }

    protected Boolean doInBackground(final String... args) {
        shortdata = dbhelper.getShortData();
        if (shortdata.size() == 0) {
            Log.e("shortcsv", "" + shortdata);
            return false;
        } else {

            File exportDir = new File(Environment.getExternalStorageDirectory(), "/VishalGrc/");
            Log.e("Nishant", exportDir.getAbsolutePath());
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, storename.concat("_").concat("Short").concat("_").concat(datetime().concat("_").concat(filename)));
            try {

                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                Cursor curCSV = dbhelper.shortdata();
                csvWrite.writeNext(curCSV.getColumnNames());
                while (curCSV.moveToNext()) {
                    String arrStr[] = null;
                    String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                    for (int i = 0; i < curCSV.getColumnNames().length; i++) {
                        mySecondStringArray[i] = curCSV.getString(i);
                    }
                    csvWrite.writeNext(mySecondStringArray);
                }
                csvWrite.close();

                curCSV.close();
                MediaScannerConnection.scanFile (getApplicationContext(), new String[] {file.toString()}, null, null);

                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    protected void onPostExecute(final Boolean success) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
        if (success) {
            Toast.makeText(continueoldmobile.this, "ShortFile Export successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(continueoldmobile.this, "No Short File", Toast.LENGTH_SHORT).show();
        }
    }


}

public class ExportRecievedUserCSV extends AsyncTask<String, Void, Boolean> {
    private final ProgressDialog dialog = new ProgressDialog(continueoldmobile.this);
    ArrayList<String> recieved = new ArrayList<>();
    DBController dbhelper;

    @Override
    protected void onPreExecute() {

        this.dialog.setMessage("Exporting CSV Files");
        this.dialog.show();
        dbhelper = new DBController(continueoldmobile.this);
    }

    protected Boolean doInBackground(final String... args) {
//            recieved = dbhelper.getRecievedhuNo();
//            if (recieved.size() == 0) {
//                Log.e("recieved", "" + recieved);
//                return false;
//            } else {

        File exportDir = new File(Environment.getExternalStorageDirectory()+"/VishalGrc");
        Log.e("Nishant", exportDir.getAbsolutePath());
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, storename.concat("_").concat(datetime().concat("_").concat("FOR_SCAN_HU.csv")));
        try {

            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor curCSV = dbhelper.Reciveduser();
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String arrStr[] = null;
                String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                for (int i = 0; i < curCSV.getColumnNames().length; i++) {
                    mySecondStringArray[i] = curCSV.getString(i);
                }
                csvWrite.writeNext(mySecondStringArray);
            }
            csvWrite.close();

            curCSV.close();
            MediaScannerConnection.scanFile (getApplicationContext(), new String[] {file.toString()}, null, null);

            return true;
        } catch (IOException e) {
            return false;
        }
        // }
    }

    protected void onPostExecute(final Boolean success) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
        if (success) {
            Toast.makeText(continueoldmobile.this, "Total Hu No File export successful!", Toast.LENGTH_SHORT).show();
            clearfield();
            Intent intent = new Intent(getApplicationContext(),NavigationActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(continueoldmobile.this, "No File Export", Toast.LENGTH_SHORT).show();
        }
    }


}


    public String datetime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Log.e("Time", formattedDate);
        return formattedDate;


    }

}
