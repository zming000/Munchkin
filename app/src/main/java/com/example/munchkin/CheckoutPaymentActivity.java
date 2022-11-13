package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CheckoutPaymentActivity extends AppCompatActivity {
    TextView mshipTo_value, mshipMethod_value, mchangeShipAddr_textView, mreturnToShipping_textView, mcontactEmail_value;
    ImageView mCheckoutPage3_backImageView;
    RadioButton mpaymentMethods_rb1, mpaymentMethods_rb2;
    Button mbtnCompleteOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_payment);

        mreturnToShipping_textView = findViewById(R.id.returnToShipping_textView);
        mchangeShipAddr_textView = findViewById(R.id.changeShipAddr_textView);
        mCheckoutPage3_backImageView = findViewById(R.id.CheckoutPage3_backImageView);
        mshipTo_value = findViewById(R.id.shipTo_value);
        mcontactEmail_value = findViewById(R.id.contactEmail_value);
        mshipMethod_value = findViewById(R.id.shipMethod_value);
        mpaymentMethods_rb1 = findViewById(R.id.paymentMethods_rb1);
        mpaymentMethods_rb2 = findViewById(R.id.paymentMethods_rb2);
        mbtnCompleteOrder = findViewById(R.id.btnCompleteOrder);

        mchangeShipAddr_textView.setOnClickListener(view -> finish());

        mreturnToShipping_textView.setOnClickListener(view -> finish());

        mCheckoutPage3_backImageView.setOnClickListener(view -> finish());

        mcontactEmail_value.setText(getIntent().getStringExtra("email"));

        mshipTo_value.setText(getIntent().getStringExtra("address") + ", " + getIntent().getStringExtra("city") +
                ", " + getIntent().getStringExtra("postcode") + " " + getIntent().getStringExtra("state") + ", " +
                getIntent().getStringExtra("country") + ".");

        String method = getIntent().getStringExtra("shipping");

        if(method.equals("Pos Laju (RM 6)")){
            mshipMethod_value.setText("Pos Laju RM 6.00");
        }
        else{
            mshipMethod_value.setText("DHL RM 10.00");
        }

        mbtnCompleteOrder.setOnClickListener(view -> {
            if(mpaymentMethods_rb1.isChecked() || mpaymentMethods_rb2.isChecked()){
                FirebaseFirestore addOrder = FirebaseFirestore.getInstance();
                String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String orderID = "ON" + sdf.format(new Date());

                //save to database
                Map<String,Object> order = new HashMap<>();
                //order.put("custId", );
                order.put("custName", getIntent().getStringExtra("lastName") + " " + getIntent().getStringExtra("firstName"));
                order.put("date", currentDate);
                order.put("shippingAddress", mshipTo_value.getText().toString());
                order.put("status", "Pending");
                order.put("custPhone", getIntent().getStringExtra("phoneNumber"));
                //order.put("totalItem", );
                //order.put("totalPrice", );

                addOrder.collection("orders").document(orderID).set(order);

                Toast.makeText(CheckoutPaymentActivity.this, "Your order have been placed!", Toast.LENGTH_SHORT).show();
                //intent
            }
            else{
                Toast.makeText(CheckoutPaymentActivity.this, "Please select payment method!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}