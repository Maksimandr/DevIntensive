package com.softdesign.devintensive.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by MKS-PC on 11.07.2016.
 */
public class UiHelper {

    private static Context mContext = DevintensiveAplication.getContext();

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getActionBarHeight() {
        int result = 0;
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            result = TypedValue.complexToDimensionPixelSize(tv.data, mContext.getResources().getDisplayMetrics());
        }
        return result;
    }

    public static int getDisplayMetrics(int side) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        // since SDK_INT = 1;
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {
            }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }

        if (side == 0) {
            return widthPixels;
        } else {
            return heightPixels;
        }
    }

    public static int lerp(int start, int end, float friction) {
        return (int) (start + (end - start) * friction);
    }

    public static float currentFriction(int start, int end, int currentValue) {
        return (float) (currentValue - start) / (end - start);
    }
}
