package com.example.munchkin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditBook extends AppCompatActivity {

    ImageView mBackBtn;
    CardView mUploadBtn, mUpdateBtn;

    private final String[] collections = {"Picture Books", "Activity Books", "Easy Reader", "Fiction Novels", "Non-fiction Books", "Educational Textbooks", "Exercise Books"};
    private AutoCompleteTextView mAutoCompleteTextView;
    ArrayAdapter<String> mAdapterItems;

    private ImageView mBookImg;
    private TextInputEditText mBookIdET, mBookTitleET, mBookPriceET;
    private TextInputLayout mBookIdLayout;
    private String bookId = "";
    private String bookTitle = "";
    private String bookPrice = "";
    private String bookCollection = "";



    private FirebaseFirestore db;

    private int pos = -1;

    private boolean changeCheck = false;
    private String errorMsg = "";
    private boolean errorCheck = false;
    private boolean imgChange = false;

    private Uri imgUri;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit_book);

        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");

        mBackBtn = findViewById(R.id.adminEditBook_backImageView);
        mAutoCompleteTextView = findViewById(R.id.adminEditBook_autocompleteText);
        mBookImg = findViewById(R.id.adminEditBook_bookImage);
        mBookIdET = findViewById(R.id.adminEditBook_bookIdField);
        mBookTitleET = findViewById(R.id.adminEditBook_bookTitleField);
        mBookPriceET = findViewById(R.id.adminEditBook_bookPriceField);
        mUploadBtn = findViewById(R.id.adminEditBook_uploadCard);
        mUpdateBtn = findViewById(R.id.adminEditBook_updateButton);
        mBookIdLayout = findViewById(R.id.adminEditBook_bookIdField_layout);

        mAdapterItems = new ArrayAdapter<>(this, R.layout.list_item, collections);
        mAutoCompleteTextView.setAdapter(mAdapterItems);

        mAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            changeCheck = true;
            bookCollection = parent.getItemAtPosition(position).toString();
        });

        Bundle extra = getIntent().getExtras();

        if (extra != null)
        {
            pos = extra.getInt("position");
        }

        //Get data from database
        db = FirebaseFirestore.getInstance();

        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int i = 0;

                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            String id = document.getId();
                            String title = document.getString("title");
                            double price = Double.parseDouble(Objects.requireNonNull(document.getString("price")));
                            String collection = document.getString("collection");

                            if (i == pos)
                            {
                                Book mBook = new Book(id, title, price, collection);

                                mBookIdET.setText(document.getId());
                                mBookIdET.setFocusable(false);
                                mBookIdET.setClickable(false);
                                mBookIdET.setCursorVisible(false);
                                mBookIdLayout.setBoxBackgroundColor(Color.LTGRAY);

                                mBookTitleET.setText(mBook.bookTitle);
                                mBookPriceET.setText(formatter.format(mBook.bookPrice));

                                mAutoCompleteTextView.setText(mBook.bookCollection, false);

                                bookId = mBook.bookId;
                                bookTitle = mBook.bookTitle;
                                bookPrice = formatter.format(mBook.bookPrice);
                                bookCollection = mBook.bookCollection;

                                mStorageReference = FirebaseStorage.getInstance().getReference("images/IMG_" + mBook.bookId);

                                try
                                {
                                    File localFile = File.createTempFile("tempFile", ".jpg");
                                    mStorageReference.getFile(localFile)
                                            .addOnSuccessListener(taskSnapshot -> {
                                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                mBookImg.setImageBitmap(bitmap);
                                            }).addOnFailureListener(e -> Log.d("RETRIEVE_FAIL", "Retrieving book image failed: " + mBook.bookId));

                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }

                                break;
                            }

                            i++;
                        }

                    }
                    else
                    {
                        finish();
                        Toast.makeText(EditBook.this, "Error displaying edit page!", Toast.LENGTH_SHORT).show();
                    }
                });

        mUploadBtn.setOnClickListener(view -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(i, 101);
        });

        mBookTitleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeCheck = true;
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
                changeCheck = true;
                errorCheck = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                bookPrice = Objects.requireNonNull(mBookPriceET.getText()).toString();
            }
        });

        mBackBtn.setOnClickListener(view -> finish());

        mUpdateBtn.setOnClickListener(view -> {
            errorMsg = "";

            if (changeCheck)
            {
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
                    if (imgChange)
                    {
                        //delete old image
                        mStorageReference = FirebaseStorage.getInstance().getReference("images/IMG_" + bookId);
                        mStorageReference.delete()
                                .addOnCompleteListener(task -> Log.d("DELETE_SUCCESS", "Deleting book image successful: " + bookId)).addOnFailureListener(e -> Log.d("DELETE_FAIL", "Deleting book image failed: " + bookId));

                        //add new image
                        String fileName = "IMG_" + bookId;
                        mStorageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);

                        mStorageReference.putFile(imgUri)
                                .addOnSuccessListener(taskSnapshot -> Log.d("NEWUPLOAD_SUCCESS", "Successful in uploading new image: " + bookId)).addOnFailureListener(e -> Log.d("NEWUPLOAD_ERROR", "Error uploading new image: " + bookId));
                    }

                    bookPrice = formatter.format(Double.parseDouble(bookPrice));

                    //update data
                    Map<String, Object> book = new HashMap<>();
                    book.put("title", bookTitle);
                    book.put("price", bookPrice);
                    book.put("collection", bookCollection);

                    db.collection("books")
                            .document(bookId)
                            .update(book)
                            .addOnSuccessListener(unused -> {
                                Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    Intent intent = new Intent(getApplicationContext(), EditList.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);

                                    Toast.makeText(EditBook.this, "Book details successfully edited!", Toast.LENGTH_SHORT).show();
                                }, 3000);
                            }).addOnFailureListener(e -> {
                                finish();
                                Toast.makeText(EditBook.this, "Some error has occurred, failed to update book!", Toast.LENGTH_SHORT).show();
                            });
                }
                else
                {
                    //display error dialog message box
                    KAlertDialog eMessage = new KAlertDialog(EditBook.this, KAlertDialog.ERROR_TYPE);
                    eMessage.setTitleText("Error!");
                    eMessage.setContentText(errorMsg);
                    eMessage.setCanceledOnTouchOutside(true);
                    eMessage.show();
                }


            }
            else
            {
                //display error dialog message box due to no change
                errorMsg = "No changes, nothing to update!";
                KAlertDialog eMessage = new KAlertDialog(EditBook.this, KAlertDialog.ERROR_TYPE);
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

        if (requestCode == 101 && data != null && data.getData() != null)
        {
            imgUri = data.getData();
            mBookImg.setImageURI(imgUri);

            imgChange = true;
            Toast.makeText(this, "New image successfully selected!", Toast.LENGTH_SHORT).show();
        }
    }
}