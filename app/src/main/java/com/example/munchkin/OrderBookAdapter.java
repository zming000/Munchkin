package com.example.munchkin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderBookAdapter extends RecyclerView.Adapter<OrderBookAdapter.OrderBookViewHolder>{

    Context context;
    ArrayList<OrderBook> obList;

    StorageReference mStorageReference;

    public OrderBookAdapter(Context context, ArrayList<OrderBook> obList) {
        this.context = context;
        this.obList = obList;
    }

    public static class OrderBookViewHolder extends RecyclerView.ViewHolder {
        ImageView mBookImg;
        TextView mBookIdTV, mBookTitleTV, mPriceTV, mCollectionTV, mTotalTV;

        public OrderBookViewHolder(final View view) {
            super(view);

            mBookImg = view.findViewById(R.id.orderBookCard_bookImage);
            mBookIdTV = view.findViewById(R.id.orderBookCard_bookIdTV);
            mBookTitleTV = view.findViewById(R.id.orderBookCard_bookTitleTV);
            mPriceTV = view.findViewById(R.id.orderBookCard_bookPriceTV);
            mCollectionTV = view.findViewById(R.id.orderBookCard_bookCollectionTV);
            mTotalTV = view.findViewById(R.id.orderBookCard_totalPriceTV);
        }
    }

    @NonNull
    @Override
    public OrderBookAdapter.OrderBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_book_card, parent, false);
        return new OrderBookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderBookAdapter.OrderBookViewHolder holder, int position) {
        OrderBook ob = obList.get(position);

        mStorageReference = FirebaseStorage.getInstance().getReference("images/IMG_" + ob.mBook.getBookId());

        try
        {
            File localFile = File.createTempFile("tempFile", ".jpg");
            mStorageReference.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        holder.mBookImg.setImageBitmap(bitmap);
                    }).addOnFailureListener(e -> Log.d("RETRIEVE_FAIL", "Retrieving book image failed: " + ob.mBook.getBookId()));

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        holder.mBookIdTV.setText(ob.mBook.getBookId());
        holder.mBookTitleTV.setText(ob.mBook.getBookTitle());

        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        String tempPrice = formatter.format(ob.mBook.getBookPrice());

        holder.mPriceTV.setText("RM" + tempPrice);
        holder.mCollectionTV.setText(ob.mBook.getBookCollection());

        tempPrice = formatter.format(ob.totalBookPrice);

        if (ob.bookQty > 1)
        {
            holder.mTotalTV.setText(ob.bookQty + " items (RM " + tempPrice + ")");
        }
        else
        {
            holder.mTotalTV.setText(ob.bookQty + " item (RM " + tempPrice + ")");
        }
    }

    @Override
    public int getItemCount() {
        return obList.size();
    }
}