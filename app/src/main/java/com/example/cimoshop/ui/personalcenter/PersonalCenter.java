package com.example.cimoshop.ui.personalcenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.cimoshop.R;
import com.example.cimoshop.databinding.PersonalCenterFragmentBinding;

public class PersonalCenter extends Fragment {

    private PersonalCenterViewModel mViewModel;
    PersonalCenterFragmentBinding binding;


    public static PersonalCenter newInstance() {
        return new PersonalCenter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.personal_center_fragment,container,false);
        //setHasOptionsMenu(true);
        // final NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_personalCenter_to_loginFragment);
            }
        });


        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PersonalCenterViewModel.class);
        // TODO: Use the ViewModel
    }

}
