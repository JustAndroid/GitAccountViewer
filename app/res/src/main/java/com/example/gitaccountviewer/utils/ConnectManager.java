package com.example.gitaccountviewer.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Николай on 03.08.2016.
 */
public class ConnectManager {
    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) new Application().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            return true;
        }
        return false;
    }
}
