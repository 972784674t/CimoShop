package com.example.cimoshop.ui.goodsclass.gallery;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cimoshop.api.VolleySingleton;
import com.example.cimoshop.dataSource.PixabayDataSourceFactory;
import com.example.cimoshop.entity.Pixabay;
import com.google.gson.Gson;

import java.util.List;


public class GalleryViewModel extends AndroidViewModel {

    private static final String TAG = "CIMO GalleryViewModel";

    public GalleryViewModel(@NonNull Application application) {
        super(application);
    }

    MutableLiveData<List<Pixabay.HitsBean>> _hitsBean = new MutableLiveData<>();

    private String[] queryKey = new String[]{"animal","natural","universe","Space","sea","Scenery","city"};

    String getUrl(){
        int index = (int) (Math.random() * queryKey.length);
        return "https://pixabay.com/api/?key=16322793-d4bcfe56af2f14816d6549dee&lang=zh&lang=en&q="+queryKey[index]+"&per_page=50&page=1";
    }

    public void fetchData(){
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                getUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Pixabay.HitsBean> list = gson.fromJson(response, Pixabay.class).getHits();
                        _hitsBean.postValue(list);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,"loadInitial: "+error);
                    }
                }
        );
        VolleySingleton.getINSTANCE(getApplication()).addToRequestQueue(stringRequest);
    }

}
