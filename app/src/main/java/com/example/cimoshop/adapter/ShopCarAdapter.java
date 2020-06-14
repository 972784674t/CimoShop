package com.example.cimoshop.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.module.DraggableModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.cimoshop.R;
import com.example.cimoshop.entity.UserShopCar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * @author 谭海山
 * 购物车 recycleView 适配器
 */
public class ShopCarAdapter extends BaseQuickAdapter<UserShopCar, BaseViewHolder> implements DraggableModule, OnItemClickListener, OnItemChildClickListener {

    private static final String TAG = "ShopCarAdapter";

    /**
     * 数据源
     */
    private static List<UserShopCar> SHOP_CAR_ITEM_LIST = new ArrayList<>();

    /**
     * 购物袋 : HashMap
     */
    public static HashMap<Integer, UserShopCar> SHOPPING_BAG = new HashMap<>();

    private CheckBox selectAllCheckBox;
    private TextView selectedImageNumber;
    private TextView totalPrice;

    public ShopCarAdapter() {
        super(R.layout.shopcaritem);
        //为复选框注册点击事件
        addChildClickViewIds(R.id.shopCarItemcheckBox);
    }

    public HashMap<Integer, UserShopCar> getShoppingBag() {
        return SHOPPING_BAG;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, UserShopCar userShopCar) {

        ImageView shopImage;
        TextView shopPrice;
        TextView shopSize;
        TextView shopImageBuyer;
        ShimmerLayout shimmerLayout;
        CheckBox shopCarItemCheckBox;

        shopImage = baseViewHolder.getView(R.id.shopcaritemimg);
        shopPrice = baseViewHolder.getView(R.id.shopcaritemprice);
        shopSize = baseViewHolder.getView(R.id.shopcaritemsize);
        shopImageBuyer = baseViewHolder.getView(R.id.buyer);
        shimmerLayout = baseViewHolder.getView(R.id.shopCarItemShimmerLayout);
        shopCarItemCheckBox = baseViewHolder.getView(R.id.shopCarItemcheckBox);

        shopCarItemCheckBox.setChecked(userShopCar.isCheck());

        shopPrice.setText(userShopCar.getPrice());
        shopSize.setText(userShopCar.getSize());
        shopImageBuyer.setText("购买人ID：" + userShopCar.getUserId());

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

    @Override
    public void setDiffNewData(@org.jetbrains.annotations.Nullable List<UserShopCar> list) {
        super.setDiffNewData(list);
        SHOP_CAR_ITEM_LIST = list;
        //将checkBox和item关联
        for (int i = 0; i < SHOP_CAR_ITEM_LIST.size(); i++) {
            SHOPPING_BAG.put(i, SHOP_CAR_ITEM_LIST.get(i));
        }
    }

    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {

    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {

    }

    /**
     * 关联适配器内容和结算 UI
     * <br/>传入全选 checkBox，已选图片数量 textView，总价 textView ,用于更新 UI
     *
     * @param selectAllCheckBox
     * @param selectedImageNumber
     * @param totalPrice
     */
    public void connectionAdapterAndSettlementUI(CheckBox selectAllCheckBox, TextView selectedImageNumber, TextView totalPrice) {
        this.selectAllCheckBox = selectAllCheckBox;
        this.selectedImageNumber = selectedImageNumber;
        this.totalPrice = totalPrice;
    }

    /**
     * 勾选 item
     *
     * @param position
     */
    public void selectItem(Integer position) {
        SHOPPING_BAG.get(position).setCheck(true);
        updataSettlementUI(position, 1);
    }

    /**
     * 取消勾选 item
     *
     * @param position
     */
    public void unSelectIem(Integer position) {
        SHOPPING_BAG.get(position).setCheck(false);
        updataSettlementUI(position, -1);
    }

    /**
     * 选择或取消选择 item 时更新结算 UI
     *
     * @param position item位置
     * @param Minus    减数 或 加数
     */
    private void updataSettlementUI(Integer position, int Minus) {
        isCheckAll();
        int totalPrice = Integer.parseInt(this.totalPrice.getText().toString());
        if (Minus > 0) {
            totalPrice = totalPrice + Integer.parseInt(SHOPPING_BAG.get(position).getPrice());
        } else {
            totalPrice = totalPrice - Integer.parseInt(SHOPPING_BAG.get(position).getPrice());
        }
        this.totalPrice.setText("" + totalPrice);
        int selectedImageNumber = Integer.parseInt(this.selectedImageNumber.getText().toString()) + Minus;
        this.selectedImageNumber.setText("" + selectedImageNumber);
    }

    /**
     * 更新全选UI：如果已经全选，则全选按钮打钩,如果不是全选则全选取消打钩
     */
    private void isCheckAll() {
        int t = 0;
        for (int i = 0; i < SHOPPING_BAG.size(); i++) {
            if (SHOPPING_BAG.get(i).isCheck()) {
                t++;
            }
        }
        if (t == SHOPPING_BAG.size()) {
            this.selectAllCheckBox.setChecked(true);
        } else {
            this.selectAllCheckBox.setChecked(false);
        }
    }

    /**
     * 勾选所有 item
     */
    public void selectAllItem() {
        SHOP_CAR_ITEM_LIST.clear();
        int totalPrice = 0;
        for (int i = 0; i < SHOPPING_BAG.size(); i++) {
            SHOPPING_BAG.get(i).setCheck(true);
            SHOP_CAR_ITEM_LIST.add(SHOPPING_BAG.get(i));
            totalPrice += Integer.parseInt(SHOP_CAR_ITEM_LIST.get(i).getPrice());
            setData(i, SHOP_CAR_ITEM_LIST.get(i));
            notifyItemChanged(i);
        }
        this.totalPrice.setText("" + totalPrice);
        this.selectedImageNumber.setText("" + SHOP_CAR_ITEM_LIST.size());
    }

    /**
     * 取消勾选所有 item
     */
    public void unSelectAllItem() {
        SHOP_CAR_ITEM_LIST.clear();
        for (int i = 0; i < SHOPPING_BAG.size(); i++) {
            SHOPPING_BAG.get(i).setCheck(false);
            SHOP_CAR_ITEM_LIST.add(SHOPPING_BAG.get(i));
            setData(i, SHOP_CAR_ITEM_LIST.get(i));
            notifyItemChanged(i);
        }
        this.totalPrice.setText("" + 0);
        this.selectedImageNumber.setText("" + 0);
    }



}
