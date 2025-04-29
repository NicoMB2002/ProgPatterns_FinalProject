package org.nicolas.model;

import org.nicolas.model.User;

import java.util.ArrayList;

public class Student extends User {

    private int userId;
    private String name;
    private UserType typeOfUSer;

    public Student(int userID, String name, String password) {
        super(userID, name, password);
    }

    public void seeBorrowedBooks () {}
}
