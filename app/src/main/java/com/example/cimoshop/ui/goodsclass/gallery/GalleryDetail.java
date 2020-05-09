package com.example.cimoshop.ui.goodsclass.gallery;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cimoshop.R;
import com.example.cimoshop.entity.Pixabay;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import io.supercharge.shimmerlayout.ShimmerLayout;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import uk.co.senab.photoview.PhotoView;

public class GalleryDetail extends Fragment {

    private static final String TAG = "CIMO galleryDetail";

    private GalleryDetailViewModel mViewModel;
    private ShimmerLayout shimmerLayout;
    private PhotoView photoView;
    private CircleImageView upserImg;
    private TextView upserName;
    private TextView imgPrice;
    private TextView imgAddress;
    private Chip size1;
    private Chip size2;
    private Chip size3;
    private MaterialButton buttonLike;
    private MaterialButton buttonFav;



    public static GalleryDetail newInstance() {
        return new GalleryDetail();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.gallery_detail_fragment, container, false);
        shimmerLayout = root.findViewById(R.id.shimerDetialIMG);
        photoView = root.findViewById(R.id.photoView);
        upserImg = root.findViewById(R.id.uperimg);
        upserName = root.findViewById(R.id.upername);
        imgPrice = root.findViewById(R.id.imgPrice);
        size1 = root.findViewById(R.id.size1);
        size2 = root.findViewById(R.id.size2);
        size3 = root.findViewById(R.id.size3);
        buttonLike = root.findViewById(R.id.buttonlike);
        buttonFav = root.findViewById(R.id.buttonfav);
        imgAddress = root.findViewById(R.id.imgaddress);
        return root;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(GalleryDetailViewModel.class);

        shimmerLayout.setShimmerColor(0X55FFFFFF);
        shimmerLayout.setShimmerAngle(0);
        shimmerLayout.startShimmerAnimation();

        //根据id获取图片数据
        assert getArguments() != null;
        final Pixabay.HitsBean hitsBean = getArguments().getParcelable("CHECKED_PHOTO_ID");

        //传入图片URL，Glide初始化
        assert hitsBean != null;
        Glide.with(requireContext()).load( hitsBean.getLargeImageURL() )
                .placeholder(R.drawable.ic_cimowebshoplogo)  //占位图片初始化
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (shimmerLayout != null) {     //加载成功则停止动画
                            shimmerLayout.stopShimmerAnimation();
                        }
                        return false;
                    }
                })
                .into(photoView);    //装载图片

        //upserImg.setImageResource(hitsBean.getUserImageURL());
        upserName.setText("上传者："+hitsBean.getUser());

        Glide.with(this)
                .load(hitsBean.getUserImageURL())
                .placeholder(R.drawable.px)
                .into(upserImg);

        imgPrice.setText("¥ "+hitsBean.getPreviewHeight());
        size1.setText(hitsBean.getPreviewWidth()+" × "+hitsBean.getPreviewHeight());
        size2.setText(hitsBean.getWebformatWidth()+" × "+hitsBean.getWebformatHeight());
        size3.setText(hitsBean.getImageWidth()+" × "+hitsBean.getImageHeight());
        buttonLike.setText(hitsBean.getLikes()+"  likes");
        buttonFav.setText(hitsBean.getFavorites()+"  favorites");
        imgAddress.setText(Html.fromHtml("<u>"+"点击这里去详细地址"+"</u>"));

        imgAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(hitsBean.getPageURL());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            }
        });

    }

}
