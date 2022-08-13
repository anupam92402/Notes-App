package com.example.notesapp.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.notesapp.R;

public class SplashScreenFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_splash_screen, container, false);

        Animation topAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.top_animation);
        Animation downAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.down_animation);

        view.findViewById(R.id.app_logo).setAnimation(topAnim);
        view.findViewById(R.id.app_name).setAnimation(downAnim);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Navigation.findNavController(view).navigate(R.id.action_splashScreenFragment_to_loginFragment);
            }
        }, 3000);
        return view;
    }


}