package org.nicolas.model;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.exceptions.InvalidUserException;

import java.util.Scanner;

public abstract class User {
    private int userID;
    private String name;
    private String password;

    public User(int userID, String name, String password) {
        this.userID = userID;
        this.name = name;
        this.password = password;
    }

    public abstract void borrowBook(String isbn, int userID);

    public void changeName (String newName) {
        LibraryDatabase.updateUserName(userID, newName);
    }

    public void changePassword (String newPassword) {
        LibraryDatabase.updateUserPassword(userID, newPassword);
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
