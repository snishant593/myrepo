package com.aequmindia.com.vishalmegamart.mvvm.View.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.aequmindia.com.vishalmegamart.Common.Config;
import com.aequmindia.com.vishalmegamart.Common.DBController;
import com.aequmindia.com.vishalmegamart.Common.FileUtils;
import com.aequmindia.com.vishalmegamart.Common.PermissionsUtil;
import com.aequmindia.com.vishalmegamart.R;
import com.aequmindia.com.vishalmegamart.mvvm.View.Adapter.invoicelistadapter;
import com.aequmindia.com.vishalmegamart.mvvm.model.Datamodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class NavigationActivity extends Activity
        implements NavigationView.OnNavigationItemSelectedListener {
    Button Downloaddata,next,continueold;
    ActionBar actionBar;
    ArrayList<Datamodel> InvoiceList;
    public static final int requestcode = 1;
    RecyclerView recyclerView;
    invoicelistadapter invoicelistadapter;
    File file;
    String filename;
    ArrayList<String> checkrecieved = new ArrayList<>();
    ArrayList<String> checkshort = new ArrayList<>();
    ArrayList<String> checkexcess = new ArrayList<>();
    DBController controller;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        controller = new DBController(getApplicationContext());


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        Downloaddata = (Button) findViewById(R.id.btndownload);
        next=(Button)findViewById(R.id.next);
        continueold = (Button)findViewById(R.id.continuedata);

        checkPermissions();
//        Uploaddata=(Button)findViewById(R.id.button10);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.isVerticalScrollBarEnabled();
        toolbar.setTitle("Store Gate Entry Cum Grc");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setLogo(R.drawable.ic_launchervishal);



//        actionBar = getSupportActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setIcon(R.drawable.ic_launchervishal);


        continueold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkrecieved =controller.getShortconitnueData();
                checkshort = controller.getconitnueDataexcesstable();


                if (checkrecieved.size()==0 && checkshort.size()==0)
                {
                    Toast.makeText(NavigationActivity.this, "No Old Data", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(),continueoldmobile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);


            }
        });




        Downloaddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
//                fileintent.setType("text/csv");
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

            //  Toast.makeText(NavigationActivity.this, "Data DownLoaded", Toast.LENGTH_SHORT).show();

        });


        next.setOnClickListener(new View.OnClickListener() {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);


            @Override
            public void onClick(View view)
            {
                if (invoicelistadapter == null) {
                     Toast.makeText(NavigationActivity.this, "Please Import Csv", Toast.LENGTH_SHORT).show();
                     return;
                  }
                if (invoicelistadapter.getList().size() == 0)
                {

                     Toast.makeText(NavigationActivity.this, "Please Select Invoice", Toast.LENGTH_SHORT).show();
                     return;
                }

                    Bundle bundle = new Bundle();
                    bundle.putString("FILENAME",filename);
                    Intent intent = new Intent(getApplicationContext(), GateEntryPass.class);
                    intent.putExtras(bundle);
                    startActivity(intent);




                SharedPreferences.Editor editor = sharedPreferences.edit();

                //Adding values to editor
                editor.putString(Config.SHARED_PREF_FILE, filename);
                editor.apply();



                Log.e("Filename",filename);




            }
        });




        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {






                    return;




                } else {

                    Bundle bundle = new Bundle();
                    bundle.putString("FILENAME",filename);

                    Intent intent = new Intent(getApplicationContext(), GateEntryPass.class);
                    intent.putExtras(bundle);
                    startActivity(intent);


                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


            }
        });*/

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
    }





    @SuppressLint("ResourceAsColor")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null)
            return;
        switch (requestCode) {
            case requestcode:

                controller = new DBController(getApplicationContext());
                SQLiteDatabase db = controller.getWritableDatabase();
                String tableName = "retail_prod";
                String tableName2 = "retail_hu_excess";
                String tableName3 = "retail_gate";
                String tableName4 = "retail_filename";
                db.execSQL("delete from " + tableName);
                db.execSQL("delete from " + tableName2);
                db.execSQL("delete from " + tableName3);
                db.execSQL("delete from " + tableName4);

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

                                String[] str = line.split(",", 4);  // defining 4 columns with null or blank field //values acceptance
                                //Id, Company,Name,Price  Id INTEGER PRIMARY KEY, Store_Nm TEXT,Prod_Desc TEXT,Invoice_No INTEGER,HU_No INTEGER,HU_Status TEXT,Final_Status TEXT
                                //  String Id = str[0].toString();
                                // for mobile concat @ in huno
                                String StoreName = str[0].toString();
                                String ProdDescp = str[1].toString();
                                String Invoice = str[2].toString();
                                String HuNo = str[3].toString().concat("@");

                                // contentValues.put("Id", Id);
                                contentValues.put("Store_Nm", StoreName);
                                contentValues.put("Prod_Desc", ProdDescp);
                                contentValues.put("Invoice_No", Invoice);
                                contentValues.put("HU_No", HuNo);



                                db.insert(tableName, null, contentValues);

                                System.out.println("LLLLL: " + line);
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

        controller.deleteFirstRow();
        InvoiceList = controller.getalldatainvoice();
        if (InvoiceList.size() != 0) {
            invoicelistadapter = new invoicelistadapter(NavigationActivity.this, android.R.layout.simple_dropdown_item_1line, InvoiceList);

            recyclerView.setAdapter(invoicelistadapter);
            // invoicelistadapter.itemRemoved(0);

        }

        Downloaddata.setEnabled(false);
        Downloaddata.setBackgroundResource(R.color.transperent);


       // datetime();
    }

    public void Exitbuttondialog() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

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


    @Override
    public void onBackPressed() {

       Exitbuttondialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.uploadata)
        {



//            if (filename == null)
//            {
//                Toast.makeText(this, "No Data To Export", Toast.LENGTH_SHORT).show();
//                return false ;
//            }

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//
//                new ExportDatabaseCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                new ExportCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//            } else {
//
//                new ExportDatabaseCSVTask().execute();
//            }



        }
       // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    public String datetime()
//    {
//        Calendar c = Calendar.getInstance();
//        System.out.println("Current time => "+c.getTime());
//
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String formattedDate = df.format(c.getTime());
//        // formattedDate have current date/time
//       // Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
//        Log.e("Time",formattedDate);
//        return formattedDate;
//    }



//    public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {
//
//
//        private final ProgressDialog dialog = new ProgressDialog(NavigationActivity.this);
//        DBController dbhelper;
//        @Override
//        protected void onPreExecute() {
//            this.dialog.setMessage("Exporting Csv Files");
//            this.dialog.show();
//            dbhelper = new    DBController(NavigationActivity.this);
//        }
//
//        protected Boolean doInBackground(final String... args)
//        {
//            String storename = InvoiceList.get(0).getStorename();
//            File exportDir = new File(Environment.getExternalStorageDirectory(), "/CSVFOLDER/");
//            Log.e("Nishant",exportDir.getAbsolutePath());
//            if (!exportDir.exists()) { exportDir.mkdirs(); }
//
//            File file = new File(exportDir, "hh".concat("_").concat(datetime().concat("_").concat(filename)));
//            try {
//
//                file.createNewFile();
//                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
//                Cursor curCSV = dbhelper.raw();
//                csvWrite.writeNext(curCSV.getColumnNames());
//                while(curCSV.moveToNext()) {
//                    String arrStr[]=null;
//                    String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
//                    for(int i=0;i<curCSV.getColumnNames().length;i++)
//                    {
//                        mySecondStringArray[i] =curCSV.getString(i);
//                    }
//                    csvWrite.writeNext(mySecondStringArray);
//                }
//                csvWrite.close();
//                curCSV.close();
//                return true;
//            } catch (IOException e) {
//                return false;
//            }
//        }
//
//        protected void onPostExecute(final Boolean success) {
//            if (this.dialog.isShowing())
//            {
//                this.dialog.dismiss();
//            }
//            if (success) {
//                Toast.makeText(NavigationActivity.this, "Export successful!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(NavigationActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//
//    public class ExportCSVTask extends AsyncTask<String, Void, Boolean> {
//
//
//        private final ProgressDialog dialog = new ProgressDialog(NavigationActivity.this);
//        DBController dbhelper;
//        @Override
//        protected void onPreExecute() {
//            this.dialog.setMessage("Exporting CSV Files");
//            this.dialog.show();
//            dbhelper = new    DBController(NavigationActivity.this);
//        }
//
//        protected Boolean doInBackground(final String... args)
//        {
//            String storename = InvoiceList.get(0).getStorename();
//            File exportDir = new File(Environment.getExternalStorageDirectory(), "/CSVFOLDER/");
//            Log.e("Nishant",exportDir.getAbsolutePath());
//            if (!exportDir.exists()) { exportDir.mkdirs(); }
//
//            File file = new File(exportDir, "gg".concat("_").concat("EXCESS").concat(datetime().concat("_").concat(filename)));
//            try {
//
//                file.createNewFile();
//                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
//                Cursor curCSV = dbhelper.second();
//                csvWrite.writeNext(curCSV.getColumnNames());
//                while(curCSV.moveToNext()) {
//                    String arrStr[]=null;
//                    String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
//                    for(int i=0;i<curCSV.getColumnNames().length;i++)
//                    {
//                        mySecondStringArray[i] =curCSV.getString(i);
//                    }
//                    csvWrite.writeNext(mySecondStringArray);
//                }
//                csvWrite.close();
//                curCSV.close();
//                return true;
//            } catch (IOException e) {
//                return false;
//            }
//        }
//
//        protected void onPostExecute(final Boolean success) {
//            if (this.dialog.isShowing())
//            {
//                this.dialog.dismiss();
//            }
//            if (success) {
//                Toast.makeText(NavigationActivity.this, "Export successful!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(NavigationActivity.this, "Export failed", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


    private void checkPermissions(){
        PermissionsUtil.askPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionsUtil.PERMISSION_ALL: {

                if (grantResults.length > 0) {

                    List<Integer> indexesOfPermissionsNeededToShow = new ArrayList<>();

                    for(int i = 0; i < permissions.length; ++i) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            indexesOfPermissionsNeededToShow.add(i);
                        }
                    }

                    int size = indexesOfPermissionsNeededToShow.size();
                    if(size != 0) {
                        int i = 0;
                        boolean isPermissionGranted = true;

                        while(i < size && isPermissionGranted) {
                            isPermissionGranted = grantResults[indexesOfPermissionsNeededToShow.get(i)]
                                    == PackageManager.PERMISSION_GRANTED;
                            i++;
                        }

                        if(!isPermissionGranted) {

                            showDialogNotCancelable("Permissions mandatory",
                                    "All the permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            checkPermissions();
                                        }
                                    });
                        }
                    }
                }
            }
        }
    }

    private void showDialogNotCancelable(String title, String message,
                                         DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setCancelable(false)
                .create()
                .show();
    }



    private void ShareFile() {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "/codesss/");
        String fileName = "person.csv";
        File sharingGifFile = new File(exportDir, fileName);
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("application/csv");
        Uri uri = Uri.fromFile(sharingGifFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share CSV"));
    }




}



