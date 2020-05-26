package com.example.cimoshop.ui.personalcenter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cimoshop.MainActivity;
import com.example.cimoshop.R;
import com.example.cimoshop.utils.MyTools;
import com.example.cimoshop.ui.login.Login;
import com.example.cimoshop.ui.personalcenter.favorites.MyFavorites;
import com.example.cimoshop.ui.personalcenter.mywarehouse.MyWareHouse;
import com.example.cimoshop.ui.personalcenter.myworks.MyWorks;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

/**
 * @author 谭海山
 */
public class PersonalCenter extends Fragment {

    private static final String TAG = "cimoPersonalcenter";

    public static PersonalCenter newInstance() {
        return new PersonalCenter();
    }

    private static final String[] TAB_LABEL = {"我的作品","我的喜欢","我的仓库"};

    private MaterialButton button;
    private MaterialToolbar toolbar;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.personal_center_fragment,container,false);
        initView(root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PersonalCenterViewModel mViewModel = ViewModelProviders.of(this).get(PersonalCenterViewModel.class);
        initViewpage2();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( 2 == requestCode ) {
            String token = data.getStringExtra("token");
            Log.d(TAG,token);
        }
    }
    /**
     * 控件初始化
     * @param root layout布局
     */
    private void initView(View root) {
        toolbar = root.findViewById(R.id.personalCenterToobar);
        tabLayout = root.findViewById(R.id.personalcentertabLayout);
        viewPager2 = root.findViewById(R.id.personalviewpage2);
        button = root.findViewById(R.id.button);
        //状态栏文字透明
        MyTools.makeStatusBarTransparent(getActivity());
        //修复标题栏与状态栏重叠
        MyTools.fitTitleBar(getActivity(),toolbar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), Login.class);
//                startActivity(intent);
                Intent intent = new Intent();
                intent.setClass(getContext(),Login.class);
                startActivityForResult(intent,2);
            }
        });

    }

    /**
     * viewpage2初始化
     */
    private void initViewpage2() {
        final ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(MyWorks.newInstance());
        fragmentList.add(MyFavorites.newInstance());
        fragmentList.add(MyWareHouse.newInstance());

        viewPager2.setAdapter(new FragmentStateAdapter(getParentFragmentManager(),getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getItemCount() {
                return fragmentList.size();
            }
        });

        //关联并应用ViewPage2和Tab
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(TAB_LABEL[position]);
            }
        }).attach();
    }

}
