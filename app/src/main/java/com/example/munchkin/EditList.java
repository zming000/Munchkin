package com.example.munchkin;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class EditList extends AppCompatActivity {

    ImageView mBackBtn;
    TextView mNoBookTV;

    private ArrayList<Book> mBookList;

    private EditBookAdapter mAdapter;
    private RecyclerView mRecyclerView;
    FirebaseFirestore db;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit_list);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Fetching book data...");
        mProgressDialog.show();

        mBackBtn = findViewById(R.id.adminEditBook_backImageView);
        mNoBookTV = findViewById(R.id.adminEditBook_noBookTV);
        mRecyclerView = findViewById(R.id.adminEditBook_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        mBookList = new ArrayList<>();
        mAdapter = new EditBookAdapter(EditList.this, mBookList);

        mRecyclerView.setAdapter(mAdapter);

        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            String id = document.getId();
                            String title = document.getString("title");
                            double price = Double.parseDouble(Objects.requireNonNull(document.getString("price")));
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


                    }
                    else
                    {
                        Log.d("Error", "Error getting documents: ", task.getException());

                    }
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                });

        mBackBtn.setOnClickListener(view -> finish());
    }
}