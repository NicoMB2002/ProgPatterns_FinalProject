package org.nicolas.model;

import org.nicolas.model.User;

import java.util.ArrayList;

public class Student extends User {

    private int userId;
    private String name;
    private UserType typeOfUSer;
    private ArrayList<Book> borrowedBooks;

    public Student(int userID, String name, String password) {
        super(userID, name, password);
        this.borrowedBooks = new ArrayList<>();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public UserType getTypeOfUSer() {
        return typeOfUSer;
    }

    public void setTypeOfUSer(UserType typeOfUSer) {
        this.typeOfUSer = typeOfUSer;
    }

    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(ArrayList<Book> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }
}
