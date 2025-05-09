package org.nicolas.model;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.util.LocalizationManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.io.Console;

public class Librarian extends User {
    private UserType userType;
    private ResourceBundle messages;

    //CONSTRUCTOR///////////////////////////////////////////////////////////////////////////////////////////////////////
    public Librarian(int userID, String name, String password) {
        super(userID, name, password);
        this.userType = UserType.LIBRARIAN;
    }

    //HELPER METHODS////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * adds a book to the database
     * @param ISBN the id of the book to add
     * @param title the title of the book to add
     * @param author the author of the book to add
     * @param copies the number of copies of the new book
     * @param console the system console
     */
    public void addBook (String ISBN, String title, String author, int copies, Console console) {
        ArrayList<Book> booksFound = LibraryDatabase.getFilteredBooks(ISBN, title, author);

        if (booksFound.size() == 0 || booksFound.isEmpty()) {
            LibraryDatabase.insertIntoBooks(ISBN, title, author, copies);
            return;
        }

        System.out.println(LocalizationManager.getMessage("book.exists"));
        String ans = console.readLine().toUpperCase().charAt(0) + "";

        if (ans.equals("Y")) {
            System.out.println(LocalizationManager.getMessage("prompt.selectFromList"));
            int existingBook = Integer.parseInt(console.readLine());
            Book tempbook = booksFound.get(existingBook);
            tempbook= LibraryDatabase.getBookThroughISBN(tempbook.getISBN());

            System.out.printf("%s   %s, %s, COPIES : %d [BORROWED : %d    AVAILABLE : %d]\n",
                    tempbook.getISBN(), tempbook.getTitle(), tempbook.getAuthor(), tempbook.getCopies(),
                    tempbook.getBorrowedCopies(), tempbook.getAvailableCopies());

            System.out.println(LocalizationManager.getMessage("prompt.addCopies.question"));
            ans = console.readLine().toUpperCase().charAt(0) + "";

            if (ans.equals("Y")) {
                System.out.println(LocalizationManager.getMessage("prompt.copies"));
                int copiesToAdd = Integer.parseInt(console.readLine());
                addCopiesToBook(tempbook, copiesToAdd);
                LibraryDatabase.updateBookCopies(tempbook);
                System.out.println(LocalizationManager.getMessage("error.message.success"));
                return;
            } else {
                System.out.println(LocalizationManager.getMessage("error.message.operationAborted"));
                return;
            }

        } else if (ans.equals("N")) {
            System.out.println(LocalizationManager.getMessage("prompt.createNewBook"));
            ans = console.readLine().toUpperCase().charAt(0) + "";

            if (ans.equals("Y")) {
                LibraryDatabase.insertIntoBooks(ISBN, title, author, copies);
                System.out.println(LocalizationManager.getMessage("error.message.success"));
                return;
            } else {
                System.out.println(LocalizationManager.getMessage("error.message.operationAborted"));
                return;
            }
        } else {
            System.out.println(LocalizationManager.getMessage("error.message.invalidChoice"));
            return;
        }
    }

    /**
     * adds some new copies to a book and checks / changes the available copies count to match
     * @param inputBook the book to be modified
     * @param newCopiesNum the new number of copies
     */
    public void addCopiesToBook (Book inputBook, int newCopiesNum) {
        int newTotalCopies = inputBook.getCopies() + newCopiesNum;
        inputBook.setCopies(newTotalCopies);
        int newAvCopies = newTotalCopies - inputBook.getBorrowedCopies();
        inputBook.setAvailableCopies(newAvCopies);
    }

    /**
     * changes the number of copies of a book in the database
     * @param ISBN the book to update
     * @param newNumOfCopies the new number of copies
     */
    public void changeCopies (String ISBN, int newNumOfCopies) {
        LibraryDatabase.removeBookCopies(ISBN, newNumOfCopies);
    }

    /**
     * completly removes book from the database
     * @param ISBN the id of the book to remove
     */
    public void removeBook (String ISBN) {
        LibraryDatabase.deleteBookByIsbn(ISBN);
    }

    //INHERITED H-METHODS///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * borrows a book for a student
     * @param isbn the id of the book to borrow
     * @param studentId the id of the user for which the book if borrowed
     * @param console the system console
     */
    @Override
    public void borrowBook(String isbn, int studentId, Console console) {
        Student tempStudent = LibraryDatabase.getStudentFromId(studentId);
        if (tempStudent == null) {
            System.out.println("Student not found or UserType Librarian. " +
                    "\nNote : librarians are not allowed to borrow books");
            return; //breaking out of the method early
        } else {
            // Check 'borrowedBooks' to see if the student has a maximum of 3 books
            if (tempStudent.getBorrowedBooks().size() >= 3) {
                System.out.println("Student cannot borrow more than 3 books.");
                return;
            }

            //Check if the student already borrowed the book by its ID
            for (Book borrowedBook : tempStudent.getBorrowedBooks()) {
                if (borrowedBook.getISBN().equals(isbn)) {
                    System.out.println("\nStudent already borrowed this book.");
                    return;
                }
            }

            Book bookToBorrow = LibraryDatabase.getBookThroughISBN(isbn);
            if (bookToBorrow == null) {
                System.out.println("\nNo book matching the ISBN found in the system. Please check if the book exits first.");
                return;
            }

            //Borrow the book if it's available
            if (bookToBorrow.getISBN().equals(isbn) && bookToBorrow.getAvailableCopies() > 0) {
                //If the book is available then it gets added to the 'borrowedBooks' list
                tempStudent.getBorrowedBooks().add(bookToBorrow);

                // Reflect changes in the database
                LocalDate currentDate = LocalDate.now();
                //reflect change in the borrowing ->>>> check DB if needed
                bookToBorrow.setBorrowedCopies(bookToBorrow.getBorrowedCopies() + 1);
                bookToBorrow.setAvailableCopies(bookToBorrow.getAvailableCopies() - 1);
                //Update the borrowed books count

                LibraryDatabase.insertIntoBorrowedBooks(studentId, isbn, currentDate);
                LibraryDatabase.updateBookCopies(bookToBorrow);


                System.out.println();
                System.out.printf("%s  %s,  %s  COPIES : %d  [BORROWED : %d    AVAILABLE : %d]\n\n",
                        isbn, bookToBorrow.getTitle(), bookToBorrow.getAuthor(),
                        bookToBorrow.getCopies(), bookToBorrow.getBorrowedCopies(), bookToBorrow.getAvailableCopies());
                return;
            }
            System.out.println(LocalizationManager.getMessage("book.unavailable"));
        }
    }

    /**
     * returns a book for a user and reflect the changes to the database
     * @param isbn the id of the book to return
     * @param userID the id of the user whishing to return the book
     */
    public void returnBook(String isbn, int userID) {
        Student tempStudent = LibraryDatabase.getStudentFromId(userID);
        Book bookToReturn = null;

        for (Book book : tempStudent.getBorrowedBooks()) { // Check if the student has this book borrowed
            if (book.getISBN().equals(isbn)) {
                bookToReturn = book;
                break;
            }
        }

        if (bookToReturn == null) {
            System.out.println(LocalizationManager.getMessage("book.not_borrowed"));
            return;
        }

        tempStudent.getBorrowedBooks().remove(bookToReturn); // Remove from the list

        // Update available/borrowed copies in the DB
        bookToReturn.setBorrowedCopies(bookToReturn.getBorrowedCopies() - 1);
        bookToReturn.setAvailableCopies(bookToReturn.getAvailableCopies() + 1);
        LibraryDatabase.updateBookCopies(bookToReturn);

        // Remove from borrowedBooks table in DB
        LibraryDatabase.deleteFromBorrowedBooks(userID, isbn);

        System.out.println(LocalizationManager.getMessage("book.return.success") + bookToReturn.getTitle());
    }

    /**
     * adds a user to the database
     * @param newStudentName the student's new name
     * @param newStudentPassword the student's base password
     */
    public void addUser (String newStudentName, String newStudentPassword) {
        LibraryDatabase.insertIntoUser(newStudentName, "STUDENT", newStudentPassword);
        LibraryDatabase.selectLastInsertedStudent();
    }

    /**
     * completely removes a user from the database
     * @param userId the id of the user to remove
     */
    public void removeUser (int userId) {
        LibraryDatabase.deleteStudentFromId(userId);
    }

    //BASE METHODS//////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        return String.format("%d  %s  ROLE : %s\n", getUserID(), getName(), userType);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Librarian librarian = (Librarian) o;
        return userType == librarian.userType && Objects.equals(messages, librarian.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userType, messages);
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public ResourceBundle getMessages() {
        return messages;
    }
}
