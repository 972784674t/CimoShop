package com.example.cimoshop.ui.goodsclass.gallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cimoshop.R;
import com.example.cimoshop.entity.Pixabay;
import com.example.cimoshop.mytools.myTools;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import de.hdodenhof.circleimageview.CircleImageView;
import io.supercharge.shimmerlayout.ShimmerLayout;
import uk.co.senab.photoview.PhotoView;

public class GalleryDetail extends AppCompatActivity {

    private static final String TAG = "CIMO galleryDetail";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);

        myTools.makeStatusBarTransparent(this);

        shimmerLayout = findViewById(R.id.shimerDetialIMG);
        photoView = findViewById(R.id.photoView);
        upserImg = findViewById(R.id.uperimg);
        upserName = findViewById(R.id.upername);
        imgPrice = findViewById(R.id.imgPrice);
        size1 = findViewById(R.id.size1);
        size2 = findViewById(R.id.size2);
        size3 = findViewById(R.id.size3);
        buttonLike = findViewById(R.id.buttonlike);
        buttonFav = findViewById(R.id.buttonfav);
        imgAddress = findViewById(R.id.imgaddress);

    }

    @Override
    protected void onStart() {
        super.onStart();

        shimmerLayout.setShimmerColor(0X55FFFFFF);
        shimmerLayout.setShimmerAngle(0);
        shimmerLayout.startShimmerAnimation();

        //根据id获取图片数据

        final Pixabay.HitsBean hitsBean = getIntent().getExtras().getParcelable("CHECKED_PHOTO_ID");

        //传入图片URL，Glide初始化
        assert hitsBean != null;
        Glide.with(getApplicationContext()).load( hitsBean.getLargeImageURL() )
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
                startActivity(intent);
            }
        });

    }
}
