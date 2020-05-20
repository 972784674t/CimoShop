package com.example.cimoshop.adapter;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cimoshop.R;
import com.example.cimoshop.entity.Pixabay;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class GalleryAdapter extends ListAdapter<Pixabay.HitsBean, GalleryViewHolder> {

    private boolean isFooter = false;

    static int MORMAL_VIEW_TYPE = 0;
    static int FOOTER_VIEW_TYPE = 1;

    public GalleryAdapter(@NonNull DiffUtil.ItemCallback<Pixabay.HitsBean> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        final GalleryViewHolder holder = new GalleryViewHolder(itemView);

        //item点击事件navigation跳转
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将图片信息通过Parcelable传输到detail页面
                Bundle bundle = new Bundle();
                bundle.putParcelable("CHECKED_PHOTO_ID", getItem(holder.getAdapterPosition()));
                NavController navController = Navigation.findNavController(v);
                //navController.navigate(R.id.action_gallery_to_galleryDetail, bundle);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {

        holder.shimmerLayout.setShimmerColor(0X55FFFFFF);
        holder.shimmerLayout.setShimmerAngle(0);
        holder.shimmerLayout.startShimmerAnimation();

        //Glide初始化图片
        Glide.with(holder.itemView).load(getItem(position)
                .getWebformatURL())           //使用WebformatURL
                .placeholder(R.drawable.ic_crop_original_black_24dp)  //站位图片初始化
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (holder.shimmerLayout != null) {     //加载成功则停止动画
                            holder.shimmerLayout.stopShimmerAnimation();
                        }
                        return false;
                    }
                })
                .into(holder.imageGalleryItem);    //转载图片

    }

}

/**
 * item类
 */
class GalleryViewHolder extends RecyclerView.ViewHolder {

    ShimmerLayout shimmerLayout;
    ImageView imageGalleryItem;

    GalleryViewHolder(@NonNull View itemView) {
        super(itemView);
        shimmerLayout = itemView.findViewById(R.id.simmeritem);
        imageGalleryItem = itemView.findViewById(R.id.imageGalleryitem);

    }
}





