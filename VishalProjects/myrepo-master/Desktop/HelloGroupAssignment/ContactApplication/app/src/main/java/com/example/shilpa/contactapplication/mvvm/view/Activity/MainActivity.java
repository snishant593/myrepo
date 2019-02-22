package com.example.shilpa.contactapplication.mvvm.view.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


import com.example.shilpa.contactapplication.R;
import com.example.shilpa.contactapplication.mvvm.model.ContactList;
import com.example.shilpa.contactapplication.mvvm.view.Fragment.AllContactFragment;
import com.example.shilpa.contactapplication.mvvm.view.Fragment.FavoriteFragment;
import com.example.shilpa.contactapplication.mvvm.view.adapter.ContactAdapter;
import com.example.shilpa.contactapplication.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.view.PagerAdapter.POSITION_NONE;


public class MainActivity extends AppCompatActivity implements Constants.ContactListClickListener {


    private TabLayout tabLayout;
    private ViewPager viewPager;
     private FloatingActionButton fab;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




           tabLayout=(TabLayout)findViewById(R.id.tabs);
            viewPager = (ViewPager) findViewById(R.id.viewpager);

          tabLayout.setupWithViewPager(viewPager);
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent addContactIntent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                    startActivity(addContactIntent);
                }

            });








    }

    @Override
    protected void onStart() {
     getAllContacts();

        super.onStart();
    }

    public  void getAllContacts() {

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED){

            setupViewPager(viewPager);


        } else {

            requestForLocationPermission();
        }
    }

    private void requestForLocationPermission()
    {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS))
        {
            setupViewPager(viewPager);
        }
        else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // getAllContacts();
                   setupViewPager(viewPager);
                    //        FetchContactanddisplay();
                }
                break;
        }
    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FavoriteFragment(), "Favorite");
        adapter.addFragment(new AllContactFragment(), "All");
        viewPager.setAdapter(adapter);
        viewPager.getAdapter().notifyDataSetChanged();


    }

    @Override
    public void onContactListClicked(ContactList contactList, String type) {

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

        @Override
        public int getItemPosition(Object object) {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE;
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
