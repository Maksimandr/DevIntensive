package com.softdesign.devintensive.utils;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DevintensiveAplication extends Application {

    public static SharedPreferences sSharedPreferences;
    private static DevintensiveAplication sContext;

    public static Context getContext() {
        return sContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

}
