package com.example.cimoshop.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.cimoshop.R;
import com.example.cimoshop.entity.UserWareHouses;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author 谭海山
 */
public class MyWareHouseAdapter extends BaseQuickAdapter<UserWareHouses, BaseViewHolder> {

    public MyWareHouseAdapter(@Nullable List<UserWareHouses> data) {
        super(R.layout.my_works_fragment, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, UserWareHouses userWareHouses) {

    }

}
