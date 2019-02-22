package com.example.shilpa.contactapplication.mvvm.view.Fragment;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.shilpa.contactapplication.R;
import com.example.shilpa.contactapplication.mvvm.model.PhoneNoDetail;
import com.example.shilpa.contactapplication.mvvm.view.adapter.PhonenoDetailAdapter;

import java.util.ArrayList;



/**
 * Created by shilpa on 17/5/18.
 */

public class ContactDetailFragment  extends Fragment {

    Activity context;

    TextView textView, phone;
    PhonenoDetailAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<PhoneNoDetail>phoneNoDetailArrayList;
    Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.contact_detail_fragment, container, false);

        textView = (TextView) rootView.findViewById(R.id.textview);
        phone = (TextView) rootView.findViewById(R.id.phoneno);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        phoneNoDetailArrayList = new ArrayList<PhoneNoDetail>();

        toolbar=(Toolbar)rootView.findViewById(R.id.toolbar);

        /*final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout)rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout)rootView.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle("");
                    isShow = false;
                }
            }
        });*/

        // Inflate the layout for this fragment
        Bundle arguments = getArguments();

        if (arguments != null) {
            String name = arguments.getString("name");
            String phoneno = arguments.getString("phoneno");
            textView.setText(name);

           /* HashMap<String, String> meMap = new HashMap<String, String>();
            meMap.put("name", name);
            meMap.put("phoneno", phoneno);*/
            PhoneNoDetail phoneNoDetail = new PhoneNoDetail();

            phoneNoDetail.setPhoneno(phoneno);
            phoneNoDetail.setName(name);



            if (adapter == null) {
                phoneNoDetailArrayList.add(phoneNoDetail);

                adapter = new PhonenoDetailAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, phoneNoDetailArrayList);
                recyclerView.setAdapter(adapter);
//                textView.setText(name);
//                phone.setText(phoneno);
            }

        }
            return rootView;


        }



}









