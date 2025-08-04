package admin;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.jchiiki.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import admin.fragments.Admin_HomeFragment;
import admin.fragments.Admin_SettingsFragment;

public class Admin_ActivityCallsFragments extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_calls_fragments);

        bottomNavigationView = findViewById(R.id.adminBottomNavigationViewHome);

        replaceFragment(new Admin_HomeFragment());

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;

                int id = item.getItemId();

                if (id == R.id.home) {
                    selectedFragment = new Admin_HomeFragment();
                }
                else if (id == R.id.settings){
                    selectedFragment = new Admin_SettingsFragment();
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