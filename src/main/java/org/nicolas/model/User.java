package org.nicolas.model;

import org.nicolas.exceptions.InvalidUserException;

import java.util.Scanner;

public class User {
    private int userID;
    private String name;
    private String password;

    public User(int userID, String name, String password) {
        this.userID = userID;
        this.name = name;
        this.password = password;

    }

    public void borrowBook(String isbn) {

    }

    protected void changeName () {}

    protected void changePassword () {}

    public boolean login(int userID, String password) {
        return (this.userID == userID && this.password.equals(password));
    }


    public int getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
