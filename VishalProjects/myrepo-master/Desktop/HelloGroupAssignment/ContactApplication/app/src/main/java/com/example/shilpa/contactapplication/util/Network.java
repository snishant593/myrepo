package com.example.shilpa.contactapplication.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by nitesh on 13/8/17.
 */

public class Network {

    /**
     * Check Network is available or not
     *
     * @return True if connected, False otherwise.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo != null) return networkInfo.isConnected();

        return false;
    }

}
