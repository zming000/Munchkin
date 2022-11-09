package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckoutInformationActivity extends AppCompatActivity {

    //
    // constant values
    //

    //
    // other variables
    //


    //
    // views
    //
    ImageView mBackBtn;

    ConstraintLayout mOrderSummary;
    TextView showOrderSummaryTxt;
    ImageView dropDownArrow;
    LinearLayout mOrderSummaryDropDown;

    EditText mShippingCountry;
    EditText mFirstName;
    EditText mLastName;
    EditText mCompany;
    EditText mAddress;
    EditText mApartment;
    EditText mPostcode;
    EditText mCity;
    EditText mState;
    EditText mPhone;

    Button mContinueToShipBtn;

    //
    // values
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_information);

        //
        // find views
        //
        mBackBtn = findViewById(R.id.CheckoutPage1_backImageView);

        mOrderSummary = findViewById(R.id.orderSummary_constraintLayout1);
        showOrderSummaryTxt = findViewById(R.id.CheckoutPage1_showOrderSummary_textView);
        dropDownArrow = findViewById(R.id.drop_down_for_more);
        mOrderSummaryDropDown = findViewById(R.id.orderSummary_dropDown);

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

        //
        //initialization of values...
        //
        mOrderSummary.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        // initialize order summary values from database for constraint layout and linear layout
        // initialize contact info

        //
        // listeners
        //
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go back to shopping cart activity
                finish();

            }
        });

        mOrderSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

            }
        });

        mContinueToShipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String country = "";
                String firstName = "";
                String lastName = "";
                String

                //check if ... is empty
                if()

            }
        });



    }
}