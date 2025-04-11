package org.nicolas;

import java.util.ArrayList;

public class LibrarySystem {
    private User currentUser;

    public void addBook (String ISBN, String title, String author, int copies) {}

    public void removeBook (String ISBN) {}

    public void displayCatalog () {}

    public void borrowBook (String ISBN) {}

    public void returnBook (String ISBN) {}

    public ArrayList<Book> searchBook (String ISBNFilter, String titleFilter, String authorFilter) {
        ArrayList<Book> booksFound = new ArrayList<>();
        return booksFound;
    }
}
