package org.nicolas.model;

import org.nicolas.database.LibraryDatabase;

import java.io.Console;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Student extends User {
    private UserType userType;
    private ArrayList<Book> borrowedBooks;
    private ResourceBundle messages;

    public Student(int userID, String name, String password) {
        super(userID, name, password);
        this.userType = UserType.STUDENT;
        this.borrowedBooks = new ArrayList<>();
    }

    public void seeBorrowedBooksList () {
        ArrayList<String> borrowedBooksList = LibraryDatabase.getUserBorrowedBooks(getUserID());
        if (borrowedBooksList.isEmpty()) {
            System.out.println("List Empty");
        } else {
            int counter = 0;
            for (String bookInfo : borrowedBooksList) {
                System.out.println(counter + ". " + bookInfo);
            }

            while (counter < 2) {
                System.out.println(counter);
            }
        }
    }

    @Override
    public void borrowBook(String isbn, int userId, Console console) {
        // Check 'borrowedBooks' to see if the student has a maximum of 3 books
        if (borrowedBooks.size() >= 3) {
            System.out.println(messages.getString("book.limit"));
            return;
        }

        //Check if the student already borrowed the book by its ID
        for (Book borrowedBook : borrowedBooks) {
            if (borrowedBook.getISBN().equals(isbn)) {
                System.out.println(messages.getString("book.owned"));
                return;
            }
        }

        Book bookToBorrow = LibraryDatabase.getBookThroughISBN(isbn);
        if (bookToBorrow == null) {
            System.out.println(messages.getString("book.notFound"));
            return;
        }

        //Borrow the book if it's available
        if (bookToBorrow.getISBN().equals(isbn) && bookToBorrow.getAvailableCopies() > 0) {
            //If the book is available then it gets added to the 'borrowedBooks' list
            getBorrowedBooks().add(bookToBorrow);

            // Reflect changes in the database
            LocalDate currentDate = LocalDate.now();
            //reflect change in the borrowing ->>>> check DB if needed
            bookToBorrow.setBorrowedCopies(bookToBorrow.getBorrowedCopies() + 1);
            bookToBorrow.setAvailableCopies(bookToBorrow.getCopies() - bookToBorrow.getBorrowedCopies());
            //Update the borrowed books count

            LibraryDatabase.insertIntoBorrowedBooks(getUserID(), isbn, currentDate);
            LibraryDatabase.updateBookCopies(bookToBorrow);


            System.out.println();
            System.out.printf("%s  %s,  %s  COPIES : %d  [BORROWED : %d    AVAILABLE : %d]\n\n",
                    isbn, bookToBorrow.getTitle(), bookToBorrow.getAuthor(),
                    bookToBorrow.getCopies(), bookToBorrow.getBorrowedCopies(), bookToBorrow.getAvailableCopies());
            return;
        }
        System.out.println(messages.getString("book.unavailable"));
    }

    @Override
    public void returnBook(String isbn, int userId) {
        Book bookToReturn = null;

        // Check if the student has this book borrowed
        for (Book book : borrowedBooks) {
            if (book.getISBN().equals(isbn)) {
                bookToReturn = book;
                break;
            }
        }

        if (bookToReturn == null) {
            System.out.println(messages.getString("book.not_borrowed"));
            return;
        }

        // Remove from the list
        borrowedBooks.remove(bookToReturn);

        // Update available/borrowed copies in the DB
        bookToReturn.setAvailableCopies(bookToReturn.getAvailableCopies() + 1);
        LibraryDatabase.updateBookCopies(bookToReturn);

        // Remove from borrowedBooks table in DB
        LibraryDatabase.deleteFromBorrowedBooks(getUserID(), isbn);

        System.out.println(messages.getString("book.return.success") + bookToReturn.getTitle());

    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(ArrayList<Book> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }
}
