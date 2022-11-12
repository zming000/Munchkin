package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5000;

    private Animation topAnim;
    private ImageView appLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.splash_screen);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);

        //Hooks
        appLogo = findViewById(R.id.splashscreen_logo);

        appLogo.setAnimation(topAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, SignIn.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }
}