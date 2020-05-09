package com.example.cimoshop.mytools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 工具类
 */
public class myTools {

    /**
     * 状态栏字体自适应
     * @param activity this
     * @param dark dark
     */
    public static void setMIUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            Class clazz = activity.getWindow().getClass();
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 状态栏透明
     * @param activity this
     */
    public static void makeStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 获取toolbar高度
     * @return  toolbar高度
     */
    private static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 根据toolbar高度动态修复重叠问题
     * @param activity this
     * @param titleBar toolbar
     */
    public static void fitTitleBar(Activity activity, final View titleBar) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        makeStatusBarTransparent(activity);
        final ViewGroup.LayoutParams layoutParams = titleBar.getLayoutParams();
        assert layoutParams != null;
        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT ||
                layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            titleBar.post(new Runnable() {
                @Override
                public void run() {
                    layoutParams.height = titleBar.getHeight() + getStatusBarHeight();
                    titleBar.setPadding(titleBar.getPaddingLeft(),
                            titleBar.getPaddingTop() + getStatusBarHeight(),
                            titleBar.getPaddingRight(),
                            titleBar.getPaddingBottom());
                }
            });
        } else {
            layoutParams.height += getStatusBarHeight();
            titleBar.setPadding(titleBar.getPaddingLeft(),
                    titleBar.getPaddingTop() + getStatusBarHeight(),
                    titleBar.getPaddingRight(),
                    titleBar.getPaddingBottom());
        }
    }

}
