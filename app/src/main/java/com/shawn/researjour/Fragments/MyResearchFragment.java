package com.shawn.researjour.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shawn.researjour.R;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyResearchFragment extends Fragment {

    private Toolbar mTopToolbar;



    public MyResearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_research, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        mTopToolbar=getActivity().findViewById(R.id.toolbar);
        mTopToolbar.setVisibility(View.VISIBLE);
        super.onCreate(savedInstanceState);
    }

}
