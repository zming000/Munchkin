package com.example.munchkin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Account extends AppCompatActivity {
    Button mbtnLogout;
    CardView mcvOrderHistory;
    SharedPreferences spMunchkin;
    TextView mtvUName, mtvEmail;
    FirebaseFirestore getEmail;

    //key name
    private static final String SP_NAME = "munchkinPref";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        mtvUName = findViewById(R.id.tvUName);
        mtvEmail = findViewById(R.id.tvEmail);
        mcvOrderHistory = findViewById(R.id.cvOrderHistory);
        mbtnLogout = findViewById(R.id.btnLogout);

        getEmail = FirebaseFirestore.getInstance();
        spMunchkin = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        String id = spMunchkin.getString(KEY_USERNAME, null);

        mtvUName.setText(id);

        getEmail.collection("Account Details").document(id).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();

                            mtvEmail.setText(doc.getString("email"));
                        }
                    });

        mcvOrderHistory.setOnClickListener(view -> {
            startActivity(new Intent(Account.this, OrderHistory.class));
        });

        mbtnLogout.setOnClickListener(view -> {
            //logout
            spMunchkin.edit().clear().apply();

            FirebaseFirestore updateStatus = FirebaseFirestore.getInstance();
            Map<String,Object> logout = new HashMap<>();
            logout.put("accountStatus", "Offline");

            updateStatus.collection("Account Details").document(id)
                    .update(logout);

            startActivity(new Intent(Account.this, SignIn.class));
            finishAffinity();
            finish();
        });

        bottomNavigationView.setSelectedItemId(R.id.account);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.account:
                    return true;
            }
            return false;
        });
    }

    //quit application
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Leaving Munchkin?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    finishAffinity();
                    finish();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}