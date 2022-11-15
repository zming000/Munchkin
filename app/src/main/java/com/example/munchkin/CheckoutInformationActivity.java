package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CheckoutInformationActivity extends AppCompatActivity {

    //
    // views
    //
    CartItemAdapter mAdapter;
    ArrayList<CartItem> mCartItemArrayList;
    ImageView mBackBtn;
    RecyclerView mcartSummary_items_recycler_view;
    ConstraintLayout mOrderSummary;
    LinearLayout mOrderSummaryDropDown;
    TextView showOrderSummaryTxt, mUsernameText, mEmailText, mCheckoutPage1_orderTotalPrice_textView, msubtotal_value, mtotal_value, mtotal_qty;
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
        mtotal_qty = findViewById(R.id.total_qtyValue);
        mtotal_value = findViewById(R.id.total_value);
        msubtotal_value = findViewById(R.id.subtotal_value);
        mCheckoutPage1_orderTotalPrice_textView = findViewById(R.id.CheckoutPage1_orderTotalPrice_textView);
        mcartSummary_items_recycler_view = findViewById(R.id.cartSummary_items_recycler_view);
        mBackBtn = findViewById(R.id.CheckoutPage1_backImageView);
        mUsernameText = findViewById(R.id.checkoutPage_contactInfo_username);
        mEmailText = findViewById(R.id.checkoutPage_contactInfo_email);
        mOrderSummary = findViewById(R.id.orderSummary_constraintLayout1);
        showOrderSummaryTxt = findViewById(R.id.CheckoutPage1_showOrderSummary_textView);
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

        //get username from shared preference
        String uName = getIntent().getStringExtra("username");

        String country = getIntent().getStringExtra("country");

        if(country != null){
            mShippingCountry.setText(getIntent().getStringExtra("country"));
            mFirstName.setText(getIntent().getStringExtra("firstName"));
            mLastName.setText(getIntent().getStringExtra("lastName"));
            mCompany.setText(getIntent().getStringExtra("company"));
            mAddress.setText(getIntent().getStringExtra("address"));
            mApartment.setText(getIntent().getStringExtra("apartment"));
            mPostcode.setText(getIntent().getStringExtra("postcode"));
            mCity.setText(getIntent().getStringExtra("city"));
            mState.setText(getIntent().getStringExtra("state"));
            mPhone.setText(getIntent().getStringExtra("phoneNumber"));
        }


        mcartSummary_items_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        mCartItemArrayList = new ArrayList<>();
        mAdapter = new CartItemAdapter(this, mCartItemArrayList, uName);
        mcartSummary_items_recycler_view.setAdapter(mAdapter);

        getOrderDetailsFromFirestore(uName);

        mUsernameText.setText(uName);
        getEmail.collection("Account Details").document(uName).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();

                        mEmailText.setText(doc.getString("email"));
                    }
                });

        mBackBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutInformationActivity.this, ShoppingCartActivity.class);
            startActivity(intent);
            finish();
        });

        mOrderSummary.setOnClickListener(v -> {
            mOrderSummaryDropDown.getLayoutTransition().enableTransitionType(LayoutTransition.APPEARING);

            //determine visibility
            int viLinearLayout = (mOrderSummaryDropDown.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
            mOrderSummaryDropDown.setVisibility(viLinearLayout);

            ImageView imageView = findViewById(R.id.drop_down_for_more);

            if(mOrderSummaryDropDown.getVisibility() == View.VISIBLE){
                imageView.setImageResource(R.drawable.ic_arrow_up);
                imageView.setTag(R.drawable.ic_arrow_up);
                showOrderSummaryTxt.setText("Hide order summary");

            }
            else{
                imageView.setImageResource(R.drawable.ic_arrow_down);
                imageView.setTag(R.drawable.ic_arrow_down);
                showOrderSummaryTxt.setText("Show order summary");
            }

        });

        //change error messages
        errorChangeOnEachFields();

        mContinueToShipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    intent.putExtra("phoneNumber", mPhone.getText().toString());
                    intent.putExtra("email", mEmailText.getText().toString());
                    intent.putExtra("username", uName);

                    startActivity(intent);
                    finishAffinity();
                    finish();
                }
                else{
                    Toast.makeText(CheckoutInformationActivity.this, "Please ensure each field input correctly!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getOrderDetailsFromFirestore(String username) {
        FirebaseFirestore cartDB = FirebaseFirestore.getInstance();
        ArrayList<String> id = new ArrayList<>();

        cartDB.collection("Account Details").document(username).collection("Shopping Cart")
                .whereEqualTo("status", "Unpaid")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(CheckoutInformationActivity.this, "Error Loading Cart!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    mCartItemArrayList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED) {
                            mCartItemArrayList.add(dc.getDocument().toObject(CartItem.class));
                        }
                    }
                    double total = 0;
                    int qty = 0;
                    for (int i = 0; i < mCartItemArrayList.size(); i++) {
                        CartItem pos = mCartItemArrayList.get(i);

                        total += (Double.parseDouble(pos.price) * Double.parseDouble(pos.quantity));
                        qty += Integer.parseInt(pos.quantity);
                        mCheckoutPage1_orderTotalPrice_textView.setText("RM " + total + "0");
                        msubtotal_value.setText("RM " + total + "0");
                        mtotal_value.setText("RM " + total + "0");
                        mtotal_qty.setText(String.valueOf(qty));
                    }

                    mAdapter.notifyDataSetChanged();
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
                else if(!digitExist(mPostcode.getText().toString())){
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
                    mtilStateTerritory.setError("Please enter your state!");
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