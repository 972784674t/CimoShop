package com.example.cimoshop.datasource;

import android.content.Context;

import androidx.annotation.NonNull;

import androidx.paging.DataSource;

import com.example.cimoshop.entity.Pixabay;

/**
 * JetPack Paging 分页数据源工厂 （此类已弃用）
 *数据源加载已合并到 GalleryViewModel 中
 *
 * @author 谭海山
 */
public class PixabayDataSourceFactory extends DataSource.Factory<Integer, Pixabay.HitsBean> {

    private Context context;

    public PixabayDataSourceFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public DataSource<Integer, Pixabay.HitsBean> create() {
        return new PixabayDataSource(context);
    }

}
