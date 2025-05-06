package org.nicolas.model;

import org.nicolas.exceptions.InvalidISBNException;

import java.util.Objects;

public class Book {
    private String ISBN;
    private String title;
    private String author;
    private int copies;
    private int availableCopies;
    private int borrowedCopies;

    //CONSTRUCTOR///////////////////////////////////////////////////////////////////////////////////////////////////////
    public Book(String ISBN, String title, String author, int copies, int availableCopies, int borrowedCopies) {
        this.ISBN = ISBNChecker(ISBN);
        this.title = title;
        this.author = author;
        this.copies = copies;
        this.availableCopies = availableCopies;
        this.borrowedCopies = borrowedCopies;
    }

    //HELPER METHODS////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * checks if an ISBN is in a valid format
     * @param inputISBN the input isbn
     * @return the isbn if it is valid
     * @throws InvalidISBNException the exception thrown when the ISBN is not valid
     */
    protected String ISBNChecker(String inputISBN) throws InvalidISBNException {
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

    //BASE METHODS//////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        return String.format("%s  %s,  %s  COPIES: %d  [BORROWED: %d  AVAILABLE: %d]%n",
                ISBN, title, author, copies, borrowedCopies, availableCopies);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return copies == book.copies && availableCopies == book.availableCopies && borrowedCopies == book.borrowedCopies && Objects.equals(ISBN, book.ISBN) && Objects.equals(title, book.title) && Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ISBN, title, author, copies, availableCopies, borrowedCopies);
    }

    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getCopies() {
        return copies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getBorrowedCopies() {
        return borrowedCopies;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public void setBorrowedCopies(int borrowedCopies) {
        this.borrowedCopies = borrowedCopies;
    }
}
