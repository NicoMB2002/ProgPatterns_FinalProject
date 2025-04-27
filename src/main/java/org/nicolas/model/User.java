package org.nicolas.model;

import org.nicolas.exceptions.InvalidUserException;

import java.util.Scanner;

public class User {
    private int userID;
    private String name;
    private String password;
    private User currentUser;

    public User(int userID, String name, String password, User currentUser) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.currentUser = currentUser;

    }

    public void borrowBook(String isbn) {
        if (currentUser instanceof Student) {
//            LibraryDatabase.borrowBook();
        } else if (currentUser instanceof Librarian) {
            // Librarian borrow-for-student logic
        } else {
            throw new InvalidUserException("Only students/librarians can borrow books.");
        }
    }

    protected void changeName () {}

    protected void changePassword () {}

    public boolean login (int userID, String password) {
        if (currentUser.userID == userID && password.equals(currentUser.password)) {
            return true;
        }
        return false;

    }

    public User getCurrentUser() {
        return currentUser;
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

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
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
