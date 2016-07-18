package com.softdesign.devintensive.data.network.req;


import android.util.Log;

import com.softdesign.devintensive.utils.ConstantManager;

public class UserLoginReq {

    private static final String TAG = ConstantManager.TAG_PREFIX + "UserLoginReq ";

    private String email;
    private String password;

    public UserLoginReq(String email, String password) {
        Log.d(TAG, "Constructor");

        this.email = email;
        this.password = password;
    }
}
