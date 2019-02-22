package com.aequmindia.com.vishalmegamart.mvvm.View.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aequmindia.com.vishalmegamart.Common.DBController;
import com.aequmindia.com.vishalmegamart.R;
import com.aequmindia.com.vishalmegamart.mvvm.model.Datamodel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GateEntryPass extends AppCompatActivity {
    ArrayList<Datamodel> InvoiceList;
    DBController dbController = new DBController(this);
    String  storename;
    TextView Store,datetime;
    Button Exit,Next;
    EditText Gateentry,SealNo,SealNo2,kmreading;
    CheckBox broken1,broken2;
    String brokentext1,brokentext2,nobroken,filename;
    android.support.v7.app.ActionBar actionBar;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateentryfirst);
        Store = (TextView)findViewById(R.id.storename);
        Exit = (Button)findViewById(R.id.exit);
       Next = (Button)findViewById(R.id.next);
        datetime = (TextView)findViewById(R.id.gatetime);
        Gateentry = (EditText)findViewById(R.id.gatepass);
        SealNo = (EditText)findViewById(R.id.sealno);
        SealNo2 = (EditText)findViewById(R.id.sealno2);
        kmreading = (EditText)findViewById(R.id.kmreading);
        broken1 = (CheckBox)findViewById(R.id.brokenno1);
        broken2 = (CheckBox)findViewById(R.id.brokenno2);


        actionBar = getSupportActionBar();
      //  actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.ic_launchervishal);

        InvoiceList = dbController.getalldatainvoice();
        storename = InvoiceList.get(0).getStorename();
        Store.setText(String.valueOf(storename));

        datetime.setText(String.valueOf(datetime()));

        Bundle bundle1 = getIntent().getExtras();
        filename = bundle1.getString("FILENAME");
        Log.d("filename", filename);

        

        Next.setOnClickListener(new View.OnClickListener() {
      @Override
           public void onClick(View v) {

          if (Gateentry.getText().toString().matches("")||kmreading.getText().toString().matches("")||SealNo.getText().toString().matches(""))
          {
              Toast.makeText(GateEntryPass.this, "Please Filled All Field", Toast.LENGTH_SHORT).show();
              return;
          }

          checkboxvalue();
          dbController.Insertgateentry(Gateentry.getText().toString(),kmreading.getText().toString(),SealNo.getText().toString(),brokentext1,SealNo2.getText().toString(),brokentext2);
          dbController.insertfile(filename);

          //  Toast.makeText(GateEntryPass.this, "GATE ENTRY SUCCESSFULLY ADDED", Toast.LENGTH_SHORT).show();
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

              new ExportGATECSV().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


          } else {

              new ExportGATECSV().execute();



          }


          Bundle bundle = new Bundle();
          bundle.putString("FILENAME",filename);

          Intent intent = new Intent(getApplicationContext(),Gunactivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          intent.putExtras(bundle);
          startActivity(intent);

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
    public void onBackPressed() {

        Exitbuttondialog();

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


    public void checkboxvalue(){
        if (broken1.isChecked())
        {
            brokentext1 = "Yes";
        }
        else if (broken2.isChecked())
        {
            brokentext2 = "Yes";
        }
        else
        {
           brokentext1 = "";
           brokentext2 = "";
        }
    }



    public String datetime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Log.e("Time", formattedDate);
        return formattedDate;


    }


    public String datetimeforfile() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Log.e("Time", formattedDate);
        return formattedDate;


    }




    public class ExportGATECSV extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(GateEntryPass.this);
        ArrayList<String> shortdata = new ArrayList<>();
        DBController dbhelper;

        @Override
        protected void onPreExecute() {

            this.dialog.setMessage("Exporting CSV Files");
            this.dialog.show();
            dbhelper = new DBController(GateEntryPass.this);
        }

        protected Boolean doInBackground(final String... args) {
//            shortdata = dbhelper.getShortData();
//            if (shortdata.size() == 0) {
//                Log.e("gatecsv", "" + shortdata);
//                return false;
//            } else {

                File exportDir = new File(Environment.getExternalStorageDirectory(), "/VishalGrc/");
                Log.e("Nishant", exportDir.getPath());
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                File file = new File(exportDir, storename.concat("_").concat(datetimeforfile().concat("_").concat("GATE_EXIT_NO.csv")));
                try {

                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    Cursor curCSV = dbhelper.gatedata();
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
                Toast.makeText(GateEntryPass.this, "GateFile Export successful!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(GateEntryPass.this, "No Gate File", Toast.LENGTH_SHORT).show();
            }
        }


    }






}
