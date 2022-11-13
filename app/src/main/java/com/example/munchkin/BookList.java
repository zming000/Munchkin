package com.example.munchkin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class BookList extends AppCompatActivity {
    //declare variable
    RecyclerView mrvBook;
    ArrayList<com.example.nav.ModelBookList> bookList;
    com.example.nav.AdapterBookList bookAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        //assign variable
        mrvBook = findViewById(R.id.rvCollection);
        mrvBook.setLayoutManager(new LinearLayoutManager(this));

        //initialize variables
        bookList = new ArrayList<>();

        //initialize adapter
        bookAdapter = new com.example.nav.AdapterBookList(this, bookList);
        mrvBook.setAdapter(bookAdapter);

        getBookDetailsFromFirestore();
    }

    private void getBookDetailsFromFirestore() {
        FirebaseFirestore bookDB  = FirebaseFirestore.getInstance();
        String collectionName = getIntent().getStringExtra("collection");

        bookDB.collection("books")
                .whereEqualTo("collection", collectionName)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(BookList.this, "Error Loading Book!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    bookList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            bookList.add(dc.getDocument().toObject(com.example.nav.ModelBookList.class));
                        }
                    }

                    //if no records found
                    if(bookList.size() == 0){
                        Toast.makeText(BookList.this, "No books!", Toast.LENGTH_SHORT).show();
                    }

                    bookAdapter.notifyDataSetChanged();
                });
    }
}