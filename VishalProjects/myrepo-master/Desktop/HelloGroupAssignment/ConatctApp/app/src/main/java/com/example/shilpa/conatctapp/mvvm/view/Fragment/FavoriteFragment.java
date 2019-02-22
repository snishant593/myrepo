package com.example.shilpa.conatctapp.mvvm.view.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shilpa.conatctapp.R;

import java.util.ArrayList;

import com.example.shilpa.conatctapp.mvvm.model.ContactList;

/**
 * Created by shilpa on 20/5/18.
 */

public class FavoriteFragment  extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ContactList> StoreContacts ;
    ArrayList<ContactList> contactData;
   // Cursor cursor ;
    ContactAdapter adapter;
    Cursor phones;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    Context context;




    public FavoriteFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        StoreContacts = new ArrayList<ContactList>();
        getAllContacts();



       // FetchContactanddisplay();

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onStart() {
     //EnableRuntimePermission();
        super.onStart();
    }


    public  void getAllContacts() {

        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED){

           // phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
            FetchContactanddisplay();

        } else {

            requestForLocationPermission();
        }
    }

    private void requestForLocationPermission()
    {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS))
        {
        }
        else {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
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
                    GetContactsIntoArrayList();
           //        FetchContactanddisplay();
                }
                break;
        }
    }

//    public void EnableRuntimePermission()
//    {
//
//            // The permission is NOT already granted.
//            // Check if the user has been asked about this permission already and denied
//            // it. If so, we want to give more explanation about why the permission is needed.
//
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale
//                    (getActivity(), Manifest.permission.READ_CONTACTS))
////                ActivityCompat.shouldShowRequestPermissionRationale
////                        (getActivity(), Manifest.permission.CALL_PHONE) && ( ActivityCompat.shouldShowRequestPermissionRationale
////                (getActivity(), Manifest.permission.SEND_SMS)))
//            {
//
//                Toast.makeText(getActivity(), "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
//
//            }
//
//        else {
//
//            ActivityCompat.requestPermissions(getActivity(),new String[]{
//                    Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS}, RequestPermissionCode);
//
//        }
//        return;
//    }
//    @Override
//    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
//
//        switch (RC) {
//
//            case RequestPermissionCode:
//
//                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
//
//                  GetContactsIntoArrayList();
//                  FetchContactanddisplay();
//
//
//                    Toast.makeText(getActivity(),"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
//
//                } else {
//
//                    Toast.makeText(getActivity(),"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
//
//                }
//                break;
//        }
//    }

    public ArrayList<ContactList> GetContactsIntoArrayList()
    {
        contactData = new ArrayList<ContactList>();


        phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
             null, "starred=?",
                new String[] {"1"}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (phones.moveToFirst())

        {
            do {


                ContactList contactList = new ContactList();

                contactList.setName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                contactList.setPhoneno(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                contactList.setId(phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));

                // phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactData.add(contactList);
                // StoreContacts.add(name + " "  + ":" + " " + phonenumber);

           /* HashMap<String,String> map=new HashMap<String,String>();
            map.put("name", name);
            map.put("number", phonenumber);
            contactData.add(map);*/
            } while (phones.moveToNext());
        }




        return contactData;

    }

    public void FetchContactanddisplay() {
        try {


          //  StoreContacts = GetContactsIntoArrayList();
            if (adapter == null) {
                adapter = new ContactAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, GetContactsIntoArrayList());
                recyclerView.setAdapter(adapter);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
