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

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FPWOtp extends AppCompatActivity {
    //declare variables
    TextView mtvFPWPhoneText, mtvFPWResend, mtvTimer;
    PinView mpvFPWOtp;
    Button mbtnFPWVerify;
    FirebaseAuth mAuth;
    String verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpw_otp);

        //obtaining the View with specific ID
        mtvFPWPhoneText = findViewById(R.id.tvFPWPhoneText);
        mtvFPWResend = findViewById(R.id.tvFPWResend);
        mtvTimer = findViewById(R.id.tvTimer);
        mpvFPWOtp = findViewById(R.id.pvFPWOtp);
        mbtnFPWVerify = findViewById(R.id.btnFPWVerify);

        //return instance of the class
        mAuth = FirebaseAuth.getInstance();

        //get phone number
        String phNum = getIntent().getStringExtra("phNum");
        mtvFPWPhoneText.setText(phNum);

        //send otp
        sendOTPtoPhone(phNum);

        //resend otp
        resendFPWOtp();

        mbtnFPWVerify.setOnClickListener(v -> {
            String value = Objects.requireNonNull(mpvFPWOtp.getText()).toString();
            if(value.isEmpty()){
                Toast.makeText(FPWOtp.this, "Invalid OTP Code!", Toast.LENGTH_SHORT).show();
            }
            else {
                verifyCode(value);
            }
        });
    }

    private void sendOTPtoPhone(String phNum) {
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
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            String code = Objects.requireNonNull(mpvFPWOtp.getText()).toString();
            verifyCode(code);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(FPWOtp.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verID,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verID, token);
            Toast.makeText(FPWOtp.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(FPWOtp.this, ResetPW.class);
                        intent.putExtra("Username", getIntent().getStringExtra("username"));

                        startActivity(intent);
                        finishAffinity();
                        finish();
                    }
                    else{
                        if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(FPWOtp.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void resendFPWOtp() {
        mtvFPWResend.setOnClickListener(view1 -> {
            String num = getIntent().getStringExtra("phNum");
            sendOTPtoPhone(num);
            mtvFPWResend.setVisibility(View.GONE);
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
                    mtvFPWResend.setVisibility(View.VISIBLE);
                }
            }.start();
        });
    }
}