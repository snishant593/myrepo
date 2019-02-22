package com.example.shilpa.contactapplication.mvvm.viewmodel;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.widget.ImageView;

import com.example.shilpa.contactapplication.mvvm.model.ContactList;

/**
 * Created by shilpa on 24/5/18.
 */

public class ContactListViewmodel {
    private ContactList contactList;

    public ContactListViewmodel(ContactList contactList) {
        this();
        this.contactList = contactList;
    }

    private ContactListViewmodel() {
    }

    public Integer getid() {
        return contactList.getid();

    }

    public String getPhoneno() {

        return contactList.getPhoneno();
    }

    public String getName() {

        return contactList.getName();
    }


    public String getImage() {

        return String.valueOf(contactList.getImage().charAt(0));
    }

    public String getcontactid() {

        return contactList.getContact_id();
    }


}
