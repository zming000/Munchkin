package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignIn extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilSignInUsername, mtilSignInPW;
    TextInputEditText metSignInUsername, metSignInPW;
    Button mbtnSignIn;
    SharedPreferences spMunchkin;

    //key name
    private static final String SP_NAME = "munchkinPref";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //assign variables
        mtilSignInUsername = findViewById(R.id.tilSignInUsername);
        mtilSignInPW = findViewById(R.id.tilSignInPW);
        metSignInUsername = findViewById(R.id.etSignInUsername);
        metSignInPW = findViewById(R.id.etSignInPW);
        mbtnSignIn = findViewById(R.id.btnSignIn);

        //initialize shared preference
        spMunchkin = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        //get username from shared preference
        String uName = spMunchkin.getString(KEY_USERNAME, null);

        //check if user signed in or not
        if(uName.equals("adminTest01")){
            startActivity(new Intent(SignIn.this, AdminMainActivity.class));
            finishAffinity();
            finish();
        }
        else if(uName != null){
            startActivity(new Intent(SignIn.this, MainActivity.class));
            finishAffinity();
            finish();
        }

        metSignInUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilSignInUsername.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metSignInPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilSignInPW.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnSignIn.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metSignInUsername.getText()).toString().trim().isEmpty()){
                mtilSignInUsername.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metSignInPW.getText()).toString().trim().isEmpty()){
                mtilSignInPW.setError("Field cannot be empty!");
            }
            else{
                FirebaseFirestore munchkinDB = FirebaseFirestore.getInstance();
                String id = metSignInUsername.getText().toString();
                String pw = metSignInPW.getText().toString();

                //specially for admin only
                if(id.equals("adminTest01") && pw.equals("Admin12345")){
                    startActivity(new Intent(SignIn.this, AdminMainActivity.class));

                    SharedPreferences.Editor spEditor = spMunchkin.edit();
                    spEditor.putString(KEY_USERNAME, id);
                    spEditor.apply();

                    finishAffinity();
                    finish();
                }

                munchkinDB.collection("Account Details")
                        .document(id)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot docResult = task.getResult();

                                if (docResult != null) {
                                    //check the existence of username
                                    if (docResult.exists()) {
                                        String pw2 = docResult.getString("password");
                                        //check if the password matched
                                        if (pw.matches(Objects.requireNonNull(pw2))) {
                                            String accStatus = docResult.getString("accountStatus");

                                            if(!accStatus.equals("Offline")){ //check if account being logged in
                                                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                                                alertDialogBuilder.setTitle("Account Logged In");
                                                alertDialogBuilder
                                                        .setMessage("Your account already logged in on another device, please log out from that device first!")
                                                        .setCancelable(false)
                                                        .setPositiveButton("OK", (dialog, iD) -> dialog.cancel());

                                                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                                alertDialog.show();
                                            }
                                            else {
                                                //save necessary data for later use
                                                SharedPreferences.Editor spEditor = spMunchkin.edit();
                                                spEditor.putString(KEY_USERNAME, id);
                                                spEditor.apply();

                                                FirebaseFirestore updateStatus = FirebaseFirestore.getInstance();
                                                Map<String, Object> login = new HashMap<>();
                                                login.put("accountStatus", "Online");

                                                updateStatus.collection("Account Details").document(id)
                                                        .update(login);

                                                startActivity(new Intent(SignIn.this, MainActivity.class));
                                                finish();
                                            }
                                        }
                                        else {
                                            Toast.makeText(SignIn.this, "Wrong Username or Password!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        mtilSignInUsername.setError("Username does not exist!");
                                    }
                                }
                            }
                        });
            }
        });
    }

    //quit app
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

    //sign in -> forgot password
    public void forgotPW(View view) {
        startActivity(new Intent(SignIn.this, ForgotPassword.class));
        finish();
    }

    //sign in -> sign up
    public void signupText(View view) {
        startActivity(new Intent(SignIn.this, SignUp.class));
        finish();
    }
}