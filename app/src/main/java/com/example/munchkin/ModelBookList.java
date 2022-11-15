package com.example.munchkin;

public class ModelBookList {
    String title, bookID, price, collection;

    public ModelBookList(){

    }

    public ModelBookList(String title, String bookID, String price, String collection) {
        this.title = title;
        this.bookID = bookID;
        this.price = price;
        this.collection = collection;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
