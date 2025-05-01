package org.nicolas.controller;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.exceptions.InvalidISBNException;
import org.nicolas.exceptions.InvalidUserException;
import org.nicolas.model.Librarian;
import org.nicolas.model.Student;
import org.nicolas.model.User;
import org.nicolas.view.UserView;

import java.awt.*;
import java.io.Console;
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

    public void setErrorMessage (String message) {
        System.out.println("[ERROR : " + message + "]");
    }

    public void mainMenu () {
        Console console = System.console();
        console.flush(); //ensures the console is empty

        //System.out.println("                                    " + messages.getString(""logoutOption"));
        System.out.println(messages.getString("welcome") + "\n\n");
        System.out.println(messages.getString("menu.main.login"));
        System.out.println(messages.getString("menu.main.exit"));

        String ans = console.readLine().toUpperCase().charAt(0) + "";
        int tryCounter = 0;
        switch (ans) {
            case "1" :
                handleLogin();
                break;
            case "2" :
                System.out.println(messages.getString("logout"));
                System.exit(0);
                break;
            case "X" :
                System.out.println(messages.getString("logout"));
                System.exit(0);
                break;
            default:
                setErrorMessage("Invalid choice, please try again");
                if (tryCounter <= 3) {
                    mainMenu();
                } else {
                    System.exit(1); //Exception Termination
                }
        }

    }

    public void handleLogin() {
        Scanner console = new Scanner(System.in);

        System.out.println(messages.getString("welcome") + "                     "
                + messages.getString("logoutOption"));
        System.out.print(messages.getString("login.user_id"));
        int id = console.nextInt();
        console.nextLine();
        System.out.print(messages.getString("login.password"));
        String password = console.nextLine();

        User user = LibraryDatabase.findUserById(id);
        System.out.println(messages.getString("loading"));
        if (user != null && user.getPassword().equals(password)) {
            System.out.println(messages.getString("login.success"));
            this.model = user; // Set the logged-in user
            System.out.println(messages.getString("welcome") + LibraryDatabase.findUserById(id));

            showUserMenu(); // shows different menu based on type
        } else {
            System.out.println(messages.getString("login.failure"));
        }
    }

    private void showUserMenu() {
        Scanner console = new Scanner(System.in);

        if (model instanceof Student) {
            // Student menu
            while (true) {
                System.out.println(messages.getString("menu.student.title"));
                System.out.println(messages.getString("menu.student.borrow"));
                System.out.println(messages.getString("menu.student.return"));
                System.out.println(messages.getString("menu.student.logout"));

                int choice = console.nextInt();
                console.nextLine();

                switch (choice) {
                    case 1:
                        System.out.println(messages.getString("prompt.isbn.borrow"));

                        String isbn = console.nextLine();
                        try {
                            model.borrowBook(isbn, model.getUserID()); // model (Student) handles borrowing
                        } catch (InvalidISBNException e) {
                            System.out.println("Invalid ISBN format: " + e.getMessage());
                            continue;
                        }
                        break;
                    case 2:
                        System.out.println(messages.getString("prompt.isbn.return"));

                        String returnIsbn = console.nextLine();
                         model.returnBook(returnIsbn);
                        break;
                    case 3:
                        System.out.println(messages.getString("logout"));
                        return;
                    default:
                        System.out.println(messages.getString("invalid.choice"));

                }
            }
        } else if (model instanceof Librarian) {
            // Librarian menu
            while (true) {
                System.out.println("\n" + messages.getString("menu.librarian.title"));

                System.out.println("\n" + messages.getString("menu.librarian.add"));

                System.out.println(messages.getString("menu.librarian.remove"));

                System.out.println(messages.getString("menu.librarian.logout"));


                int choice = console.nextInt();
                console.nextLine();

                switch (choice) {
                    case 1:
                        System.out.println("\n" + messages.getString("prompt.isbn.add"));

                        String isbn = console.nextLine();

                        System.out.println(messages.getString("prompt.title.add"));

                        String title = console.nextLine();

                        System.out.println(messages.getString("prompt.author.add"));

                        String author = console.nextLine();

                        System.out.println(messages.getString("prompt.copies.add"));

                        int copies = console.nextInt();
                        console.nextLine();
                        LibraryDatabase.insertIntoBooks(isbn, title, author, copies);
                        break;
                    case 2:
                        System.out.println(messages.getString("prompt.isbn.remove"));

                        String removeIsbn = console.nextLine();
                        LibraryDatabase.deleteBookByIsbn(removeIsbn);
                        break;
                    case 3:
                        System.out.println(messages.getString("menu.librarian.logout"));
                        return;
                    default:
                        System.out.println(messages.getString("invalid.choice"));
                }
            }
        } else {
            System.out.println("Unknown user type. Cannot continue.");
        }
    }


}
