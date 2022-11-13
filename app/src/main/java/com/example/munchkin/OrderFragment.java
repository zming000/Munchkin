package com.example.munchkin;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class OrderFragment extends Fragment {

    ImageView mBackBtn;
    TextView mNoOrderTV;

    private ArrayList<Order> mOrderList;

    private OrderAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    FirebaseFirestore db;
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

        mOrderList = new ArrayList<>();
        mAdapter = new OrderAdapter(getContext(), mOrderList);

        mRecyclerView.setAdapter(mAdapter);

        //retrieve data
        db.collection("orders")
                .get()
                .addOnCompleteListener(task -> {
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

                            ArrayList<String> tempBookId;
                            tempBookId = (ArrayList<String>) document.get("orderedItems");

                            ArrayList<String> tempBookTitle;
                            tempBookTitle = (ArrayList<String>) document.get("orderedItemsName");

                            ArrayList<String> tempBookPrice;
                            tempBookPrice = (ArrayList<String>) document.get("orderedItemsPrice");

                            ArrayList<String> tempBookCollection;
                            tempBookCollection = (ArrayList<String>) document.get("orderedItemsCollection");

                            ArrayList<String> tempBookQty;
                            tempBookQty = (ArrayList<String>) document.get("orderedItemsQty");

                            ArrayList<OrderBook> orderedBooks = new ArrayList<>();

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

                    }
                    else
                    {
                        Log.d("Error", "Error getting documents: ", task.getException());

                    }
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                });

        mBackBtn.setOnClickListener(view1 -> getActivity().finish());

        return view;
    }
}