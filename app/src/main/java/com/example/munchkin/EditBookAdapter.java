package com.example.munchkin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EditBookAdapter extends RecyclerView.Adapter<EditBookAdapter.EditBookHolder>{

    private Context context;
    private ArrayList<Book> bookList;

    private StorageReference mStorageReference;

    public EditBookAdapter(Context context, ArrayList<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    public class EditBookHolder extends RecyclerView.ViewHolder {
        private ImageView mBookImage;
        private TextView mBookIdTV;
        private TextView mBookTitleTV;
        private TextView mBookPriceTV;
        private TextView mBookCollectionTV;
        private Button mEditBtn;

        public EditBookHolder(final View view) {
            super(view);

            mBookImage = view.findViewById(R.id.adminEditCard_bookImage);
            mBookIdTV = view.findViewById(R.id.adminEditCard_bookId_textView);
            mBookTitleTV = view.findViewById(R.id.adminEditCard_bookTitle_textView);
            mBookPriceTV = view.findViewById(R.id.adminEditCard_bookPrice_textView);
            mBookCollectionTV = view.findViewById(R.id.adminEditCard_bookCollection_textView);
            mEditBtn = view.findViewById(R.id.adminEditCard_btn);
        }
    }

    @NonNull
    @Override
    public EditBookAdapter.EditBookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.edit_book_card, parent, false);
        return new EditBookHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EditBookAdapter.EditBookHolder holder, int position) {
        Book b = bookList.get(position);

        mStorageReference = FirebaseStorage.getInstance().getReference("images/IMG_" + b.bookId);

        try
        {
            File localFile = File.createTempFile("tempFile", ".jpg");
            mStorageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            holder.mBookImage.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("RETRIEVE_FAIL", "Retrieving book image failed: " + b.bookId);
                        }
                    });

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        holder.mBookIdTV.setText(b.bookId);
        holder.mBookTitleTV.setText(b.bookTitle);

        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        String tempPrice = formatter.format(b.bookPrice);

        holder.mBookPriceTV.setText(tempPrice);
        holder.mBookCollectionTV.setText(b.bookCollection);
        holder.mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (context, EditBook.class);
                intent.putExtra("position", holder.getAdapterPosition());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}