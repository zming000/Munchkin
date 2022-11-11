package com.example.munchkin;

public class OrderBook {
    Book mBook;
    int bookQty;
    double totalBookPrice;

    public OrderBook(Book book, int bookQty, double totalBookPrice) {
        mBook = book;
        this.bookQty = bookQty;
        this.totalBookPrice = totalBookPrice;
    }

    public Book getBook() {
        return mBook;
    }

    public void setBook(Book book) {
        mBook = book;
    }

    public int getBookQty() {
        return bookQty;
    }

    public void setBookQty(int bookQty) {
        this.bookQty = bookQty;
    }

    public double getTotalBookPrice() {
        return totalBookPrice;
    }

    public void setTotalBookPrice(double totalBookPrice) {
        this.totalBookPrice = totalBookPrice;
    }
}