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

                                ArrayList<String> tempBookId = new ArrayList<String>();
                                tempBookId = (ArrayList<String>) document.get("orderedItems");

                                ArrayList<OrderBook> orderedBooks = new ArrayList<OrderBook>();
                                int totalItem = document.getLong("totalItem").intValue();
                                double totalPrice = document.getLong("totalPrice");

                                Log.d("TEST", tempBookId.get(0) + tempBookId.get(1));

                                for (int i=0; i<tempBookId.size(); i++)
                                {
                                    //get book data object from database (search for book using bookId)
                                    db.collection("books").document(tempBookId.get(i))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        DocumentSnapshot bDocument = task.getResult();
                                                        if (bDocument.exists())
                                                        {
                                                            String bId = bDocument.getId();
                                                            String bTitle = bDocument.getString("title");
                                                            double bPrice = Double.parseDouble(bDocument.getString("price"));
                                                            String bCollection = bDocument.getString("collection");

                                                            //get book quantity
                                                            int bQty = document.getLong(bId).intValue();

                                                            //calculate the price
                                                            double bTotalPrice = bQty * bPrice;

                                                            //create OrderBook object & add into arraylist
                                                            Book mBook = new Book(bId, bTitle, bPrice, bCollection);
                                                            OrderBook mOrderBook = new OrderBook(mBook, bQty, bTotalPrice);
                                                            orderedBooks.add(mOrderBook);

                                                        }
                                                    }
                                                    else
                                                    {
                                                        Log.d("GET_BOOK_FOR_ORDER_FAIL", "",task.getException());
                                                    }
                                                }
                                            });
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