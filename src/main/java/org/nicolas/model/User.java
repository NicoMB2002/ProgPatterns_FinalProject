package org.nicolas.model;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.exceptions.InvalidISBNException;

import java.util.ArrayList;
import java.io.Console;

public abstract class User {
    private int userID;
    private String name;
    private String password;

    //CONSTRUCTOR///////////////////////////////////////////////////////////////////////////////////////////////////////
    public User(int userID, String name, String password) {
        this.userID = userID;
        this.name = name;
        this.password = password;
    }

    //HELPER METHODS////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * checks if an ISBN is in a valid format
     * @param inputISBN the input isbn
     * @return the isbn if it is valid
     * @throws InvalidISBNException the exception thrown when the ISBN is not valid
     */
    private String ISBNChecker(String inputISBN) throws InvalidISBNException {
        inputISBN = inputISBN.replace("-", ""); // remove dashes if any
        if (inputISBN.length() != 13 && inputISBN.length() != 10) {
            throw new InvalidISBNException("ISBN must be exactly 10 or 13 characters long without dashes.");
        }
        for (char c : inputISBN.toCharArray()) {
            if (Character.isLetter(c)) {
                throw new InvalidISBNException("ISBN should not contain letters.");
            }
        }
        return inputISBN;
    }

    /**
     * borrows a book for a user
     * @param isbn the id of the book to borrow
     * @param userID the id of the user
     * @param console the system console
     */
    public abstract void borrowBook(String isbn, int userID, Console console);

    /**
     * returns a borrowed book of a user
     * @param isbn the id of the book to return
     * @param userID the id of the user whishing to return the book
     */
    public abstract void returnBook(String isbn, int userID);

    /**
     * finds a book in the database matching the pattern of certain filter
     * @param isbnFilter the book id filter
     * @param titleFilter the title filter
     * @param authorFilter the author filter
     * @return an ArrayList of books matching the filters
     */
    public ArrayList<Book> findBook (String isbnFilter, String titleFilter, String authorFilter) {
        isbnFilter = (isbnFilter.isEmpty() || isbnFilter.isBlank() || isbnFilter == null) ?
                isbnFilter : ISBNChecker(isbnFilter);
        return LibraryDatabase.getFilteredBooks(isbnFilter, titleFilter, authorFilter);
    }

    /**
     * changes the name of a user
     * @param newName the new name of the user
     */
    public void changeName (String newName) {
        LibraryDatabase.updateUserName(userID, newName);
    }

    /**
     * changes the password of a user
     * @param newPassword the new password of the user
     */
    public void changePassword (String newPassword) {
        LibraryDatabase.updateUserPassword(userID, newPassword);
    }

    //BASE METHODS//////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        return name + "\n" + "{" +
                "userID=" + userID +
                "}";
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
