package com.example.munchkin;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    Context cartContext;
    ArrayList<CartItem> mCartItemArrayList;
    String uName;

    public CartItemAdapter(Context context, ArrayList<CartItem> cartItemArrayList, String username) {

        cartContext = context;
        mCartItemArrayList = cartItemArrayList;
        uName = username;

    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(cartContext).inflate(R.layout.list_item_cart_item, parent, false);

        return new CartItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem mCartItem = mCartItemArrayList.get(position);
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference("images/IMG_" + mCartItem.bookId);
        FirebaseFirestore updateQty = FirebaseFirestore.getInstance();

        //use holder to set values into the views
        holder.mBookCover.setTag(R.drawable.ic_sample_book);

        if(cartContext instanceof CheckoutInformationActivity || cartContext instanceof CheckoutShippingActivity
                || cartContext instanceof CheckoutPaymentActivity){
            holder.mAddButton.setVisibility(View.GONE);
            holder.mMinusButton.setVisibility(View.GONE);
            holder.mQuantity.setVisibility(View.GONE);
            holder.mBookPrice.setText("RM " + mCartItem.price + "  x " + mCartItem.quantity);
        }
        else{
            holder.mQuantity.setText(mCartItem.quantity);
            holder.mBookPrice.setText("RM " + mCartItem.price);
        }

        try
        {
            File localFile = File.createTempFile("tempFile", ".jpg");
            mStorageReference.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        holder.mBookCover.setImageBitmap(bitmap);
                    }).addOnFailureListener(e -> Log.d("RETRIEVE_FAIL", "Retrieving book image failed: " + mCartItem.bookId));

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        holder.mBookTitle.setText(mCartItem.title);

        holder.mAddButton.setOnClickListener(view -> {
            int addQty = Integer.parseInt(holder.mQuantity.getText().toString());
            addQty++;
            holder.mQuantity.setText(String.valueOf(addQty));

            //save to database
            Map<String,Object> qty = new HashMap<>();
            qty.put("quantity", String.valueOf(addQty));

            updateQty.collection("Account Details").document(uName).collection("Shopping Cart").document(mCartItem.bookId)
                    .update(qty);
        });

        holder.mMinusButton.setOnClickListener((view -> {

            int minusQty = Integer.parseInt(holder.mQuantity.getText().toString());
            minusQty--;
            if(minusQty > 0) {
                holder.mQuantity.setText(String.valueOf(minusQty));

                //save to database
                Map<String, Object> qty = new HashMap<>();
                qty.put("quantity", String.valueOf(minusQty));

                updateQty.collection("Account Details").document(uName).collection("Shopping Cart").document(mCartItem.bookId)
                        .update(qty);
            }
            else{
                Toast.makeText(cartContext, "Quantity at least 1 book!", Toast.LENGTH_SHORT).show();
            }
        }));


    }

    @Override
    public int getItemCount() {
        return mCartItemArrayList.size();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView mBookCover, mAddButton, mMinusButton;
        TextView mBookTitle, mBookPrice, mQuantity;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            // find view by IDs
            mBookCover = itemView.findViewById(R.id.bookCover_imageView);
            mBookTitle = itemView.findViewById(R.id.bookTitle_textView);
            mBookPrice = itemView.findViewById(R.id.bookPrice_textView);

            mAddButton = itemView.findViewById(R.id.increaseBookQuantity_icon);
            mQuantity = itemView.findViewById(R.id.bookQuantity_textView);
            mMinusButton = itemView.findViewById(R.id.decreaseBookQuantity_icon);

        }
    }
}