package org.nicolas;

public class Book {
    private final String ISBN;
    private String title;
    private String author;
    private int copies;
    private int availableCopies;
    private int borrowedCopies;

    public Book(String ISBN, String title, String author, int copies, int availableCopies, int borrowedCopies) {
        try {
            this.ISBN = ISBNChecker(ISBN);
        } catch (InvalidISBNException e) {
            System.out.println(e.getMessage());
        }
        this.title = title;
        this.author = author;
        this.copies = copies;
        this.availableCopies = availableCopies;
        this.borrowedCopies = borrowedCopies;
    }

    private String ISBNChecker(String inputISBN) throws InvalidISBNException {
        if (inputISBN.length() != 13 || inputISBN.length() != 17) {
            throw new InvalidISBNException("ISBN must be 13 characters long. Or 17 characters long counting '-'.");
        }

        for (int i = 0; i < inputISBN.length(); i++) {
            if (Character.isLetter(inputISBN.charAt(i))) {
                throw new InvalidISBNException("ISBN should not contain letters.");
            }
        }

        return inputISBN;
    }

    public void addAvailableCopies (int newCopies) {}

    public void borrowCopy () {}

    public boolean checkTotalNumOfCopies () {
        boolean isOk;
        return isOk = (borrowedCopies + availableCopies == copies) ? true : false;
    }

    public int checkAvailableCopies () {
        int numOfAvCopies;

        return 0;
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
