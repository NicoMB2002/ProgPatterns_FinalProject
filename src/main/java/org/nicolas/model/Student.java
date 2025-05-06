package org.nicolas.model;

import org.nicolas.database.LibraryDatabase;

import java.io.Console;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class Student extends User {
    private UserType userType;
    private ArrayList<Book> borrowedBooks;
    private ResourceBundle messages;

    //CONSTRUCTOR///////////////////////////////////////////////////////////////////////////////////////////////////////
    public Student(int userID, String name, String password) {
        super(userID, name, password);
        this.userType = UserType.STUDENT;
        this.borrowedBooks = new ArrayList<>();
    }

    //HELPER METHODS////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * prints the list of borrowed books, if the list is empty, it displays the appropriate message
     */
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

    /**
     * Check if the student already borrowed the book by its ID
     * @param borrowedBooks the borrowed books list of the student
     * @param isbn the isbn of the book to be borrowed
     * @return true if the book is already borrowed, false if not
     */
    private boolean isAlreadyBorrowed (ArrayList<Book> borrowedBooks, String isbn) {
        for (Book borrowedBook : borrowedBooks) {
            if (borrowedBook.getISBN().equals(isbn)) {
                System.out.println(messages.getString("book.owned"));
                return true;
            }
        }
        return false;
    }

    //INHERITED H-METHODS///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * borrows a book in the system and compiles the changes to the database
     * @param isbn the id of the book to borrow
     * @param userId the id of the user
     * @param console the system console
     */
    @Override
    public void borrowBook(String isbn, int userId, Console console) {
        // Check 'borrowedBooks' to see if the student has a maximum of 3 books
        if (borrowedBooks.size() >= 3) {
            System.out.println(messages.getString("book.limit"));
            return;
        }

        //Check if the student already borrowed the book by its ID
        if (isAlreadyBorrowed(borrowedBooks, isbn)) {
            return;
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

            //reflect change in the borrowing & DB
            LocalDate currentDate = LocalDate.now();
            bookToBorrow.setBorrowedCopies(bookToBorrow.getBorrowedCopies() + 1);
            bookToBorrow.setAvailableCopies(bookToBorrow.getCopies() - bookToBorrow.getBorrowedCopies());
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

    /**
     * returns a book in the database
     * @param isbn the id of the book to return
     * @param userId the id of the user whishing to return the book
     */
    @Override
    public void returnBook(String isbn, int userId) {
        Book bookToReturn = null;

        for (Book book : borrowedBooks) { // Check if the student has this book borrowed
            if (book.getISBN().equals(isbn)) {
                bookToReturn = book;
                break;
            }
        }

        if (bookToReturn == null) {
            System.out.println(messages.getString("book.not_borrowed"));
            return;
        }

        borrowedBooks.remove(bookToReturn); // Remove from the list

        // Update available/borrowed copies in the DB
        bookToReturn.setAvailableCopies(bookToReturn.getAvailableCopies() + 1);
        LibraryDatabase.updateBookCopies(bookToReturn);

        // Remove from borrowedBooks table in DB
        LibraryDatabase.deleteFromBorrowedBooks(getUserID(), isbn);

        System.out.println(messages.getString("book.return.success") + bookToReturn.getTitle());
    }

    //BASE METHODS//////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        return String.format("%d  %s  ROLE : %s\n", getUserID(), getName(), userType);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return userType == student.userType && Objects.equals(borrowedBooks, student.borrowedBooks) && Objects.equals(messages, student.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userType, borrowedBooks, messages);
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
