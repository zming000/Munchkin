package com.example.munchkin;

public class CartItem {

    private String cartItemID;
    private int quantity;

    private String bookID;
    private String bookTitle;
    private String bookPrice;
    private int imageID;

    public CartItem(){

    }

    public CartItem(String cartItemID, String bookID, String bookTitle, String bookPrice, int imageID, int quantity){

        this.cartItemID = cartItemID;
        this.bookID = bookID;

        this.bookTitle = bookTitle;
        this.bookPrice = bookPrice;
        this.imageID = imageID;

        this.quantity = quantity;

    }

    public String getCartItemID() {
        return cartItemID;
    }

    public void setCartItemID(String cartItemID) {
        this.cartItemID = cartItemID;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
