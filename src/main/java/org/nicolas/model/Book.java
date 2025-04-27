package org.nicolas.model;

import org.nicolas.exceptions.InvalidISBNException;

public class Book {
    private String ISBN;
    private String title;
    private String author;
    private int copies;
    private int availableCopies;
    private int borrowedCopies;

    public Book(String ISBN, String title, String author, int copies, int availableCopies, int borrowedCopies) {

        this.ISBN = ISBNChecker(ISBN);
        this.title = title;
        this.author = author;
        this.copies = copies;
        this.availableCopies = availableCopies;
        this.borrowedCopies = borrowedCopies;
    }

    private String ISBNChecker(String inputISBN) throws InvalidISBNException { //TODO change the checking for '-' and remove them if there is
        inputISBN = inputISBN.replace("-", ""); // remove dashes if any
        if (inputISBN.length() != 13) {
            throw new InvalidISBNException("ISBN must be exactly 13 characters long without dashes.");
        }
        for (char c : inputISBN.toCharArray()) {
            if (Character.isLetter(c)) {
                throw new InvalidISBNException("ISBN should not contain letters.");
            }
        }
        return inputISBN;
    }

    public void addAvailableCopies (int newCopies) {}

    public void borrowCopy () {}

    public boolean checkTotalNumOfCopies () {
        return borrowedCopies + availableCopies == copies;
    }

    @Override
    public String toString() {
        return "Book{" +
                "ISBN='" + ISBN + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", copies=" + copies +
                ", availableCopies=" + availableCopies +
                ", borrowedCopies=" + borrowedCopies +
                '}';
    }

    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public int getBorrowedCopies() {
        return borrowedCopies;
    }

    public void setBorrowedCopies(int borrowedCopies) {
        this.borrowedCopies = borrowedCopies;
    }
}
