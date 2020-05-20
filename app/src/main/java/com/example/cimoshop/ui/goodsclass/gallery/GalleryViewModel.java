package com.example.cimoshop.ui.goodsclass.gallery;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cimoshop.api.VolleySingleton;
import com.example.cimoshop.entity.Pixabay;
import com.google.gson.Gson;

import java.util.List;


public class GalleryViewModel extends AndroidViewModel {

    private static final String TAG = "GalleryViewModel";

    //搜索关键字key
    private String[] queryKey = new String[]{"animal","natural","universe","Space","sea","Scenery","city"};

    //当前页数初始化
    private int currentPage = 1;

    //总页数初始化
    private int totalPage = 1;

    //下拉刷新时随机获取key
    int index = (int) (Math.random() * queryKey.length);
    private String key = queryKey[index];

    //是否下拉刷新
    private boolean isNewQuery = true;

    //是否正在加载
    private boolean isLoading = false;

    //单次请求的item数
    private int perPage = 50;

    //是否已经没数据
    static boolean ISEND = false;

    //加载状态
    static boolean LOADINGSTATUS = true;

    MutableLiveData<List<Pixabay.HitsBean>> _hitsBean = new MutableLiveData<>();

    public GalleryViewModel(@NonNull Application application) {
        super(application);
    }

    String getUrl(){
        return "https://pixabay.com/api/?key=16322793-d4bcfe56af2f14816d6549dee&lang=zh&lang=en&q="+key+"&per_page="+perPage+"&page="+currentPage;
    }

    String getNewKey(){
        int index = (int) (Math.random() * queryKey.length);
        return queryKey[index];
    }

    /**
     * 下拉刷新时，重置查询
     */
    void resetQuery(){
        currentPage = 1;
        totalPage = 1;
        isNewQuery = true;
        key = getNewKey();
        fetchData();
    }

    /**
     * 加载更多
     */
    public void fetchData(){
        if ( isLoading ){
            return ;
        }
        if ( currentPage > totalPage ){
            ISEND = true;
            return ;
        }
        isLoading = true;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                getUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Pixabay.HitsBean> list = gson.fromJson(response, Pixabay.class).getHits();
                        totalPage = (gson.fromJson(response, Pixabay.class).getTotalHits()) / perPage + 1;
                        //如果是刷新的请求
                        if ( isNewQuery ){
                            Log.d(TAG,"下拉刷新ing");
                            _hitsBean.postValue(list);
                        } else {
                            Log.d(TAG,"加载跟多ing");
                            _hitsBean.getValue().addAll(list);
                        }
                        isLoading = false;
                        LOADINGSTATUS  = true;
                        //下次调用不是NewQuery
                        isNewQuery = false;
                        currentPage++;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,"loadInitial: "+error);
                        LOADINGSTATUS = false;
                        isLoading = false;
                    }
                }
        );
        VolleySingleton.getInstance(getApplication()).addToRequestQueue(stringRequest);
    }

}
