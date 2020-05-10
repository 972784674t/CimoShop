package com.example.cimoshop.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.cimoshop.R;
import com.example.cimoshop.mytools.myTools;
import com.example.cimoshop.ui.goodsclass.gallery.Gallery;
import com.example.cimoshop.ui.personalcenter.PersonalCenter;
import com.example.cimoshop.ui.shopcat.ShopCatFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragmentCIMO";

    private HomeViewModel homeViewModel;

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = root.findViewById(R.id.viewpage);
        bottomNavigationView = root.findViewById(R.id.bv);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onActivityCreated");

        //状态栏文字透明
        myTools.makeStatusBarTransparent(getActivity());

        //修复标题栏与状态栏重叠
        //myTools.fitTitleBar(this,toolbar);
        myTools.setMIUI(getActivity(),false);

        initViewPage();
        initBottomNav();

        BadgeDrawable shopCat = bottomNavigationView.getOrCreateBadge(R.id.navigation_shopCat);
        shopCat.setNumber(5);

    }

    /**
     * viewPage初始化
     */
    private void initViewPage() {

        viewPager.setAdapter(new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch ( position ){
                    case 0:
                        fragment = new Gallery();
                        break;
                    case 1:
                        fragment =  new ShopCatFragment();
                        break;
                    case 2:
                        fragment =  new PersonalCenter();
                        break;
                }
                assert fragment != null;
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
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

        viewPager.setOffscreenPageLimit(3);
    }

    /**
     * 底部导航栏初始化
     */
    private void initBottomNav() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch ( item.getItemId() ){
                    case R.id.navigation_goodsClass:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.navigation_shopCat:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_personalCenter:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });
    }

}
