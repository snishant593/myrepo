package com.example.shilpa.conatctapp.mvvm.model;


/**
 * Created by shilpa on 17/5/18.
 */

public class ContactList {
    String name;
    String phoneno;
    Integer id;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
