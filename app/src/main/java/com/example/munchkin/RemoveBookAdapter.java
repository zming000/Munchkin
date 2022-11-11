package com.example.munchkin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RemoveBookAdapter extends RecyclerView.Adapter<RemoveBookAdapter.RemoveBookHolder>{

    private Context context;
    private ArrayList<Book> bookList;

    private Dialog mDialog;
    private Button dialogYesBtn, dialogNoBtn;
    private TextView dialogTV;

    private FirebaseFirestore db;

    private StorageReference mStorageReference;

    public RemoveBookAdapter(Context context, ArrayList<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    public class RemoveBookHolder extends RecyclerView.ViewHolder {
        private ImageView mBookImage;
        private TextView mBookIdTV;
        private TextView mBookTitleTV;
        private TextView mBookPriceTV;
        private TextView mBookCollectionTV;
        private Button mDeleteBtn;

        public RemoveBookHolder(final View view) {
            super(view);

            mBookImage = view.findViewById(R.id.adminRemoveBook_bookImage);
            mBookIdTV = view.findViewById(R.id.adminRemoveBook_bookId_textView);
            mBookTitleTV = view.findViewById(R.id.adminRemoveBook_bookTitle_textView);
            mBookPriceTV = view.findViewById(R.id.adminRemoveBook_bookPrice_textView);
            mBookCollectionTV = view.findViewById(R.id.adminRemoveBook_bookCollection_textView);
            mDeleteBtn = view.findViewById(R.id.adminRemoveBook_btn);
        }
    }

    @NonNull
    @Override
    public RemoveBookAdapter.RemoveBookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.remove_book_card, parent, false);
        return new RemoveBookHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RemoveBookAdapter.RemoveBookHolder holder, int position) {
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

        String tempPrice = String.valueOf(b.bookPrice);
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        tempPrice = formatter.format(b.bookPrice);

        holder.mBookPriceTV.setText(tempPrice);
        holder.mBookCollectionTV.setText(b.bookCollection);
        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDeleteConfirmDialog(b.bookId, position, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void openDeleteConfirmDialog(String id, int pos, RemoveBookAdapter.RemoveBookHolder holder) {
        //initialize & set up dialog
        mDialog = new Dialog(context);
        mDialog.setContentView(R.layout.delete_confirmation_dialog);
        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        mDialog.setCancelable(false);

        dialogYesBtn = mDialog.findViewById(R.id.deleteConfirmDialog_yesBtn);
        dialogNoBtn = mDialog.findViewById(R.id.deleteConfirmDialog_noBtn);
        dialogTV = mDialog.findViewById(R.id.deleteConfirmDialogTV);

        db = FirebaseFirestore.getInstance();

        String temp = "Are you sure you want to delete book with book ID of " + id + "?";
        int end = temp.length() - 1;

        SpannableString ss = new SpannableString(temp);
        StyleSpan boldItalicSpan = new StyleSpan(Typeface.BOLD_ITALIC);
        ss.setSpan(boldItalicSpan, 53, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        dialogTV.setText(ss);

        //display dialog
        mDialog.show();

        //if click no, cancel & display toast message
        dialogNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Book deletion is cancelled!", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
        });

        //if click yes, delete & display toast message
        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("books")
                        .document(id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                mStorageReference = FirebaseStorage.getInstance().getReference("images/IMG_" + id);
                                mStorageReference.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("DELETE_SUCCESS", "Deleting book image successful: " + id);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("DELETE_FAIL", "Deleting book image failed: " + id);
                                            }
                                        });

                                Intent i = new Intent(context, RemoveBook.class);
                                ((Activity) context).finish();
                                ((Activity) context).overridePendingTransition(0, 0);
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(0, 0);

                                Toast.makeText(context, "Book successfully deleted.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Some error has occurred, deletion failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                mDialog.dismiss();
            }
        });

    }
}