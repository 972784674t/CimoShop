package com.example.cimoshop.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author 谭海山
 */
public class SharedPrefsTools {

    /**
     * 私有化Sharepreferences对象
     */
    private static SharedPrefsTools INSTANCE;
    private static SharedPreferences shp ;
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



}
