package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CheckoutShippingActivity extends AppCompatActivity {

    RadioGroup mRadioGroup;
    Button mbtnContinueToPayment;
    TextView mshipTo_value, mreturnToInformation_textView, mchangeShipAddr_textView, mcontactEmail_value;
    ImageView mCheckoutPage2_backImageView;
    String shippingMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_shipping);

        mshipTo_value = findViewById(R.id.shipTo_value);
        mchangeShipAddr_textView = findViewById(R.id.changeShipAddr_textView);
        mreturnToInformation_textView = findViewById(R.id.returnToInformation_textView);
        mcontactEmail_value = findViewById(R.id.contactEmail_value);
        mRadioGroup = findViewById(R.id.radioGroup);
        mCheckoutPage2_backImageView = findViewById(R.id.CheckoutPage2_backImageView);
        mbtnContinueToPayment = findViewById(R.id.btnContinueToPayment);

        mcontactEmail_value.setText(getIntent().getStringExtra("email"));

        mshipTo_value.setText(getIntent().getStringExtra("address") + ", " + getIntent().getStringExtra("city") +
                ", " + getIntent().getStringExtra("postcode") + " " + getIntent().getStringExtra("state") + ", " +
                getIntent().getStringExtra("country") + ".");

        mRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
            shippingMethod = radioButton.getText().toString();
        });

        mbtnContinueToPayment.setOnClickListener(view -> {
            if(shippingMethod != null) {
                //go payment ui
                Intent intent = new Intent(CheckoutShippingActivity.this, CheckoutPaymentActivity.class);
                intent.putExtra("country", getIntent().getStringExtra("country"));
                intent.putExtra("firstName", getIntent().getStringExtra("firstName"));
                intent.putExtra("lastName", getIntent().getStringExtra("lastName"));
                intent.putExtra("company", getIntent().getStringExtra("company"));
                intent.putExtra("address", getIntent().getStringExtra("address"));
                intent.putExtra("apartment", getIntent().getStringExtra("apartment"));
                intent.putExtra("postcode", getIntent().getStringExtra("postcode"));
                intent.putExtra("city", getIntent().getStringExtra("city"));
                intent.putExtra("state", getIntent().getStringExtra("state"));
                intent.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"));
                intent.putExtra("shipping", shippingMethod);
                intent.putExtra("email", getIntent().getStringExtra("email"));

                startActivity(intent);
            }
            else{
                Toast.makeText(CheckoutShippingActivity.this, "Please select shipping method!", Toast.LENGTH_SHORT).show();
            }
        });

        mchangeShipAddr_textView.setOnClickListener(view -> finish());
        mCheckoutPage2_backImageView.setOnClickListener(view -> finish());
        mreturnToInformation_textView.setOnClickListener(view -> finish());
    }
}