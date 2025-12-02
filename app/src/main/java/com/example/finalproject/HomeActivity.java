package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.finalproject.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView navView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);


        navView = findViewById(R.id.bottomNav);
        Fragment homeNavFragment = new HomeNavFragment();
        Fragment profileNavFragment = new ProfileNavFragment();
        Fragment leaderboardFragment = new LeaderboardFragment();

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        SharedViewModel model = new ViewModelProvider(this).get(SharedViewModel.class);
        model.fetchUserData(userId);

        setCurrentFragment(homeNavFragment);

        navView.setOnItemSelectedListener(item -> {
            int id = item.getItemId(); //makes life easier
            if (id == R.id.home) {
                setCurrentFragment(homeNavFragment);
            } else if (id == R.id.leaderboard) {
                setCurrentFragment(leaderboardFragment);
            } else if (id == R.id.profile) {
                setCurrentFragment(profileNavFragment);
            }
            return true;
        });

    }


    public void setCurrentFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).commit();
    }
}