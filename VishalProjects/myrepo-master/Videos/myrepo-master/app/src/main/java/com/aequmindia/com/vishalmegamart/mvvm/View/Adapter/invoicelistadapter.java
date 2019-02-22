package com.aequmindia.com.vishalmegamart.mvvm.View.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aequmindia.com.vishalmegamart.Common.Config;
import com.aequmindia.com.vishalmegamart.Common.DBController;
import com.aequmindia.com.vishalmegamart.R;
import com.aequmindia.com.vishalmegamart.mvvm.model.Datamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nishant on 17/5/18.
 */

public class invoicelistadapter extends RecyclerView.Adapter<invoicelistadapter.ViewHolder>{

    ArrayList<Datamodel>invoicelist;
    private LayoutInflater mInflater;
    private Context mContext;
    Bundle bundle;
    ArrayList<String> list = new ArrayList<String>();
    Set<String> set = new HashSet<String>();


//    @NonNull
//   private    OnItemCheckListener onItemCheckListener;
//    {
//
//    }
//
//    public interface OnItemCheckListener {
//        void onItemCheck(Datamodel item);
//        void onItemUncheck(Datamodel item);
//    }



    // data is passed into the constructor
    public invoicelistadapter(Context context,int layoutResourceId, ArrayList<Datamodel>invoicelist) {
        this.mInflater = LayoutInflater.from(context);
        this.invoicelist = invoicelist;
        this.mContext = context;
      //  this.onItemCheckListener = onItemCheckListener;

    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DBController dbController = new DBController(mContext);

        holder.invoiceno.setText(invoicelist.get(position).getInvoiceno());
        // holder.checkBox.setChecked(invoicelist.get(position).getChecked());
        // holder.checkBox.setTag(position);

        // holder.checkBox.setChecked(mSelectedItemsIds.get(position));



//
//            ((ViewHolder) holder).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                   // int pos = ViewHolder.getAdapterPosition();
//                    Datamodel currentItem = invoicelist.get(position);
//                    if (holder.checkBox.isChecked()) {
//                        onItemCheckListener.onItemCheck(currentItem);
//                        currentItem.setChecked(true);
//                        notifyDataSetChanged();
//
//                    } else {
//                        onItemCheckListener.onItemUncheck(currentItem);
//                        currentItem.setChecked(false);
//                        notifyDataSetChanged();
//
//                    }

//                    ((ViewHolder) holder).checkBox.setChecked(
//                            !((ViewHolder) holder).checkBox.isChecked());
//                    if (((ViewHolder) holder).checkBox.isChecked()) {
//                        onItemClick.onItemCheck(currentItem);
//
//                    } else {
//                        onItemClick.onItemUncheck(currentItem);
//
//                    }
//                }


//                }
//            });






        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

             // checkCheckBox(position, !mSelectedItemsIds.get(position));


                if (isChecked)
                {
                    list.add(holder.invoiceno.getText().toString());
                    Log.e("value","" + list);
                  //  Toast.makeText(mContext,"checked",Toast.LENGTH_LONG);


                    totalhucount();

                }
               /* else if(!isChecked){
                    Toast.makeText(mContext,"hhhhhh",Toast.LENGTH_LONG);

                }*/


                else {


                    list.remove(holder.invoiceno.getText().toString());
                    totalhucount();
                    dbController.Hustatusinvoiceremove(holder.invoiceno.getText().toString());
                    Log.e("remove value","" + list);

                }
           }
        });
    }

    @Override
    public int getItemCount() {
        return invoicelist.size();
    }


     public ArrayList<String> getList()
     {
     return list;
     }




    /**
//     * Check the Checkbox if not checked
//     **/
//    public void checkCheckBox(int position, boolean value) {
//        if (value)
//            mSelectedItemsIds.put(position, false);
//        else
//            mSelectedItemsIds.delete(position);
//
//        notifyDataSetChanged();
//    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView invoiceno;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            invoiceno = itemView.findViewById(R.id.invoiceno);
            checkBox = itemView.findViewById(R.id.checkBox);
        }


        // allows clicks events to be caught

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }


    }








    public int totalhucount() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
      //  username = sharedPreferences.getString(Config.SHARED_PREF_USERNAME, "");
        DBController dbController = new DBController(mContext);
        int result = 0;
        for (String obj : list) {
            boolean included = false;
            Cursor mTriID = dbController.mGetInvoiceNo();

            while (mTriID.moveToNext()) {

                String text1 = String.valueOf(mTriID.getLong(mTriID.getColumnIndex("Invoice_No")));
               if (obj.equals(text1))
                {
                    Cursor res  = dbController.mGetTotalHucount(obj);
                    if (res.moveToNext()) {
                        result += (res.getInt(res.getColumnIndex("HU_No")));
                    }
                    included = true;
                    dbController.Hustatusinvoice(obj);
                    Log.e("Data", "Found");
                    break;
                }
            }
            mTriID.moveToPosition(-1);
            if (!included) {
                Log.e("Data", "Notfound");
            }
        }


        SharedPreferences.Editor editor = sharedPreferences.edit();
//        String[] mStringArray = new String[list.size()];
//        list.toArray(mStringArray);



        //Adding values to editor
        editor.putString(Config.SHARED_PREF_USERNAME, String.valueOf(result));
       // editor.putString(Config.SHARED_PREF_MOBILE,mobileno);
        editor.apply();



        Log.e("ValueData",String.valueOf(result) );
       return result;



   }





}
