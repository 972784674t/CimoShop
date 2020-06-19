package com.example.cimoshop.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

/**
 * 单例 Volley
 *
 * @author 谭海山
 */
public class VolleySingleton {

    private static final String TAG = "cimoVolley";

    /**
     * 私有化属性
     */
    @SuppressLint("StaticFieldLeak")
    private static VolleySingleton INSTANCE;
    private Context mContext;
    private RequestQueue requestQueue;

    /**
     * 私有化构造方法
     */
    private VolleySingleton(Context context) {
        this.mContext = context;
        requestQueue = getRequestQueue();
    }

    /**
     * VolleySingleton
     *
     * @param context Context
     * @return volley实例
     */
    public static synchronized VolleySingleton getInstance(Context context) {
        if ( INSTANCE == null ) {
            INSTANCE = new VolleySingleton(context);
        }
        return INSTANCE;
    }

    /**
     * 提供获得请求队列的方法
     *
     * @return 请求队列
     */
    private RequestQueue getRequestQueue() {
        if ( requestQueue == null ) {
            requestQueue = Volley.newRequestQueue(mContext);
        }
        return requestQueue;
    }

    /**
     * 加入请求队列
     *
     * @param req request
     * @param <T> T
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Volley 错误信息处理
     *
     * @param error VolleyError error
     * @param context context
     */
    public static void errorMessage(VolleyError error, Context context) {
        switch (error.getClass().toString()) {
            case "class com.android.volley.NoConnectionError":
                Log.d(TAG, "onErrorResponse: " + error);
                Toast.makeText(context,
                        "Oops. 网络连接出错了！",
                        Toast.LENGTH_LONG).show();
                break;
            case "class com.android.volley.ClientError":
                Toast.makeText(context,
                        "Oops. 服务器出错了!",
                        Toast.LENGTH_LONG).show();
                break;
            case "class com.android.volley.ParseError":
                Toast.makeText(context,
                        "Oops. 数据解析出错了!",
                        Toast.LENGTH_LONG).show();
                break;
            case "class com.android.volley.TimeoutError":
                Toast.makeText(context,
                        "Oops. 请求超时了!",
                        Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

}
