package com.example.rizvan.myart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rizvan.myart.R;

/**
 * Created by Rizvan on 24.10.2015.
 */
public class DefaultFragmentTabTwo extends Fragment{

    private static final int LAYOUT = R.layout.default_fragment_two;

    private View view;

    public static DefaultFragmentTabTwo getInstance () {
        Bundle args = new Bundle();
        DefaultFragmentTabTwo fragment = new DefaultFragmentTabTwo();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        return view;
    }
}
