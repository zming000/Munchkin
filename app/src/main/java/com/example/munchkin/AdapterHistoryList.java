package com.example.munchkin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterHistoryList extends RecyclerView.Adapter<AdapterHistoryList.HistoryListViewHolder> {
    //declare variables
    Context historyContext;
    ArrayList<ModelHistoryList> historyArrayList;

    public AdapterHistoryList(Context historyContext, ArrayList<ModelHistoryList> historyArrayList) {
        this.historyContext = historyContext;
        this.historyArrayList = historyArrayList;
    }

    @NonNull
    @Override
    public AdapterHistoryList.HistoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(historyContext).inflate(R.layout.item_history, parent, false);

        return new HistoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHistoryList.HistoryListViewHolder holder, int position) {
        //get position
        ModelHistoryList mhl = historyArrayList.get(position);

        holder.mtvDate.setText(mhl.date);
        holder.mtvItemsValue.setText(mhl.totalItem);
        holder.mtvTotal.setText(mhl.totalPrice);
        holder.mtvStatus.setText(mhl.status);
    }

    @Override
    public int getItemCount() {
        return historyArrayList.size();
    }

    public static class HistoryListViewHolder extends RecyclerView.ViewHolder{
        //declare variables
        TextView mtvDate, mtvItemsValue, mtvTotal, mtvStatus;

        public HistoryListViewHolder(@NonNull View itemView) {
            super(itemView);

            //assign variables
            mtvDate = itemView.findViewById(R.id.tvDate);
            mtvItemsValue = itemView.findViewById(R.id.tvItemsValue);
            mtvTotal = itemView.findViewById(R.id.tvTotal);
            mtvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
