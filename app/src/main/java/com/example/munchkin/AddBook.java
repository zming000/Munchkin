package com.example.munchkin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class AddBook extends AppCompatActivity {

    private ImageView mBackBtn;

    private String[] collections = {"Picture Books", "Activity Books", "Easy Reader", "Fiction Novels", "Non-fiction Books", "Educational Textbooks", "Exercise Books"};
    private AutoCompleteTextView mAutoCompleteTextView;
    private ArrayAdapter<String> mAdapterItems;

    private TextInputEditText mBookIdET, mBookTitleET, mBookPriceET;
    private String bookId = "";
    private String bookTitle = "";
    private String bookPrice = "";
    private String bookCollection = "";

    private CardView mUploadBtn, mSaveBtn;

    private FirebaseFirestore db;

    private String errorMsg = "";
    private boolean errorCheck = false;

    private Uri imgUri;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_add_book);

        mBackBtn = findViewById(R.id.adminAddBook_backImageView);
        mAutoCompleteTextView = findViewById(R.id.adminAddBook_autocompleteText);
        mBookIdET = findViewById(R.id.adminAddBook_bookIdField);
        mBookTitleET = findViewById(R.id.adminAddBook_bookTitleField);
        mBookPriceET = findViewById(R.id.adminAddBook_bookPriceField);
        mUploadBtn = findViewById(R.id.adminAddBook_uploadCard);
        mSaveBtn = findViewById(R.id.adminAddBook_saveButton);

        mAdapterItems = new ArrayAdapter<String>(this, R.layout.list_item, collections);
        mAutoCompleteTextView.setAdapter(mAdapterItems);

        db = FirebaseFirestore.getInstance();

        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bookCollection = parent.getItemAtPosition(position).toString();
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, 100);
                errorCheck = false;
            }
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
                bookId = mBookIdET.getText().toString();
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
                bookTitle = mBookTitleET.getText().toString();
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
                bookPrice = mBookPriceET.getText().toString();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    book.put("collection", bookCollection);

                    //check if book with the specific id already exists
                    db.collection("books")
                            .whereEqualTo(FieldPath.documentId(), bookId)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        if (pDialog.isShowing())
                                                        {
                                                            pDialog.dismiss();
                                                        }

                                                        db.collection("books").document(bookId)
                                                                .set(book)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        finish();
                                                                        Toast.makeText(AddBook.this, "Book added successfully!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        finish();
                                                                        Toast.makeText(AddBook.this, "Some error has occurred, failed to add book!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        if (pDialog.isShowing())
                                                        {
                                                            pDialog.dismiss();
                                                        }
                                                        Toast.makeText(AddBook.this, "Error uploading book image!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
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