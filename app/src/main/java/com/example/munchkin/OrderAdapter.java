package com.example.munchkin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private Context context;
    private ArrayList<Order> ordersList;

    public OrderAdapter(Context context, ArrayList<Order> ordersList)
    {
        this.context = context;
        this.ordersList = ordersList;
    }

    public class OrderHolder extends RecyclerView.ViewHolder
    {
        private TextView orderNoTV;
        private TextView statusTV;
        private TextView dateTV;
        private TextView itemTotalTV;
        private TextView priceTotalTV;
        private TextView seeMoreBtn;
        private ImageView seeMoreArrowBtn;

        public OrderHolder(final View view)
        {
            super(view);

            orderNoTV = view.findViewById(R.id.orderCard_orderNo_textView);
            statusTV = view.findViewById(R.id.orderCard_orderStatus_textView);
            dateTV = view.findViewById(R.id.orderCard_orderDate_textView);
            itemTotalTV = view.findViewById(R.id.orderCard_orderItemTotal_textView);
            priceTotalTV = view.findViewById(R.id.orderCard_orderPriceTotal_textView);
            seeMoreBtn = view.findViewById(R.id.orderCard_seeMoreTextView);
            seeMoreArrowBtn = view.findViewById(R.id.orderCard_seeMoreArrow);
        }
    }


    @NonNull
    @Override
    public OrderAdapter.OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card, parent, false);
        return new OrderHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderHolder holder, int position) {
        Order o = ordersList.get(position);

        holder.orderNoTV.setText("Order No#" + o.orderId);
        holder.statusTV.setText(o.orderStatus);
        holder.dateTV.setText("Date: " + o.orderDate);
        holder.itemTotalTV.setText("Item Total: " + o.totalItem);

        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        String tempPrice;
        tempPrice = formatter.format(o.totalPrice);
        holder.priceTotalTV.setText("Price Total: RM" + tempPrice);

        holder.seeMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (context, OrderDetails.class);
                intent.putExtra("orderID", o.orderId);
                context.startActivity(intent);
            }
        });

        holder.seeMoreArrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (context, OrderDetails.class);
                intent.putExtra("orderID", o.orderId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }
}