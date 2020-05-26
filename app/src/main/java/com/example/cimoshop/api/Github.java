package com.example.cimoshop.api;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cimoshop.utils.MyTools;
import com.example.cimoshop.utils.SharedPrefsTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 谭海山
 */
public class Github {

    private static final String TAG = "cimoGithubApi";

    private Application application;
    private static Github INSTANCE;
    private static String TOKEN;
    private Github(Application application){
        this.application = application;
    }

    public Github getInstance(){
        if ( INSTANCE == null ){
            INSTANCE = new Github(application);
        }
        return INSTANCE;
    }

    private String getTOKEN(){
        return SharedPrefsTools.getInstance(application).getToken("github");
    }

    public static String getGithubUserInfo(){
        final String[] userInfo = new String[1];
        String baseUrl = "https://api.github.com/user";
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                baseUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        userInfo[0] = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "loadMore: " + error.getClass());
                        Log.d(TAG,"error："+ error);
                        Toast.makeText(INSTANCE.application, "github授权失败", Toast.LENGTH_SHORT).show();
                        switch (error.getClass().toString()) {
                            case "class com.android.volley.NoConnectionError":
                                Log.d(TAG, "onErrorResponse: " + error);
                                Toast.makeText(INSTANCE.application,
                                        "Oops. 网络连接出错了！",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case "class com.android.volley.ClientError":
                                Toast.makeText(INSTANCE.application,
                                        "Oops. 服务器出错了!",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case "class com.android.volley.ParseError":
                                Toast.makeText(INSTANCE.application,
                                        "Oops. 数据解析出错了!",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case "class com.android.volley.TimeoutError":

                                Toast.makeText(INSTANCE.application,
                                        "Oops. 请求超时了!",
                                        Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Authorization","token "+INSTANCE.getTOKEN());
                Log.d(TAG,headers.get("Authorization"));
                return headers;
            }
        };
        VolleySingleton.getInstance(INSTANCE.application).addToRequestQueue(stringRequest);

        return userInfo[0];
    }


}
