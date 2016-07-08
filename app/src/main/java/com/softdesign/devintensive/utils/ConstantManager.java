package com.softdesign.devintensive.utils;

/**
 * Содержит константы
 */
public interface ConstantManager {
    String TAG_PREFIX = "DEV ";
    String COLOR_MODE_KEY = "COLOR_MODE_KEY";
    String EDIT_MODE_KEY = "EDIT_MODE_KEY";

    String USER_PHONE_KEY = "USER_KEY_1";
    String USER_EMAIL_KEY = "USER_KEY_2";
    String USER_VK_KEY = "USER_KEY_3";
    String USER_GIT_KEY = "USER_KEY_4";
    String USER_BIO_KEY = "USER_KEY_5";
    String USER_PHOTO_KEY = "USER_KEY_6";

    int LOAD_PROFILE_PHOTO = 1;
    int REQUEST_CAMERA_PICTURE = 99;
    int REQUEST_GALLERY_PICTURE = 88;

    int PERMITION_REQUEST_SETTINGS_CODE = 101;
    int CAMERA_REQUEST_PERMITION_CODE = 102;
    int EXTERNAL_STORAGE_REQUEST_PERMITION_CODE = 103;
}
