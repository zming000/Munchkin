package com.example.munchkin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderDetails extends AppCompatActivity {

    ImageView mBackBtn;
    CardView mUpdateStatusBtn;

    private TextView mOrderTV, mStatusTV, mTotalItemTV, mCustIdTV, mCustNameTV, mShipAddressTV, mOrderDateTimeTV, mTotalTV;

    private String oId = "";
    private FirebaseFirestore db;

    ArrayList<OrderBook> orderedBooks;
    private OrderBookAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private String tempStatus = "Pending";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_order_details);

        mBackBtn = findViewById(R.id.orderDetails_backImageView);
        mOrderTV = findViewById(R.id.orderDetails_mainTitle);
        mStatusTV = findViewById(R.id.orderDetails_statusText);
        mTotalItemTV = findViewById(R.id.orderDetails_totalItemText);
        mCustIdTV = findViewById(R.id.orderDetails_custIdText);
        mCustNameTV = findViewById(R.id.orderDetails_custNameText);
        mShipAddressTV = findViewById(R.id.orderDetails_shippingAddressText);
        mOrderDateTimeTV = findViewById(R.id.orderDetails_orderDateText);
        mTotalTV = findViewById(R.id.orderDetails_totalPriceText);
        mUpdateStatusBtn = findViewById(R.id.orderDetails_updateStatusBtn);

        mRecyclerView = findViewById(R.id.orderDetails_recylerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        orderedBooks = new ArrayList<>();

        mAdapter = new OrderBookAdapter(OrderDetails.this, orderedBooks);
        mRecyclerView.setAdapter(mAdapter);

        //get order id from previous activity
        Bundle extra = getIntent().getExtras();
        if (extra != null)
        {
            oId = extra.getString("orderID");
        }

        if (!oId.equals(""))
        {
            //retrieve order object with the specific id from database
            db = FirebaseFirestore.getInstance();
            db.collection("orders").document(oId)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                mOrderTV.setText("Order No#" + document.getId());
                                mStatusTV.setText(document.getString("status"));
                                mOrderDateTimeTV.setText("Order Date:" + document.getString("date"));

                                mCustIdTV.setText("Customer ID: " + document.getString("custId"));
                                mCustNameTV.setText("Customer Name: " + document.getString("custName"));
                                mShipAddressTV.setText("Shipping Address: \n" + document.getString("shippingAddress"));

                                String totalItem = document.getString("totalItem");

                                DecimalFormat formatter = new DecimalFormat("###,###,##0.00");

                                String tempPriceStr = document.getString("totalPrice");
                                String tempPriceSubstr = tempPriceStr.replace("RM ", "");
                                String totalPrice = formatter.format(Double.parseDouble(Objects.requireNonNull(tempPriceSubstr)));

                                mTotalItemTV.setText("Total Item: " + totalItem);

                                mTotalTV.setText("Total: RM" + totalPrice);

                                ArrayList<String> tempBookId;
                                tempBookId = (ArrayList<String>) document.get("orderedItems");

                                ArrayList<String> tempBookTitle;
                                tempBookTitle = (ArrayList<String>) document.get("orderedItemsName");

                                ArrayList<String> tempBookPrice;
                                tempBookPrice = (ArrayList<String>) document.get("orderedItemsPrice");

                                ArrayList<String> tempBookCollection;
                                tempBookCollection = (ArrayList<String>) document.get("orderedItemsCollection");

                                ArrayList<String> tempBookQty;
                                tempBookQty = (ArrayList<String>) document.get("orderedItemsQty");

                                ArrayList<OrderBook> orderedBooks = new ArrayList<>();

                                mAdapter = new OrderBookAdapter(OrderDetails.this, orderedBooks);
                                mRecyclerView.setAdapter(mAdapter);

                                for (int i=0; i<tempBookId.size(); i++)
                                {
                                    //create book object
                                    Book mBook = new Book(tempBookId.get(i), tempBookTitle.get(i), Double.parseDouble(tempBookPrice.get(i)), tempBookCollection.get(i));

                                    //create orderBook object
                                    int tQty = Integer.parseInt(tempBookQty.get(i));
                                    double tPrice = tQty * Double.parseDouble(tempBookPrice.get(i));
                                    OrderBook mOrderBook = new OrderBook(mBook, tQty, tPrice);

                                    orderedBooks.add(mOrderBook);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                            else
                            {
                                finish();
                                Toast.makeText(OrderDetails.this, "Failed to display order details! (Error: Order not found)", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            finish();
                            Toast.makeText(OrderDetails.this, "Retrieving order details failed! " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else
        {
            finish();
            Toast.makeText(this, "Failed to display order details!", Toast.LENGTH_SHORT).show();
        }

        mUpdateStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUpdateStatusDialog();
            }

            private void openUpdateStatusDialog() {
                final String[] status = {"Pending", "Shipping", "Completed", "Cancelled", "Cancelled & Refunded"};
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetails.this);
                builder.setTitle("Change order status");

                builder.setSingleChoiceItems(status, 0, (dialogInterface, i) -> tempStatus = status[i]);

                builder.setPositiveButton("Update", (dialogInterface, i) -> {
                    db.collection("orders").document(oId)
                            .get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    if (document.exists()) {
                                        //check if the status is similar
                                        String oStatus = document.getString("status");

                                        if (oStatus.equals(tempStatus))
                                        {
                                            //close the dialog
                                            dialogInterface.dismiss();
                                        }
                                        else
                                        {
                                            //close the dialog
                                            dialogInterface.dismiss();

                                            //update order status on db
                                            Map<String, Object> orderStatus = new HashMap<>();
                                            orderStatus.put("status", tempStatus);

                                            db.collection("orders")
                                                    .document(oId)
                                                    .update(orderStatus)
                                                    .addOnSuccessListener(unused -> {
                                                        Handler handler = new Handler();
                                                        handler.postDelayed(() -> {
                                                            Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.putExtra("loadOrderFrag", 1);
                                                            startActivity(intent);
                                                            overridePendingTransition(0, 0);

                                                            Toast.makeText(OrderDetails.this, "Order status successfully updated!", Toast.LENGTH_SHORT).show();
                                                        }, 2000);
                                                    });
                                        }
                                    }
                                }
                            });

                    //close activity and re-open it
                });

                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                    //no changes, close the dialog
                    tempStatus = "Pending";
                    dialogInterface.dismiss();
                });

                builder.show();
            }
        });


        mBackBtn.setOnClickListener(view -> finish());
    }
}