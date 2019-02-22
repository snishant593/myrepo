package com.vishalcilpher.nishant.cipherliquidation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cipherlab.barcode.GeneralString;
import com.cipherlab.barcode.ReaderManager;
import com.cipherlab.barcode.decoder.BcReaderType;
import com.cipherlab.barcode.decoder.Enable_State;
import com.cipherlab.barcode.decoder.KeyboardEmulationType;
import com.cipherlab.barcode.decoder.OutputEnterChar;
import com.cipherlab.barcode.decoder.OutputEnterWay;
import com.cipherlab.barcode.decoderparams.ReaderOutputConfiguration;
import com.cipherlab.barcodebase.ReaderCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ReaderCallback{


    Button Downloaddata,Exit,Enter;

    File file;
    String filename;
    String BarcodeCode;

    DBController dbController;
    public static final int requestcode = 1;
    LinearLayout linearLayout;
    AutoCompleteTextView barcode;
    TextView offer,totalcount;

    ArrayList<String> duplicatebarcode;
    int count = 0;

    // Create an IntentFilter to get intents which we want
    private IntentFilter filter;

    // ReaderManager is using to communicate with Barcode Reader Service
    private ReaderManager mReaderManager;

    private Thread mMyThread2 = null;
    private boolean mIsRunning = false;
    private int mDecodeCount = 0;
    private ReaderCallback mReaderCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Downloaddata = (Button) findViewById(R.id.importcsv);
        offer = (TextView)findViewById(R.id.offersdisplay);
        Exit = (Button)findViewById(R.id.exit) ;
        totalcount = (TextView)findViewById(R.id.totalcount) ;
        Enter = (Button) findViewById(R.id.ENTER);


        linearLayout=(LinearLayout)findViewById(R.id.layout1);
        barcode = (AutoCompleteTextView) findViewById(R.id.barcodescan);


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
        registerReceiver(DataReceiver, filter);


        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (barcode.getText().toString().matches(""))
                {
                    Toast.makeText(MainActivity.this, "Please Enter Barcode No", Toast.LENGTH_SHORT).show();
                    return;
                }
                databaseOperation(barcode.getText().toString());
            }
        });




        Downloaddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
                chooser.addCategory(Intent.CATEGORY_OPENABLE);
                chooser.setDataAndType(uri, "*/*");
                try {
                    startActivityForResult(chooser, requestcode);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "No activity can handle picking a file. Showing alternatives.", Toast.LENGTH_SHORT).show();
                }
            }

        });




        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Exitbuttondialog();
            }
        });


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        mIsRunning = false;
        // ***************************************************//
        // Unregister Broadcast Receiver before app close
        // ***************************************************//
        unregisterReceiver(DataReceiver);

        // ***************************************************//
        // release(unbind) before app close
        // ***************************************************//
        if (mReaderManager != null)
        {
            mReaderManager.Release();
        }
    }



    public void databaseOperation(String Barcode)
    {
        duplicatebarcode = dbController.gethualreadyscanned(Barcode);

        if (duplicatebarcode.size() >= 1)
        {


            offer.setText(duplicatebarcode.get(0));
            count++;
            totalcount.setText(String.valueOf(count));
            barcode.setText("");



        } else {
            offernotfounddialog();
            count++;
            totalcount.setText(String.valueOf(count));
          //  barcode.setText("");
            offer.setText("");


        }


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


                    finish();


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

    public void offernotfounddialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("BARCODE");

        // Setting Dialog Message
        alertDialog.setMessage("Barcode" + "  " + BarcodeCode + "  " + "not Found");

        // Setting Icon to Dialog
        //     alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

//        // Setting Negative "NO" Button
//        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // Write your code here to invoke NO event
//                dialog.cancel();
//            }
//        });

        // Showing Alert Message
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        Exitbuttondialog();
    }

    /// create a BroadcastReceiver for receiving intents from barcode reader service
    private final BroadcastReceiver DataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Software trigger must receive this intent message
            if (intent.getAction().equals(GeneralString.Intent_SOFTTRIGGER_DATA)) {

                // extra string from intent
                BarcodeCode = intent.getStringExtra(GeneralString.BcReaderData);

                // show decoded data
                barcode.setText(BarcodeCode);
                databaseOperation(BarcodeCode);
            } else if (intent.getAction().equals(GeneralString.Intent_PASS_TO_APP)) {
                // If user disable KeyboardEmulation, barcode reader service will broadcast Intent_PASS_TO_APP

                // extra string from intent
                BarcodeCode = intent.getStringExtra(GeneralString.BcReaderData);


                // show decoded data
                mDecodeCount++;
                barcode.setText(BarcodeCode);
                databaseOperation(BarcodeCode);

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











    @SuppressLint("ResourceAsColor")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null)
            return;
        switch (requestCode) {
            case requestcode:

                dbController = new DBController(getApplicationContext());
                SQLiteDatabase db = dbController.getWritableDatabase();
                String tableName = "retail_prod";

                db.execSQL("delete from " + tableName);

                try {
                    if (resultCode == RESULT_OK) {
                        try {

                            Uri uri = data.getData();
                            String path = FileUtils.getPath(this, uri);
                            file = new File(path);
                            filename = file.getName();


                            // File f = new File(uri.getEncodedPath());
                            Log.e("Filename", "" + path + "" + file.getName());

                            BufferedReader mBufferedReader = null;
                            String line;
                            ContentValues contentValues = new ContentValues();

                            db.beginTransaction();

                            InputStream inputStream = getContentResolver().openInputStream(uri);

                            mBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            while ((line = mBufferedReader.readLine()) != null) {

                                String[] str = line.split("\t", 2);  // defining 4 columns with null or blank field //values acceptance
                                //Id, Company,Name,Price  Id INTEGER PRIMARY KEY, Store_Nm TEXT,Prod_Desc TEXT,Invoice_No INTEGER,HU_No INTEGER,HU_Status TEXT,Final_Status TEXT
                                //  String Id = str[0].toString();
                                String Barcode = str[0].toString();
                                String offers = str[1].toString();


                                // contentValues.put("Id", Id);
                                contentValues.put("BARCODE", Barcode);
                                contentValues.put("OFFERS", offers);



                                db.insert(tableName, null, contentValues);

                                Log.e("DATA INSERTED IN TABLE","" + line);
                            }
                            mBufferedReader.close();
                            db.setTransactionSuccessful();
                            db.endTransaction();
                        } catch (IOException e) {
                            if (db.inTransaction())
                                db.endTransaction();
                            Dialog d = new Dialog(this);
                            d.setTitle(e.getMessage().toString() + "first");
                            d.show();
                            // db.endTransaction();
                        }
                    } else {
                        if (db.inTransaction())
                            db.endTransaction();
                        Dialog d = new Dialog(this);
                        d.setTitle("Only CSV files allowed");
                        d.show();
                    }
                } catch (Exception ex) {
                    if (db.inTransaction())
                        db.endTransaction();

                    Dialog d = new Dialog(this);
                    d.setTitle(ex.getMessage().toString() + "second");
                    d.show();
                    // db.endTransaction();
                }


        }

        Downloaddata.setEnabled(false);
        linearLayout.setVisibility(View.VISIBLE);
        Downloaddata.setBackgroundResource(R.color.transperent);
    }


    @Override
    public void onDecodeComplete(String s) throws RemoteException {

    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}
