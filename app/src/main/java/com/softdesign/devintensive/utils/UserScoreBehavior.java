package com.softdesign.devintensive.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.softdesign.devintensive.ui.activities.MainActivity;


public class UserScoreBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    public UserScoreBehavior() {
    }

    public UserScoreBehavior(Context context, AttributeSet attrs) {
    }

    /**
     * Возвращает зависимость между LinearLayout и NestedScrollView
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof NestedScrollView;
    }

    /**
     * Изменяет высоту LinearLayout с id=user_score от 112 до 72dp
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        child.setY(dependency.getY());
        layoutParams.height = (int) (dependency.getY() * 0.227 + 107.776);
        dependency.setPadding(dependency.getPaddingLeft(), layoutParams.height, dependency.getPaddingRight(), dependency.getPaddingBottom());
        return true;
    }
}