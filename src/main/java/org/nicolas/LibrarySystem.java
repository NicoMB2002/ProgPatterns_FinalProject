package org.nicolas;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.model.Book;
import org.nicolas.model.User;

import java.util.ArrayList;
import java.util.Scanner;

public class LibrarySystem {
    private User currentUser;

    public void addBook (String ISBN, String title, String author, int copies) {
        Scanner console = new Scanner(System.in);
        ArrayList<Book> booksFound = LibraryDatabase.getFilteredBooks(ISBN, title, author); //TODO check the return type and if it needs to be changed

        if (booksFound.size() == 0 || booksFound.isEmpty()) {
            LibraryDatabase.insertIntoBooks(ISBN, title, author, copies);
        } else {
            System.out.println("The book you want to add already seems to exist. Is it one of those books?");
            System.out.println("Y/N");
            char ans = console.next().charAt(0);

            if (ans == 'Y' || ans == 'y') {
                System.out.println("Please select the book from the list");
                int existingBook = console.nextInt();
                Book tempbook = booksFound.get(existingBook);
                tempbook= LibraryDatabase.getBookThroughISBN(tempbook.getISBN());

                System.out.printf("%s   %s, %s, COPIES : %d [BORROWED : %d    AVAILABLE : %d]",
                        tempbook.getISBN(), tempbook.getTitle(), tempbook.getAuthor(), tempbook.getCopies(),
                        tempbook.getBorrowedCopies(), tempbook.getAvailableCopies());

                System.out.println("Dou you wish to add copies to this book?");
                System.out.println("Y/N");
                ans = console.next().charAt(0);

                if (ans == 'Y' || ans == 'y') {
                    System.out.println("Please enter the number of copies to add : ");
                    int copiesToAdd = console.nextInt();
                    addCopiesToBook(tempbook, copiesToAdd);
                } else {
                    System.out.println("Nothing added.");
                }

            } else if (ans == 'N' || ans == 'n') {
                System.out.println("Create a new book?");
                System.out.println("Y/N");
                ans = console.next().charAt(0);

                if (ans == 'Y' || ans == 'y') {
                    LibraryDatabase.insertIntoBooks(ISBN, title, author, copies);
                } else {
                    System.out.println("Nothing added.");
                }
            } else {
                System.out.println("Invalid choice"); //TODO create exception
            }
        }

    }

    public void addCopiesToBook (Book inputBook, int newCopiesNum) {
        int newTotalCopies = inputBook.getCopies() + newCopiesNum;
        inputBook.setCopies(newTotalCopies);
        int newAvCopies = newTotalCopies - inputBook.getBorrowedCopies();
        inputBook.setAvailableCopies(newAvCopies);
    }

    public void removeBook (String ISBN) {
        Scanner console = new Scanner(System.in);
        System.out.print("Please enter the new total number of copies : ");
        int newNumOfCopies = console.nextInt();
        LibraryDatabase.removeBookCopies(ISBN, newNumOfCopies);
    }

    public void displayCatalog () {
        LibraryDatabase.selectAllBooks();
    }

    public void borrowBook (String ISBN) {}

    public void returnBook (String ISBN) {}

}
