package com.example.munchkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ShoppingCartActivity extends AppCompatActivity {

    CartItemAdapter mAdapter;
    ArrayList<CartItem> mCartItemArrayList;
    ImageView mImgViewBack;
    Button mBtnCheckout;
    RecyclerView mCartItemRecyclerView;
    TextView mCartEmptyTextView;
    SwipeRefreshLayout mswipeCart;
    FirebaseFirestore cartDB;
    SharedPreferences spMunchkin;
    String uName;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        mImgViewBack = findViewById(R.id.shoppingCartPage_backImageView);
        mBtnCheckout = findViewById(R.id.shoppingCart_checkout_button);
        mCartEmptyTextView = findViewById(R.id.cart_is_empty_textView);
        mCartItemRecyclerView = findViewById(R.id.cart_items_recycler_view);
        mswipeCart = findViewById(R.id.swipeCart);
        mCartItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        spMunchkin = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        uName = "tester1";
                //spMunchkin.getString(KEY_USERNAME, null);

        //listeners...
        mImgViewBack.setOnClickListener(v -> finish());

        //initialize variables
        cartDB = FirebaseFirestore.getInstance();
        mCartItemArrayList = new ArrayList<>();
        mAdapter = new CartItemAdapter(this, mCartItemArrayList, uName);
        mCartItemRecyclerView.setAdapter(mAdapter);

        getOrderDetailsFromFirestore();

        mswipeCart.setOnRefreshListener(() -> {
            getOrderDetailsFromFirestore();
            mswipeCart.setRefreshing(false);
        });

        mBtnCheckout.setOnClickListener(v -> {

            //start checkout - information activity
            Intent intent = new Intent(ShoppingCartActivity.this, CheckoutInformationActivity.class);
            intent.putExtra("username", uName);

            startActivity(intent);

        });
    }

    private void getOrderDetailsFromFirestore() {
        cartDB.collection("Account Details").document(uName).collection("Shopping Cart")
                .whereEqualTo("status", "Unpaid")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(ShoppingCartActivity.this, "Error Loading Cart!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    mCartItemArrayList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            mCartItemArrayList.add(dc.getDocument().toObject(CartItem.class));
                        }
                    }

                    //if no records found
                    if(mCartItemArrayList.size() == 0){
                        mCartEmptyTextView.setVisibility(View.VISIBLE);
                        mBtnCheckout.setEnabled(false);
                    }
                    else{
                        mCartEmptyTextView.setVisibility(View.GONE);
                        mBtnCheckout.setEnabled(true);
                    }

                    mAdapter.notifyDataSetChanged();
                });
    }
}