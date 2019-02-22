/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aequmindia.com.vishalmegamart.Camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aequmindia.com.vishalmegamart.Common.Config;
import com.aequmindia.com.vishalmegamart.Common.DBController;
import com.aequmindia.com.vishalmegamart.R;
import com.aequmindia.com.vishalmegamart.mvvm.View.Activity.CSVWriter;
import com.aequmindia.com.vishalmegamart.mvvm.View.Activity.MainActivity;
import com.aequmindia.com.vishalmegamart.mvvm.View.Activity.NavigationActivity;
import com.aequmindia.com.vishalmegamart.mvvm.model.Datamodel;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class barcodefirstactivity extends Activity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView hucount;
    private TextView barcodeValue;
    private Button Save,AddhunoButton,ExportCsv;
    Toolbar toolbar;
    private EditText Addhuno;
    String Huresult;
    ArrayList<String> list = new ArrayList<String>();
    String  userTypedString;
    Barcode barcode;
    ArrayList<Datamodel> InvoiceList;



    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    DBController dbController = new DBController(this);
    String storename;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodeValue = (TextView) findViewById(R.id.barcode_value);
        AddhunoButton = (Button)findViewById(R.id.button_huno);
        Addhuno = (EditText)findViewById(R.id.addhuno);
        ExportCsv=(Button)findViewById(R.id.exportcsv);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);
        Save = (Button) findViewById(R.id.save);
        hucount = (TextView) findViewById(R.id.textView9);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Config.SHARED_PREF_USERNAME, "");
        hucount.setText(String.valueOf(username));


        userTypedString = barcodeValue.getText().toString();
        InvoiceList = dbController.getalldatainvoice();
        storename = InvoiceList.get(0).getStorename();


        Bundle bundle = getIntent().getExtras();
        filename = bundle.getString("FILENAME");
        Log.d("filename",filename);



        // dbController.CheckIsDataAlreadyInDBorNot(userTypedString);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.read_barcode).setOnClickListener(this);


        ExportCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                    new ExportRecievedCSV().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                   new ExportExcessCSV().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                   new ExportShortCSV().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                   new ExportRecievedUserCSV().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } else {

                    new ExportRecievedCSV().execute();
                   // new ExportExcessCSV().execute();
                  //  new ExportShortCSV().execute();
                }
                Save.setVisibility(View.VISIBLE);
                ExportCsv.setVisibility(View.INVISIBLE);


            }
        });

        AddhunoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Addhuno.getText().toString().matches(""))
                {
                    Toast.makeText(barcodefirstactivity.this, "Please Add Hu No", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkToDbfornewhu(Addhuno.getText().toString());
               // dbController.newhustatus(Addhuno.getText().toString(),storename);

               // Toast.makeText(barcodefirstactivity.this, "New Hu NO Added", Toast.LENGTH_SHORT).show();
               // Addhuno.setText("");
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Savebuttondialog();

            }
        });



    }



    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(getApplicationContext(),NavigationActivity.class);
//        startActivity(intent);
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     *
     *@see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_BARCODE_CAPTURE)
        {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                  //  statusMessage.setText(R.string.barcode_success);
                    barcodeValue.setText(barcode.displayValue);
                    checkToDb(barcode.displayValue);

                    if (list.size()==0)
                    {

                        list.add(barcode.displayValue);
                    }
                    else {

                        checkduplicatevalue(barcode.displayValue);
                    }

                    //list.add(barcode.displayValue);
              //      list.add("1046024716");
                //    list.add("1046024739");
//                    list.add ("1046042159");
//                    list.add ("1046042164");
//                    list.add ("1046042167");
//                    list.add ("1046042169");
//                    list.add ("1046042172");
//                    list.add ("1046046127");
//                    list.add ("1046074532");
//                    list.add ("1046082910");
//                    list.add ("1046114664");
//                    list.add ("1046120588");
//                    list.add ("1046159502");
//                    list.add ("1046185789");
//                    list.add ("1046185797");
//                    list.add ("1046185804");
//                    list.add ("1046185810");
//                    list.add ("1046185815");
//                    list.add ("1046185827");
//                    list.add ("1046185833");
//                    list.add ("1046185841");
//                    list.add  ("1046185852");
//                    list.add ("1046185858");
//                    list.add ("1046185863");
//                    list.add ("1046185869");
//                    list.add ("1046185874");
//                    list.add ("1046185880");
//                    list.add ("1046185915");
//                    list.add ("1046185921");
//                    list.add ("1046185927");
//                    list.add ("1046185933");
//                    list.add ("1046185939");
//                    list.add ("1046185945");
//                    list.add ("1046185956");
//                    list.add ("1046185962");
//                    list.add ("1046185967");
//                    list.add ("1046185979");
//                    list.add ("1046185985");
//                    list.add  ("1046185990");
//                    list.add  ("1046186013");
//                    list.add  ("1046186020");
//                    list.add  ("1046186030");
//                    list.add  ("1046186040");
//                    list.add  ("1046186048");
//                    list.add  ("1046186054");
//                    list.add (" 1046186064");
//                    list.add ("1046186068");
//                    list.add ("1046186089");
//                    list.add ("1046186095");
//                    list.add (" 1046186103");
//                    list.add ("1046186107");
//                    list.add ("1046186130");
//                    list.add ("1046186137");
//                    list.add ("1046186142");
//                    list.add ("1046186148");
//                    list.add ("1046186153");
//                    list.add ("1046186169");
//                    list.add ("1046186175");
//                    list.add ("1046186179");
//                    list.add ("1046186183");
//                    list.add ("1046186187");
//                    list.add ("1046186206");
//                    list.add ("1046186210");
//                    list.add ("1046186214");
//                    list.add ("1046186220");
//                    list.add ("1046186224");
//                    list.add ("1046186244");
//                    list.add ("1046186257");
//                    list.add ("1046186272");
//                    list.add ("1046186284");
//                    list.add ("1046186297");
//                    list.add ("1046186307");
//                    list.add ("1046186311");
//                    list.add ("1046186315");
//                    list.add ("1046186332");
//                    list.add ("1046186336");
//                    list.add ("1046186345");
//                    list.add ("1046186349");
//                    list.add ("1046186361");
//                    list.add(" 1046186368");
//                    list.add("1046186398");
//                    list.add("1046186406");
//                    list.add("1046186410");
//                    list.add("1046186415");
//                    list.add("1046186425");
//                    list.add("1046186428");
//                    list.add("1046186446");
//                    list.add("1046186466");
//                    list.add("1046186470");
//                    list.add("1046186474");
//                    list.add("1046186478");
//                    list.add("1046186481");
//                    list.add("1046186490");
//                    list.add("1046186502");
//                    list.add("1046186510");
//                    list.add("1046186522");
//                    list.add("1046186527");
//                    list.add("1046186531");
//                    list.add("1046186534");
//                    list.add("1046186537");
//                    list.add("1046186541");
//                    list.add("1046186572");
//                    list.add("1046186575");
//                    list.add("1046186580");
//                    list.add("1046186584");
//                    list.add("1046186588");
//                    list.add("1046186594");
//                    list.add("1046186601");
//                    list.add(" 1046186605");
//                    list.add("1046186609");
//                    list.add(" 1046186612");
//                    list.add("1046186624");
//                    list.add("1046186627");
//                    list.add("1046186630");
//                    list.add("1046190938");
//                    list.add("1046191481");
//                    list.add("1046191513");
//                    list.add("1046193844");
//                    list.add("1046195160");
//                    list.add("1046195161");
//                    list.add("1046195162");
//                    list.add("1046195163");
//                    list.add("1046195165");
//                    list.add("1046195169");
//                    list.add("1046196953");
//                    list.add("1046196954");
//                    list.add("1046197488");
//                    list.add("1046198755");
//                    list.add(" 1046198793");
//                    list.add("1046199413");
//                    list.add(" 1046199414");
//                    list.add("1046199416");
//                    list.add("1046199828");
//                    list.add("1046200463");
//                    list.add("1046200880");
//                    list.add(" 1046200881");
//                    list.add("1046201010");
//                    list.add("1046201012");
//                    list.add("1046201015");
//                    list.add("1046201017");
//                    list.add("1046201018");
//                    list.add("1046201020");
//                    list.add("1046201024");
//                    list.add("1046201025");
//                    list.add("1046201026");
//                    list.add("1046201118");
//                    list.add("1046201242");
//                    list.add("1046201700");
//                    list.add("1046201726");
//                    list.add("1046202618");
//                    list.add("1046202619");
//                    list.add("1046202776");
//                    list.add("1046202778");
//                    list.add("1046202781");
//                    list.add("1046205448");
//                    list.add("1046205451");
//                    list.add("1046205453");
//                    list.add("1046205456");
//                    list.add("1046205458");
//                    list.add("1046205461");
//                    list.add("1046205679");
//                    list.add("1046205683");
//                    list.add("1046205687");
//                    list.add("1046205690");
//                    list.add("1046205693");
//                    list.add("1046206669");
//                    list.add("1046206682");
//                    list.add("1046206683");
//                    list.add("1046206684");
//                    list.add("1046206685");
//                    list.add("1046207139");
//                    list.add("1046207521");
//                    list.add("1046207939");
//                    list.add("1046208003");
//                    list.add("1046208004");
//                    list.add("1046208005");
//                    list.add("1046208537");
//                    list.add("1046208846");
//                    list.add("1046208950");
//                    list.add("1046208951");
//                    list.add("1046209164");
//                    list.add("1046209165");
//                    list.add("1046209166");
//                    list.add("1046209167");
//                    list.add("1046209168");
//                    list.add("1046209169");
//                    list.add("1046210175");
//                    list.add("1046210176");
//                    list.add("1046210177");
//                    list.add("1046210178");
//                    list.add("1046210179");
//                    list.add("1046211246");
//                    list.add("1046211958");
//                    list.add("1046213005");
//                    list.add("1046213009");
//                    list.add("1046213014");
//                    list.add("1046213018");
//                    list.add("1046213022");
//                    list.add("1046213027");
//                    list.add("1046213033");
//                    list.add("1046213037");
//                    list.add("1046213042");
//                    list.add("1046213047");
//                    list.add("1046213052");
//                    list.add("1046213056");
//                    list.add("1046213802");
//                    list.add("1046217846");
//                    list.add("1046217847");
//                    list.add("1046217852");
//                    list.add("1046217854");
//                    list.add("1046220879");
//                    list.add("1046220982");
//                    list.add("1046220985");
//                    list.add("1046220989");
//                    list.add("1046220992");
//                    list.add("1046220995");
//                    list.add("1046220997");
//                    list.add("1046221000");
//                    list.add("1046221003");
//                    list.add("1046221007");
//                    list.add("1046221791");
//                    list.add("1046225962");
//                    list.add("1046225963");
//                    list.add("1046225966");
//                    list.add("1046225967");
//                    list.add("1046225970");
//                    list.add("1046225972");
//                    list.add("1046225974");
//                    list.add("1046225976");
//                    list.add("1046225978");
//                    list.add("1046225980");
//                    list.add("1046225981");
//                    list.add("1046225982");
//                    list.add("1046225984");
//                    list.add("1046225986");
//                    list.add("1046225989");
//                    list.add("1046225991");
//                    list.add("1046225994");
//                    list.add("1046225995");
//                    list.add("1046225998");
//                    list.add("1046226001");
//                    list.add("1046226003");
//                    list.add("1046226005");
//                    list.add("1046226008");
//                    list.add("1046226009");
//                    list.add("1046226012");
//                    list.add("1046226018");
//                    list.add("1046226022");
//                    list.add("1046226025");
//                    list.add("1046227637");
//                    list.add("1046227639");
//                    list.add("1046231859");
//                    list.add("1046231861");
//                    list.add("1046231863");
//                    list.add("1046231864");
//                    list.add("1046231867");
//                    list.add("1046231870");
//                    list.add("1046231872");
//                    list.add("1046231874");
//                    list.add("1046231876");
//                    list.add("1046231878");
//                    list.add("1046231880");
//                    list.add("1046231882");
//                    list.add("1046231884");
//                    list.add("1046231886");
//                    list.add("1046231888");
//                    list.add("1046235083");
//                    list.add("1046235085");
//                    list.add("1046235088");
//                    list.add("1046237215");
//                    list.add("1046237220");
//                    list.add("1046237223");
//                    list.add("1046237227");
//                    list.add("1046237229");
//                    list.add("1046237231");
//                    list.add("1046237232");
//                    list.add("1046237235");
//                    list.add("1046239486");
//                    list.add("1046240033");
//                    list.add("1046240034");
//                    list.add("1046240035");
//                    list.add("1046240037");
//                    list.add("1046240039");
//                    list.add("1046240040");
//                    list.add("1046240041");
//                    list.add("1046242220");
//                    list.add("1046242451");
//                    list.add("1046242454");
//                    list.add("1046242458");
//                    list.add("1046242463");
//                    list.add("1046242468");
//                    list.add("1046242470");
//                    list.add("1046242476");
//                    list.add("1046242481");
//                    list.add("1046242486");
//                    list.add("1046242489");
//                    list.add("1046242494");
//                    list.add("1046242499");
//                    list.add("1046242504");
//                    list.add("1046242508");
//                    list.add("1046242707");
//                    list.add("1046243474");
//                    list.add("1046243475");
//                    list.add("1046246313");
//                    list.add("1046246344");
//                    list.add("1046248514");
//                    list.add("1046248521");
//                    list.add("1046248527");
//                    list.add("1046248533");
//                    list.add("1046248543");
//                    list.add("1046248553");
//                    list.add("1046248562");
//                    list.add("1046248571");
//                    list.add("1046248577");
//                    list.add("1046248589");
//                    list.add("1046248597");
//                    list.add("1046248604");
//                    list.add("1046248842");
//                    list.add("1046248911");
//                    list.add("1046248919");
//                    list.add("1046248927");
//                    list.add("1046248931");
//                    list.add("1046248938");
//                    list.add("1046248943");
//                    list.add("1046248949");
//                    list.add("1046248957");
//                    list.add("1046248962");
//                    list.add("1046248966");
//                    list.add("1046248978");
//                    list.add("1046248982");
//                    list.add("1046248989");
//                    list.add("1046249514");
//                    list.add("1046250157");
//                    list.add("1046250342");

                    Log.d(TAG, "onActivityResult:" + list    + list.size());
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                  //  statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
               // statusMessage.setText(String.format(getString(R.string.barcode_error),
                      //  CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void addListToDb()
    {
        for (String obj : list) {
            boolean included = false;
            Cursor mTriID = dbController.mGetTotalSave();

            while (mTriID.moveToNext()) {

                String text1 = String.valueOf(mTriID.getLong(mTriID.getColumnIndex("HU_No")));
                if (obj.equals(text1))
                {
                    dbController.updateExistinghustatus(obj);
                    included = true;
                    Log.e("HuNoRecieved", obj);
                    break;
                }
            }
            mTriID.moveToPosition(-1);
            if (!included)
            {
                Log.e("NewHuNO", obj);


            }
        }

//        Toast.makeText(this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();


        }



    public void checkToDbfornewhu(String userTypedString)
    {

        Boolean check;
        check = dbController.CheckIsDataAlreadyInDBorNot(userTypedString);

        if (check == true)
        {
            Toast.makeText(this, "New HuNo Added Successfully", Toast.LENGTH_SHORT).show();
            Addhuno.setText("");
            Log.e("AddnewHu","Exist");
        }
        else
        {
            Addhunobuttondialog();
            Log.e("Addnewhu","DontExist");
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

                    dbController.newhustatus(Addhuno.getText().toString(),storename);

                    Toast.makeText(barcodefirstactivity.this, "New HuNo Added Successfully", Toast.LENGTH_SHORT).show();
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


    public void checkToDb(String userTypedString)
    {

       Boolean check;
       check = dbController.CheckIsDataAlreadyInDBorNot(userTypedString);

       if (check == true)
       {
           Log.e("Nishant","Exist");
       }
       else
       {
           clearbuttondialog();
           Log.e("Nishant","DontExist");
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

                    dbController.newhustatus(barcode.displayValue,storename);
                    Log.e("Barcodedialog",barcode.displayValue);

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

    public void Duplicatedialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("This Hu is already Scanned");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to Remove it");

        // Setting Icon to Dialog
        //     alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {

                   // dbController.newhustatus(barcode.displayValue,storename);
                    Log.e("Barcodedialog",barcode.displayValue);

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


    public void checkduplicatevalue(String hu)
    {
        for (String dup : list) {
            if (hu.matches(dup))
            {
                // Duplicatedialog();
                Toast.makeText(this, "Hu Already Scanned", Toast.LENGTH_SHORT).show();
                Log.e("add", "list");
            }
            else {
                list.add(barcode.displayValue);
                Log.e("Noadd", "list");
            }

        }


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


        private final ProgressDialog dialog = new ProgressDialog(barcodefirstactivity.this);
        DBController dbhelper;
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please Wait");
            this.dialog.show();
            dbhelper =   new DBController(barcodefirstactivity.this);
        }

        protected Boolean doInBackground(final String... args)
        {
            try {
                addListToDb();

                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing())
            {
                this.dialog.dismiss();
            }
            if (success) {
                Toast.makeText(barcodefirstactivity.this, "Saved successful!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(barcodefirstactivity.this, "Saved successful!", Toast.LENGTH_SHORT).show();

            }
        }
    }



    public class ExportRecievedCSV extends AsyncTask<String, Void, Boolean> {

        ArrayList<String>recieved=  new ArrayList<>();


        private final ProgressDialog dialog = new ProgressDialog(barcodefirstactivity.this);
        DBController dbhelper;
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting Csv Files");
            this.dialog.show();
            dbhelper = new    DBController(barcodefirstactivity.this);
        }

        protected Boolean doInBackground(final String... args)
        {
            recieved = dbhelper.getRecievedhuNo();
            if (recieved.size() ==0)
            {
                Log.e("excessdata","" + recieved);
                return false;
            }
            else {
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
            if (this.dialog.isShowing())
            {
                this.dialog.dismiss();
            }
            if (success) {
                Toast.makeText(barcodefirstactivity.this, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(barcodefirstactivity.this, "No Hu Recieved", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class ExportExcessCSV extends AsyncTask<String, Void, Boolean>
    {
        ArrayList<String>excess=  new ArrayList<>();
        private final ProgressDialog dialog = new ProgressDialog(barcodefirstactivity.this);
        DBController dbhelper;

        @Override
        protected void onPreExecute() {

            this.dialog.setMessage("Exporting CSV Files");
            this.dialog.show();
            dbhelper = new DBController(barcodefirstactivity.this);
        }

        protected Boolean doInBackground(final String... args)
        {
            excess = dbhelper.getExcessData();
            if (excess.size() ==0)
            {
                Log.e("excessdata","" + excess);
                return false;
            }
            else {

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
                Toast.makeText(barcodefirstactivity.this, " ExcessFile Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(barcodefirstactivity.this, "No Excess File", Toast.LENGTH_SHORT).show();
            }
        }


    }


    public class ExportShortCSV extends AsyncTask<String, Void, Boolean>
    {
        ArrayList<String>shortdata=  new ArrayList<>();
        private final ProgressDialog dialog = new ProgressDialog(barcodefirstactivity.this);
        DBController dbhelper;

        @Override
        protected void onPreExecute() {

            this.dialog.setMessage("Exporting CSV Files");
            this.dialog.show();
            dbhelper = new DBController(barcodefirstactivity.this);
        }

        protected Boolean doInBackground(final String... args)
        {
            shortdata = dbhelper.getShortData();
            if (shortdata.size() ==0)
            {
                Log.e("shortcsv","" + shortdata);
                return false;
            }
            else {

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
                Toast.makeText(barcodefirstactivity.this, "ShortFile Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(barcodefirstactivity.this, "No Short File", Toast.LENGTH_SHORT).show();
            }
        }


    }




    public class ExportRecievedUserCSV extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(barcodefirstactivity.this);
        ArrayList<String> recieved = new ArrayList<>();
        DBController dbhelper;

        @Override
        protected void onPreExecute() {

            this.dialog.setMessage("Exporting CSV Files");
            this.dialog.show();
            dbhelper = new DBController(barcodefirstactivity.this);
        }

        protected Boolean doInBackground(final String... args) {
            recieved = dbhelper.getRecievedhuNo();
//            if (recieved.size() == 0) {
//                Log.e("recieved", "" + recieved);
//                return false;
          //  } else {

                File exportDir = new File(Environment.getExternalStorageDirectory(), "/CSVFOLDER/");
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
                Toast.makeText(barcodefirstactivity.this, "Total Hu No File export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(barcodefirstactivity.this, "No File Export", Toast.LENGTH_SHORT).show();
            }
        }


    }




    public String datetime()
    {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Log.e("Time",formattedDate);
        return formattedDate;
    }


}







