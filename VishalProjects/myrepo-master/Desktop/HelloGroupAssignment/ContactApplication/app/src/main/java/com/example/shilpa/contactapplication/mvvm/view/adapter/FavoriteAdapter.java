package com.example.shilpa.contactapplication.mvvm.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shilpa.contactapplication.BR;
import com.example.shilpa.contactapplication.R;
import com.example.shilpa.contactapplication.callbacks.OnItemClickListener;
import com.example.shilpa.contactapplication.mvvm.model.ContactList;
import com.example.shilpa.contactapplication.mvvm.viewmodel.ContactListViewmodel;
import com.example.shilpa.contactapplication.util.Constants;
import com.example.shilpa.contactapplication.util.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shilpa on 25/5/18.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

private final LayoutInflater layoutInflater;
private List<ContactList> contactLists;
private final OnItemClickListener onItemClickListener;
private final Constants.ContactListClickListener contactListClickListener;
        Context context;




public FavoriteAdapter(Context context,ArrayList<ContactList> contactLists,
        OnItemClickListener onItemClickListener,Constants.ContactListClickListener listener) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.contactLists = contactLists;
        this.onItemClickListener = onItemClickListener;
        this.contactListClickListener=listener;
        this.context = context;


        }

@Override
public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.favourite_detail_row, parent, false);

        return new RecyclerViewHolder<>(binding);



        }

@Override
public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecyclerViewHolder) {
        ContactList contactList = contactLists.get(position);
final Integer ID = contactLists.get(position).getid();

final RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
        viewHolder.getBinding().setVariable(BR.contactlistviewmodel,new ContactListViewmodel(contactList));
        viewHolder.getBinding().executePendingBindings();
        viewHolder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
     @Override
       public void onClick(View v) {
        int position = viewHolder.getAdapterPosition();
        onItemClickListener.onItemClick(contactLists.get(position), v, position);

        }
        });
        }


/*

        //get first letter of each String item
        String firstLetter = String.valueOf(contactLists.get(position).getName().charAt(0));

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(position);
        //int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

*/




        }



@Override
public int getItemCount() {
        return contactLists.size();
        }

@Override
public int getItemViewType(int position) {
        return contactLists.get(position).getViewType();
        }

public void add(int position, ContactList contactList) {
        contactLists.add(position, contactList);
        notifyDataSetChanged();

        }






        }
