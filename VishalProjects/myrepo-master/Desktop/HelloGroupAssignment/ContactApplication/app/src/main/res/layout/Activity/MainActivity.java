package com.example.shilpa.conatctapp.mvvm.view.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shilpa.conatctapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    ListView listView ;
    ArrayList<ContactList> StoreContacts ;
    ContactAdapter adapter;
    Cursor cursor ;
    String name, phonenumber ;
    public  static final int RequestPermissionCode  = 1 ;
    private ActivityMainBinding binding;
    ArrayList<ContactList> contactData;
    BottomSheetBehavior SheetBehavior;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
     private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        try {




            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            binding.tabs.setupWithViewPager(viewPager);
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent addContactIntent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                    startActivity(addContactIntent);
                }

            });



            StoreContacts = new ArrayList<ContactList>();



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
     //   EnableRuntimePermission();
      //  isPermissionGranted();
        super.onStart();
    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale
                (com.example.shilpa.conatctapp.mvvm.view.Activity.MainActivity.this, Manifest.permission.READ_CONTACTS) &&
                ActivityCompat.shouldShowRequestPermissionRationale
                        (com.example.shilpa.conatctapp.mvvm.view.Activity.MainActivity.this, Manifest.permission.CALL_PHONE) && ( ActivityCompat.shouldShowRequestPermissionRationale
                (com.example.shilpa.conatctapp.mvvm.view.Activity.MainActivity.this, Manifest.permission.SEND_SMS)))
        {

            Toast.makeText(com.example.shilpa.conatctapp.mvvm.view.Activity.MainActivity.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(com.example.shilpa.conatctapp.mvvm.view.Activity.MainActivity.this,new String[]{
                    Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    FetchContactanddisplay();


                    Toast.makeText(com.example.shilpa.conatctapp.mvvm.view.Activity.MainActivity.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(com.example.shilpa.conatctapp.mvvm.view.Activity.MainActivity.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    public ArrayList<ContactList> GetContactsIntoArrayList() {
        contactData = new ArrayList<ContactList>();


        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.moveToFirst())

        {
            do {


                ContactList contactList = new ContactList();

                contactList.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                contactList.setPhoneno(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                contactList.setId(cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));

                // phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
               contactData.add(contactList);
                // StoreContacts.add(name + " "  + ":" + " " + phonenumber);

           /* HashMap<String,String> map=new HashMap<String,String>();
            map.put("name", name);
            map.put("number", phonenumber);
            contactData.add(map);*/
            } while (cursor.moveToNext());
        }




        return contactData;

    }

    public void FetchContactanddisplay(){
     StoreContacts = GetContactsIntoArrayList();
     if(adapter== null){
      adapter = new ContactAdapter(this, android.R.layout.simple_dropdown_item_1line,StoreContacts);
    //  binding.recyclerview.setAdapter(adapter);

      }
    }




    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FavoriteFragment(), "Favorite");
        adapter.addFragment(new AllContactFragment(), "All");
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
