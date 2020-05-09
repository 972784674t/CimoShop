package com.example.cimoshop.dataSource;

import android.content.Context;
import androidx.annotation.NonNull;

import androidx.paging.DataSource;
import com.example.cimoshop.entity.Pixabay;

public class PixabayDataSourceFactory extends DataSource.Factory<Integer, Pixabay.HitsBean> {

    private Context context;

    public PixabayDataSourceFactory(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public DataSource<Integer, Pixabay.HitsBean> create() {
        return  new PixabayDataSource(context);
    }
}
