package com.example.jchiiki;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.jchiiki.fragments.AchievementsFragment;
import com.example.jchiiki.fragments.EveryDayFragment;
import com.example.jchiiki.fragments.HomeFragment;
import com.example.jchiiki.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ActivityCallsFragments extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calls_fragments);

        bottomNavigationView = findViewById(R.id.bottomNavigationViewHome);

        replaceFragment(new HomeFragment());

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;

                int id = item.getItemId();

                if (id == R.id.home) {
                    selectedFragment = new HomeFragment();
                }
                else if (id == R.id.everyday_practice) {
                    selectedFragment = new EveryDayFragment();
                }
                else if (id == R.id.achievements) {
                    selectedFragment = new AchievementsFragment();
                }
                else if (id == R.id.settings) {
                    selectedFragment = new SettingsFragment();
                }

                replaceFragment(selectedFragment);

                return true;
            }
        });
    }

    // Phương thức để thay thế fragment
    private void replaceFragment(Fragment selectedFragment) {
        // Tạo một giao dịch để thay thế fragment
        getSupportFragmentManager().beginTransaction() // Thêm giao dịch vào hàng đợi
                .replace(R.id.fragmentContainerView, selectedFragment) // Thay thế fragment hiện tại bằng fragment mới
                .commit(); // Thực hiện giao dịch
    }


}