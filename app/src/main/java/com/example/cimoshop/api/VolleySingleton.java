package com.example.cimoshop.api;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Volley单例模式
 */
public class VolleySingleton {

    //私有化属性
    @SuppressLint("StaticFieldLeak")
    private static VolleySingleton INSTANCE;
    private RequestQueue requestQueue;
    private Context mContext;

    //私有化构造
    private VolleySingleton(Context context) {
        this.mContext = context;
        requestQueue = getRequestQueue();
    }

    //提供获得请求队列的方法
    private RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue= Volley.newRequestQueue(mContext);
        }
        return requestQueue;
    }

    //单例模式
    public static synchronized VolleySingleton getINSTANCE(Context context){
        if( INSTANCE == null ){
            INSTANCE = new VolleySingleton(context);
        }
        return INSTANCE;
    }

    public <T> void  addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

}
