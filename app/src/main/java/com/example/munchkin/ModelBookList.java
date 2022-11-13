package com.example.munchkin;

public class ModelBookList {
    String title, bookID, price;

    public ModelBookList(){

    }

    public ModelBookList(String title, String price, String bookID) {
        this.bookID = bookID;
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }
}
