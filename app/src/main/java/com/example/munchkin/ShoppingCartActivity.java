package com.example.munchkin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity {

    //
    // constant values
    //

    //
    // other variables
    //
    CartItemAdapter mAdapter;
    ArrayList mCartItemArrayList;

    //
    // views
    //
    private ImageView mImgViewBack;
    private TextView mMainTitle;
    private Button mBtnCheckout;

    private RecyclerView mCartItemRecyclerView;
    private TextView mCartEmptyTextView;


    //
    // values
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        //
        // find values
        //
        mImgViewBack = findViewById(R.id.shoppingCartPage_backImageView);
        mMainTitle = findViewById(R.id.shoppingCartPage_main_title);
        mBtnCheckout = findViewById(R.id.shoppingCart_checkout_button);

        mCartEmptyTextView = findViewById(R.id.cart_is_empty_textView);
        mCartItemRecyclerView = findViewById(R.id.cart_items_recycler_view);

        //
        //initialize
        //

        //initialize database

        mCartItemRecyclerView.setHasFixedSize(true);
        mCartItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //listeners...
        mImgViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        mBtnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start checkout - information activity
                Intent intent = new Intent(ShoppingCartActivity.this, CheckoutInformationActivity.class);
                startActivity(intent);

            }
        });

        mCartItemArrayList = new ArrayList<>();
        mAdapter = new CartItemAdapter(this, mCartItemArrayList);
        mCartItemRecyclerView.setAdapter(mAdapter);

        //database listener

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                        CartItem cartItem = (CartItem) mCartItemArrayList.get(viewHolder.getAdapterPosition());

                        //delete from database
                        //database.child(alarmClock.getAlarmId()).setValue(null);

                    }
                });

        //attach helper object into recycler view
        helper.attachToRecyclerView(mCartItemRecyclerView);

    }

}