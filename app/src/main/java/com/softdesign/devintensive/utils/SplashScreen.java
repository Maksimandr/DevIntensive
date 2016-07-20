package com.softdesign.devintensive.utils;


import android.util.Log;

import com.softdesign.devintensive.ui.activities.BaseActivity;


public class SplashScreen extends BaseActivity {

    private static final String TAG = ConstantManager.TAG_PREFIX + "SplashScreen";
    public final int code;

    public SplashScreen(int code) {
        Log.d(TAG, "Constructor");

        this.code = code;
    }

}
