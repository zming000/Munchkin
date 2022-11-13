package com.example.munchkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity {
    //declare variables
    TextView mtvPhoneText, mtvResend, mtvTimer;
    PinView mpvOTP;
    Button mbtnVerify;
    FirebaseAuth mAuth;
    String verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        //obtaining the View with specific ID
        mtvPhoneText = findViewById(R.id.tvPhoneText);
        mtvResend = findViewById(R.id.tvResend);
        mtvTimer = findViewById(R.id.tvTimer);
        mpvOTP = findViewById(R.id.pvOTP);
        mbtnVerify = findViewById(R.id.btnVerify);

        //return instance of the class
        mAuth = FirebaseAuth.getInstance();

        //get phone number
        String phNum = getIntent().getStringExtra("phoneNumber");
        mtvPhoneText.setText(phNum);

        //send otp
        sendOTPtoUserPhone(phNum);

        //resend otp
        resendOTP();

        mbtnVerify.setOnClickListener(v -> {
            String value = Objects.requireNonNull(mpvOTP.getText()).toString();
            if(value.isEmpty()){
                Toast.makeText(OTPVerification.this, "Invalid OTP Code!", Toast.LENGTH_SHORT).show();
            }
            else {
                verifyCode(value);
            }
        });
    }

    private void sendOTPtoUserPhone(String phNum) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phNum)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //callback functions that handle the results of the request
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential authCred) {
            String code = Objects.requireNonNull(mpvOTP.getText()).toString();
            verifyCode(code);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(OTPVerification.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verID,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verID, token);
            Toast.makeText(OTPVerification.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
            verificationID = verID;
        }
    };

    private void verifyCode(String cred) {
        PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(verificationID, cred);
        signInByCredentials(authCredential);
    }

    private void signInByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth signInCred = FirebaseAuth.getInstance();
        signInCred.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        addInfoToFirestore();
                    }
                    else{
                        if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(OTPVerification.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //add details into firestore
    private void addInfoToFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String value = getIntent().getStringExtra("username");

        //details to insert into firestore
        Map<String,Object> userAcc = new HashMap<>();
        userAcc.put("username", value);
        userAcc.put("firstName", getIntent().getStringExtra("fName"));
        userAcc.put("lastName", getIntent().getStringExtra("lName"));
        userAcc.put("phoneNumber", getIntent().getStringExtra("phoneNumber"));
        userAcc.put("email", getIntent().getStringExtra("email"));
        userAcc.put("password", getIntent().getStringExtra("password"));
        userAcc.put("accountStatus", "Offline");

        db.collection("Account Details").document(value)
                .set(userAcc)
                .addOnSuccessListener(unused -> {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(OTPVerification.this);
                    alertDialogBuilder.setTitle("Created Account Successfully!");
                    alertDialogBuilder
                            .setMessage("Let's try to login!")
                            .setCancelable(false)
                            .setPositiveButton("Login", (dialog, id) -> {
                                startActivity(new Intent(OTPVerification.this, SignIn.class));
                                finishAffinity();
                                finish();
                            });

                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                })
                .addOnFailureListener(e -> {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(OTPVerification.this);
                    alertDialogBuilder.setTitle("Fail to create account!");
                    alertDialogBuilder
                            .setMessage("Please try again!")
                            .setCancelable(false)
                            .setPositiveButton("Try Again",
                                    (dialog, id) -> {
                                        startActivity(new Intent(OTPVerification.this, SignUp.class));
                                        finish();
                                    });

                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                });
    }

    public void resendOTP() {
        mtvResend.setOnClickListener(view1 -> {
            String num = getIntent().getStringExtra("phoneNumber");
            sendOTPtoUserPhone(num);
            mtvResend.setVisibility(View.GONE);
            mtvTimer.setVisibility(View.VISIBLE);

            //countdown timer 60 seconds
            //initialize timer duration
            long duration = TimeUnit.MINUTES.toMillis(1);

            //Initialize countdown timer
            new CountDownTimer(duration, 1000) {
                @Override
                public void onTick(long l) {
                    String secDuration =  String.format(Locale.ENGLISH, "%02d : %02d",
                            TimeUnit.MILLISECONDS.toMinutes(l),
                            TimeUnit.MILLISECONDS.toSeconds(l) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(1)));
                    //Set converted string
                    mtvTimer.setText(secDuration);
                }

                @Override
                public void onFinish() {
                    mtvTimer.setVisibility(View.GONE);
                    mtvResend.setVisibility(View.VISIBLE);
                }
            }.start();
        });
    }
}