package com.example.munchkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderDetails extends AppCompatActivity {

    private ImageView mBackBtn;

    private TextView mOrderTV, mStatusTV, mTotalItemTV, mCustIdTV, mCustNameTV, mShipAddressTV, mOrderDateTimeTV, mTotalTV;
    private CardView mUpdateStatusBtn;

    private String oId = "";
    private FirebaseFirestore db;

    private Order mOrder;
    private OrderBookAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

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
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()) {
                                    mOrderTV.setText("Order No#" + document.getId());
                                    mStatusTV.setText(document.getString("status"));
                                    mOrderDateTimeTV.setText("Order Date:" + document.getString("date"));

                                    mCustIdTV.setText(document.getString("custId"));
                                    mCustNameTV.setText(document.getString("custName"));
                                    mShipAddressTV.setText(document.getString("shippingAddress"));

                                    int totalItem = document.getLong("totalItem").intValue();
                                    double totalPrice = document.getLong("totalPrice");
                                    mTotalItemTV.setText("Total Item: " + totalItem);

                                    DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
                                    String tempPrice = formatter.format(totalPrice);
                                    mTotalTV.setText("Total: RM" + tempPrice);

                                    ArrayList<String> tempBookId = new ArrayList<String>();
                                    tempBookId = (ArrayList<String>) document.get("orderedItems");

                                    ArrayList<OrderBook> orderedBooks = new ArrayList<OrderBook>();
                                    mAdapter = new OrderBookAdapter(OrderDetails.this, orderedBooks);
                                    mRecyclerView.setAdapter(mAdapter);

                                    for (int i=0; i<tempBookId.size(); i++)
                                    {
                                        //get book data object from database (search for book using bookId)
                                        db.collection("books").document(tempBookId.get(i))
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            DocumentSnapshot bDocument = task.getResult();
                                                            if (bDocument.exists())
                                                            {
                                                                String bId = bDocument.getId();
                                                                String bTitle = bDocument.getString("title");
                                                                double bPrice = Double.parseDouble(bDocument.getString("price"));
                                                                String bCollection = bDocument.getString("collection");

                                                                //get book quantity
                                                                int bQty = document.getLong(bId).intValue();

                                                                //calculate the price
                                                                double bTotalPrice = bQty * bPrice;

                                                                //create OrderBook object & add into arraylist
                                                                Book mBook = new Book(bId, bTitle, bPrice, bCollection);
                                                                OrderBook mOrderBook = new OrderBook(mBook, bQty, bTotalPrice);
                                                                orderedBooks.add(mOrderBook);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Log.d("GET_BOOK_FOR_ORDER_FAIL", "",task.getException());
                                                        }
                                                    }
                                                });
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
                        }
                    });

        }
        else
        {
            finish();
            Toast.makeText(this, "Failed to display order details!", Toast.LENGTH_SHORT).show();
        }

        //update button


        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}