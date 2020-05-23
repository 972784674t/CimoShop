package com.example.cimoshop.ui.shopcat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author 谭海山
 */
public class ShopCatViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShopCatViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("当前购物车空空如也哦");
    }

    public LiveData<String> getText() {
        return mText;
    }
}