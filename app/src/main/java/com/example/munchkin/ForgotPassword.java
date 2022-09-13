package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilFPWUsername, mtilFPWPhoneNumber;
    TextInputEditText metFPWUsername, metFPWPhoneNumber;
    Button mbtnGetOTP;
    FirebaseFirestore userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //obtaining the View with specific ID
        mtilFPWUsername = findViewById(R.id.tilFPWUsername);
        mtilFPWPhoneNumber = findViewById(R.id.tilFPWPhoneNumber);
        metFPWUsername = findViewById(R.id.etFPWUsername);
        metFPWPhoneNumber = findViewById(R.id.etFPWPhoneNumber);
        mbtnGetOTP = findViewById(R.id.btnGetOTP);

        //return instance of the class
        userDB = FirebaseFirestore.getInstance();

        //change error message
        metFPWUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilFPWUsername.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metFPWPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilFPWPhoneNumber.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnGetOTP.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metFPWUsername.getText()).toString().trim().isEmpty()){
                mtilFPWUsername.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metFPWPhoneNumber.getText()).toString().trim().isEmpty()){
                mtilFPWPhoneNumber.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metFPWPhoneNumber.getText()).length() < 9){
                mtilFPWPhoneNumber.setError("Invalid length of phone number!");
            }
            else{
                String id = Objects.requireNonNull(metFPWUsername.getText()).toString();

                userDB.collection("Account Details")
                        .document(id)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document != null) {
                                    //check the existence of document/username
                                    if (document.exists()) {
                                        String phText = document.getString("Phone Number");

                                        if(Objects.requireNonNull(phText).equals("+60" + metFPWPhoneNumber.getText().toString())) {
                                            //proceed to verify otp
                                            Intent intent = new Intent(ForgotPassword.this, FPWOtp.class);
                                            intent.putExtra("username", id);
                                            intent.putExtra("phNum", "+60" + metFPWPhoneNumber.getText().toString());

                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                        else{
                                            mtilFPWPhoneNumber.setError("Use Verified Phone Number!");
                                        }
                                    }
                                    else {
                                        mtilFPWUsername.setError("Username does not exist!");
                                    }
                                }
                            }
                        });
            }
        });
    }

    public void backToLogin(View view) {
    }
}