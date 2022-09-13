package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.Objects;

public class SignUp extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilUsername, mtilFName, mtilLName, mtilEmail, mtilPassword, mtilCPassword;
    TextInputEditText metUsername, metFName, metLName, metEmail, metPassword, metCPassword;
    Button mbtnNext;
    Boolean statusUsername, statusFName, statusLName, statusEmail, statusPassword, statusCPassword, statusVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //obtaining the View with specific ID
        mtilUsername = findViewById(R.id.tilSignUpUsername);
        mtilFName = findViewById(R.id.tilSignUpFName);
        mtilLName = findViewById(R.id.tilSignUpLName);
        mtilEmail = findViewById(R.id.tilSignUpEmail);
        mtilPassword = findViewById(R.id.tilSignUpPassword);
        mtilCPassword = findViewById(R.id.tilSignUpCPassword);
        metUsername = findViewById(R.id.etSignUpUsername);
        metFName = findViewById(R.id.etSignUpFName);
        metLName = findViewById(R.id.etSignUpLName);
        metEmail = findViewById(R.id.etSignUpEmail);
        metPassword = findViewById(R.id.etSignUpPassword);
        metCPassword = findViewById(R.id.etSignUpCPassword);
        mbtnNext = findViewById(R.id.btnNext);

        //change error messages
        errorChangeOnEachFields();

        mbtnNext.setOnClickListener(v -> {
            //validate each field
            statusVerification = validationOnEachFields();
            if(statusVerification) {
                checkUsername();
            }
            else{
                Toast.makeText(SignUp.this, "Please ensure each field input correctly!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //sign up -> sign in
    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUp.this, SignIn.class));
        finish();
    }

    public void loginText(View view) {
        startActivity(new Intent(SignUp.this, SignIn.class));
        finish();
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields(){
        metUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, with letters, with digit, no uppercase)
                if(Objects.requireNonNull(metUsername.getText()).toString().contains(" ")){
                    mtilUsername.setError("Username cannot contain spaces!");
                }
                else if(!metUsername.getText().toString().matches(".*[a-zA-Z]+.*")){
                    mtilUsername.setError("Please contain letter(s) in Username!");
                }
                else if(!digitExist(metUsername.getText().toString())){
                    mtilUsername.setError("Please contain number(s) in Username!");
                }
                else if(uppercaseExist(metUsername.getText().toString())){
                    mtilUsername.setError("Username cannot contain uppercase!");
                }
                else if(Objects.requireNonNull(metUsername.getText()).length() < 7){
                    mtilUsername.setError("Username too short! At least total of 7 character(s) and number(s)");
                }
                else{
                    mtilUsername.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metFName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (without digit)
                if(digitExist(Objects.requireNonNull(metFName.getText()).toString())){
                    mtilFName.setError("First Name cannot contain number(s)!");
                }
                else{
                    mtilFName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metLName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (without digit)
                if(digitExist(Objects.requireNonNull(metLName.getText()).toString().trim())){
                    mtilLName.setError("Last Name cannot contain number(s)!");
                }
                else{
                    mtilLName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (valid format)
                if(!Objects.requireNonNull(metEmail.getText()).toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
                    mtilEmail.setError("Invalid email format!");
                }
                else{
                    mtilEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, strong password)
                if(whitespaceExist(Objects.requireNonNull(metPassword.getText()).toString())){
                    mtilPassword.setError("Whitespace(s) not allowed!");
                }
                else if(metPassword.getText().toString().length() < 8 || !digitExist(metPassword.getText().toString())
                        || !uppercaseExist(metPassword.getText().toString())){
                    mtilPassword.setError("Password not strong enough! At least 8 characters with digit(s) and uppercase(s)!");
                }
                else{
                    mtilPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metCPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (match password)
                if(!Objects.requireNonNull(metCPassword.getText()).toString()
                        .matches(Objects.requireNonNull(metPassword.getText()).toString())){
                    mtilCPassword.setError("Password not match!");
                }
                else{
                    mtilCPassword.setErrorEnabled(false);
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
        //check input conditions (no whitespace, with letters, with digit, no uppercase)
        statusUsername = !Objects.requireNonNull(metUsername.getText()).toString().contains(" ") &&
                metUsername.getText().toString().matches(".*[a-zA-Z]+.*") &&
                digitExist(metUsername.getText().toString()) &&
                !uppercaseExist(metUsername.getText().toString()) &&
                (Objects.requireNonNull(metUsername.getText()).length() > 7);

        //check input condition (without digit)
        statusFName = !digitExist(Objects.requireNonNull(metFName.getText()).toString());

        //check input condition (without digit)
        statusLName = !digitExist(Objects.requireNonNull(metLName.getText()).toString().trim());

        //check input condition (valid format)
        statusEmail = Objects.requireNonNull(metEmail.getText()).toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

        //check input conditions (no whitespace, strong password)
        statusPassword = !whitespaceExist(Objects.requireNonNull(metPassword.getText()).toString()) &&
                metPassword.getText().toString().length() >= 8 &&
                digitExist(metPassword.getText().toString()) &&
                uppercaseExist(metPassword.getText().toString());

        //check input condition (match password)
        statusCPassword = Objects.requireNonNull(metCPassword.getText()).toString()
                .matches(Objects.requireNonNull(metPassword.getText()).toString());

        return statusUsername && statusFName && statusLName && statusEmail && statusPassword && statusCPassword;
    }

    //check if Username existed
    private void checkUsername(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String value = Objects.requireNonNull(metUsername.getText()).toString();

        db.collection("Account Details").document(value).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        //check the existence of document ID
                        if(Objects.requireNonNull(document).exists()){
                            mtilUsername.setError("Username have been used!");
                        }
                        else{
                            Intent intent = new Intent(SignUp.this, GetPhoneNumber.class);
                            intent.putExtra("usernameNext", metUsername.getText().toString());
                            intent.putExtra("fNameNext", getCapsSentences(Objects.requireNonNull(metFName.getText()).toString()));
                            intent.putExtra("lNameNext", getCapsSentences(Objects.requireNonNull(metLName.getText()).toString()));
                            intent.putExtra("emailNext", Objects.requireNonNull(metEmail.getText()).toString());
                            intent.putExtra("passwordNext", Objects.requireNonNull(metPassword.getText()).toString());

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
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

    //change 1st letter into uppercase
    private String getCapsSentences(String tagName) {
        String[] splitWord = tagName.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splitWord.length; i++) {
            String word = splitWord[i];
            if (i > 0 && word.length() > 0) {
                sb.append(" ");
            }
            String cap = word.substring(0, 1).toUpperCase()
                    + word.substring(1);
            sb.append(cap);
        }
        return sb.toString();
    }
}