package org.nicolas.model;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.model.User;

import java.util.ArrayList;
import java.util.Scanner;

public class Student extends User {

    private int userId;
    private String name;
    private UserType typeOfUSer;
    private ArrayList<Book> borrowedBooks;

    public Student(int userID, String name, String password) {
        super(userID, name, password);
        this.borrowedBooks = new ArrayList<>();
    }

    public void borrowBook(String isbn) {
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

        ArrayList<Book> books = LibraryDatabase.returnListOfBooks(); //returnListOfBooks() returns Arraylist

        //Borrow the book if it's available
        for (Book book : books) {
            if (book.getISBN().equals(isbn) && book.getAvailableCopies() > 0) {
                borrowedBooks.add(book); //If the book is available then it gets added to the 'borrowedBooks' list

                // Reflect changes in the database
                LibraryDatabase.updateBookCopies(book);

                //Update the borrowed books count
                book.setBorrowedCopies(book.getBorrowedCopies() + 1);

                System.out.println("\nBook borrowed: " + book.getTitle());
                System.out.println("Info:\nISBN: " + book.getISBN() + ", Title: " + book.getTitle() + ", " +
                        "Author: " + book.getAuthor() + " Copies: " + book.getAvailableCopies() + "\n");
                return;
            }
        }
        System.out.println("Book not available or no copies left.");
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
