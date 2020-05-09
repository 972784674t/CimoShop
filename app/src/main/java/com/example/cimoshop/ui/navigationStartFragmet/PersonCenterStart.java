package com.example.cimoshop.ui.navigationStartFragmet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cimoshop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonCenterStart extends Fragment {

    public PersonCenterStart() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person_center_start, container, false);
    }
}
