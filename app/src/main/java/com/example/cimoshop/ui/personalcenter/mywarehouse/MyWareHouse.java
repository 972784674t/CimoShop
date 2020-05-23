package com.example.cimoshop.ui.personalcenter.mywarehouse;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cimoshop.R;

public class MyWareHouse extends Fragment {

    private MyWareHouseViewModel mViewModel;

    public static MyWareHouse newInstance() {
        return new MyWareHouse();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_ware_house_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MyWareHouseViewModel.class);
        // TODO: Use the ViewModel
    }

}
