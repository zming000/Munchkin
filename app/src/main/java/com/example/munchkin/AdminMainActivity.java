package com.example.munchkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView adminBottomNavView;
    private Menu adminMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main_admin);


        adminBottomNavView = findViewById(R.id.adminBottomNav);
        adminMenu = adminBottomNavView.getMenu();
        adminBottomNavView.setOnItemSelectedListener(adminNavListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.adminPageContainer, new AdminFragment()).commit();
    }

    private NavigationBarView.OnItemSelectedListener adminNavListener =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.administration:
                            selectedFragment = new AdminFragment();
                            break;
                        case R.id.orders:
                            selectedFragment = new OrderFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.adminPageContainer, selectedFragment).commit();

                    return true;
                }
            };
}