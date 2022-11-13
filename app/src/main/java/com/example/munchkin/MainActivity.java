package com.example.munchkin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView pictureBook = findViewById(R.id.PictureBooks);
        CardView activityBook = findViewById(R.id.ActivityBooks);
        CardView easyReader = findViewById(R.id.EasyReader);
        CardView fictionNovels = findViewById(R.id.FictionNovels);
        CardView nonFiction = findViewById(R.id.NonFiction);
        CardView educationalTextbooks = findViewById(R.id.EducationalTextbooks);
        CardView exerciseBooks = findViewById(R.id.ExerciseBooks);

        pictureBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.nav.BookList.class);
                intent.putExtra("collection", "Picture Books");
                startActivity(intent);
            }
        });

        activityBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.nav.BookList.class);
                intent.putExtra("collection", "Activity Books");
                startActivity(intent);
            }
        });

        easyReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.nav.BookList.class);
                intent.putExtra("collection", "Easy Reader");
                startActivity(intent);
            }
        });

        fictionNovels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.nav.BookList.class);
                intent.putExtra("collection", "Fiction Novels");
                startActivity(intent);
            }
        });

        nonFiction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.nav.BookList.class);
                intent.putExtra("collection", "Non-fiction Books");
                startActivity(intent);
            }
        });

        educationalTextbooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.nav.BookList.class);
                intent.putExtra("collection", "Educational Textbooks");
                startActivity(intent);
            }
        });

        exerciseBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.nav.BookList.class);
                intent.putExtra("collection", "Exercise Books");
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.account:
                    startActivity(new Intent(getApplicationContext(), Account.class));
                    overridePendingTransition(0,0);
                    finish();
                    return true;
                case R.id.home:
                    return true;
            }
            return false;
        });
    }
}