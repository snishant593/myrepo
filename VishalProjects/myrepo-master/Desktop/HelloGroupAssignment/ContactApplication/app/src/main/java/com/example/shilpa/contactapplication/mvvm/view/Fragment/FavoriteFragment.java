package com.example.shilpa.contactapplication.mvvm.view.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
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


import java.util.ArrayList;

import com.example.shilpa.contactapplication.R;
import com.example.shilpa.contactapplication.callbacks.OnItemClickListener;
import com.example.shilpa.contactapplication.mvvm.model.ContactList;
import com.example.shilpa.contactapplication.mvvm.view.adapter.ContactAdapter;
import com.example.shilpa.contactapplication.mvvm.view.adapter.FavoriteAdapter;

/**
 * Created by shilpa on 20/5/18.
 */

public class FavoriteFragment  extends Fragment {

    RecyclerView recyclerView;

    ArrayList<ContactList> contactData;

    Cursor phones;





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



        return rootView;
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    public ArrayList<ContactList> GetContactsIntoArrayList() {



           contactData = new ArrayList<ContactList>();


           phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                   null, "starred=?",
                   new String[]{"1"}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
           if (phones.moveToFirst())

           {
               do {


                   ContactList contactList = new ContactList();

                   // contactList.setName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                   // contactList.setPhoneno(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                   //  contactList.setId(phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));

                   contactList.setName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                   contactList.setPhoneno(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                   contactList.setId(phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
                   contactList.setImage(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));


                   // ContactList contactList = new ContactList(phonenumber,name,id);
                   contactData.add(contactList);                // StoreContacts.add(name + " "  + ":" + " " + phonenumber);


               } while (phones.moveToNext());
           }


           return contactData;


   }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity()==null)
            return;


        recyclerView.setAdapter(new ContactAdapter(
                getActivity(), GetContactsIntoArrayList(),
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, View view, int position) {
                    }
                }, null));
    }

}
