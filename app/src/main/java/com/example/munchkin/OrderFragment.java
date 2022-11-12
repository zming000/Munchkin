package com.example.munchkin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OrderFragment extends Fragment {

    private ImageView mBackBtn;
    private TextView mNoOrderTV;

    private Context context;
    private ArrayList<Order> mOrderList;

    private OrderAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore db;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Fetching order data...");
        mProgressDialog.show();

        mBackBtn = view.findViewById(R.id.OrderFragment_backImageView);
        mNoOrderTV = view.findViewById(R.id.orderPage_noOrderTV);

        mRecyclerView = view.findViewById(R.id.orderPage_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();

        mOrderList = new ArrayList<Order>();
        mAdapter = new OrderAdapter(getContext(), mOrderList);

        mRecyclerView.setAdapter(mAdapter);

        //retrieve data
        db.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                String orderId = document.getId();
                                String orderStatus = document.getString("status");
                                String orderDate = document.getString("date");

                                String custId = document.getString("custId");
                                String custName = document.getString("custName");
                                String shipAddress = document.getString("shippingAddress");

                                int totalItem = Integer.parseInt(document.getString("totalItem"));
                                double totalPrice = Double.parseDouble(document.getString("totalPrice"));

                                ArrayList<String> tempBookId = new ArrayList<String>();
                                tempBookId = (ArrayList<String>) document.get("orderedItems");

                                ArrayList<String> tempBookTitle = new ArrayList<String>();
                                tempBookTitle = (ArrayList<String>) document.get("orderedItemsName");

                                ArrayList<String> tempBookPrice = new ArrayList<String>();
                                tempBookPrice = (ArrayList<String>) document.get("orderedItemsPrice");

                                ArrayList<String> tempBookCollection = new ArrayList<String>();
                                tempBookCollection = (ArrayList<String>) document.get("orderedItemsCollection");

                                ArrayList<String> tempBookQty = new ArrayList<String>();
                                tempBookQty = (ArrayList<String>) document.get("orderedItemsQty");

                                ArrayList<OrderBook> orderedBooks = new ArrayList<OrderBook>();

                                for (int i=0; i<tempBookId.size(); i++)
                                {
                                    //create book object
                                    Book mBook = new Book(tempBookId.get(i), tempBookTitle.get(i), Double.parseDouble(tempBookPrice.get(i)), tempBookCollection.get(i));

                                    //create orderBook object
                                    int tQty = Integer.parseInt(tempBookQty.get(i));
                                    double tPrice = tQty * Double.parseDouble(tempBookPrice.get(i));
                                    OrderBook mOrderBook = new OrderBook(mBook, tQty, tPrice);

                                    orderedBooks.add(mOrderBook);
                                }

                                //create order object
                                Order mOrder = new Order(orderId, orderStatus, orderDate, totalItem, totalPrice, orderedBooks, custId, custName, shipAddress);

                                //add order object
                                mOrderList.add(mOrder);
                            }

                            mAdapter.notifyDataSetChanged();

                            //check if arraylist is empty
                            //check if arraylist is empty
                            if (mAdapter.getItemCount() == 0)
                            {
                                mRecyclerView.setVisibility(View.GONE);
                                mNoOrderTV.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                mNoOrderTV.setVisibility(View.GONE);
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
                getActivity().finish();
            }
        });

        return view;
    }
}