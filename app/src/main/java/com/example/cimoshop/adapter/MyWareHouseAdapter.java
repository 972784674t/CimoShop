package com.example.cimoshop.adapter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.cimoshop.R;

import org.jetbrains.annotations.NotNull;

import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * 我的仓库适配器
 *
 * @author 谭海山
 */
public class MyWareHouseAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public MyWareHouseAdapter() {
        super(R.layout.item_myfavorite);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, String s) {

        ShimmerLayout shimmerLayout;
        ImageView imageView;

        shimmerLayout = baseViewHolder.getView(R.id.myfavoriteitemShimmer);
        imageView = baseViewHolder.getView(R.id.myfavoriteItemImg);

        shimmerLayout.setShimmerColor(0X55FFFFFF);
        shimmerLayout.setShimmerAngle(0);
        shimmerLayout.startShimmerAnimation();

        //Glide初始化图片
        Glide.with(baseViewHolder.itemView)
                //加载的URL
                .load(s)
                //占位图初始化
                .placeholder(R.drawable.ic_crop_original_black_24dp)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //加载成功则停止动画
                        shimmerLayout.stopShimmerAnimation();
                        return false;
                    }
                })
                //装载图片
                .into(imageView);
    }
}
