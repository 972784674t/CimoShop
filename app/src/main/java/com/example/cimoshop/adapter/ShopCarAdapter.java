package com.example.cimoshop.adapter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.cimoshop.R;
import com.example.cimoshop.entity.UserShopCar;

import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * @author 谭海山
 * 购物车recycleView适配器
 */
public class ShopCarAdapter extends BaseQuickAdapter <UserShopCar, BaseViewHolder> {

    public ShopCarAdapter() { super(R.layout.shopcaritem); }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, UserShopCar userShopCar) {

        ImageView shopImage;
        TextView shopPrice;
        TextView shopSize;
        ShimmerLayout shimmerLayout;

        shopImage = baseViewHolder.getView(R.id.shopcaritemimg);
        shopPrice = baseViewHolder.getView(R.id.shopcaritemprice);
        shopSize = baseViewHolder.getView(R.id.shopcaritemsize);
        shimmerLayout = baseViewHolder.getView(R.id.shopCarItemShimmerLayout);

        shopPrice.setText(userShopCar.getPrice());
        shopSize.setText(userShopCar.getSize());

        shimmerLayout.setShimmerColor(0X55FFFFFF);
        shimmerLayout.setShimmerAngle(0);
        shimmerLayout.startShimmerAnimation();

        Glide.with(baseViewHolder.itemView)
                .load(userShopCar.getShopCarItemUrl())
                .placeholder(R.drawable.ic_crop_original_black_24dp)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        shimmerLayout.stopShimmerAnimation();
                        return false;
                    }
                })
                .into(shopImage);

    }
}
