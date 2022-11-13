package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Objects;

public class AdminMainActivity extends AppCompatActivity {

    BottomNavigationView adminBottomNavView;
    Menu adminMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_admin);


        adminBottomNavView = findViewById(R.id.adminBottomNav);
        adminMenu = adminBottomNavView.getMenu();
        adminBottomNavView.setOnItemSelectedListener(adminNavListener);

        int intentFragment = 0;

        //check if got intent
        Bundle extra = getIntent().getExtras();
        if (extra != null)
        {
            intentFragment = extra.getInt("loadOrderFrag");
        }

        if (intentFragment == 1)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.adminPageContainer, new OrderFragment()).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.adminPageContainer, new AdminFragment()).commit();
        }
    }

    private final NavigationBarView.OnItemSelectedListener adminNavListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.administration:
                        selectedFragment = new AdminFragment();
                        break;
                    case R.id.orders:
                        selectedFragment = new OrderFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.adminPageContainer, Objects.requireNonNull(selectedFragment)).commit();

                return true;
            };
}