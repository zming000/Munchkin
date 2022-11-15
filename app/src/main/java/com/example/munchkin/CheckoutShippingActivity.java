package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CheckoutShippingActivity extends AppCompatActivity {

    RadioGroup mRadioGroup;
    Button mbtnContinueToPayment;
    TextView mshipTo_value, mreturnToInformation_textView, mchangeShipAddr_textView, mcontactEmail_value;
    TextView showOrderSummaryTxt, mCheckoutPage2_orderTotalPrice_textView, msubtotal_value, mtotal_value, mtotal_qty;
    ImageView mCheckoutPage2_backImageView;
    CartItemAdapter mAdapter;
    ArrayList<CartItem> mCartItemArrayList;
    RecyclerView mcartSummary_items_recycler_view;
    ConstraintLayout mOrderSummary;
    LinearLayout mOrderSummaryDropDown;
    String shippingMethod, uName;

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

        mtotal_qty = findViewById(R.id.total_qtyValue);
        mtotal_value = findViewById(R.id.total_value);
        msubtotal_value = findViewById(R.id.subtotal_value);
        mCheckoutPage2_orderTotalPrice_textView = findViewById(R.id.CheckoutPage2_orderTotalPrice_textView);
        mcartSummary_items_recycler_view = findViewById(R.id.cartSummary_items_recycler_view);

        mOrderSummary = findViewById(R.id.orderSummary_constraintLayout2);
        showOrderSummaryTxt = findViewById(R.id.CheckoutPage2_showOrderSummary_textView);
        mOrderSummaryDropDown = findViewById(R.id.orderSummary_dropDown);

        //get username from shared preference
        uName = getIntent().getStringExtra("username");
        String shipping = getIntent().getStringExtra("shipping");

        if(shipping != null) {
            if (shipping.equals("Pos Laju (RM 6)")) {
                mRadioGroup.check(R.id.pos_laju_RB);
            }
            else{
                mRadioGroup.check(R.id.dhl_RB);
            }
        }

        mcartSummary_items_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        mCartItemArrayList = new ArrayList<>();
        mAdapter = new CartItemAdapter(this, mCartItemArrayList, uName);
        mcartSummary_items_recycler_view.setAdapter(mAdapter);

        getOrderDetailsFromFirestore(uName);

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
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("shipping", shippingMethod);
                intent.putExtra("username", uName);

                startActivity(intent);
            }
            else{
                Toast.makeText(CheckoutShippingActivity.this, "Please select shipping method!", Toast.LENGTH_SHORT).show();
            }
        });

        mchangeShipAddr_textView.setOnClickListener(view -> {
            Intent intent = new Intent(CheckoutShippingActivity.this, CheckoutInformationActivity.class);
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
            intent.putExtra("email", getIntent().getStringExtra("email"));
            intent.putExtra("username", uName);
            startActivity(intent);
            finish();
        });

        mCheckoutPage2_backImageView.setOnClickListener(view -> {
            Intent intent = new Intent(CheckoutShippingActivity.this, CheckoutInformationActivity.class);
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
            intent.putExtra("email", getIntent().getStringExtra("email"));
            intent.putExtra("username", uName);
            startActivity(intent);
            finish();
        });

        mreturnToInformation_textView.setOnClickListener(view -> {
            Intent intent = new Intent(CheckoutShippingActivity.this, CheckoutInformationActivity.class);
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
            intent.putExtra("username", uName);
            startActivity(intent);
            finish();
        });
    }

    private void getOrderDetailsFromFirestore(String username) {
        FirebaseFirestore cartDB = FirebaseFirestore.getInstance();
        ArrayList<String> id = new ArrayList<>();

        cartDB.collection("Account Details").document(username).collection("Shopping Cart")
                .whereEqualTo("status", "Unpaid")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(CheckoutShippingActivity.this, "Error Loading Cart!", Toast.LENGTH_SHORT).show();
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
                        mCheckoutPage2_orderTotalPrice_textView.setText("RM " + total + "0");
                        msubtotal_value.setText("RM " + total + "0");
                        mtotal_value.setText("RM " + total + "0");
                        mtotal_qty.setText(String.valueOf(qty));
                    }
                    mAdapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CheckoutShippingActivity.this, CheckoutInformationActivity.class);
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
        intent.putExtra("username", uName);

        startActivity(intent);
        finish();
    }
}