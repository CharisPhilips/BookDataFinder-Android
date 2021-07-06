package com.bdf.bookDataFinder.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class ServiceUtils {

    public final static boolean isInternetOn(Context context) {
        boolean flag = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            flag = true;
        } else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            flag = false;
        }
        return flag;
    }
}
