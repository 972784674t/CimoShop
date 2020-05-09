package com.example.cimoshop.adapter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.cimoshop.R;
import com.example.cimoshop.entity.Pixabay;


import io.supercharge.shimmerlayout.ShimmerLayout;

public class GalleryAdapter_BRVAH extends BaseQuickAdapter<Pixabay.HitsBean, BaseViewHolder> implements LoadMoreModule {

    public GalleryAdapter_BRVAH() {
        super(R.layout.gallery_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Pixabay.HitsBean hitsBean) {
        final ShimmerLayout shimmerLayout;
        ImageView imageGalleryItem;
        shimmerLayout = baseViewHolder.getView(R.id.simmeritem);
        imageGalleryItem = baseViewHolder.getView(R.id.imageGalleryitem);

        shimmerLayout.setShimmerColor(0X55FFFFFF);
        shimmerLayout.setShimmerAngle(0);
        shimmerLayout.startShimmerAnimation();

        //Glide初始化图片
        Glide.with(baseViewHolder.itemView).load(hitsBean
                .getWebformatURL())           //使用WebformatURL
                .placeholder(R.drawable.ic_crop_original_black_24dp)  //站位图片初始化
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //加载成功则停止动画
                        shimmerLayout.stopShimmerAnimation();
                        return false;
                    }
                })
                .into(imageGalleryItem);    //转载图片

    }
}
