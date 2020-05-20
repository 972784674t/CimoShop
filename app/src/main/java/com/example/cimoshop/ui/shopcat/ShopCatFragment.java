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


/**
 * @author 谭海山
 */
public class ShopCatFragment extends Fragment {

    private ShopCatViewModel shopCatViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shopCatViewModel =
                ViewModelProviders.of(this).get(ShopCatViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shopcat, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        shopCatViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
