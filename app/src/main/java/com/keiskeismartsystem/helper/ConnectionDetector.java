package com.keiskeismartsystem.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zeta on 10/13/2015.
 */
public class ConnectionDetector {
    private Context _context;
    public ConnectionDetector(Context context){
        this._context = context;
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(_context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if(networkInfo != null){
                for (int i = 0; i< networkInfo.length; i++){
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
