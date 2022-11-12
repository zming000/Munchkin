package com.example.munchkin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RemoveBook extends AppCompatActivity {

    private ImageView mBackBtn;
    private TextView mNoBookTV;

    private ArrayList<Book> mBookList;

    private RemoveBookAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore db;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_remove_book);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Fetching book data...");
        mProgressDialog.show();

        mBackBtn = findViewById(R.id.adminRemoveBook_backImageView);
        mNoBookTV = findViewById(R.id.adminRemoveBook_noBookTV);
        mRecyclerView = findViewById(R.id.adminRemoveBook_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        mBookList = new ArrayList<Book>();
        mAdapter = new RemoveBookAdapter(RemoveBook.this, mBookList);

        mRecyclerView.setAdapter(mAdapter);

        db.collection("books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                String id = document.getId();
                                String title = document.getString("title");
                                double price = Double.parseDouble(document.getString("price"));
                                String collection = document.getString("collection");


                                Book mBook = new Book(id, title, price, collection);
                                mBookList.add(mBook);
                            }

                            mAdapter.notifyDataSetChanged();

                            //check if arraylist is empty
                            if (mAdapter.getItemCount() == 0)
                            {
                                mRecyclerView.setVisibility(View.GONE);
                                mNoBookTV.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                mNoBookTV.setVisibility(View.GONE);
                            }

                            if (mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        }
                        else
                        {
                            Log.d("Error", "Error getting documents: ", task.getException());

                            if (mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        }
                    }
                });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}