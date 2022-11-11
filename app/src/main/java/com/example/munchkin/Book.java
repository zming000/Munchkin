package com.example.munchkin;

public class Book {
    String bookId;
    String bookTitle;
    double bookPrice;
    String bookCollection;

    public Book() {}

    public Book(String bookId, String bookTitle, double bookPrice, String bookCollection) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookPrice = bookPrice;
        this.bookCollection = bookCollection;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getBookCollection() {
        return bookCollection;
    }

    public void setBookCollection(String bookCollection) {
        this.bookCollection = bookCollection;
    }
}
