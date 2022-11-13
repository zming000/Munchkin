package com.example.munchkin;

public class ModelHistoryList {
    String date, status, totalItem, totalPrice;

    public ModelHistoryList() {
    }

    public ModelHistoryList(String date, String status, String totalItem, String totalPrice) {
        this.date = date;
        this.status = status;
        this.totalItem = totalItem;
        this.totalPrice = totalPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(String totalItem) {
        this.totalItem = totalItem;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
