package com.example.munchkin;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CheckoutPaymentActivity extends AppCompatActivity {
    TextView mshipTo_value, mshipMethod_value, mchangeShipAddr_textView, mreturnToShipping_textView, mcontactEmail_value;
    TextView showOrderSummaryTxt, mCheckoutPage3_orderTotalPrice_textView, msubtotal_value, mtotal_value, mshipping_value, mtotal_qty;
    ImageView mCheckoutPage3_backImageView;
    RadioButton mpaymentMethods_rb1, mpaymentMethods_rb2;
    Button mbtnCompleteOrder;
    CartItemAdapter mAdapter;
    ArrayList<CartItem> mCartItemArrayList;
    RecyclerView mcartSummary_items_recycler_view;
    ConstraintLayout mOrderSummary;
    LinearLayout mOrderSummaryDropDown;
    String method;
    String[] bookIDs, bookCollections, bookTitles, bookPrices, bookQuantity;

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

        mtotal_qty = findViewById(R.id.total_qtyValue);
        mtotal_value = findViewById(R.id.total_value);
        mshipping_value = findViewById(R.id.shipping_value);
        msubtotal_value = findViewById(R.id.subtotal_value);
        mCheckoutPage3_orderTotalPrice_textView = findViewById(R.id.CheckoutPage3_orderTotalPrice_textView);
        mcartSummary_items_recycler_view = findViewById(R.id.cartSummary_items_recycler_view);

        mOrderSummary = findViewById(R.id.orderSummary_constraintLayout3);
        showOrderSummaryTxt = findViewById(R.id.CheckoutPage3_showOrderSummary_textView);
        mOrderSummaryDropDown = findViewById(R.id.orderSummary_dropDown);



        //get username from shared preference
        String uName = getIntent().getStringExtra("username");
        method = getIntent().getStringExtra("shipping");

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

        mchangeShipAddr_textView.setOnClickListener(view -> {
            Intent intent = new Intent(CheckoutPaymentActivity.this, CheckoutInformationActivity.class);
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
            finishAffinity();
            finish();
        });

        mreturnToShipping_textView.setOnClickListener(view -> finish());

        mCheckoutPage3_backImageView.setOnClickListener(view -> finish());

        mcontactEmail_value.setText(getIntent().getStringExtra("email"));

        mshipTo_value.setText(getIntent().getStringExtra("address") + ", " + getIntent().getStringExtra("city") +
                ", " + getIntent().getStringExtra("postcode") + " " + getIntent().getStringExtra("state") + ", " +
                getIntent().getStringExtra("country") + ".");

        if(method.equals("Pos Laju (RM 6)")){
            mshipMethod_value.setText("Pos Laju RM 6.00");
            mshipping_value.setText("RM 6.00");
        }
        else{
            mshipMethod_value.setText("DHL RM 10.00");
            mshipping_value.setText("RM 10.00");
        }

        mbtnCompleteOrder.setOnClickListener(view -> {
            if(mpaymentMethods_rb1.isChecked() || mpaymentMethods_rb2.isChecked()){
                FirebaseFirestore addOrder = FirebaseFirestore.getInstance();
                String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String orderID = "ON" + sdf.format(new Date());

                //save to database
                Map<String,Object> order = new HashMap<>();
                order.put("custId", uName);
                order.put("custName", getIntent().getStringExtra("lastName") + " " + getIntent().getStringExtra("firstName"));
                order.put("date", currentDate);
                order.put("shippingAddress", mshipTo_value.getText().toString());
                order.put("status", "Pending");
                order.put("custPhone", "+60" + getIntent().getStringExtra("phoneNumber"));
                order.put("totalItem", mtotal_qty.getText().toString());
                order.put("totalPrice", mtotal_value.getText().toString());
                order.put("orderedItems", Arrays.asList(bookIDs));
                order.put("orderedItemsName", Arrays.asList(bookTitles));
                order.put("orderedItemsPrice", Arrays.asList(bookPrices));
                order.put("orderedItemsCollection", Arrays.asList(bookCollections));
                order.put("orderedItemsQty", Arrays.asList(bookQuantity));

                addOrder.collection("orders").document(orderID).set(order)
                            .addOnSuccessListener(unused -> {
                                FirebaseFirestore getItem = FirebaseFirestore.getInstance();
                                FirebaseFirestore deleteItem = FirebaseFirestore.getInstance();
                                getItem.collection("Account Details").document(uName).collection("Shopping Cart")
                                        .addSnapshotListener((value, error) -> {
                                            if (error != null) {
                                                Toast.makeText(CheckoutPaymentActivity.this, "Error Loading Cart!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            //clear list
                                            mCartItemArrayList.clear();

                                            //use the id to check if the driver available within the duration requested
                                            for (DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()) {
                                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                                    Map<String,Object> status = new HashMap<>();
                                                    status.put("status", "Paid");
                                                    deleteItem.collection("Account Details").document(uName).collection("Shopping Cart")
                                                            .document(dc.getDocument().getId()).update(status);
                                                }
                                            }
                                            mCartItemArrayList.clear();

                                            Toast.makeText(CheckoutPaymentActivity.this, "Your order have been placed!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(CheckoutPaymentActivity.this, MainActivity.class));
                                            finishAffinity();
                                            finish();
                                        });
                            })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CheckoutPaymentActivity.this, "Fail to place order!", Toast.LENGTH_SHORT).show();
                        });
            }
            else{
                Toast.makeText(CheckoutPaymentActivity.this, "Please select payment method!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrderDetailsFromFirestore(String username) {
        FirebaseFirestore cartDB = FirebaseFirestore.getInstance();
        FirebaseFirestore getPrice = FirebaseFirestore.getInstance();
        ArrayList<String> id = new ArrayList<>();

        cartDB.collection("Account Details").document(username).collection("Shopping Cart")
                .whereEqualTo("status", "Unpaid")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(CheckoutPaymentActivity.this, "Error Loading Cart!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    mCartItemArrayList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED) {
                            mCartItemArrayList.add(dc.getDocument().toObject(CartItem.class));
                            id.add(dc.getDocument().getId());
                        }
                    }

                    bookIDs = new String[id.size()];
                    bookCollections = new String[id.size()];
                    bookTitles = new String[id.size()];
                    bookPrices = new String[id.size()];
                    bookQuantity = new String[id.size()];

                    double total = 0;
                    int qty = 0;
                    for (int i = 0; i < mCartItemArrayList.size(); i++) {
                        CartItem pos = mCartItemArrayList.get(i);
                        int finalI = i;

                        qty += Integer.parseInt(pos.quantity);

                        if (finalI == id.size() - 1) {
                            if (method.equals("Pos Laju (RM 6)")) {
                                total += (Double.parseDouble(pos.price) * Double.parseDouble(pos.quantity));
                                total += 6.00;
                            } else {
                                total += (Double.parseDouble(pos.price) * Double.parseDouble(pos.quantity));
                                total += 10.00;
                            }
                        } else {
                            total += (Double.parseDouble(pos.price) * Double.parseDouble(pos.quantity));
                        }

                        getPrice.collection("Account Details").document(username)
                                .collection("Shopping Cart").document(id.get(i)).get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();

                                        bookIDs[finalI] = doc.getString("bookId");
                                        bookPrices[finalI] = doc.getString("price");
                                        bookQuantity[finalI] = doc.getString("quantity");
                                        bookCollections[finalI] = doc.getString("collection");
                                        bookTitles[finalI] = doc.getString("title");

                                    }
                                });

                        mCheckoutPage3_orderTotalPrice_textView.setText("RM " + total);
                        msubtotal_value.setText("RM " + total);
                        mtotal_value.setText("RM " + total);
                        mtotal_qty.setText(String.valueOf(qty));
                    }

                    mAdapter.notifyDataSetChanged();
                });
    }
}