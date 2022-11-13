package com.example.munchkin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class OrderHistory extends AppCompatActivity {
    SwipeRefreshLayout mswipeHistory;
    RecyclerView mrvOrderHistory;
    ArrayList<ModelHistoryList> historyList;
    AdapterHistoryList historyAdapter;
    FirebaseFirestore historyDB;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        //assign variable
        mrvOrderHistory = findViewById(R.id.orderHistory);
        mswipeHistory = findViewById(R.id.swipeHistory);
        mrvOrderHistory.setLayoutManager(new LinearLayoutManager(this));

        //initialize variables
        historyDB = FirebaseFirestore.getInstance();
        historyList = new ArrayList<>();

        //initialize adapter
        historyAdapter = new AdapterHistoryList(this, historyList);
        mrvOrderHistory.setAdapter(historyAdapter);

        getOrderDetailsFromFirestore();

        mswipeHistory.setOnRefreshListener(() -> {
            getOrderDetailsFromFirestore();
            mswipeHistory.setRefreshing(false);
        });
    }

    private void getOrderDetailsFromFirestore() {
        SharedPreferences spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        //get user id from shared preference
        String uID = "tester1";
                //spDrivme.getString(KEY_ID, null);

        historyDB.collection("orders")
                .whereEqualTo("custId", uID)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(OrderHistory.this, "Error Loading Orders!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    historyList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED) {
                            historyList.add(dc.getDocument().toObject(ModelHistoryList.class));
                        }
                    }

                    //if no records found
                    if(historyList.size() == 0){
                        Toast.makeText(OrderHistory.this, "No orders!", Toast.LENGTH_SHORT).show();
                    }

                    historyAdapter.notifyDataSetChanged();
                });
    }
}