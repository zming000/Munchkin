package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ResetSuccess extends AppCompatActivity {
    //declare variable
    Button mbtnGotoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_success);

        //obtaining the View with specific ID
        mbtnGotoLogin = findViewById(R.id.btnGotoLogin);

        mbtnGotoLogin.setOnClickListener(view -> {
            startActivity(new Intent(ResetSuccess.this, SignIn.class));
            finish();
        });
    }

    //Reset Password -> login
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ResetSuccess.this, SignIn.class));
        finish();
    }
}