package com.example.shilpa.conatctapp.mvvm.view.Adapter;


import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.shilpa.conatctapp.R;

import java.util.ArrayList;
import java.util.List;

import com.example.shilpa.conatctapp.mvvm.view.Fragment.ContactDetailFragment;
import com.example.shilpa.conatctapp.mvvm.model.ContactList;

/**
 * Created by shilpa on 17/5/18.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    Context context;
    private List<ContactList> contactlist;
    ContactDetailFragment fragment;


    public ContactAdapter(Context context, int layoutInflater, ArrayList<ContactList> contactList) {

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.contactlist = contactList;
        this.context = context;
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_items_listview, parent, false);
        final ContactAdapter.ViewHolder vh = new ContactAdapter.ViewHolder(view);


        return vh;


    }

    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder holder, final int position) {
        holder.txtName.setText(contactlist.get(position).getName());
        holder.phoneno.setText(contactlist.get(position).getPhoneno());
        final String name = contactlist.get(position).getName();
        final String phoneno = contactlist.get(position).getPhoneno();
        final Integer ID = contactlist.get(position).getId();

        holder.mainlayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(ID));
                intent.setData(uri);
                context.startActivity(intent);
                /*com.example.shilpa.conatctapp.mvvm.view.Fragment myFragment = new ContactDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name",name);
                bundle.putString("phoneno",phoneno);
                // set MyFragment Arguments
                myFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container_login, myFragment).addToBackStack(null).commit();
*/


              /* Intent intent = new Intent(activity, demo.class);
                activity.startActivity(intent);*/

            }
        });


        //get first letter of each String item
        String firstLetter = String.valueOf(contactlist.get(position).getName().charAt(0));

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getColor(position);
        //int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

        holder.imageView.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return contactlist.size();

    }

    public void add(int position, ContactList contactList) {
        contactlist.add(position, contactList);
        notifyItemInserted(position);

    }

    public void setContactlist(List<ContactList> contactlist) {
        this.contactlist = contactlist;
    }

    public List<ContactList> getContactlist() {
        return contactlist;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, phoneno;
        ConstraintLayout mainlayout;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView.getRootView());
            phoneno = (TextView) itemView.findViewById(R.id.phoneno);
            txtName = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.circleView);

            mainlayout = (ConstraintLayout) itemView.findViewById(R.id.mainlayout);


        }

    }

}
