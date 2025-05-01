package org.nicolas.model;

import org.nicolas.database.LibraryDatabase;

import java.util.Scanner;

public class Librarian extends User {
    private UserType userType;

    //-------PROPOSING THIS CLASS AS THE ADMIN CLASS-------

    public Librarian(int userID, String name, String password) {
        super(userID, name, password);
        this.userType = UserType.LIBRARIAN;
    }

    @Override
    public void borrowBook(String isbn, int studentId) {
        Student tempStudent = LibraryDatabase.getStudentFromId(studentId);
        if (tempStudent.equals(null)) {
            System.out.println("Student not found or UserType Librarian. " +
                    "\nNote : librarians are not allowed to borrow books");
        } else {
            Scanner console = new Scanner(System.in);
            // Check 'borrowedBooks' to see if the student has a maximum of 3 books
            if (tempStudent.getBorrowedBooks().size() >= 3) {
                System.out.println("Student cannot borrow more than 3 books.");
                return;
            }

            System.out.println("Enter the book ISBN to borrow a book: ");
            int ISBN = console.nextInt();

            //Check if the student already borrowed the book by its ID
            for (Book borrowedBook : tempStudent.getBorrowedBooks()) {
                if (borrowedBook.getISBN() == isbn) {
                    System.out.println("\nStudent already borrowed this book.");
                    return;
                }
            }
            Book bookToBorrow = LibraryDatabase.getBookThroughISBN(isbn);


            //Borrow the book if it's available
            if (bookToBorrow.getISBN().equals(isbn) && bookToBorrow.getAvailableCopies() > 0) {
                //If the book is available then it gets added to the 'borrowedBooks' list
                tempStudent.getBorrowedBooks().add(bookToBorrow);

                // Reflect changes in the database
                Date currentDate = new Date();
                currentDate.getCurrentDate();

                LibraryDatabase.insertIntoBorrowedBooks(studentId, isbn, currentDate);
                LibraryDatabase.updateBookCopies(bookToBorrow);

                //Update the borrowed books count
                bookToBorrow.setBorrowedCopies(bookToBorrow.getBorrowedCopies() + 1);

                System.out.println("\nBook borrowed: " + bookToBorrow.getTitle());
                System.out.printf("%s  %s,  %s  COPIES : %d  [BORROWED : %d    AVAILABLE : %d]",
                        isbn, bookToBorrow.getTitle(), bookToBorrow.getAuthor(),
                        bookToBorrow.getCopies(), bookToBorrow.getBorrowedCopies(), bookToBorrow.getAvailableCopies());
                return;
            }
            System.out.println("Book not available or no copies left.");
        }
    }

    public void returnBook(String isbn, int userID) {

    }
}
