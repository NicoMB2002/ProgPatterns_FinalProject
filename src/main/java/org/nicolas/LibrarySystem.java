package org.nicolas;

import java.util.ArrayList;
import java.util.Scanner;

public class LibrarySystem {
    private User currentUser;

    public void addBook (String ISBN, String title, String author, int copies) {
        Scanner console = new Scanner(System.in);

        ArrayList<Book> booksFound = searchBook(ISBN, title, author);

        if (booksFound.size() == 0 || booksFound.isEmpty()) {
            Book newBook = new Book(ISBN, title, author, copies, copies, 0);
        } else {
            System.out.println("The book you want to add already seems to exist. Is it one of those books?");
            int counter = 0;
            for (Book books : booksFound) {
                System.out.println(counter + ".  " + booksFound);
            }

            System.out.println("Y/N");
            char ans = console.next().charAt(0);

            if (ans == 'Y' || ans == 'y') {
                System.out.println("Please select the book from the list");
                int existingBook = console.nextInt();
                Book tempbook = booksFound.get(existingBook);


            } else if (ans == 'N' || ans == 'n') {

            } else {
                System.out.println("Invalid choice"); //TODO create exception
            }

        }

    }

    public void removeBook (String ISBN) {}

    public void displayCatalog () {}

    public void borrowBook (String ISBN) {}

    public void returnBook (String ISBN) {}

    public ArrayList<Book> searchBook (String ISBNFilter, String titleFilter, String authorFilter) {
        ArrayList<Book> booksFound = new ArrayList<>();
        LibraryDatabase.findBook(ISBNFilter, titleFilter, authorFilter); //TODO check the return type and if it needs to be changed
        return booksFound;
    }
}
