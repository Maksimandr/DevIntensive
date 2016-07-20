package com.softdesign.devintensive.data.managers;


import android.content.Context;
import android.util.Log;

import com.softdesign.devintensive.data.network.PicassoCache;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveAplication;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DataManager {

    private static final String TAG = ConstantManager.TAG_PREFIX + "DataManager";

    private static DataManager INSTANCE = null;
    private Picasso mPicasso;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DaoSession mDaoSession;

    public DataManager() {
        Log.d(TAG, "Constructor");
        this.mPreferencesManager = new PreferencesManager();
        this.mContext = DevintensiveAplication.getContext();
        this.mRestService = ServiceGenerator.createService(RestService.class);
        this.mPicasso = new PicassoCache(mContext).getPicassoInstance();
        this.mDaoSession = DevintensiveAplication.getDaoSession();
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

    public Context getContext() {
        Log.d(TAG, "getContext");
        return mContext;
    }

    public Picasso getPicasso() {
        Log.d(TAG, "getPicasso");
        return mPicasso;
    }

    //region ================= Network =================

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        Log.d(TAG, "loginUser");
        return mRestService.loginUser(userLoginReq);
    }

    public Call<UserListRes> getUserListFromNetwork() {
        Log.d(TAG, "getUserListFromNetwork");
        return mRestService.getUserList();
    }

    public Call<ResponseBody> uploadUserPhoto(MultipartBody.Part photo) {
        Log.d(TAG, "uploadUserPhoto");
        return mRestService.uploadUserPhoto(mPreferencesManager.getUserId(), photo);
    }

    //endregion

    //region ================= Database =================


    public DaoSession getDaoSession() {
        Log.d(TAG, "getDaoSession");
        return mDaoSession;
    }

    public List<User> getUsersListFromDb() {
        Log.d(TAG, "getUsersListFromDb");
        List<User> userList = new ArrayList<>();
        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.CodeLines.gt(0))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    public List<User> getUsersListByName(String query) {
        Log.d(TAG, "getUsersListByName");

        List<User> userList = new ArrayList<>();
        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.Rating.gt(0), UserDao.Properties.SearchName.like("%" + query.toUpperCase() + "%"))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }

    //endregion
}
