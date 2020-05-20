package com.example.cimoshop.ui.personalcenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.cimoshop.R;
import com.example.cimoshop.mytools.MyTools;
import com.example.cimoshop.ui.login.Login;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/**
 * @author 谭海山
 */
public class PersonalCenter extends Fragment {

    public static PersonalCenter newInstance() {
        return new PersonalCenter();
    }

    private MaterialButton button;
    private MaterialToolbar toolbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.personal_center_fragment,container,false);
        toolbar = root.findViewById(R.id.personalCenterToobar);

        //状态栏文字透明
        MyTools.makeStatusBarTransparent(getActivity());

        //修复标题栏与状态栏重叠
        MyTools.fitTitleBar(getActivity(),toolbar);

        button = root.findViewById(R.id.button);


        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PersonalCenterViewModel mViewModel = ViewModelProviders.of(this).get(PersonalCenterViewModel.class);
        // TODO: Use the ViewModel

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });
    }

}
