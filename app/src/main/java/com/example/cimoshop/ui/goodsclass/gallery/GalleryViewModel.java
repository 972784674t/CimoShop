package com.example.cimoshop.ui.goodsclass.gallery;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cimoshop.api.VolleySingleton;
import com.example.cimoshop.entity.Pixabay;
import com.google.gson.Gson;

import java.util.List;


/**
 * @author 谭海山
 */
public class GalleryViewModel extends AndroidViewModel {

    private static final String TAG = "cimoGallery";

    //搜索关键字key
    private String[] queryKey = new String[]{"animal","natural","universe","Space","sea","Scenery","city"};

    //当前页数初始化
    private int currentPage = 1;

    public int getCurrentPage(){
        return currentPage;
    }

    public String getKey() {
        return key;
    }

    //总页数初始化
    private int totalPage = 1;

    public int getTotalPage(){
        return totalPage;
    }

    //下拉刷新时随机获取key
    private int index = (int) (Math.random() * queryKey.length);
    private String key = queryKey[index];

    //是否下拉刷新
    private boolean isNewQuery = true;

    //是否正在加载
    private boolean isLoading = false;

    //单次请求的item数
    private int perPage = 50;

    //是否已经没数据
    static boolean ISEND = false;

    //加载结果
    static String LOADING_RESULT = "s";

    //数据源
    MutableLiveData<List<Pixabay.HitsBean>> hitsBean = new MutableLiveData<>();

    private MutableLiveData<String> Pageinit;

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        Pageinit = new MutableLiveData<>();
        Pageinit.setValue("This is home fragment");
    }

    /**
     * 获取新的关键字
     * @return  新的Key
     */
    public String getNewKey(){
        int index = (int) (Math.random() * queryKey.length);
        return queryKey[index];
    }

    /**
     * 链接API
     * @return 带有API的URL
     */
    private String getUrl(){
        return "https://pixabay.com/api/?key=16322793-d4bcfe56af2f14816d6549dee&lang=zh&lang=en&q="+key+"&per_page="+perPage+"&page="+currentPage;
    }

    /**
     * 下拉刷新时，重置查询条件
     */
    void resetQuery(){

        currentPage = 1;
        totalPage = 1;
        isNewQuery = true;
        key = getNewKey();
        ISEND = false;

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                getUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Pixabay.HitsBean> list = gson.fromJson(response, Pixabay.class).getHits();
                        totalPage = (gson.fromJson(response, Pixabay.class).getTotalHits()) / perPage + 1;
                        Log.d(TAG,"下拉刷新 -> currentPage："+currentPage+"\ttotalPage："+totalPage+"\tkey："+key);
                        //刷新的请求，用postValue()
                        hitsBean.postValue(list);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "loadMore: " + error.getClass());
                        VolleySingleton.errorMessage(error, getApplication().getApplicationContext());
                    }
                });
        VolleySingleton.getInstance(getApplication()).addToRequestQueue(stringRequest);
    }

}
