package com.example.cimoshop.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.cimoshop.account.GithubAccount;

/**
 * SharedPreferences工具类 <br/>
 * 用于对本APP内的SharedPreferences文件进行操作
 *
 * @author 谭海山
 */
public class SharedPrefsTools {

    /**
     * 私有化Sharepreferences对象
     */
    private static SharedPrefsTools INSTANCE;
    private static SharedPreferences shp;
    private Application application;
    private SharedPrefsTools(Application application) {
        this.application = application;
    }

    /**
     * 静态工厂
     * @return SharedPrefsTools实例
     */
    public static synchronized SharedPrefsTools getInstance(Application application) {
        if (INSTANCE == null){
            INSTANCE = new SharedPrefsTools(application);
            shp = INSTANCE.application.getSharedPreferences("account",Context.MODE_PRIVATE);
        }
        return INSTANCE;
    }

    /**
     * 保存用户 token
     * @param accountType 用户账号类型：github？baidu
     * @param token 账户对应 token
     */
    public void saveToken(String accountType,String token){
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(accountType+"token",token);
        editor.apply();
    }

    /**
     * 通过账户类型获取对应 token
     * @param accountType 账户类型 如：github
     * @return 对应的token
     */
    public String getToken(String accountType){
        return shp.getString(accountType+"token","null");
    }

    /**
     * 保存github账号信息到 sharepreferences中
     * @param githubAccount github账号信息
     */
    public void saveUserInfo(GithubAccount githubAccount){
        SharedPreferences.Editor editor = shp.edit();
        editor.putString("userName",githubAccount.getLogin());
        editor.putString("pictures",String.valueOf(githubAccount.getPublic_repos()));
        editor.putString("followers",String.valueOf(githubAccount.getFollowers()));
        editor.putString("following",String.valueOf(githubAccount.getFollowing()));
        editor.putString("avatar_url",githubAccount.getAvatar_url());
        editor.apply();
    }

    /**
     * 从sharepreferences获取用户信息
     *
     * @return GithubAccount UserInfo
     */
    public GithubAccount getUserInfo(){
        GithubAccount githubAccount = new GithubAccount();
        githubAccount.setLogin(shp.getString("userName","请先登录哦"));
        githubAccount.setPublic_repos(Integer.parseInt(shp.getString("pictures","0")));
        githubAccount.setFollowers(Integer.parseInt(shp.getString("followers","0")));
        githubAccount.setFollowing(Integer.parseInt(shp.getString("following","0")));
        githubAccount.setAvatar_url(shp.getString("avatar_url","null"));
        return githubAccount;
    }

    /**
     * 退出登录，将清空shp中保存的用户信息
     */
    public void logout(){
        SharedPreferences.Editor editor = shp.edit();
        editor.putString("userName","请先登录哦");
        editor.putString("pictures","0");
        editor.putString("followers","0");
        editor.putString("following","0");
        editor.putString("avatar_url","null");
        editor.putString("githubtoken","null");
        editor.apply();
    }


}
