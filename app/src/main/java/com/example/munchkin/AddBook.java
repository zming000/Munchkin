package com.example.munchkin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddBook extends AppCompatActivity {

    ImageView mBackBtn;
    CardView mUploadBtn, mSaveBtn;
    AutoCompleteTextView mAutoCompleteTextView;
    ArrayAdapter<String> mAdapterItems;
    private final String[] collections = {"Picture Books", "Activity Books", "Easy Reader", "Fiction Novels", "Non-fiction Books", "Educational Textbooks", "Exercise Books"};

    private TextInputEditText mBookIdET, mBookTitleET, mBookPriceET;
    private String bookId = "";
    private String bookTitle = "";
    private String bookPrice = "";
    private String bookCollection = "";
    private String errorMsg = "";
    private boolean errorCheck = false;

    private FirebaseFirestore db;

    private Uri imgUri;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_book);

        mBackBtn = findViewById(R.id.adminAddBook_backImageView);
        mAutoCompleteTextView = findViewById(R.id.adminAddBook_autocompleteText);
        mBookIdET = findViewById(R.id.adminAddBook_bookIdField);
        mBookTitleET = findViewById(R.id.adminAddBook_bookTitleField);
        mBookPriceET = findViewById(R.id.adminAddBook_bookPriceField);
        mUploadBtn = findViewById(R.id.adminAddBook_uploadCard);
        mSaveBtn = findViewById(R.id.adminAddBook_saveButton);

        mAdapterItems = new ArrayAdapter<>(this, R.layout.list_item, collections);
        mAutoCompleteTextView.setAdapter(mAdapterItems);

        db = FirebaseFirestore.getInstance();

        mAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> bookCollection = parent.getItemAtPosition(position).toString());

        mBackBtn.setOnClickListener(view -> finish());

        mUploadBtn.setOnClickListener(view -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(i, 100);
            errorCheck = false;
        });

        mBookIdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorCheck = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                bookId = Objects.requireNonNull(mBookIdET.getText()).toString();
            }
        });

        mBookTitleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorCheck = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                bookTitle = Objects.requireNonNull(mBookTitleET.getText()).toString();
            }
        });

        mBookPriceET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorCheck = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                bookPrice = Objects.requireNonNull(mBookPriceET.getText()).toString();
            }
        });

        mSaveBtn.setOnClickListener(view -> {
            errorMsg = "";

            //check for missing fields & redundant book ID
            if (imgUri == null)
            {
                errorCheck = true;
                errorMsg += "\nNo Book Image!";
            }

            if (bookId.equals(""))
            {
                errorCheck = true;
                errorMsg += "\nNo Book ID!";
            }

            if (bookTitle.equals(""))
            {
                errorCheck = true;
                errorMsg += "\nNo Book Title!";
            }

            if (bookPrice.equals(""))
            {
                errorCheck = true;
                errorMsg += "\nNo Book Price!";
            }

            if (bookCollection.equals(""))
            {
                errorCheck = true;
                errorMsg += "\nNo Book Collection!";
            }

            if (!errorCheck)
            {
                //format price
                DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
                bookPrice = formatter.format(Double.parseDouble(bookPrice));

                Map<String, Object> book = new HashMap<>();
                book.put("title", bookTitle);
                book.put("price", bookPrice);
                book.put("bookID", bookId);
                book.put("collection", bookCollection);

                //check if book with the specific id already exists
                db.collection("books")
                        .whereEqualTo(FieldPath.documentId(), bookId)
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                errorMsg = "Redundant Book ID!";
                                KAlertDialog eMessage = new KAlertDialog(AddBook.this, KAlertDialog.ERROR_TYPE);
                                eMessage.setTitleText("Error!");
                                eMessage.setContentText(errorMsg);
                                eMessage.setCanceledOnTouchOutside(true);
                                eMessage.show();
                            }
                            else
                            {
                                String fileName = "IMG_" + bookId;
                                mStorageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);

                                KAlertDialog pDialog = new KAlertDialog(AddBook.this, KAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog.setTitleText("Uploading image...");
                                pDialog.setCanceledOnTouchOutside(false);
                                pDialog.show();

                                mStorageReference.putFile(imgUri)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            if (pDialog.isShowing())
                                            {
                                                pDialog.dismiss();
                                            }

                                            db.collection("books").document(bookId)
                                                    .set(book)
                                                    .addOnSuccessListener(unused -> {
                                                        finish();
                                                        Toast.makeText(AddBook.this, "Book added successfully!", Toast.LENGTH_SHORT).show();
                                                    }).addOnFailureListener(e -> {
                                                        finish();
                                                        Toast.makeText(AddBook.this, "Some error has occurred, failed to add book!", Toast.LENGTH_SHORT).show();
                                                    });
                                        }).addOnFailureListener(e -> {
                                            if (pDialog.isShowing())
                                            {
                                                pDialog.dismiss();
                                            }
                                            Toast.makeText(AddBook.this, "Error uploading book image!", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        });
            }
            else
            {
                //display error dialog message box
                KAlertDialog eMessage = new KAlertDialog(AddBook.this, KAlertDialog.ERROR_TYPE);
                eMessage.setTitleText("Error!");
                eMessage.setContentText(errorMsg);
                eMessage.setCanceledOnTouchOutside(true);
                eMessage.show();
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null)
        {
            imgUri = data.getData();
            Toast.makeText(this, "Image successfully selected!", Toast.LENGTH_SHORT).show();
        }
    }
}