package com.wid.rvtracklibrary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wid.rvtracklibrary.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init();
        new PagerSnapHelper().attachToRecyclerView(binding.titleRecycler);

        final TitleAdapter titleAdapter = new TitleAdapter(getContext());
        binding.titleRecycler.setAdapter(titleAdapter);

        binding.titleRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        //binding.rvTrackView.attachToRecyclerView(binding.titleRecycler);

        titleAdapter.setTitles(generateSampleTitles());

        return binding.getRoot();
    }

    private void init() {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
    }

    private List<String> generateSampleTitles() {
        final List<String> titles = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            titles.add("Title " + (i + 1));
        }
        return titles;
    }


}