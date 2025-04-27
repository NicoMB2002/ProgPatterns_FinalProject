package org.nicolas.model;

import org.nicolas.model.User;

import java.util.ArrayList;

public class Student extends User {

    private int userId;
    private String name;
    private ArrayList<Book> borrowedBooks;

    public Student(int userID, String name, String password, User currentUser, ArrayList<Book> borrowedBooks) {
        super(userID, name, password, currentUser);
        this.borrowedBooks = borrowedBooks;
    }

    public void seeBorrowedBooks () {}
}
