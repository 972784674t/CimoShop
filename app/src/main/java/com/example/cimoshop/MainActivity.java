package com.example.cimoshop;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.cimoshop.ui.goodsclass.GoodsClassFragment;
import com.example.cimoshop.ui.home.HomeFragment;
import com.example.cimoshop.ui.navigationStartFragmet.PersonCenterStart;
import com.example.cimoshop.ui.shopcat.ShopCatFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    MaterialToolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.activityToolbar);

        //状态栏文字透明
        makeStatusBarTransparent(this);

        //修复标题栏与状态栏重叠
        fitTitleBar(this,toolbar);
        setMIUI(this,false);
        //setStatusBarLightMode(this,true);

        initViewPage();
        initBottomNav();

        BadgeDrawable shopCat = bottomNavigationView.getOrCreateBadge(R.id.navigation_shopCat);
        shopCat.setNumber(5);

    }

    /**
     * viewPage初始化
     */
    private void initViewPage() {
        viewPager = findViewById(R.id.viewpage);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch ( position ){
                    case 0:
                        fragment =  new HomeFragment();
                        break;
                    case 1:
                        fragment = new GoodsClassFragment();
                        break;
                    case 2:
                        fragment =  new ShopCatFragment();
                        break;
                    case 3:
                        fragment =  new PersonCenterStart();
                        break;
                }
                assert fragment != null;
                return fragment;
            }

            @Override
            public int getCount() {
                return 4;
            }
        });

        //滑动事件监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 底部导航栏初始化
     */
    private void initBottomNav() {
        bottomNavigationView = findViewById(R.id.bv);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch ( item.getItemId() ){
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.navigation_goodsClass:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_shopCat:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.navigation_personalCenter:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return false;
            }
        });
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
    public static int getStatusBarHeight() {
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


    public static void setStatusBarLightMode(Activity activity, boolean isLightMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            int option = window.getDecorView().getSystemUiVisibility();
            if (isLightMode) {
                option |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                option &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            window.getDecorView().setSystemUiVisibility(option);
        }
    }

    /**
     * 状态栏字体自适应
     * @param activity this
     * @param dark dark
     */
    public static void setMIUI(Activity activity,boolean dark) {
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

}
