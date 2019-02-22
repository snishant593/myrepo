package com.example.shilpa.contactapplication.mvvm.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shilpa on 17/5/18.
 */

public class ContactList {



    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("phoneno")
    @Expose
    private String phoneno;



    private  String contact_id;

    private  String image;

    private int viewType;

    public ContactList() {
      //  this.viewType = viewType;
    }



    public String getImage() {
        return image;
    }

    public String setImage(String image) {
        this.image = image;
        return image;
    }

    public int getViewType() {
        return viewType;
    }


    public Integer getid()
    {
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

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

}
