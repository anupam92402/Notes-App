package com.example.notesapp.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notesapp.R;
import com.example.notesapp.ui.adapters.ViewPagerAdapter;

import java.util.ArrayList;

public class ViewPagerFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new FirstScreen());
        fragmentList.add(new SecondScreen());
        fragmentList.add(new ThirdScreen());

        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentList,
                requireActivity().getSupportFragmentManager(),
                getLifecycle());

        ViewPager2 vp = view.findViewById(R.id.viewPager);
        vp.setAdapter(adapter);
        return view;
    }
}