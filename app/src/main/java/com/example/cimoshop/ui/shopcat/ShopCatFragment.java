package com.example.cimoshop.ui.shopcat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.example.cimoshop.R;
import com.example.cimoshop.mytools.MyTools;
import com.google.android.material.appbar.MaterialToolbar;


/**
 * @author 谭海山
 */
public class ShopCatFragment extends Fragment {

    private ShopCatViewModel shopCatViewModel;
    private MaterialToolbar toolbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shopCatViewModel = ViewModelProviders.of(this).get(ShopCatViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shopcat, container, false);

        final TextView textView = root.findViewById(R.id.text_notifications);

        shopCatViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        toolbar = root.findViewById(R.id.shopCatToobar);

        //状态栏文字透明
        MyTools.makeStatusBarTransparent(getActivity());

        //修复标题栏与状态栏重叠
        MyTools.fitTitleBar(getActivity(),toolbar);

        return root;
    }
}
