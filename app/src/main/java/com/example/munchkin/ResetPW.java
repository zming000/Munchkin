package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResetPW extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilResetPW, mtilResetCPW;
    TextInputEditText metResetPW, metResetCPW;
    Button mbtnResetPW;
    Boolean statusRPassword, statusRCPassword, statusRVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pw);

        //obtaining the View with specific ID
        mtilResetPW = findViewById(R.id.tilResetPW);
        mtilResetCPW = findViewById(R.id.tilResetCPW);
        metResetPW = findViewById(R.id.etResetPW);
        metResetCPW = findViewById(R.id.etResetCPW);
        mbtnResetPW = findViewById(R.id.btnResetPW);

        //change error messages
        errorChangeOnEachFields();

        mbtnResetPW.setOnClickListener(v -> {
            //validate each field
            statusRVerification = validationOnEachFields();
            if(statusRVerification){
                updatePassword();
            }
            else{
                Toast.makeText(ResetPW.this, "Please ensure each field input correctly!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields(){
        metResetPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, strong password)
                if(whitespaceExist(Objects.requireNonNull(metResetPW.getText()).toString())){
                    mtilResetPW.setError("Whitespace(s) not allowed!");
                }
                else if(metResetPW.getText().toString().length() < 8 || !digitExist(metResetPW.getText().toString())
                        || !uppercaseExist(metResetPW.getText().toString())){
                    mtilResetPW.setError("Password not strong enough! At least 8 characters with digit(s) and uppercase(s)!");
                }
                else{
                    mtilResetPW.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metResetCPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (match password)
                if(!Objects.requireNonNull(metResetCPW.getText()).toString()
                        .matches(Objects.requireNonNull(metResetPW.getText()).toString())){
                    mtilResetCPW.setError("Password not match!");
                }
                else{
                    mtilResetCPW.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    //Validations on each field to ensure correct input
    private boolean validationOnEachFields(){
        //check input conditions (no whitespace, strong password)
        statusRPassword = !whitespaceExist(Objects.requireNonNull(metResetPW.getText()).toString()) &&
                metResetPW.getText().toString().length() >= 8 &&
                digitExist(metResetPW.getText().toString()) &&
                uppercaseExist(metResetPW.getText().toString());

        //check input condition (match password)
        statusRCPassword = Objects.requireNonNull(metResetCPW.getText()).toString()
                .matches(Objects.requireNonNull(metResetCPW.getText()).toString());

        return statusRPassword && statusRCPassword;
    }

    //update password
    private void updatePassword(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = getIntent().getStringExtra("Username");

        db.collection("Account Details").document(id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        //check the existence of document ID
                        if(Objects.requireNonNull(document).exists()){
                            Map<String,Object> userAcc = new HashMap<>();
                            userAcc.put("Password", Objects.requireNonNull(metResetPW.getText()).toString());

                            db.collection("Account Details").document(id)
                                    .update(userAcc)
                                    .addOnSuccessListener(unused -> {
                                        startActivity(new Intent(ResetPW.this, ResetSuccess.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(ResetPW.this, "Fail to change password!", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
    }

    //Reset Password -> sign in
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Remember password?");
        alertDialogBuilder
                .setMessage("Click yes to login!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                    startActivity(new Intent(ResetPW.this, SignIn.class));
                            finish();
                        })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //validation checking
    //check digit
    private boolean digitExist(String text){
        return text.matches(".*\\d.*");
    }

    //check uppercase
    private boolean uppercaseExist(String text){
        for(int i = 0; i < text.length(); i++) {
            if (Character.isUpperCase(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    //check whitespace
    private boolean whitespaceExist(String text){
        for(int i = 0; i < text.length(); i++){
            if(Character.isWhitespace(text.charAt(i))){
                return true;
            }
        }
        return false;
    }
}