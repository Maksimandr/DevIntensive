package com.softdesign.devintensive.data.network.interceptors;


import android.util.Log;

import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.managers.PreferencesManager;
import com.softdesign.devintensive.utils.ConstantManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    private static final String TAG = ConstantManager.TAG_PREFIX + "HeaderInterceptor ";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.d(TAG, "intercept");

        PreferencesManager pm = DataManager.getInstance().getPreferencesManager();

        Request original = chain.request();

        Request.Builder requeqstBuilder = original.newBuilder()
                .header("X-Access-Token", pm.getAuthToken())
                .header("Request-User-Id", pm.getUserId())
                .header("User-Agent", "DevIntensive")
                .header("Cache-Control", "max-age=" + (60 * 60 * 24));

        Request request = requeqstBuilder.build();
        return chain.proceed(request);
    }
}
