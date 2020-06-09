package com.example.cimoshop.ui.goodsclass.gallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cimoshop.R;
import com.example.cimoshop.db.UserDAO;
import com.example.cimoshop.entity.Pixabay;
import com.example.cimoshop.utils.SharedPrefsTools;
import com.example.cimoshop.utils.UITools;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;
import io.supercharge.shimmerlayout.ShimmerLayout;
import uk.co.senab.photoview.PhotoView;

public class GalleryDetail extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CIMO galleryDetail";

    private static boolean IS_LOGON = false;

    //当前用户名
    private static String USER_NAME;

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
    private BottomAppBar bottomAppBar;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);

        setTheme(R.style.AppTheme);

        UITools.makeStatusBarTransparent(this);

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
        bottomAppBar = findViewById(R.id.bottombar);
        floatingActionButton = findViewById(R.id.fab);

    }

    @Override
    protected void onStart() {
        super.onStart();

        IS_LOGON = (SharedPrefsTools.getInstance(getApplication()).getToken("github") == "null")?false:true;

        if (IS_LOGON == true){
            USER_NAME = SharedPrefsTools.getInstance(getApplication()).getUserInfo().getLogin();
        }

        shimmerLayout.setShimmerColor(0X55FFFFFF);
        shimmerLayout.setShimmerAngle(0);
        shimmerLayout.startShimmerAnimation();

        //从Parcelable获取图片数据
        final Pixabay.HitsBean hitsBean = getIntent().getExtras().getParcelable("CHECKED_PHOTO_ID");

        //传入图片URL，Glide初始化
        assert hitsBean != null;
        Glide.with(getApplicationContext())
                //加载的URL
                .load(hitsBean.getWebformatURL())
                //占位图片初始化
                .placeholder(R.drawable.ic_cimowebshoplogo)
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
        upserName.setText("" + hitsBean.getUser());

        Glide.with(this)
                .load(hitsBean.getUserImageURL())
                .placeholder(R.drawable.px)
                .into(upserImg);

        imgPrice.setText("¥ " + hitsBean.getPreviewHeight());
        size1.setText(hitsBean.getPreviewWidth() + " × " + hitsBean.getPreviewHeight());
        size2.setText(hitsBean.getWebformatWidth() + " × " + hitsBean.getWebformatHeight());
        size3.setText(hitsBean.getImageWidth() + " × " + hitsBean.getImageHeight());
        buttonLike.setText(hitsBean.getLikes() + "  likes");
        buttonFav.setText(hitsBean.getFavorites() + "  favorites");
        imgAddress.setText(Html.fromHtml("<u>" + "点击这里去详细地址" + "</u>"));

        //如果已经点赞，则图标为红色
        if (UserDAO.getInstance(getApplicationContext()).isFavoriteImage(USER_NAME,hitsBean.getWebformatURL())){
            MenuItem item = bottomAppBar.getMenu().findItem(R.id.fav);
            item.setIcon(R.drawable.ic_favorite);
        }

        initAllOnClick(hitsBean);

    }

    /**
     * 初始化所有页面相关点击数据
     * @param hitsBean 数据源
     */
    private void initAllOnClick(Pixabay.HitsBean hitsBean) {

        //去详细地址
        imgAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(hitsBean.getPageURL());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //bottomAppBar返回
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //加入购物车
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( IS_LOGON ){
                    isAddtoCat();
                } else {
                    Toast.makeText(getApplicationContext(),"请先登录哦",Toast.LENGTH_SHORT).show();
                }
            }

        });

        //bottomAppBar menu点击事件处理
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.fav:
                        initFavoriteImage(item, hitsBean);
                        break;
                    case R.id.share:
                        share();
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 初始化收藏图片操作
     * @param item MenuItem
     * @param hitsBean hitsBean
     */
    private void initFavoriteImage(MenuItem item, Pixabay.HitsBean hitsBean) {
        if (IS_LOGON == false){
            Toast.makeText(getApplicationContext(),"您还没有登录哦",Toast.LENGTH_SHORT).show();
        } else {
                //根据图标状态判断是否点赞
                if (item.getIcon().getConstantState() == getDrawable(R.drawable.ic_favorite).getConstantState()) {
                    item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                    if ( UserDAO.getInstance(getApplicationContext()).delUserFavoriteImage(hitsBean.getWebformatURL()) ){
                        Toast.makeText(getApplicationContext(),"取消收藏成功",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"取消收藏失败",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    item.setIcon(R.drawable.ic_favorite);
                    if( UserDAO.getInstance(getApplicationContext()).insertUserFavoriteImage(USER_NAME,hitsBean.getWebformatURL()) ){
                        Toast.makeText(getApplicationContext(),"收藏成功",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"收藏失败",Toast.LENGTH_SHORT).show();
                    }
                }

        }
    }

    /**
     * 是否加入购物车
     */
    void isAddtoCat() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("CIMO")
                .setMessage("确认将该图片加入购物车吗")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplication(), "加入购物车成功", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    /**
     * 分享操作
     */
    void share() {

        View view = LayoutInflater.from(this).inflate(R.layout.sharegallery, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        LinearLayout todouyin = view.findViewById(R.id.todouyin);
        LinearLayout tobaidu = view.findViewById(R.id.tobaidu);
        LinearLayout toqqzoom = view.findViewById(R.id.toqqzoom);
        LinearLayout togithub = view.findViewById(R.id.togithub);

        tobaidu.setOnClickListener(this);
        toqqzoom.setOnClickListener(this);
        togithub.setOnClickListener(this);
        todouyin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tobaidu:
                Toast.makeText(getApplication(), "成功分享到百度", Toast.LENGTH_SHORT).show();
                break;
            case R.id.todouyin:
                Toast.makeText(getApplication(), "成功分享到抖音", Toast.LENGTH_SHORT).show();
                break;
            case R.id.toqqzoom:
                Toast.makeText(getApplication(), "成功分享到QQ空间", Toast.LENGTH_SHORT).show();
                break;
            case R.id.togithub:
                Toast.makeText(getApplication(), "成功分享到github", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     *重写onBackPressed，配对进入动画
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }
}
