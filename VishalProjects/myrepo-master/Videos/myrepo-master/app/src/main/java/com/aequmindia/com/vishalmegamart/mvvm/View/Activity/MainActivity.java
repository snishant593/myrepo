package com.aequmindia.com.vishalmegamart.mvvm.View.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;
import android.device.scanner.configuration.Symbology;
import android.device.scanner.configuration.Triggering;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aequmindia.com.vishalmegamart.Camera.barcodefirstactivity;
import com.aequmindia.com.vishalmegamart.Common.Config;
import com.aequmindia.com.vishalmegamart.Common.DBController;
import com.aequmindia.com.vishalmegamart.R;
import com.aequmindia.com.vishalmegamart.mvvm.View.Adapter.invoicelistadapter;
import com.aequmindia.com.vishalmegamart.mvvm.model.Datamodel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity
{

    private final static String SCAN_ACTION = ScanManager.ACTION_DECODE;//default action
    Toolbar toolbar;
    String storename, filename;
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> templist = new ArrayList<String>();

    DBController dbController = new DBController(this);
    ArrayList<Datamodel> InvoiceList;
    private ActionBar actionBar;
    private TextView  hucount,showScanResult;
    private EditText Addhuno;
    private Button Save, AddhunoButton, ExportCsv, mScan;
    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;
    ArrayList<String> checkrecieved = new ArrayList<>();
    ArrayList<String> checkshort = new ArrayList<>();
    ArrayList<String> checkexcess = new ArrayList<>();

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            showScanResult.setText("");
            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);
            int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0);
            byte temp = intent.getByteExtra(ScanManager.BARCODE_TYPE_TAG, (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            barcodeStr = new String(barcode, 0, barcodelen);
            showScanResult.append(barcodeStr);


                checkToDb(barcodeStr);
                if (list.size() == 0)
                {
                    list.add(barcodeStr);
                }
                else {

                    checkduplicatevalue(barcodeStr);
                }




            Log.e("BarcodeScannerHandheld", " " + list);
            //showScanResult.setText(barcodeStr);

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scannerbarcode);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
//                new IntentFilter("hu-msg"));
        Save = (Button) findViewById(R.id.save);
        AddhunoButton = (Button) findViewById(R.id.button_huno);
        Addhuno = (EditText) findViewById(R.id.addhuno);
        ExportCsv = (Button) findViewById(R.id.exportcsv);

        showScanResult = (TextView) findViewById(R.id.barcode_value);
        hucount = (TextView) findViewById(R.id.textView9);
        showScanResult.setText("Scan HuNo");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Config.SHARED_PREF_USERNAME, "");
        hucount.setText(username);

        Log.e("sharedvalue", username);


        setupView();


    }


    @Override
    public void onBackPressed() {
       // super.onBackPressed();

    }


    private void initScan() {
        // TODO Auto-generated method stub
        mScanManager = new ScanManager();
        mScanManager.openScanner();

        mScanManager.switchOutputMode(0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
    }

    private void setupView() {
        // TODO Auto-generated method stub

        InvoiceList = dbController.getalldatainvoice();

       storename = InvoiceList.get(0).getStorename();


        Bundle bundle = getIntent().getExtras();
        filename = bundle.getString("FILENAME");
        Log.d("filename", filename);


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


            }
        });

        AddhunoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if (Addhuno.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, "Please Add Hu No", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkToDbfornewhu(Addhuno.getText().toString());



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
                    Toast.makeText(MainActivity.this, "No Data To Save", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    Savebuttondialog();
                }

            }
        });


        mScan = (Button) findViewById(R.id.read_barcode);
        mScan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //if(type == 3)
                mScanManager.stopDecode();
                isScaning = true;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mScanManager.startDecode();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NavigationActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mScanManager != null) {
            mScanManager.stopDecode();
            isScaning = false;
        }
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initScan();
        showScanResult.setText("Scan HuNo");
        IntentFilter filter = new IntentFilter();
        int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG};
        String[] value_buf = mScanManager.getParameterString(idbuf);
        if (value_buf != null && value_buf[0] != null && !value_buf[0].equals("")) {
            filter.addAction(value_buf[0]);
        } else {
            filter.addAction(SCAN_ACTION);
        }

        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        //  MenuItem settings = menu.add(0, 1, 0, R.string.menu_settings).setIcon(R.drawable.ic_action_settings);
        // 绑定到actionbar  
        //SHOW_AS_ACTION_IF_ROOM 显示此项目在动作栏按钮如果系统决定有它。 可以用1来代替
        //  MenuItem version = menu.add(0, 2, 0, R.string.menu_settings);
        //  settings.setShowAsAction(1);
        //version.setShowAsAction(0);
        return super.onCreateOptionsMenu(menu);
    }


    public void checkduplicatevalue(String hu)
    {
//        for (String value:list) {
//            if (value.matches(hu))
//            {
//                Toast.makeText(this, "Hu Already Scanned", Toast.LENGTH_SHORT).show();
//            }
//            else
//            {
//
//
//               // Toast.makeText(this, "HU added"+hu, Toast.LENGTH_SHORT).show();
//              //  list.add(hu);
//            }
//        }


        Iterator<String> iterator = list.iterator();

        while (iterator.hasNext()){
            String value = iterator.next();

            if(value.equals(hu)) {
               // list.remove(hu);
                Toast.makeText(this, "Hu Already Scanned", Toast.LENGTH_SHORT).show();
            }
            else{
                //checkToDb(hu);
                templist.add(hu);
                System.out.println(hu);
               }
        }
        for(String val:templist){
            if(!list.contains(val)){
                list.add(val);
            }
        }
//        Log.e("listttt",list.toString());
       /* for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.matches(hu))
            {
                // Remove the current element from the iterator and the list.
                iterator.remove();
            }
            else
            {
                list.add(string);
            }
*/
//            if (hu.matches(dup))
//            {

           // list.remove(hu);
               // Duplicatedialog();
               // Toast.makeText(this, "Hu Already Scanned", Toast.LENGTH_SHORT).show();
                //Log.e("add", "list");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case 1:
                try {
                    Intent intent = new Intent("android.intent.action.SCANNER_SETTINGS");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                break;
            case 2:
                PackageManager pk = getPackageManager();
                PackageInfo pi;
                try {
                    pi = pk.getPackageInfo(getPackageName(), 0);
                    Toast.makeText(this, "V" + pi.versionName, Toast.LENGTH_SHORT).show();
                } catch (NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    public void addListToDb() {
        for (String obj : list) {
            boolean included = false;
            Cursor mTriID = dbController.mGetTotalSave();


            //  result = String.valueOf(mTriID.getInt(mTriID.getColumnIndex("HU_No")));
            while (mTriID.moveToNext()) {

                // int id = rows.getInt(0);
                String text1 = String.valueOf(mTriID.getLong(mTriID.getColumnIndex("HU_No")));
                //  String text2 = rows.getString(2);
                if (obj.equals(text1)) {
                    dbController.updateExistinghustatus(obj);
                    included = true;
                    Log.e("HuNoRecieved", obj);
                    break;
                }
            }
            mTriID.moveToPosition(-1);
            if (!included) {
                //    dbController.newhustatus(obj);
                Log.e("NewHuNO", obj);


            }
        }

//        Toast.makeText(this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();


    }

    public void checkToDbfornewhu(String userTypedString) {

        Boolean check;
        check = dbController.CheckIsDataAlreadyInDBorNot(userTypedString);

        if (check == true) {

            Toast.makeText(this, "New HuNo Added Successfully", Toast.LENGTH_SHORT).show();
            Addhuno.setText("");
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

                    Toast.makeText(MainActivity.this, "New HuNo Added Successfully", Toast.LENGTH_SHORT).show();
                    Addhuno.setText("");


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

        if (check == true )
        {
           // list.add(barcodeStr);
            Log.e("Nishant", "Exist" + list);
        } else {
            clearbuttondialog();
            Log.e("Nishant", "DontExist");
        }
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

                    dbController.newhustatus(barcodeStr, storename);
                    Log.e("Barcodedialog", barcodeStr);

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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                        new Databasesave().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    } else {

                        new Databasesave().execute();
                    }

                    ExportCsv.setVisibility(View.VISIBLE);
                    Save.setVisibility(View.INVISIBLE);


                    // dbController.newhustatus(barcode.displayValue,storename);
                    //               Log.e("Barcodedialog",barcode.displayValue);

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


public class Databasesave extends AsyncTask<String, Void, Boolean> {


    private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
    DBController dbhelper;

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Please Wait");
        this.dialog.show();
        dbhelper = new DBController(MainActivity.this);
    }

    protected Boolean doInBackground(final String... args) {
        try {
            addListToDb();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    protected void onPostExecute(final Boolean success) {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
        if (success) {
            Toast.makeText(MainActivity.this, "Saved successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Saved successful!", Toast.LENGTH_SHORT).show();

        }
    }
}


public class ExportRecievedCSV extends AsyncTask<String, Void, Boolean> {

    private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
    ArrayList<String> recieved = new ArrayList<>();
    DBController dbhelper;

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Exporting Csv Files");
        this.dialog.show();
        dbhelper = new DBController(MainActivity.this);
    }

    protected Boolean doInBackground(final String... args) {
        recieved = dbhelper.getRecievedhuNo();
        if (recieved.size() == 0) {
            Log.e("recieved", "" + recieved);
            return false;
        } else {
            File exportDir = new File(Environment.getExternalStorageDirectory(), "/CSVFOLDER/");
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
            Toast.makeText(MainActivity.this, "Export successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "No Hu Recieved", Toast.LENGTH_SHORT).show();
        }
    }
}


public class ExportExcessCSV extends AsyncTask<String, Void, Boolean> {
    private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
    ArrayList<String> excess = new ArrayList<>();
    DBController dbhelper;

    @Override
    protected void onPreExecute() {

        this.dialog.setMessage("Exporting CSV Files");
        this.dialog.show();
        dbhelper = new DBController(MainActivity.this);
    }

    protected Boolean doInBackground(final String... args) {
        excess = dbhelper.getExcessData();
        if (excess.size() == 0) {
            Log.e("excessdata", "" + excess);
            return false;
        } else {

            File exportDir = new File(Environment.getExternalStorageDirectory(), "/CSVFOLDER/");
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
            Toast.makeText(MainActivity.this, " ExcessFile Export successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "No Excess File", Toast.LENGTH_SHORT).show();
        }
    }


}


public class ExportShortCSV extends AsyncTask<String, Void, Boolean> {
    private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
    ArrayList<String> shortdata = new ArrayList<>();
    DBController dbhelper;

    @Override
    protected void onPreExecute() {

        this.dialog.setMessage("Exporting CSV Files");
        this.dialog.show();
        dbhelper = new DBController(MainActivity.this);
    }

    protected Boolean doInBackground(final String... args) {
        shortdata = dbhelper.getShortData();
        if (shortdata.size() == 0) {
            Log.e("shortcsv", "" + shortdata);
            return false;
        } else {

            File exportDir = new File(Environment.getExternalStorageDirectory(), "/CSVFOLDER/");
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
            Toast.makeText(MainActivity.this, "ShortFile Export successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "No Short File", Toast.LENGTH_SHORT).show();
        }
    }


}

    public class ExportRecievedUserCSV extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        ArrayList<String> recieved = new ArrayList<>();
        DBController dbhelper;

        @Override
        protected void onPreExecute() {

            this.dialog.setMessage("Exporting CSV Files");
            this.dialog.show();
            dbhelper = new DBController(MainActivity.this);
        }

        protected Boolean doInBackground(final String... args) {
//            recieved = dbhelper.getRecievedhuNo();
//            if (recieved.size() == 0) {
//                Log.e("recieved", "" + recieved);
//                return false;
//            } else {

                File exportDir = new File(Environment.getExternalStorageDirectory()+"/CSVFOLDER");
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
                Toast.makeText(MainActivity.this, "Total Hu No File export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "No File Export", Toast.LENGTH_SHORT).show();
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
