package com.wid.rvtracklibrary;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.wid.rvtracklibrary.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());

    }

    private void start() {
        new PagerSnapHelper().attachToRecyclerView(binding.titleRecycler);

        final TitleAdapter titleAdapter = new TitleAdapter(this);
        binding.titleRecycler.setAdapter(titleAdapter);


        //binding.rvTrackView.attachToRecyclerView(binding.titleRecycler);

        titleAdapter.setTitles(generateSampleTitles());

        new Handler().postDelayed(()->{
            //titleAdapter.removeTitles(3);
        }, 10000);
    }
    private void replaceFragment(final Fragment fragment) {
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.getClass().getName());
        fragmentTransaction.commit();
    }

    private List<String> generateSampleTitles() {
        final List<String> titles = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            titles.add("Title " + (i + 1));
        }
        return titles;
    }


}