
package com.example.cimoshop.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.cimoshop.MainActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import java.io.File;
import java.util.List;

/**
 * @author 谭海山
 * 头像选择工具类
 */
public class PictureSelectorTools {

    private static final String TAG = "PictureSelectorTools";

    private static PictureSelectorTools INSTANCE;

    /**
     * 静态工厂
     *
     * @return PictureSelectorTools实例
     */
    public static synchronized PictureSelectorTools getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PictureSelectorTools();
        }
        return INSTANCE;
    }

    /**
     * 从图库中选择图片
     * @param activity activity
     * @param imageView imageView 被选中的图片将会显示在这个imageView
     */
    public void getImageFormGallery(Activity activity, ImageView imageView) {

        PictureSelector.create(activity)
                // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .openGallery(PictureMimeType.ofImage())
                //图片加载引擎
                .imageEngine(GlideEngine.createGlideEngine())
                // 最大图片选择数量
                .maxSelectNum(1)
                // 最小选择数量
                .minSelectNum(1)
                // 每行显示个数
                .imageSpanCount(4)
                // 是否可预览图片 true or false
                .isPreviewImage(true)
                // 是否裁剪 true or false
                .isEnableCrop(true)
                // 是否显示 uCrop 工具栏，默认不显示 true or false
                .hideBottomControls(true)
                // 裁剪框是否可拖拽 true or false
                .freeStyleCropEnabled(true)
                // 是否圆形裁剪 true or false
                .circleDimmedLayer(true)
                // 是否显示裁剪矩形边框 圆形裁剪时建议设为 false  true or false
                .showCropFrame(false)
                // 是否显示裁剪矩形网格 圆形裁剪时建议设为 false  true or false
                .showCropGrid(false)
                // 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .isPreviewEggs(true)
                // 是否传入已选图片
                .selectionMode(PictureConfig.MULTIPLE)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        // 例如 LocalMedia 里面返回五种path
                        // 1.media.getPath(); 原图path，但在Android Q版本上返回的是content:// Uri类型
                        // 2.media.getCutPath();裁剪后path，需判断media.isCut();切勿直接使用
                        // 3.media.getCompressPath();压缩后path，需判断media.isCompressed();切勿直接使用
                        // 4.media.getOriginalPath()); media.isOriginal());为true时此字段才有值
                        // 5.media.getAndroidQToPath();Android Q版本特有返回的字段，但如果开启了压缩或裁剪还是取裁剪或压缩路
                        //   径；注意：.isAndroidQTransform(false);此字段将返回空
                        // 如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩
                        String logourl = null;
                        for (LocalMedia media : result) {
                            Log.i(TAG, "是否压缩:" + media.isCompressed());
                            Log.i(TAG, "压缩:" + media.getCompressPath());
                            Log.i(TAG, "原图:" + media.getPath());
                            Log.i(TAG, "是否裁剪:" + media.isCut());
                            Log.i(TAG, "裁剪:" + media.getCutPath());
                            logourl = media.getCutPath();
                            Log.i(TAG, "是否开启原图:" + media.isOriginal());
                            Log.i(TAG, "原图路径:" + media.getOriginalPath());
                            Log.i(TAG, "Android Q 特有Path:" + media.getAndroidQToPath());
                        }
                        imageView.setImageURI(Uri.fromFile(new File(logourl)));
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "PictureSelector Cancel");
                    }

                });//结果回调onActivityResult code

    }

    /**
     * 通过相加拍摄选择图片
     * @param activity activity
     * @param imageView imageView 被选中的图片将会显示在这个imageView
     */
    public void getImageFromTakePic(Activity activity,ImageView imageView) {
        //单独拍照
        PictureSelector.create(activity)
                .openCamera(PictureMimeType.ofImage())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        // 例如 LocalMedia 里面返回五种path
                        // 1.media.getPath(); 原图path，但在Android Q版本上返回的是content:// Uri类型
                        // 2.media.getCutPath();裁剪后path，需判断media.isCut();切勿直接使用
                        // 3.media.getCompressPath();压缩后path，需判断media.isCompressed();切勿直接使用
                        // 4.media.getOriginalPath()); media.isOriginal());为true时此字段才有值
                        // 5.media.getAndroidQToPath();Android Q版本特有返回的字段，但如果开启了压缩或裁剪还是取裁剪或压缩路
                        //  径；注意：.isAndroidQTransform(false);此字段将返回空
                        // 如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩
                        String logourl = null;
                        for (LocalMedia media : result) {
                            Log.i(TAG, "是否压缩:" + media.isCompressed());
                            Log.i(TAG, "压缩:" + media.getCompressPath());
                            Log.i(TAG, "原图:" + media.getPath());
                            Log.i(TAG, "是否裁剪:" + media.isCut());
                            Log.i(TAG, "裁剪:" + media.getCutPath());
                            logourl = media.getCutPath();
                            Log.i(TAG, "是否开启原图:" + media.isOriginal());
                            Log.i(TAG, "原图路径:" + media.getOriginalPath());
                            Log.i(TAG, "Android Q 特有Path:" + media.getAndroidQToPath());
                        }
                        getImageFormGallery(activity,imageView);
                        // logo.setImageURI(Uri.fromFile(new File(logourl)));
                    }
                    @Override
                    public void onCancel() {
                        Log.i(TAG, "PictureSelector Cancel");
                    }

                });//结果回调onActivityResult code
    }



}
