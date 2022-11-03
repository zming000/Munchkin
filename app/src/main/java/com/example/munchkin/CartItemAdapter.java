package com.example.munchkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    Context context;
    ArrayList<CartItem> mCartItemArrayList;

    public CartItemAdapter(Context context, ArrayList<CartItem> cartItemArrayList) {

        this.context = context;
        mCartItemArrayList = cartItemArrayList;

    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater
                .inflate(R.layout.list_item_cart_item, parent, false);

        return new CartItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {

        CartItem mCartItem = mCartItemArrayList.get(position);

        //use holder to set values into the views
        holder.mBookCover.setTag(R.drawable.ic_sample_book);

        holder.mBookTitle.setText(mCartItem.getBookTitle());
        holder.mBookPrice.setText(mCartItem.getBookPrice());

        holder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if clicked, increase book quantity

                //check stock?

            }
        });

        holder.mMinusButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if clicked, decrease book quantity

                //check if decrease until 0 (limit is 1 book left in cart)
            }
        }));

        holder.mQuantity.setText(mCartItem.getQuantity());

        holder.getPosition(mCartItem);

    }

    @Override
    public int getItemCount() {
        return mCartItemArrayList.size();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CartItem mCartItem;

        //list item views

        ImageView mBookCover;
        TextView mBookTitle;
        TextView mBookPrice;

        ImageView mAddButton;
        TextView mQuantity;
        ImageView mMinusButton;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            // find view by IDs

            mBookCover = (ImageView) itemView.findViewById(R.id.bookCover_imageView);
            mBookTitle = (TextView) itemView.findViewById(R.id.bookTitle_textView);
            mBookPrice = (TextView) itemView.findViewById(R.id.bookPrice_textView);

            mAddButton = (ImageView) itemView.findViewById(R.id.increaseBookQuantity_icon);
            mQuantity = (TextView) itemView.findViewById(R.id.bookQuantity_textView);
            mMinusButton = (ImageView) itemView.findViewById(R.id.decreaseBookQuantity_icon);

        }

        public void getPosition(CartItem cartItem){

            mCartItem = cartItem;

        }

        @Override
        public void onClick(View v) {

        }
    }
}