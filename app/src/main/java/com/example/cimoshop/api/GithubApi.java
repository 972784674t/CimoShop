package com.example.cimoshop.api;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cimoshop.account.GithubAccount;
import com.example.cimoshop.ui.personalcenter.PersonalCenter;
import com.example.cimoshop.utils.SharedPrefsTools;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Github单例
 * @author 谭海山
 */
public class GithubApi {

    private static final String TAG = "cimoGithubApi";

    private Application application;
    private static GithubApi INSTANCE;
    private static String TOKEN;
    private static String USER_INFO;

    public static boolean isGetUserInfoFinish;

    /**
     * 构造GithubApi类时需要 application和 context
     * @param application application
     */
    private GithubApi(Application application){
        this.application = application;
    }

    /**
     * 获取githubAip类实例
     * @param application application
     */
    public static synchronized GithubApi getInstance(Application application){
        if ( INSTANCE == null ){
            INSTANCE = new GithubApi(application);
        }
        return INSTANCE;
    }

    /**
     * 从Sharepreferences中获取 github token
     */
    public String getToken(){
        TOKEN = SharedPrefsTools.getInstance(application).getToken("github");
        return TOKEN;
    }

    public HashMap<String,String> initGithubHeard(){
        HashMap<String,String> headers = new HashMap<>();
        headers.put("Authorization","token "+getToken());
        Log.d(TAG,headers.get("Authorization"));
        return headers;
    }

    /**
     * 将token加入请求头Authorization中，获取github用户信息
     * @param context context
     */
    public void saveGithubUserInfoByToken(final Context context, final String token){
        Toast.makeText(context, "正在从Github获取用户信息，请稍等...", Toast.LENGTH_SHORT).show();
        String baseUrl = "https://api.github.com/user";
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                baseUrl,
                response -> {
                    USER_INFO = response;
                    Toast.makeText(context,"用户信息获取成功",Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                    GithubAccount githubAccount = gson.fromJson(USER_INFO,GithubAccount.class);
                    Log.d(TAG,"用户信息(githubAccount)："+githubAccount);
                    SharedPrefsTools.getInstance(application).saveUserInfo(githubAccount);

                },
                error -> {
                    if( token == null ) {
                        Log.d(TAG, "error：" + error);
                        Toast.makeText(context, "github授权失败", Toast.LENGTH_SHORT).show();
                        VolleySingleton.errorMessage(error, context);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                return initGithubHeard();
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

}
