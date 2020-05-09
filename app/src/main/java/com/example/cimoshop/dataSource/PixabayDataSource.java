package com.example.cimoshop.dataSource;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.paging.PageKeyedDataSource;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cimoshop.api.VolleySingleton;
import com.example.cimoshop.entity.Pixabay;
import com.google.gson.Gson;

import java.util.List;

public class PixabayDataSource extends PageKeyedDataSource<Integer, Pixabay.HitsBean> {

    private static final String TAG = "CIMO PAGING";

    //数据源加载状态广播
    private static final String DATA_SOURCE_LOADING_STATUS = "DATA_SOURCE_LOADING_STATUS";

    //广播intent
    private Intent loadingStatusIntent = new Intent(DATA_SOURCE_LOADING_STATUS);

    private Context context;

    PixabayDataSource(Context context){
        this.context = context;
    }

    private String[] queryKey = new String[]{"animals","natural","universe","Space","sea","flower"};

    private int index;


    /**
     * 第一次加载
     * @param params   页码
     * @param callback 回调
     */
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Pixabay.HitsBean> callback) {
        int index = (int) (Math.random() * queryKey.length);
        this.index = index;

        //发生数据源广播：LOADING
        loadingStatusIntent.putExtra("DATA_SOURCE_LOADING_STATUS","LOADING");
        LocalBroadcastManager.getInstance(context).sendBroadcast(loadingStatusIntent);

        String url = "https://pixabay.com/api/?key=16322793-d4bcfe56af2f14816d6549dee&q="+queryKey[index]+"&per_page=50&page=1";
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //发生数据源广播：COMPLETED
                        loadingStatusIntent.putExtra("DATA_SOURCE_LOADING_STATUS","COMPLETED");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(loadingStatusIntent);

                        context.sendBroadcast(loadingStatusIntent);
                        Gson gson = new Gson();
                        List<Pixabay.HitsBean> list = gson.fromJson(response, Pixabay.class).getHits();
                        callback.onResult(list,null,2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //发生数据源广播：FAILED
                        loadingStatusIntent.putExtra("DATA_SOURCE_LOADING_STATUS","FAILED");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(loadingStatusIntent);

                        Log.d(TAG,"loadInitial: "+error);
                    }
                }
        );
        VolleySingleton.getINSTANCE(this.context).addToRequestQueue(stringRequest);
    }

    /**
     *数据不够时加载下一页
     * @param params   页码
     * @param callback 回调
     */
    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Pixabay.HitsBean> callback) {

        //发生数据源广播：LOADING
        loadingStatusIntent.putExtra("DATA_SOURCE_LOADING_STATUS","LOADING");
        LocalBroadcastManager.getInstance(context).sendBroadcast(loadingStatusIntent);

        String url = "https://pixabay.com/api/?key=16322793-d4bcfe56af2f14816d6549dee&q="+queryKey[this.index]+"&per_page=50&page="+params.key;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //发生数据源广播：COMPLETED
                        loadingStatusIntent.putExtra("DATA_SOURCE_LOADING_STATUS","COMPLETED");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(loadingStatusIntent);

                        Gson gson = new Gson();
                        List<Pixabay.HitsBean> list = gson.fromJson(response, Pixabay.class).getHits();
                        callback.onResult(list,params.key+1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if( error.toString().indexOf("com.android.volley.NoConnection") == 1 ){
                            //发出数据源广播：无网络
                            loadingStatusIntent.putExtra("DATA_SOURCE_LOADING_STATUS","FAILED NO NET_WOKE");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(loadingStatusIntent);
                        }
                        if( error.toString().indexOf("com.android.volley.ClientError") == 1 ){
                            //发出数据源广播：数据源见底
                            loadingStatusIntent.putExtra("DATA_SOURCE_LOADING_STATUS","FAILED END ON DATA_SOURCE");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(loadingStatusIntent);
                        }
                        Log.d(TAG,"loadAfter: "+error);
                    }
                }
        );
        VolleySingleton.getINSTANCE(this.context).addToRequestQueue(stringRequest);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Pixabay.HitsBean> callback) {

    }
}
