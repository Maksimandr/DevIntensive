package com.softdesign.devintensive.ui.activities;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

public class BaseActivity extends AppCompatActivity {

    public final String TAG = ConstantManager.TAG_PREFIX + "BaseActivity";
    protected ProgressDialog mProgressDialog;

    public void showProgress(){
        Log.d(TAG, "showProgress");
        if(mProgressDialog==null){
            mProgressDialog = new ProgressDialog(this, R.style.custom_dialog);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.progress_splash);
    }

    /**
     * Прячет message ProgressDialog
     */
    public void hideProgress() {
        Log.d(TAG, "hideProgress");
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }
    }

    /**
     * Показывает message в Log
     *
     * @param message выводимое соотбщение
     */
    public void showError(String message, Exception error) {
        Log.d(TAG, "showError");
        showToast(message);
        Log.e(TAG, String.valueOf(error));
    }

    /**
     * Показывает message в Toast
     *
     * @param message выводимое соотбщение
     */
    public void showToast(String message) {
        Log.d(TAG, "showToast");
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
