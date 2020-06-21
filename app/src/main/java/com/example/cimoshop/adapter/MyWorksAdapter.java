package com.example.cimoshop.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.cimoshop.R;
import com.example.cimoshop.entity.UserShopCar;

import org.jetbrains.annotations.NotNull;

/**
 * 我的作品适配器
 *
 * @author 谭海山
 */
public class MyWorksAdapter extends BaseQuickAdapter<UserShopCar, BaseViewHolder> {

    public MyWorksAdapter() {
        super(R.layout.fragment_my_works);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, UserShopCar userShopCar) {

    }

}
