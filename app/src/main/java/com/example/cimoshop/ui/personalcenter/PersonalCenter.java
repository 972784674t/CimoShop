package com.example.cimoshop.ui.personalcenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PageKeyedDataSource;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.cimoshop.R;
import com.example.cimoshop.account.GithubAccount;
import com.example.cimoshop.api.GithubApi;
import com.example.cimoshop.api.VolleySingleton;
import com.example.cimoshop.utils.MyTools;
import com.example.cimoshop.ui.login.Login;
import com.example.cimoshop.ui.personalcenter.favorites.MyFavorites;
import com.example.cimoshop.ui.personalcenter.mywarehouse.MyWareHouse;
import com.example.cimoshop.ui.personalcenter.myworks.MyWorks;
import com.example.cimoshop.utils.PictureSelectorTools;
import com.example.cimoshop.utils.SharedPrefsTools;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author 谭海山
 */
public class PersonalCenter extends Fragment {

    private static final String TAG = "cimoPersonalcenter";

    public static PersonalCenter newInstance() {
        return new PersonalCenter();
    }

    private static final String[] TAB_LABEL = {"我的作品","我的喜欢","我的仓库"};

    //token是否存在
    private String isToken;

    private MaterialButton button;
    private MaterialToolbar toolbar;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private TextView pcuserName;
    private TextView pcuserPictures;
    private TextView pcuserFollowers;
    private TextView pcuserFollowing;
    private TextView pcuserSourceText;
    private CircleImageView pcuseravatar;

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

    /**
     * 控件初始化
     * @param root layout布局
     */
    private void initView(View root) {

        toolbar = root.findViewById(R.id.personalCenterToobar);
        tabLayout = root.findViewById(R.id.personalcentertabLayout);
        viewPager2 = root.findViewById(R.id.personalviewpage2);
        button = root.findViewById(R.id.button);
        pcuseravatar = root.findViewById(R.id.pcuseravatar);
        pcuserName = root.findViewById(R.id.pcuserName);
        pcuserFollowers = root.findViewById(R.id.pcuserfollwers);
        pcuserFollowing= root.findViewById(R.id.pcuserfollowing);
        pcuserPictures = root.findViewById(R.id.pcuserpictures);
        pcuserSourceText = root.findViewById(R.id.pcusersource);
        //状态栏文字透明
        MyTools.makeStatusBarTransparent(getActivity());
        //修复标题栏与状态栏重叠
        MyTools.fitTitleBar(getActivity(),toolbar);

        isToken = SharedPrefsTools.getInstance(getActivity().getApplication()).getToken("github");
        Log.d(TAG,"isToken："+isToken);

        if (!isToken.isEmpty()){
            initUserAccount();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch ( resultCode ){
            case 1:
                Toast.makeText(getContext(),"取消登录",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                String token = data.getStringExtra("token");
                Log.d(TAG,"token："+token);
                //登录成功后执行保存token操作
                GithubApi.getInstance(getActivity().getApplication()).saveGithubUserInfoByToken(getContext(),token);
                initUserAccount();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化用户中心界面
     * 先判断token是否存在，如果不存在则说明没有登录
     */
    private void initUserAccount(){

        GithubAccount githubAccount = SharedPrefsTools.getInstance(getActivity().getApplication()).getUserInfo();
        Log.d(TAG,"个人中心shp获取结果 -> githubAccount："+githubAccount.toString());

        Log.d(TAG,"Avatar_url："+githubAccount.getAvatar_url());
        if ( !githubAccount.getAvatar_url().equals("null") ){
            Glide.with(getContext()).load(githubAccount.getAvatar_url())
                    .placeholder(R.mipmap.empty_icon)
                    .override(500, 500)
                    .into(pcuseravatar);
        }
        pcuserName.setText(""+githubAccount.getName());
        pcuserPictures.setText(""+githubAccount.getPublic_repos());
        pcuserFollowing.setText(""+githubAccount.getFollowing());
        pcuserFollowers.setText(""+githubAccount.getFollowers());
        pcuserSourceText.setText("这些数据来自于Github");

        //更换头像
        pcuseravatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.selectpicturebottomdialog, null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();
                //用过相机
                view.findViewById(R.id.toCarmen).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PictureSelectorTools.getInstance().getImageFromTakePic(getActivity(),pcuseravatar);
                    }
                });
                //通过图库
                view.findViewById(R.id.toGralley).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PictureSelectorTools.getInstance().getImageFormGallery(getActivity(),pcuseravatar);
                    }
                });
            }
        });
    }








}
