package com.example.cimoshop.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.cimoshop.R;
import com.example.cimoshop.entity.UserShopCar;

import org.jetbrains.annotations.NotNull;

/**
 * @author 谭海山
 */
public class MyWorksAdapter extends BaseQuickAdapter<UserShopCar, BaseViewHolder> {

    public MyWorksAdapter() {
        super(R.layout.my_works_fragment);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, UserShopCar userShopCar) {

    }

}