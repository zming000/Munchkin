package com.example.munchkin;

import java.util.List;

public class Order {
    String orderId;
    String orderStatus;
    String orderDate;
    int totalItem;
    double totalPrice;
    List<OrderBook> mOrderBookList;
    String custId;
    String custName;
    String shippingAddress;

    public Order(String orderId, String orderStatus, String orderDate, int totalItem, double totalPrice, List<OrderBook> orderBookList, String custId, String custName, String shippingAddress) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.totalItem = totalItem;
        this.totalPrice = totalPrice;
        mOrderBookList = orderBookList;
        this.custId = custId;
        this.custName = custName;
        this.shippingAddress = shippingAddress;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderBook> getOrderBookList() {
        return mOrderBookList;
    }

    public void setOrderBookList(List<OrderBook> orderBookList) {
        mOrderBookList = orderBookList;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}