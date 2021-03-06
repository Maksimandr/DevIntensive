package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveAplication;

import java.util.ArrayList;
import java.util.List;


public class PreferencesManager {

    private static final String TAG = ConstantManager.TAG_PREFIX + "PreferencesManager ";

    private SharedPreferences mSharedPreferences;
    private static final String[] USER_FIELDS = {
            ConstantManager.USER_PHONE_KEY,
            ConstantManager.USER_EMAIL_KEY,
            ConstantManager.USER_VK_KEY,
            ConstantManager.USER_GIT_KEY,
            ConstantManager.USER_BIO_KEY
    };

    private static final String[] USER_STATISTIC = {
            ConstantManager.USER_RATING_VALUE,
            ConstantManager.USER_CODE_LINES_VALUE,
            ConstantManager.USER_PROJECT_VALUE,
    };

    private static final String[] USER_NAME = {
            ConstantManager.USER_FIRST_NAME_KEY,
            ConstantManager.USER_SECOND_NAME_KEY
    };


    public PreferencesManager() {
        Log.d(TAG, "Constructor");

        mSharedPreferences = DevintensiveAplication.getSharedPreferences();
    }

    /**
     * Сохраняет данные с информацией о пользователе
     *
     * @param userFields список с информацией о пользователе
     */
    public void saveUserProfileFields(List<String> userFields) {
        Log.d(TAG, "saveUserProfileFields");

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i = 0; i < USER_FIELDS.length; i++) {
            editor.putString(USER_FIELDS[i], userFields.get(i));
        }
        editor.apply();
    }

    /**
     * Загружает данные с информацией о пользователе
     *
     * @return список с информацией о пользователе
     */
    public List<String> loadUserProfileFields() {
        Log.d(TAG, "loadUserProfileFields");

        List<String> userFields = new ArrayList<>();
        for (int i = 0; i < USER_FIELDS.length; i++) {
            userFields.add(mSharedPreferences.getString(USER_FIELDS[i], null));
        }
        return userFields;
    }

    /**
     * Сохраняет данные с информацией о пользователе, полученные с сервера
     *
     * @param userValues
     */
    public void saveUserProfileStatistic(int[] userValues) {
        Log.d(TAG, "saveUserProfileStatistic");

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i = 0; i < USER_STATISTIC.length; i++) {
            editor.putString(USER_STATISTIC[i], String.valueOf(userValues[i]));
        }
        editor.apply();
    }

    /**
     * Загружает данные с информацией о пользователе
     *
     * @return
     */
    public List<String> loadUserProfileStatistic() {
        Log.d(TAG, "loadUserProfileStatistic");

        List<String> userValues = new ArrayList<>();
        for (int i = 0; i < USER_STATISTIC.length; i++) {
            userValues.add(mSharedPreferences.getString(USER_STATISTIC[i], "0"));
        }
        return userValues;
    }

    /**
     * Сохраняет указатель на файл с фото для профиля пользователя
     *
     * @param uri указатель на файл с фото
     */
    public void saveUserPhoto(Uri uri, String updated) {
        Log.d(TAG, "saveUserPhoto");

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.putString(ConstantManager.USER_PHOTO_UPDATED_KEY, updated);

        Log.d(TAG, updated);
        editor.apply();
    }

    /**
     * Загружает указатель на файл с фото для профиля пользователя
     *
     * @return указатель на файл с фото
     */
    public Uri loadUserPhoto() {
        Log.d(TAG, "loadUserPhoto");

        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY, "android.resource://com.softdesign.devintensive/drawable/user_bg"));
    }

    public String getUserPhotoUpdated() {
        Log.d(TAG, "getUserPhotoUpdated");

        return mSharedPreferences.getString(ConstantManager.USER_PHOTO_UPDATED_KEY, "0");
    }

    /**
     * Сохраняет указатель на файл с аватаром для профиля пользователя
     *
     * @param uri указатель на файл с фото
     */
    public void saveUserAvatar(Uri uri) {
        Log.d(TAG, "saveUserAvatar");

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_AVATAR_KEY, uri.toString());
        editor.apply();
    }

    /**
     * Загружает указатель на файл с аватаром для профиля пользователя
     *
     * @return указатель на файл с фото
     */
    public Uri loadUserAvatar() {
        Log.d(TAG, "loadUserAvatar");

        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_AVATAR_KEY, "android.resource://com.softdesign.devintensive/drawable/ic_account_circle_black_24dp"));
    }

    public void saveAuthToken(String authToken) {
        Log.d(TAG, "saveAuthToken");

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }

    public String getAuthToken() {
        Log.d(TAG, "getAuthToken");

        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN_KEY, "null");
    }

    public void saveUserId(String userId) {
        Log.d(TAG, "saveUserId");

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_ID_KEY, userId);
        editor.apply();
    }

    public String getUserId() {
        Log.d(TAG, "getUserId");

        return mSharedPreferences.getString(ConstantManager.USER_ID_KEY, "null");
    }

    public void saveUserName(List<String> name) {
        Log.d(TAG, "saveUserName");

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i = 0; i < USER_NAME.length; i++) {
            editor.putString(USER_NAME[i], name.get(i));
        }
        editor.apply();
    }

    public String getUserName() {
        Log.d(TAG, "getUserName");

        return mSharedPreferences.getString(ConstantManager.USER_FIRST_NAME_KEY, "Имя") + " " +
                mSharedPreferences.getString(ConstantManager.USER_SECOND_NAME_KEY, "Фамилия");
    }


}
