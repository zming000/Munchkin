package com.example.munchkin;

public class CartItem {

    String quantity, bookId, title, price;

    public CartItem(){

    }

    public CartItem(String quantity, String bookId, String title, String price) {
        this.quantity = quantity;
        this.bookId = bookId;
        this.title = title;
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
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
}
