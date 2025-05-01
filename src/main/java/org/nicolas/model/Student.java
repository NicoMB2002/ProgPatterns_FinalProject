package org.nicolas.model;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Student extends User {

    private int userId;
    private String name;
    private UserType typeOfUSer;
    private ArrayList<Book> borrowedBooks;

    public Student(int userID, String name, String password) {
        super(userID, name, password);
        this.typeOfUSer = UserType.STUDENT;
        this.borrowedBooks = new ArrayList<>();
    }

    public void seeBorrowedBooksList () {
        ArrayList<String> borrowedBooksList = LibraryDatabase.getUserBorrowedBooks(userId);
        borrowedBooksList.toString(); //see if it works
    }

    @Override
    public void borrowBook(String isbn, int userId) {
        Scanner console = new Scanner(System.in);

        // Check 'borrowedBooks' to see if the student has a maximum of 3 books
        if (borrowedBooks.size() >= 3) {
            System.out.println("You cannot borrow more than 3 books.");
            return;
        }

        System.out.println("Enter the book ISBN to borrow a book: ");
        int ISBN = console.nextInt();

        //Check if the student already borrowed the book by its ID
        for (Book borrowedBook : borrowedBooks) {
            if (borrowedBook.getISBN() == isbn) {
                System.out.println("\nYou already borrowed this book.");
                return;
            }
        }
        Book bookToBorrow = LibraryDatabase.getBookThroughISBN(isbn);

        //Borrow the book if it's available
        if (bookToBorrow.getISBN().equals(isbn) && bookToBorrow.getAvailableCopies() > 0) {
            //If the book is available then it gets added to the 'borrowedBooks' list
            borrowedBooks.add(bookToBorrow);

            // Reflect changes in the database
            Date currentDate = new Date();
            currentDate.getCurrentDate();

            LibraryDatabase.insertIntoBorrowedBooks(userId, isbn, currentDate);
            LibraryDatabase.updateBookCopies(bookToBorrow);

            //Update the borrowed books count
            bookToBorrow.setBorrowedCopies(bookToBorrow.getBorrowedCopies() + 1);

            System.out.println("\nBook borrowed: " + bookToBorrow.getTitle());
            System.out.printf("%s  %s,  %s  COPIES : %d  [BORROWED : %d    AVAILABLE : %d]",
                    isbn, bookToBorrow.getTitle(), bookToBorrow.getAuthor(), bookToBorrow.getCopies(),
                    bookToBorrow.getBorrowedCopies(), bookToBorrow.getAvailableCopies());
            return;
        }
        System.out.println("Book not available or no copies left.");
    }

    public void returnBook(String isbn) {
        Book bookToReturn = null;

        // Check if the student has this book borrowed
        for (Book book : borrowedBooks) {
            if (book.getISBN().equals(isbn)) {
                bookToReturn = book;
                break;
            }
        }

        if (bookToReturn == null) {
            System.out.println("You haven't borrowed this book.");
            return;
        }

        // Remove from the list
        borrowedBooks.remove(bookToReturn);

        // Update available/borrowed copies in the DB
        bookToReturn.setAvailableCopies(bookToReturn.getAvailableCopies() + 1);
        LibraryDatabase.updateBookCopies(bookToReturn);

        // Remove from borrowedBooks table in DB
        LibraryDatabase.deleteFromBorrowedBooks(this.getUserId(), isbn);

        System.out.println("Book returned successfully: " + bookToReturn.getTitle());
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public UserType getTypeOfUSer() {
        return typeOfUSer;
    }

    public void setTypeOfUSer(UserType typeOfUSer) {
        this.typeOfUSer = typeOfUSer;
    }

    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(ArrayList<Book> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }
}
