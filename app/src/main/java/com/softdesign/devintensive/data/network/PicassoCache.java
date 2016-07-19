package com.softdesign.devintensive.data.network;


import android.content.Context;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

public class PicassoCache {
    private static final String TAG = ConstantManager.TAG_PREFIX + "PicassoCache";

    private Context mContext;
    private Picasso mPicassoInstance;

    public PicassoCache(Context context) {
        Log.d(TAG, "Constructor");

        this.mContext = context;
        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(context, Integer.MAX_VALUE);
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(okHttp3Downloader);

        mPicassoInstance = builder.build();
        Picasso.setSingletonInstance(mPicassoInstance);
    }

    public Picasso getPicassoInstance() {
        Log.d(TAG, "getPicassoInstance");

        if (mPicassoInstance == null) {
            new PicassoCache(mContext);
        }

        return mPicassoInstance;
    }
}
