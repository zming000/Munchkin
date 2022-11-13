package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button mbtnLogout;
    SharedPreferences spMunchkin;

    //key name
    private static final String SP_NAME = "munchkinPref";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mbtnLogout = findViewById(R.id.btnLogout);

        mbtnLogout.setOnClickListener(view -> {
            //logout
            spMunchkin = getSharedPreferences(SP_NAME, MODE_PRIVATE);
            String id = spMunchkin.getString(KEY_USERNAME, null);
            spMunchkin.edit().clear().apply();

            FirebaseFirestore updateStatus = FirebaseFirestore.getInstance();
            Map<String,Object> logout = new HashMap<>();
            logout.put("accountStatus", "Offline");

            updateStatus.collection("Account Details").document(id)
                    .update(logout);

            startActivity(new Intent(MainActivity.this, SignIn.class));
            finishAffinity();
            finish();
        });
    }
}