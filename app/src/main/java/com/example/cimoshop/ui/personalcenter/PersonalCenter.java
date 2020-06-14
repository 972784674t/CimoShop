package com.example.cimoshop.ui.personalcenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.cimoshop.R;
import com.example.cimoshop.account.GithubAccount;
import com.example.cimoshop.api.VolleySingleton;
import com.example.cimoshop.db.UserDAO;
import com.example.cimoshop.entity.User;
import com.example.cimoshop.ui.login.Login;
import com.example.cimoshop.ui.personalcenter.favorites.MyFavorites;
import com.example.cimoshop.ui.personalcenter.mywarehouse.MyWareHouse;
import com.example.cimoshop.ui.personalcenter.myworks.MyWorks;
import com.example.cimoshop.ui.shopcat.ShopCatFragment;
import com.example.cimoshop.utils.FakeX509TrustManager;
import com.example.cimoshop.utils.PictureSelectorTools;
import com.example.cimoshop.utils.SharedPrefsTools;
import com.example.cimoshop.utils.UITools;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

    final ArrayList<Fragment> fragmentList = new ArrayList<>();

    private static final String[] TAB_LABEL = {"我的作品", "我的喜欢", "已经购买"};

    /**
     * token 存在则说明已经登录
     */
    private String isToken;

    private MaterialButton logonbtn;
    private MaterialToolbar toolbar;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private TextView pcuserName;
    private TextView pcuserPictures;
    private TextView pcuserFollowers;
    private TextView pcuserFollowing;
    private TextView pcuserSourceText;
    private CircleImageView pcuseravatar;

    public static PersonalCenter newInstance() {
        return new PersonalCenter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = LayoutInflater.from(getContext()).inflate(R.layout.personal_center_fragment, container, false);
        initView(root);
        if (isToken.equals("null")){
            toolbar.getMenu().clear();
        }

        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PersonalCenterViewModel mViewModel = ViewModelProviders.of(this).get(PersonalCenterViewModel.class);
        fragmentList.add(MyWorks.newInstance());
        fragmentList.add(MyFavorites.newInstance());
        fragmentList.add(MyWareHouse.newInstance());
        initViewpage2();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case 1:
                Toast.makeText(getContext(), "取消登录", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                String token = SharedPrefsTools.getInstance(getActivity().getApplication()).getToken("github");
                Log.d(TAG, "PersonalCenter token -> " + token);
                //登录成功后执行保存token操作
                saveGithubUserInfoByToken(getContext(), token);

                break;
            default:
                break;
        }
    }

    /**
     * 控件初始化
     *
     * @param root layout布局
     */
    private void initView(View root) {

        toolbar = root.findViewById(R.id.personalCenterToobar);
        tabLayout = root.findViewById(R.id.personalcentertabLayout);
        viewPager2 = root.findViewById(R.id.personalviewpage2);
        logonbtn = root.findViewById(R.id.logonbtn);
        pcuseravatar = root.findViewById(R.id.pcuseravatar);
        pcuserName = root.findViewById(R.id.pcuserName);
        pcuserFollowers = root.findViewById(R.id.pcuserfollwers);
        pcuserFollowing = root.findViewById(R.id.pcuserfollowing);
        pcuserPictures = root.findViewById(R.id.pcuserpictures);
        pcuserSourceText = root.findViewById(R.id.pcusersource);

        //状态栏文字透明
        UITools.makeStatusBarTransparent(getActivity());

        //修复标题栏与状态栏重叠
        UITools.fitTitleBar(getActivity(), toolbar);

        isToken = SharedPrefsTools.getInstance(getActivity().getApplication()).getToken("github");
        Log.d(TAG, "isToken：" + isToken);
        initUserAccountUI();
        if (!isToken.equals("null")) {
            logonbtn.setVisibility(View.GONE);
        }

        /**
         * toobar 菜单点击事件
         */
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        initLogout();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        logonbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), Login.class);
                startActivityForResult(intent, 2);
            }
        });

    }

    /**
     * 初始化退出登录
     */
    private void initLogout() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("确定要退出登录吗")
                .setMessage("退出登录后需要重新输入账号信息哦")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPrefsTools.getInstance(getActivity().getApplication()).logout();
                        Toast.makeText(getContext(), "退出登录", Toast.LENGTH_LONG).show();
                        pcuserSourceText.setText("又是愉快的一天");
                        toolbar.getMenu().clear();
                        initUserAccountUI();
                        pcuseravatar.setImageResource(R.drawable.empty_icon);
                        logonbtn.setVisibility(View.VISIBLE);
                        initViewpage2();

                        //找到父节点后通过位置找到 ShopCatFragment 子节点，并更新购物车数据
                        getParentFragmentManager().getFragments().get(1).onResume();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    /**
     * viewpage2初始化
     */
    private void initViewpage2() {

        viewPager2.setAdapter(new FragmentStateAdapter(getParentFragmentManager(), getLifecycle()) {
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

    /**
     * 初始化用户中心界面
     * 先判断token是否存在，如果不存在则说明没有登录
     */
    private void initUserAccountUI() {

        GithubAccount githubAccount = SharedPrefsTools.getInstance(getActivity().getApplication()).getUserInfo();
        Log.d(TAG, "个人中心shp获取结果 -> githubAccount：" + githubAccount.toString());

        Log.d(TAG, "Avatar_url：" + githubAccount.getAvatar_url());
        if (!githubAccount.getAvatar_url().equals("null")) {
            Glide.with(getContext()).load(githubAccount.getAvatar_url())
                    .placeholder(R.mipmap.empty_icon)
                    .override(500, 500)
                    .into(pcuseravatar);
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
                            PictureSelectorTools.getInstance().getImageFromTakePic(getActivity(), pcuseravatar);
                        }
                    });
                    //通过图库
                    view.findViewById(R.id.toGralley).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PictureSelectorTools.getInstance().getImageFormGallery(getActivity(), pcuseravatar);
                        }
                    });
                }
            });

        }
        pcuserName.setText("" + githubAccount.getLogin());
        pcuserPictures.setText("" + githubAccount.getPublic_repos());
        pcuserFollowing.setText("" + githubAccount.getFollowing());
        pcuserFollowers.setText("" + githubAccount.getFollowers());
    }

    /**
     * 将token加入请求头Authorization中，<br/>
     * 获取github用户信息，<br/>
     * 并保存到本地shp文件和数据库中<br/>
     * 同时更新当前界面 UI 和 购物车 UI
     *
     * @param context context
     */
    public void saveGithubUserInfoByToken(final Context context, final String token) {

        MaterialDialog materialDialog = new MaterialDialog.Builder(getContext())
                //.title("登录github成功")
                .content("正在从Github获取用户信息，请稍等...")
                .theme(Theme.LIGHT)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        String baseUrl = "https://api.github.com/user";
        FakeX509TrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                baseUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "response" + response);
                        materialDialog.dismiss();

                        Toast.makeText(context, "用户信息获取成功", Toast.LENGTH_SHORT).show();
                        logonbtn.setVisibility(View.GONE);
                        toolbar.inflateMenu(R.menu.personalcentermenu);
                        //将返回的用户信息保存到shp文件
                        Gson gson = new Gson();
                        GithubAccount githubAccount = gson.fromJson(response, GithubAccount.class);
                        Log.d(TAG, "用户信息(githubAccount)：" + githubAccount);
                        SharedPrefsTools.getInstance(getActivity().getApplication()).saveUserInfo(githubAccount);

                        //更新本界面 UI
                        initUserAccountUI();
                        initViewpage2();

                        //将用户信息保存到数据库
                        User user = new User();
                        user.setUserName(githubAccount.getLogin());
                        if (UserDAO.getInstance(getContext()).insertUser(user)) {
                            Log.d(TAG, "将用户" + user.getUserName() + "加入数据库成功");
                        } else {
                            Log.d(TAG, "将用户" + user.getUserName() + "加入数据库失败");
                        }

                        //找到父节点后通过位置找到 ShopCatFragment 子节点，并更新购物车数据
                        getParentFragmentManager().getFragments().get(1).onResume();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        materialDialog.dismiss();
                        toolbar.getMenu().clear();
                        Toast.makeText(context, "Oops!服务器开了点小差，重新登录试试", Toast.LENGTH_SHORT).show();
                        SharedPrefsTools.getInstance(getActivity().getApplication()).logout();
                        Log.d(TAG, "error：" + error);
                        VolleySingleton.errorMessage(error, context);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "token " + SharedPrefsTools.getInstance(getActivity().getApplication()).getToken("github"));
                Log.d(TAG, headers.get("Authorization"));
                return headers;
            }
        };
        //设置请求超时时间2秒
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


}
