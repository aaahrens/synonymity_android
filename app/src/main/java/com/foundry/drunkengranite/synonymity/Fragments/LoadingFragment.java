package com.foundry.drunkengranite.synonymity.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foundry.drunkengranite.synonymity.R;

/**
 * @author by drunkengranite
 * @date Created  on 5/20/17.
 * @description the android app for the game synonymity, where a user guesses synonyms
 * @class java 17.11
 * @class_description for use in the java class at the srjc 17.11 under sean kirkpatrick,
 * this is a java class at srjc
 * @maintainer drunkengranite
 * @license set ref MIT
 */

public class LoadingFragment extends Fragment
{


    public LoadingFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    public static LoadingFragment newInstance()
    {

        Bundle args = new Bundle();

        LoadingFragment fragment = new LoadingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
