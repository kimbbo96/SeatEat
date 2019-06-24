package com.example.myapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckConnection {



    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
       if (networkInfo != null && networkInfo.isConnected()){
           System.out.println("sss"+networkInfo.getDetailedState().toString());
           System.out.println("sss"+networkInfo.getExtraInfo());


           return  true;
       }
       else return false;
    }

}
