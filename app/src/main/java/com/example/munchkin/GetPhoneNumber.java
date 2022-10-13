package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class GetPhoneNumber extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilPhoneNumber;
    TextInputEditText metPhoneNumber;
    Button mbtnGetOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_phone_number);

        //obtaining the View with specific ID
        mtilPhoneNumber = findViewById(R.id.tilSignUpPhoneNumber);
        metPhoneNumber = findViewById(R.id.etSignUpPhoneNumber);
        mbtnGetOTP = findViewById(R.id.btnGetOTP);

        //change error message
        metPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilPhoneNumber.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnGetOTP.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to next page
            if(Objects.requireNonNull(metPhoneNumber.getText()).toString().trim().isEmpty()){
                mtilPhoneNumber.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metPhoneNumber.getText()).length() < 9){
                mtilPhoneNumber.setError("Invalid length of phone number!");
            }
            else{
                //proceed to verify otp
                Intent intent = new Intent(GetPhoneNumber.this, OTPVerification.class);
                intent.putExtra("username", getIntent().getStringExtra("usernameNext"));
                intent.putExtra("fName", getIntent().getStringExtra("fNameNext"));
                intent.putExtra("lName", getIntent().getStringExtra("lNameNext"));
                intent.putExtra("phoneNumber", "+60" + metPhoneNumber.getText().toString());
                intent.putExtra("email", getIntent().getStringExtra("emailNext"));
                intent.putExtra("password", getIntent().getStringExtra("passwordNext"));

                startActivity(intent);
            }
        });
    }
}