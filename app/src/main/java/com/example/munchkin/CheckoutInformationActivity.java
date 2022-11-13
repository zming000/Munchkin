package com.example.munchkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CheckoutInformationActivity extends AppCompatActivity {

    //
    // constant values
    //
    //key name
    private static final String SP_NAME = "munchkinPref";
    private static final String KEY_USERNAME = "username";

    //
    // views
    //
    ImageView mBackBtn;

    ConstraintLayout mOrderSummary;
    TextView showOrderSummaryTxt, mUsernameText, mEmailText;
    ImageView dropDownArrow;
    LinearLayout mOrderSummaryDropDown;
    SharedPreferences spMunchkin;


    TextInputLayout mtilCountryRegion, mtilFirstName, mtilLastName, mtilAddress, mtilPostcode, mtilCity, mtilStateTerritory, mtilPhone;
    TextInputEditText mShippingCountry, mFirstName, mLastName, mCompany, mAddress, mApartment, mPostcode, mCity, mState, mPhone;
    Boolean statusShippingCountry, statusFirstName, statusLastName, statusAddress, statusPostcode, statusCity, statusState, statusPhone, statusVerification;
    Button mContinueToShipBtn;

    FirebaseFirestore getEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_information);

        //
        // find views
        //
        mBackBtn = findViewById(R.id.CheckoutPage1_backImageView);
        mUsernameText = findViewById(R.id.checkoutPage_contactInfo_username);
        mEmailText = findViewById(R.id.checkoutPage_contactInfo_email);
        mOrderSummary = findViewById(R.id.orderSummary_constraintLayout1);
        showOrderSummaryTxt = findViewById(R.id.CheckoutPage1_showOrderSummary_textView);
        dropDownArrow = findViewById(R.id.drop_down_for_more);
        mOrderSummaryDropDown = findViewById(R.id.orderSummary_dropDown);
        mContinueToShipBtn = findViewById(R.id.btnContinueToShipping);
        mtilCountryRegion = findViewById(R.id.tilCountryRegion);
        mtilFirstName = findViewById(R.id.tilFirstName);
        mtilLastName = findViewById(R.id.tilLastName);
        mtilAddress = findViewById(R.id.tilAddress);
        mtilPostcode = findViewById(R.id.tilPostcode);
        mtilCity = findViewById(R.id.tilCity);
        mtilStateTerritory = findViewById(R.id.tilStateTerritory);
        mtilPhone = findViewById(R.id.tilPhone);
        mShippingCountry = findViewById(R.id.etCountryRegion);
        mFirstName = findViewById(R.id.etFirstName);
        mLastName = findViewById(R.id.etLastName);
        mCompany = findViewById(R.id.etCompany);
        mAddress = findViewById(R.id.etAddress);
        mApartment = findViewById(R.id.etApartmentSuite);
        mPostcode = findViewById(R.id.etPostcode);
        mCity = findViewById(R.id.etCity);
        mState = findViewById(R.id.etStateTerritory);
        mPhone = findViewById(R.id.etPhone);

        //initialize firestore
        getEmail = FirebaseFirestore.getInstance();
        //initialize shared preference
        spMunchkin = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        //get username from shared preference
        String uName = "tester1";
                //spMunchkin.getString(KEY_USERNAME, null);

        mUsernameText.setText(uName);
        getEmail.collection("Account Details").document(uName).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();

                        mEmailText.setText(doc.getString("email"));
                    }
                });



        //
        //initialization of values...
        //
//        mOrderSummary.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        // initialize order summary values from database for constraint layout and linear layout
        // initialize contact info

        //
        // listeners
        //
        mBackBtn.setOnClickListener(v -> {
            //go back to shopping cart activity
            finish();
        });

        mOrderSummary.setOnClickListener(v -> {

            //depending on situation...

            //0- change title
            showOrderSummaryTxt.setText("Hide order summary");

            //1- change arrow image
            ImageView imageView = (ImageView) v;
            assert (R.id.drop_down_for_more == imageView.getId());

            Integer integer = (Integer) imageView.getTag();
            integer = integer == null ? 0 : integer;

            switch (integer) {

                case R.drawable.ic_arrow_down:
                    imageView.setImageResource(R.drawable.ic_arrow_up);
                    imageView.setTag(R.drawable.ic_arrow_up);
                    break;
                case R.drawable.ic_arrow_up:
                    imageView.setImageResource(R.drawable.ic_arrow_down);
                    imageView.setTag(R.drawable.ic_arrow_down);
                    break;

            }

            //2- make order summary drop down visible / gone
            TransitionManager.beginDelayedTransition(mOrderSummaryDropDown, new AutoTransition());

            //determine visibility
            int viLinearLayout = (mOrderSummaryDropDown.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
            mOrderSummaryDropDown.setVisibility(viLinearLayout);

        });

        mContinueToShipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change error messages
                errorChangeOnEachFields();

                //validate each field
                statusVerification = validationOnEachFields();
                if(statusVerification) {
                    //go shipping ui
                    Intent intent = new Intent(CheckoutInformationActivity.this, CheckoutShippingActivity.class);
                    intent.putExtra("country", mShippingCountry.getText().toString());
                    intent.putExtra("firstName", mFirstName.getText().toString());
                    intent.putExtra("lastName", mLastName.getText().toString());
                    intent.putExtra("company", mCompany.getText().toString());
                    intent.putExtra("address", mAddress.getText().toString());
                    intent.putExtra("apartment", mApartment.getText().toString());
                    intent.putExtra("postcode", mPostcode.getText().toString());
                    intent.putExtra("city", mCity.getText().toString());
                    intent.putExtra("state", mState.getText().toString());
                    intent.putExtra("phoneNumber", "+60" + mPhone.getText().toString());
                    intent.putExtra("email", mEmailText.getText().toString());

                    startActivity(intent);
                }
                else{
                    Toast.makeText(CheckoutInformationActivity.this, "Please ensure each field input correctly!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Validations on each field to ensure correct input
    private boolean validationOnEachFields(){
        statusShippingCountry = !mShippingCountry.getText().toString().isEmpty() && !digitExist(mShippingCountry.getText().toString());

        statusFirstName = !digitExist(mFirstName.getText().toString());

        statusLastName = !mLastName.getText().toString().isEmpty() && !digitExist(mLastName.getText().toString());

        statusAddress = !mAddress.getText().toString().isEmpty();

        statusPostcode = !mPostcode.getText().toString().isEmpty() && digitExist(mPostcode.getText().toString()) &&
                !mPostcode.getText().toString().matches(".*[a-zA-Z]+.*");

        statusCity = !mCity.getText().toString().isEmpty() && !digitExist(mCity.getText().toString());

        statusState = !mState.getText().toString().isEmpty() && !digitExist(mState.getText().toString());

        statusPhone = !mPhone.getText().toString().isEmpty() && mPhone.getText().length() >= 9;

        return statusShippingCountry && statusFirstName && statusLastName && statusAddress && statusPostcode && statusCity && statusState && statusPhone;
    }

    private void errorChangeOnEachFields() {
        mShippingCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check empty input and numeric existence
                if(mShippingCountry.getText().toString().isEmpty()){
                    mtilCountryRegion.setError("Please enter a country");
                }
                else if(digitExist(mShippingCountry.getText().toString())){
                    mtilCountryRegion.setError("No numeric should be contained!");
                }
                else{
                    mtilCountryRegion.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check numeric existence
                if(digitExist(mFirstName.getText().toString())){
                    mtilFirstName.setError("No numeric should be contained!");
                }
                else{
                    mtilFirstName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check empty input and numeric existence
                if(mLastName.getText().toString().isEmpty()){
                    mtilLastName.setError("Please enter your last name!");
                }
                else if(digitExist(mLastName.getText().toString())){
                    mtilLastName.setError("No numeric should be contained!");
                }
                else{
                    mtilLastName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check empty input
                if(mAddress.getText().toString().isEmpty()){
                    mtilAddress.setError("Please enter your address!");
                }
                else{
                    mtilAddress.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mPostcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check empty input, numeric existence, and letters existence
                if(mPostcode.getText().toString().isEmpty()){
                    mtilPostcode.setError("Please enter your postcode!");
                }
                else if(!digitExist(mLastName.getText().toString())){
                    mtilPostcode.setError("Postcode should contain numerics only!");
                }
                else if(mPostcode.getText().toString().matches(".*[a-zA-Z]+.*")){
                    mtilPostcode.setError("Postcode should not contain letter(s)!");
                }
                else{
                    mtilPostcode.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check empty input and numeric existence
                if(mCity.getText().toString().isEmpty()){
                    mtilCity.setError("Please enter your city!");
                }
                else if(digitExist(mCity.getText().toString())){
                    mtilCity.setError("City should not contain numerics!");
                }
                else{
                    mtilCity.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, with letters, with digit, no uppercase)
                if(mState.getText().toString().isEmpty()){
                    mtilStateTerritory.setError("Please enter your city!");
                }
                else if(digitExist(mState.getText().toString())){
                    mtilStateTerritory.setError("State should not contain numerics!");
                }
                else{
                    mtilStateTerritory.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check empty input and length of the phone number
                if(mPhone.getText().toString().isEmpty()){
                    mtilPhone.setError("Please enter your phone number!");
                }
                else if(mPhone.getText().length() < 9){
                    mtilPhone.setError("Invalid length of phone number!");
                }
                else{
                    mtilPhone.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    //validation checking
    //check digit
    private boolean digitExist(String text){
        return text.matches(".*\\d.*");
    }
}