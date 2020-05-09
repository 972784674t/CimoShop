package com.example.cimoshop.ui.goodsclass;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GoodsClassViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GoodsClassViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is goods class fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}