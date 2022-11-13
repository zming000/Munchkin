package com.example.munchkin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BookDetail extends AppCompatActivity {
    ImageView mbookImage;
    TextInputEditText metTitle, metPrice, metQty;
    Button mbtnAddCart;
    int qty = 0;
    SharedPreferences spMunchkin;

    //key name
    private static final String SP_NAME = "munchkinPref";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        mbookImage = findViewById(R.id.bookImage);
        metTitle = findViewById(R.id.etTitle);
        metPrice = findViewById(R.id.etPrice);
        metQty = findViewById(R.id.etQty);
        mbtnAddCart = findViewById(R.id.btnAddCart);

        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String id = getIntent().getStringExtra("bookID");

        //initialize shared preference
        spMunchkin = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        //get username from shared preference
        String uName = spMunchkin.getString(KEY_USERNAME, null);

        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference("images/IMG_" + id);

        try
        {
            File localFile = File.createTempFile("tempFile", ".jpg");
            mStorageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            mbookImage.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("RETRIEVE_FAIL", "Retrieving book image failed: " + id);
                        }
                    });
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        metTitle.setText(name);
        metPrice.setText("RM " + price);

        metQty.setOnClickListener(view -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            view = dialogInflater.inflate(R.layout.activity_scroll_picker, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BookDetail.this, R.style.dialog);
            AlertDialog qtyDialog = dialogBuilder.setView(view).create();

            //obtaining the View with specific ID
            NumberPicker mnpPicker = view.findViewById(R.id.npPicker);
            Button mbtnOK = view.findViewById(R.id.btnOK);

            //set the values
            mnpPicker.setMaxValue(7);
            mnpPicker.setMinValue(1);
            mnpPicker.setValue(1);

            //show pop out dialog
            qtyDialog.show();
            qtyDialog.getWindow().setLayout(450, 670);

            mbtnOK.setOnClickListener(view1 -> {
                qty = mnpPicker.getValue();

                if (qty == 1) {
                    metQty.setText(qty + " book");
                } else {
                    metQty.setText(qty + " books");
                }

                qtyDialog.dismiss();
            });
        });

        mbtnAddCart.setOnClickListener(view -> {
            if(qty > 0){
                FirebaseFirestore addCart = FirebaseFirestore.getInstance();
                //save to database
                Map<String,Object> order = new HashMap<>();
                order.put("bookId", id);
                order.put("price", price);
                order.put("title", name);
                order.put("quantity", String.valueOf(qty));
                order.put("status", "Unpaid");

                addCart.collection("Account Details").document(uName).collection("Shopping Cart").document(id)
                        .set(order)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(BookDetail.this, "Added to your shopping cart!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(BookDetail.this, MainActivity.class));
                            finishAffinity();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(BookDetail.this, "Failed to add to shopping cart!", Toast.LENGTH_SHORT).show();
                        });
            }
            else{
                Toast.makeText(BookDetail.this, "Please input quantity before add to cart!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}