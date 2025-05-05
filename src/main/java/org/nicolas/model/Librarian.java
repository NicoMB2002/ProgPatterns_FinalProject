package org.nicolas.model;

import org.nicolas.database.LibraryDatabase;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.io.Console;

public class Librarian extends User {
    private UserType userType;

    //-------PROPOSING THIS CLASS AS THE ADMIN CLASS-------

    public Librarian(int userID, String name, String password) {
        super(userID, name, password);
        this.userType = UserType.LIBRARIAN;
    }

    public void addBook (String ISBN, String title, String author, int copies, Console console, ResourceBundle messages) {
        ArrayList<Book> booksFound = LibraryDatabase.getFilteredBooks(ISBN, title, author); //TODO check the return type and if it needs to be changed

        if (booksFound.size() == 0 || booksFound.isEmpty()) {
            LibraryDatabase.insertIntoBooks(ISBN, title, author, copies);
        } else {
            System.out.println(messages.getString("book.exists"));
            String ans = console.readLine().toUpperCase().charAt(0) + "";

            if (ans.equals("Y")) {
                System.out.println(messages.getString("prompt.selectFromList"));
                int existingBook = Integer.parseInt(console.readLine());
                Book tempbook = booksFound.get(existingBook);
                tempbook= LibraryDatabase.getBookThroughISBN(tempbook.getISBN());

                System.out.printf("%s   %s, %s, COPIES : %d [BORROWED : %d    AVAILABLE : %d]\n",
                        tempbook.getISBN(), tempbook.getTitle(), tempbook.getAuthor(), tempbook.getCopies(),
                        tempbook.getBorrowedCopies(), tempbook.getAvailableCopies());

                System.out.println(messages.getString("prompt.addCopies.question"));
                ans = console.readLine().toUpperCase().charAt(0) + "";

                if (ans.equals("Y")) {
                    System.out.println(messages.getString("prompt.copies"));
                    int copiesToAdd = Integer.parseInt(console.readLine());
                    addCopiesToBook(tempbook, copiesToAdd);
                    System.out.println(messages.getString("error.message.success"));
                    return;
                } else {
                    System.out.println(messages.getString("error.message.operationAborted"));
                    return;
                }

            } else if (ans.equals("N")) {
                System.out.println(messages.getString("prompt.createNewBook"));
                ans = console.readLine().toUpperCase().charAt(0) + "";

                if (ans.equals("Y")) {
                    LibraryDatabase.insertIntoBooks(ISBN, title, author, copies);
                    System.out.println(messages.getString("error.message.success"));
                    return;
                } else {
                    System.out.println(messages.getString("error.message.operationAborted"));
                    return;
                }
            } else {
                System.out.println(messages.getString("error.message.invalidChoice"));
                return;
            }
        }
    }

    public void addCopiesToBook (Book inputBook, int newCopiesNum) {
        int newTotalCopies = inputBook.getCopies() + newCopiesNum;
        inputBook.setCopies(newTotalCopies);
        int newAvCopies = newTotalCopies - inputBook.getBorrowedCopies();
        inputBook.setAvailableCopies(newAvCopies);
    }

    public void changeCopies (String ISBN, int newNumOfCopies) {
        LibraryDatabase.removeBookCopies(ISBN, newNumOfCopies);
    }

    public void removeBook (String ISBN) {
        LibraryDatabase.deleteBookByIsbn(ISBN);
//        Scanner console = new Scanner(System.in);
//        ArrayList<Book> booksFound = LibraryDatabase.getFilteredBooks(ISBN, title, author); //TODO check the return type and if it needs to be changed
//
//        if (booksFound.size() == 0 || booksFound.isEmpty()) {
//            System.out.println("No book to remove");
//        } else  if (booksFound.size() > 1) {
//            System.out.println("There seem to be multiple books matching your search. Select the one you wish to remove");
//
//            int existingBook = console.nextInt();
//            Book tempbook = booksFound.get(existingBook);
//            tempbook= LibraryDatabase.getBookThroughISBN(tempbook.getISBN());
//
//            System.out.printf("%s   %s, %s, COPIES : %d [BORROWED : %d    AVAILABLE : %d]",
//                    tempbook.getISBN(), tempbook.getTitle(), tempbook.getAuthor(), tempbook.getCopies(),
//                    tempbook.getBorrowedCopies(), tempbook.getAvailableCopies());
//
//            System.out.println("Do you wish to remove this book?");
//            System.out.println("Y/N");
//            ans = console.next().charAt(0);
//
//            if (ans == 'Y' || ans == 'y') {
//                System.out.println("Please enter the number of copies to add : ");
//                int copiesToAdd = console.nextInt();
//                addCopiesToBook(tempbook, copiesToAdd);
//            } else {
//                System.out.println("Nothing added.");
//            }
//
//        } else if (ans == 'N' || ans == 'n') {
//            System.out.println("Create a new book?");
//            System.out.println("Y/N");
//            ans = console.next().charAt(0);
//
//            if (ans == 'Y' || ans == 'y') {
//                LibraryDatabase.insertIntoBooks(ISBN, title, author, copies);
//            } else {
//                System.out.println("Nothing added.");
//            }
//        } else {
//            System.out.println("Invalid choice"); //TODO create exception
//        }
    }

    @Override
    public void borrowBook(String isbn, int studentId, Console console) {
        Student tempStudent = LibraryDatabase.getStudentFromId(studentId);
        if (tempStudent.equals(null)) {
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
                if (borrowedBook.getISBN() == isbn) {
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
                Date currentDate = new Date();
                currentDate.getCurrentDate();
                //reflect change in the borrowing ->>>> check DB if needed
                //bookToBorrow.setAvailableCopies(getAvailableCopies() -1);
                //bookToBorrow.setBorrowedCopies(getBorrowedCopies() + 1);

                //
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
            System.out.println("Book not available or no copies left. ");
        }
    }

    public void returnBook(String isbn, int userID) {
        LibraryDatabase.deleteFromBorrowedBooks(userID, isbn);
    }

    public void addUser (String newStudentName, String newStudentPassword) {
        LibraryDatabase.insertIntoUser(newStudentName, "STUDENT", newStudentPassword);
        LibraryDatabase.selectLastInsertedStudent();
    }

    public void removeUser (int userId) {
        LibraryDatabase.deleteStudentFromId(userId);
    }




}
