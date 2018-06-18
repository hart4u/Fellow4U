package com.primarynet.fellow4u;

/**
 * Created by Hart4U on 2018-06-18.
 */


import android.content.Context;
import android.util.Log;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String TAG_TOKEN = "tagtoken";
    private static final String TAG_NOSOUND = "tagNoSound";
    private static final String TAG_NOVIBRATE = "tagNoVibrate";
    private static final String TAG_BADGE_COUNT = "tagBadgeCount";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        //Log.i("token:", token);
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    public boolean saveConfigNoSound(Boolean bNoSound){
        //Log.i("bNoSound:", bNoSound);
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TAG_NOSOUND, bNoSound);
        editor.apply();
        return true;
    }

    public boolean saveConfigNoVibrate(Boolean bNoVibrate){
        //Log.i("bNoSound:", bNoSound);
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TAG_NOVIBRATE, bNoVibrate);
        editor.apply();
        return true;
    }

    public boolean saveBadgeCount(Integer nCount){
        //Log.i("bNoSound:", bNoSound);
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(TAG_BADGE_COUNT, nCount);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Log.i("GET_TOKEN:", sharedPreferences.getString(TAG_TOKEN, null));

        return  sharedPreferences.getString(TAG_TOKEN, null);
    }

    public boolean getConfigNoSound(){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Log.i("GET_TOKEN:", sharedPreferences.getString(TAG_TOKEN, null));

        return  sharedPreferences.getBoolean(TAG_NOSOUND, true);

    }

    public boolean getConfigNoVibrate(){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Log.i("GET_TOKEN:", sharedPreferences.getString(TAG_TOKEN, null));

        return  sharedPreferences.getBoolean(TAG_NOVIBRATE, true);

    }

    public int getBadgeCount(){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Log.i("GET_TOKEN:", sharedPreferences.getString(TAG_TOKEN, null));

        return  sharedPreferences.getInt(TAG_BADGE_COUNT, 0);

    }



}

