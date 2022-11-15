package com.example.munchkin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AdapterBookList extends RecyclerView.Adapter<AdapterBookList.BookListViewHolder> {
    //declare variables
    Context bookContext;
    ArrayList<ModelBookList> bookArrayList;

    public AdapterBookList(Context bookContext, ArrayList<ModelBookList> bookArrayList) {
        this.bookContext = bookContext;
        this.bookArrayList = bookArrayList;
    }

    @NonNull
    @Override
    public BookListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View bookView = LayoutInflater.from(bookContext).inflate(R.layout.book_list_item, parent, false);

        return new BookListViewHolder(bookView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookListViewHolder holder, int position) {
        //get position
        ModelBookList mbl = bookArrayList.get(position);

        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference("images/IMG_" + mbl.bookID);

        try
        {
            File localFile = File.createTempFile("tempFile", ".jpg");
            mStorageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            holder.mivBook.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("RETRIEVE_FAIL", "Retrieving book image failed: " + mbl.bookID);
                        }
                    });

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //set title, and price here
        holder.tvBookTitle.setText(mbl.title);
        holder.tvPrice.setText("RM " + mbl.price);

        //onclick the card view to more book details
        holder.cardViewID.setOnClickListener(view -> {
            //intent to book details
            Intent intent = new Intent(bookContext, BookDetail.class);
            intent.putExtra("name", mbl.title);
            intent.putExtra("price", mbl.price);
            intent.putExtra("bookID", mbl.bookID);
            intent.putExtra("collection", mbl.collection);
            bookContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    public static class BookListViewHolder extends RecyclerView.ViewHolder {
        //declare variables
        CardView cardViewID;
        ImageView mivBook;
        TextView tvPrice, tvBookTitle;

        public BookListViewHolder(@NonNull View itemView) {
            super(itemView);

            //assign variables
            mivBook = itemView.findViewById(R.id.ivBook);
            cardViewID = itemView.findViewById(R.id.cvBookDetails);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}