package com.codepath.apps.basictwitter.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {
	public static Boolean isNetworkAvailable(Context c) {
		ConnectivityManager connectivityManager
			= (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public static Boolean isOnline(String site) {
	    try {
	    	String pingSiteUrl = site != null ? site : "www.google.com";
	        Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 " + pingSiteUrl);
	        int returnVal = p1.waitFor();
	        boolean reachable = (returnVal==0);
	        return reachable;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
}
