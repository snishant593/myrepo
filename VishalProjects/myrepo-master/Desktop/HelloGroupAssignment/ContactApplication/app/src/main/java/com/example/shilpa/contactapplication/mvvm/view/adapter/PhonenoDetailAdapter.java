package com.example.shilpa.contactapplication.mvvm.view.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.shilpa.contactapplication.R;
import com.example.shilpa.contactapplication.mvvm.model.PhoneNoDetail;
import com.example.shilpa.contactapplication.mvvm.view.Fragment.ContactDetailFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shilpa on 19/5/18.
 */

public class PhonenoDetailAdapter extends RecyclerView.Adapter<PhonenoDetailAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    Context context;
    private List<PhoneNoDetail> phoneNoDetailList;
    ContactDetailFragment fragment;
    String phoneNo;


    public PhonenoDetailAdapter(Context context, int layoutInflater, ArrayList<PhoneNoDetail> phoneNoDetailList) {

        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.phoneNoDetailList = phoneNoDetailList;
    }

    public void add(int position, PhoneNoDetail contactList) {
        phoneNoDetailList.add(position, contactList);
        notifyItemInserted(position);

    }

    public void setContactlist(List<PhoneNoDetail> phoneNoDetailList) {
        this.phoneNoDetailList = phoneNoDetailList;
    }

    public List<PhoneNoDetail> getPhoneNoDetailList() {
        return phoneNoDetailList;
    }

    @Override
    public PhonenoDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phoneno_detail_listview, parent, false);
        final PhonenoDetailAdapter.ViewHolder vh = new PhonenoDetailAdapter.ViewHolder(view);

        return vh;


    }

    @Override
    public void onBindViewHolder(final PhonenoDetailAdapter.ViewHolder holder, final int position) {
        holder.phoneno.setText(phoneNoDetailList.get(position).getPhoneno());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                phoneNo = phoneNoDetailList.get(position).getPhoneno();
                Log.e("Phn", phoneNo);


                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phoneNo, null));
                   context.startActivity(intent);
                } else {
                    Toast.makeText(context, "You don't assign permission.", Toast.LENGTH_SHORT).show();
                }
            }


        });

        holder.smsimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNo, null)));
            }
        });


        // holder.txtName.setText(phoneNoDetailList.get(position).getName());


      /*  holder.mainlayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                com.example.shilpa.conatctapp.mvvm.view.Fragment myFragment = new ContactDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("phoneno", phoneno);
                // set MyFragment Arguments
                myFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container_login, myFragment).addToBackStack(null).commit();



              *//* Intent intent = new Intent(activity, demo.class);
                activity.startActivity(intent);*//*

            }
        });
*/
    }


    @Override
    public int getItemCount() {
        return phoneNoDetailList.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, phoneno;
        ImageView imageView,smsimageview;

        public ViewHolder(View itemView) {
            super(itemView.getRootView());
            phoneno = (TextView) itemView.findViewById(R.id.phoneno);
            imageView = (ImageView) itemView.findViewById(R.id.callimageview);
            smsimageview=(ImageView)itemView.findViewById(R.id.smsimageview);
            // txtName = (TextView) itemView.findViewById(R.id.name);
            // mainlayout = (ConstraintLayout) itemView.findViewById(R.id.mainlayout);


        }

    }

}


