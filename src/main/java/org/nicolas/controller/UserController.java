package org.nicolas.controller;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.exceptions.InvalidUserException;
import org.nicolas.model.Librarian;
import org.nicolas.model.Student;
import org.nicolas.model.User;
import org.nicolas.view.UserView;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

public class UserController {
    private User model;
    private UserView view;
    private ResourceBundle messages;

    public UserController(User currentUser, UserView view, ResourceBundle bundle) {
        this.model = currentUser;
        this.view = view;
        this.messages = bundle;
    }

    public void handleLogin() {
        Scanner console = new Scanner(System.in);

        System.out.print(messages.getString("login.user_id"));
        int id = console.nextInt();
        console.nextLine();
        System.out.print(messages.getString("login.password"));
        String password = console.nextLine();

        User user = LibraryDatabase.findUserById(id);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful!");
            this.model = user; // Set the logged-in user
            showUserMenu(); // shows different menu based on type
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private void showUserMenu() {
        Scanner console = new Scanner(System.in);

        if (model instanceof Student) {
            // Student menu
            while (true) {
                System.out.println("\nStudent Menu:");
                System.out.println("1. Borrow Book");
                System.out.println("2. Return Book");
                System.out.println("3. Logout");

                int choice = console.nextInt();
                console.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Enter ISBN to borrow a book: ");
                        String isbn = console.nextLine();
                        model.borrowBook(isbn, model.getUserID()); // model (Student) handles borrowing
                        break;
                    case 2:
                        System.out.print("Enter ISBN to return: ");
                        String returnIsbn = console.nextLine();
                        // model.returnBook(returnIsbn); //implement this method
                        break;
                    case 3:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } else if (model instanceof Librarian) {
            // Librarian menu
            while (true) {
                System.out.println("\nLibrarian Menu:");
                System.out.println("1. Add Book");
                System.out.println("2. Remove Book");
                System.out.println("3. Logout");

                int choice = console.nextInt();
                console.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Enter ISBN: ");
                        String isbn = console.nextLine();
                        System.out.print("Enter Title: ");
                        String title = console.nextLine();
                        System.out.print("Enter Author: ");
                        String author = console.nextLine();
                        System.out.print("Enter number of copies: ");
                        int copies = console.nextInt();
                        console.nextLine();
                        LibraryDatabase.insertIntoBooks(isbn, title, author, copies);
                        break;
                    case 2:
                        System.out.print("Enter ISBN to remove: ");
                        String removeIsbn = console.nextLine();
                        LibraryDatabase.deleteBookByIsbn(removeIsbn);
                        break;
                    case 3:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } else {
            System.out.println("Unknown user type. Cannot continue.");
        }
    }


}
