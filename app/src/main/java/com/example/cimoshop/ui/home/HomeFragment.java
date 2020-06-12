package com.example.cimoshop.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.cimoshop.R;

import com.example.cimoshop.db.UserDAO;
import com.example.cimoshop.entity.UserShopCar;
import com.example.cimoshop.utils.MyViewPager;
import com.example.cimoshop.utils.SharedPrefsTools;
import com.example.cimoshop.utils.UITools;
import com.example.cimoshop.ui.goodsclass.gallery.Gallery;
import com.example.cimoshop.ui.personalcenter.PersonalCenter;
import com.example.cimoshop.ui.shopcat.ShopCatFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


/**
 * @author 谭海山
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragmentCIMO";

    private MyViewPager viewPager;
    private BottomNavigationView bottomNavigationView;

    /**
     * 购物车数据源
     */
    private static ArrayList<UserShopCar> SHOP_CAR_ITEM_LIST = null;

    /**
     * 当前用户名
     */
    private static String USER_NAME = null;
    private BadgeDrawable shopCat;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager =  root.findViewById(R.id.viewpage);
        bottomNavigationView = root.findViewById(R.id.bv);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onActivityCreated");

        //状态栏文字透明
        UITools.makeStatusBarTransparent(getActivity());

        //修复标题栏与状态栏重叠
        UITools.setMIUI(getActivity(),true);

        initViewPage();
        initBottomNav();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume"+getId());
        getShopCarNavBadge();

    }

    @Override
    public void onPause() {
        super.onPause();
        shopCat.setNumber(0);
    }

    /**
     * viewPage初始化
     */
    private void initViewPage() {

        //fragment数据源
        final ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new Gallery());
        fragmentList.add(new ShopCatFragment());
        fragmentList.add(new PersonalCenter());

        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem( int position ) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
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

        //预加载界面
        viewPager.setOffscreenPageLimit(fragmentList.size()-1);
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
                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 设置底部导航购物车商品数量标签
     */
    private void getShopCarNavBadge() {
        USER_NAME = SharedPrefsTools.getInstance(getActivity().getApplication()).getUserInfo().getLogin();
        SHOP_CAR_ITEM_LIST = UserDAO.getInstance(getContext()).getShopCarList(USER_NAME);
        shopCat = bottomNavigationView.getOrCreateBadge(R.id.navigation_shopCat);
        shopCat.setNumber(SHOP_CAR_ITEM_LIST.size());
    }

}
