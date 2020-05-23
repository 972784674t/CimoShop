package com.example.cimoshop.ui.personalcenter.favorites;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cimoshop.R;

/**
 * @author 谭海山
 */
public class MyFavorites extends Fragment {

    private MyFavoritesViewModel mViewModel;

    public static MyFavorites newInstance() {
        return new MyFavorites();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_favorites_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MyFavoritesViewModel.class);
        // TODO: Use the ViewModel
    }

}
