package com.example.cimoshop;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.cimoshop.mytools.myTools;
import com.example.cimoshop.ui.goodsclass.GoodsClassFragment;
import com.example.cimoshop.ui.home.HomeFragment;
import com.example.cimoshop.ui.navigationStartFragmet.PersonCenterStart;
import com.example.cimoshop.ui.shopcat.ShopCatFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //状态栏文字透明
        myTools.makeStatusBarTransparent(this);

        //修复标题栏与状态栏重叠
        //myTools.fitTitleBar(this,toolbar);
        myTools.setMIUI(this,false);
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



}
