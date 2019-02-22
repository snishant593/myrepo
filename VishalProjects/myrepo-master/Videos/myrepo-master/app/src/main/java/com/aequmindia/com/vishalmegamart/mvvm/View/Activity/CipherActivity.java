package com.aequmindia.com.vishalmegamart.mvvm.View.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.cipherlab.barcode.GeneralString;
import com.cipherlab.barcode.ReaderManager;
import com.cipherlab.barcode.decoder.BcReaderType;
import com.cipherlab.barcode.decoder.Enable_State;
import com.cipherlab.barcode.decoder.KeyboardEmulationType;
import com.cipherlab.barcode.decoder.OutputEnterChar;
import com.cipherlab.barcode.decoder.OutputEnterWay;
import com.cipherlab.barcode.decoderparams.ReaderOutputConfiguration;
import com.cipherlab.barcodebase.ReaderCallback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CipherActivity extends AppCompatActivity implements ReaderCallback {


    // Create an IntentFilter to get intents which we want
    private IntentFilter filter;

    // ReaderManager is using to communicate with Barcode Reader Service
    private ReaderManager mReaderManager;

    private Thread mMyThread2 = null;
    private boolean mIsRunning = false;
    private int mDecodeCount = 0;
    String BarcodeCode;
    private ReaderCallback mReaderCallback = null;
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
    int count =1;
    ArrayList<String> checkrecieved = new ArrayList<>();
    ArrayList<String> checkshort = new ArrayList<>();
    ArrayList<String> checkexcess = new ArrayList<>();
    ArrayList<String> updatescan =  new ArrayList<>();







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher);

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
        storename = InvoiceList.get(0).getStorename();
        Bundle bundle = getIntent().getExtras();
        filename = bundle.getString("FILENAME");
        Log.d("filename", filename);
        userTypedString = showScanResult.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Config.SHARED_PREF_USERNAME, "");
        hucount.setText(username);






        mReaderManager = ReaderManager.InitInstance(this);
        mReaderCallback = this;

        // ***************************************************//
        // Register an IntentFilter
        // Add GeneralString.Intent_SOFTTRIGGER_DATA for software trigger
        // Add GeneralString.Intent_PASS_TO_APP for getting decoded data after disabling Keyboard Emulation
        // Add GeneralString.Intent_READERSERVICE_CONNECTED for knowing apk is connected with Barcode Reader Service
        // ***************************************************//
        filter = new IntentFilter();
        filter.addAction(GeneralString.Intent_SOFTTRIGGER_DATA);
        filter.addAction(GeneralString.Intent_PASS_TO_APP);
        filter.addAction(GeneralString.Intent_READERSERVICE_CONNECTED);
        registerReceiver(myDataReceiver, filter);






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
                    Toast.makeText(CipherActivity.this, "Please Add Hu No", Toast.LENGTH_SHORT).show();
                    return;
                }
                gunlist = dbController.gethualreadyscanned(Addhuno.getText().toString());
                if (gunlist.size() >=1)
                {

                    // showScanResult.setText("");
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,150);
                    Toast.makeText(CipherActivity
                            .this, "Hu Already Scanned", Toast.LENGTH_SHORT).show();
                    Addhuno.setText("");
                }
                else {


                    checkToDbfornewhu(Addhuno.getText().toString());
                }



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




        Save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                checkexcess =dbController.getExcessData();
                checkrecieved =dbController.getRecievedhuNo();

                if (checkexcess.size()== 0 && checkrecieved.size()==0)
                {
                    Toast.makeText(CipherActivity.this, "No Data To Save", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    Savebuttondialog();
                }

            }
        });




    }

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

                    ExportCsv.setVisibility(View.VISIBLE);
                    Save.setVisibility(View.INVISIBLE);
                    showScanResult.setText("");

                    Toast.makeText(CipherActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();



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






    @Override
    protected void onDestroy() {
        super.onDestroy();

        mIsRunning = false;
        // ***************************************************//
        // Unregister Broadcast Receiver before app close
        // ***************************************************//
        unregisterReceiver(myDataReceiver);

        // ***************************************************//
        // release(unbind) before app close
        // ***************************************************//
        if (mReaderManager != null)
        {
            mReaderManager.Release();
        }
    }



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

                    Toast.makeText(CipherActivity.this, "New HuNo Added Successfully", Toast.LENGTH_SHORT).show();
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
                Addhuno.setText("");
                dialog.cancel();
            }

        });

        // Showing Alert Message
        alertDialog.show();
    }





    /// create a BroadcastReceiver for receiving intents from barcode reader service
    private final BroadcastReceiver myDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Software trigger must receive this intent message
            if (intent.getAction().equals(GeneralString.Intent_SOFTTRIGGER_DATA)) {

                // extra string from intent
                BarcodeCode = intent.getStringExtra(GeneralString.BcReaderData);

                showScanResult.setText(BarcodeCode);


                Databaseoperation(BarcodeCode);



                // show decoded data

            } else if (intent.getAction().equals(GeneralString.Intent_PASS_TO_APP)) {
                // If user disable KeyboardEmulation, barcode reader service will broadcast Intent_PASS_TO_APP

                // extra string from intent
                BarcodeCode = intent.getStringExtra(GeneralString.BcReaderData);
                showScanResult.setText(BarcodeCode);

                Databaseoperation(BarcodeCode);



                // show decoded data
                mDecodeCount++;

                //  e1.setText("[" + mDecodeCount + "]   " + BarcodeCode);

            } else if (intent.getAction().equals(GeneralString.Intent_READERSERVICE_CONNECTED)) {
                // Make sure this app bind to barcode reader service , then user can use APIs to get/set settings from barcode reader service

                BcReaderType myReaderType = mReaderManager.GetReaderType();
//                e1.setText(myReaderType.toString());
                Log.e("Done",myReaderType.toString());


                ReaderOutputConfiguration settings = new ReaderOutputConfiguration();
                mReaderManager.Get_ReaderOutputConfiguration(settings);
                settings.enableKeyboardEmulation = KeyboardEmulationType.None;
                settings.autoEnterWay = OutputEnterWay.Disable;
                settings.autoEnterChar = OutputEnterChar.None;
                settings.showCodeLen = Enable_State.FALSE;
                settings.showCodeType = Enable_State.FALSE;
                //  settings.szPrefixCode = "";
                //  settings.szSuffixCode = "";
                // settings.useDelim = ':';

                mReaderManager.Set_ReaderOutputConfiguration(settings);

                mReaderManager.SetActive(true);


            }

        }
    };


    public  void Databaseoperation(String userTypedString)
    {


            gunlist = dbController.gethualreadyscanned(userTypedString);
            if (gunlist.size() >=1)
            {

               // showScanResult.setText("");
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,150);
                Toast.makeText(CipherActivity
                        .this, "Hu Already Scanned", Toast.LENGTH_SHORT).show();
            }
            else {

                checkToDb(userTypedString);
            }



    }


    public void checkToDb(String userTypedString) {

        Boolean check;
        check = dbController.CheckIsDataAlreadyInDBorNot(userTypedString);

        if (check == true)
        {

            dbController.updateExistinghustatus(userTypedString);
            textcount.setText(String.valueOf(count++));

           // showScanResult.setText("");
            Toast.makeText(this, userTypedString, Toast.LENGTH_SHORT).show();



            Log.e("Nishant", "Exist" + gunlist);

        } else {

            clearbuttondialog();
            Log.e("Nishant", "DontExist");
        }
    }

    @Override
    public void onBackPressed() {
        Exitbuttondialog();
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
                showScanResult.setText(" ");
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

    public class ExportRecievedCSV extends AsyncTask<String, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(CipherActivity.this);
        ArrayList<String> recieved = new ArrayList<>();
        DBController dbhelper;

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting Csv Files");
            this.dialog.show();
            dbhelper = new DBController(CipherActivity.this);
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
                Toast.makeText(CipherActivity.this, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CipherActivity.this, "No Hu Recieved", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class ExportExcessCSV extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(CipherActivity.this);
        ArrayList<String> excess = new ArrayList<>();
        DBController dbhelper;

        @Override
        protected void onPreExecute() {

            this.dialog.setMessage("Exporting CSV Files");
            this.dialog.show();
            dbhelper = new DBController(CipherActivity.this);
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
                Toast.makeText(CipherActivity.this, " ExcessFile Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CipherActivity.this, "No Excess File", Toast.LENGTH_SHORT).show();
            }
        }


    }


    public class ExportShortCSV extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(CipherActivity.this);
        ArrayList<String> shortdata = new ArrayList<>();
        DBController dbhelper;

        @Override
        protected void onPreExecute() {

            this.dialog.setMessage("Exporting CSV Files");
            this.dialog.show();
            dbhelper = new DBController(CipherActivity.this);
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
                Toast.makeText(CipherActivity.this, "ShortFile Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CipherActivity.this, "No Short File", Toast.LENGTH_SHORT).show();
            }
        }


    }

    public class ExportRecievedUserCSV extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(CipherActivity.this);
        ArrayList<String> recieved = new ArrayList<>();
        DBController dbhelper;

        @Override
        protected void onPreExecute() {

            this.dialog.setMessage("Exporting CSV Files");
            this.dialog.show();
            dbhelper = new DBController(CipherActivity.this);
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
                Cursor curCSV = dbhelper.Recivedusercipher();
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
                Toast.makeText(CipherActivity.this, "Total Hu No File export successful!", Toast.LENGTH_SHORT).show();
                clearfield();
                Intent intent = new Intent(getApplicationContext(),NavigationActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(CipherActivity.this, "No File Export", Toast.LENGTH_SHORT).show();
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




    public class SoftScanTriggerRunnable implements Runnable {
        @Override
        public void run() {
            int iSleepTime = 3000;

            while (mIsRunning) {
                try {
                    mReaderManager.SoftScanTrigger();

                    Thread.sleep(iSleepTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public IBinder asBinder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDecodeComplete(String arg0) throws RemoteException {
        // TODO Auto-generated method stub
        //e1.setText(arg0);
        Toast.makeText(this, "Decode Data " + arg0, Toast.LENGTH_SHORT).show();
    }




}
