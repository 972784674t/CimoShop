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


public class GalleryDetailViewModel extends AndroidViewModel {

    private static final String TAG = "GalleryDetailViewModel";

    public GalleryDetailViewModel(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<List<Pixabay.HitsBean>> _hits = new MutableLiveData<>();

    MutableLiveData<List<Pixabay.HitsBean>> get_hits() {
        return _hits;
    }


    void getImageInfo( ){
        StringRequest stringRequest1 = new StringRequest(
                Request.Method.GET,
                getUrl(),
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        List<Pixabay.HitsBean> list = gson.fromJson(response, Pixabay.class).getHits();
                        _hits.setValue(list);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,"Response ERROR");
                    }
                }
        );
        VolleySingleton.getINSTANCE(getApplication()).addToRequestQueue(stringRequest1);
    }

    private String getUrl(){
        return "https://pixabay.com/api/?key=16322793-d4bcfe56af2f14816d6549dee";
    }

}
