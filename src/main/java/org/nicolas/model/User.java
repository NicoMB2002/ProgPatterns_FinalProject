package org.nicolas.model;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.exceptions.InvalidISBNException;
import org.nicolas.exceptions.InvalidUserException;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.Console;

public abstract class User {
    private int userID;
    private String name;
    private String password;

    public User(int userID, String name, String password) {
        this.userID = userID;
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return name + "\n" + "{" +
                "userID=" + userID +
                "}";
    }

    private String ISBNChecker(String inputISBN) throws InvalidISBNException { //TODO change the checking for '-' and remove them if there is
        inputISBN = inputISBN.replace("-", ""); // remove dashes if any
        if (inputISBN.length() != 13) {
            inputISBN = "---";
            //System.out.println("[ERROR : ISBN must be exactly 13 characters long without dashes]");
            throw new InvalidISBNException("ISBN must be exactly 13 characters long without dashes.");
        }
        for (char c : inputISBN.toCharArray()) {
            if (Character.isLetter(c)) {
                //System.out.println("[ERROR : ISBN should not contain letters]");
                throw new InvalidISBNException("ISBN should not contain letters.");
            }
        }
        return inputISBN;
    }

    public abstract void borrowBook(String isbn, int userID, Console console);

    public abstract void returnBook(String isbn, int userID);

    public ArrayList<Book> findBook (String isbnFilter, String titleFilter, String authorFilter) {
        isbnFilter = (isbnFilter.isEmpty() || isbnFilter.isBlank() || isbnFilter == null) ? isbnFilter : ISBNChecker(isbnFilter);
        return LibraryDatabase.getFilteredBooks(isbnFilter, titleFilter, authorFilter);
    }

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
