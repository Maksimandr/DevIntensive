package com.softdesign.devintensive.data.managers;


import android.util.Log;

import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.ConstantManager;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DataManager {

    private static final String TAG = ConstantManager.TAG_PREFIX + "DataManager";

    private static DataManager INSTANCE = null;

    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    public DataManager() {
        Log.d(TAG, "Constructor");

        this.mPreferencesManager = new PreferencesManager();
        this.mRestService = ServiceGenerator.createService(RestService.class);
    }

    public static DataManager getInstance() {
        Log.d(TAG, "getInstance");

        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {
        Log.d(TAG, "getPreferencesManager");

        return mPreferencesManager;
    }

    //region ================= Network =================

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        Log.d(TAG, "loginUser");

        return mRestService.loginUser(userLoginReq);
    }

    public Call<UserListRes> getUserList() {
        Log.d(TAG, "getUserList");

        return mRestService.getUserList();
    }

    public Call<ResponseBody> uploadUserPhoto(MultipartBody.Part photo) {
        Log.d(TAG, "uploadUserPhoto");

        return mRestService.uploadUserPhoto(mPreferencesManager.getUserId(), photo);
    }

    //endregion
}
