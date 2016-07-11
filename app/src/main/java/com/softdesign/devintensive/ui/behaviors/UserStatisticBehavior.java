package com.softdesign.devintensive.ui.behaviors;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.UiHelper;


public class UserStatisticBehavior<V extends LinearLayout> extends AppBarLayout.ScrollingViewBehavior {

    private final int mMaxAppBarHeight;
    private final int mMinAppBarHeight;
    private final int mMaxUserStatHeight;
    private final int mMinUserStatHeight;

    public UserStatisticBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UserStatisticBehavior);
        mMinUserStatHeight = a.getDimensionPixelSize(R.styleable.UserStatisticBehavior_behavior_min_height, 56);
        a.recycle();
        mMinAppBarHeight = UiHelper.getStatusBarHeight() + UiHelper.getActionBarHeight();
        mMaxUserStatHeight = context.getResources().getDimensionPixelSize(R.dimen.user_stat_height);
        mMaxAppBarHeight = context.getResources().getDimensionPixelSize(R.dimen.profile_image_size_256);
    }

    /**
     * Возвращает зависимость между LinearLayout с id=user_stat и AppBarLayout
     *
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    /**
     * Изменяет высоту LinearLayout с id=user_stat от 112 до 56dp
     *
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        float currentFriction = UiHelper.currentFriction(mMinAppBarHeight, mMaxAppBarHeight, dependency.getBottom());
        int currentHeight = UiHelper.lerp(mMinUserStatHeight, mMaxUserStatHeight, currentFriction);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.height = currentHeight;
        child.setLayoutParams(lp);

        return super.onDependentViewChanged(parent, child, dependency);
    }
}