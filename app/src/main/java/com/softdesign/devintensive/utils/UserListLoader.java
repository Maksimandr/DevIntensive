package com.softdesign.devintensive.utils;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.softdesign.devintensive.data.managers.DataManager;

import java.util.List;

public class UserListLoader extends AsyncTaskLoader<List> {

    private static final String TAG = ConstantManager.TAG_PREFIX + "UserListLoader";

    private DataManager mDataManager;


    public UserListLoader(Context context, Bundle args) {
        super(context);
        mDataManager = DataManager.getInstance();

    }

    @Override
    public List loadInBackground() {
        Log.d(TAG, "loadInBackground");

        return mDataManager.getUsersListFromDb();
    }

    @Override
    public void forceLoad() {
        Log.d(TAG, "forceLoad");
        super.forceLoad();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d(TAG, "onStartLoading");
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        Log.d(TAG, "onStopLoading");
    }

    @Override
    public void deliverResult(List data) {
        Log.d(TAG, "deliverResult");
        super.deliverResult(data);
    }

}
