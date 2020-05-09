package com.example.cimoshop.ui.shopcat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShopCatViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShopCatViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is shopcat fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}