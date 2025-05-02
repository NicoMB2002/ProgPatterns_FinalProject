package org.nicolas.controller;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.exceptions.InvalidISBNException;
import org.nicolas.exceptions.InvalidUserException;
import org.nicolas.model.Book;
import org.nicolas.model.Librarian;
import org.nicolas.model.Student;
import org.nicolas.model.User;
import org.nicolas.view.UserView;

import java.awt.*;
import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
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

    private enum MenuState { //State + singleton design pattern to avoid recursive calls
        LOGIN, STUDENT_MAIN, LIBRARIAN_MAIN, SETTINGS, EXIT
    }

    private MenuState currentState = MenuState.LOGIN;

    public void runApplication() {
        Console console = System.console();

        while (currentState != MenuState.EXIT) {
            console.writer().print("\033[H\033[2J");
            console.flush();

            switch (currentState) {
                case LOGIN:
                    handleLogin();
                    break;
                case STUDENT_MAIN:
                    studentMenu((Student) model);
                    break;
                case LIBRARIAN_MAIN:
                    librarianMenu((Librarian) model);
                    break;
                case SETTINGS:
                    settingsMenu();
                    break;
            }
        }
        handleLogout();
    }

    protected void appHeader () {
        System.out.println("--------------------------------------------------------------------------------------------");
        System.out.println("                                                                     "
                + messages.getString("logoutOption"));
        System.out.println("                                                                             "
                + messages.getString("menu.settings"));
    }

    protected void appFooter () {
        System.out.println("                                                                       "
                + messages.getString("menu.settings.returnToMain"));
        System.out.print("->  ");
    }

    public void handleLogout () {
        System.out.println(messages.getString("logout"));
        System.out.println(messages.getString("app.Exit"));
        System.out.println(messages.getString("goodbye"));
        System.exit(0);
    }

    public void handleLogin() {
        Console console = System.console();
        console.writer().print("\033[H\033[2J");
        console.flush(); //makes the console empty for better clarity

        System.out.println(messages.getString("welcome"));
        System.out.print(messages.getString("login.user_id"));
        int id = Integer.parseInt(console.readLine());
        String stringPassword = "";
        char[] password = console.readPassword(messages.getString("login.password"));
        for (int i = 0; i < password.length; i++) {
            stringPassword += password[i]; //puts the password into a String for the DB
            //System.out.print("*"); //sets the characters as '*' instead of blank spaces
        }

        User user = LibraryDatabase.findUserById(id);
        System.out.println(messages.getString("loading"));
        if (user != null && user.getPassword().equals(stringPassword)) {
            System.out.println(messages.getString("login.success"));
            this.model = user; // Set the logged-in user
            console.writer().print("\033[H\033[2J");
            console.flush();

            if (model instanceof Librarian) {
                Librarian librarianModel = (Librarian)model;
                librarianMenu(librarianModel);
            } else {
                Student studentModel = (Student)model;
                studentMenu(studentModel);
            }
        } else {
            view.setErrorMessage(messages.getString("login.failure"));
        }
    }

    protected void settingsMenu () {
        Console console = System.console();
        console.writer().print("\033[H\033[2J");
        console.flush();

        appHeader();
        System.out.println(messages.getString("menu.settings.title") + "\n");
        System.out.println(messages.getString("menu.settings.changeName"));
        System.out.println(messages.getString("menu.settings.changePassword"));
        appFooter();

        String ans = console.readLine().toUpperCase().charAt(0) + "";
        console.writer().print("\033[H\033[2J");
        console.flush();
        appHeader();

        while (true) {
            switch (ans) {
                case "1":
                    System.out.println(messages.getString("prompt.newName"));
                    String newName = console.readLine();
                    model.changeName(newName);
                    break;
                case "2" :
                    System.out.println(messages.getString("prompt.newPassword"));
                    char[] password = console.readPassword(messages.getString("login.password"));
                    String stringPassword = "";
                    for (int i = 0; i < password.length; i++) {
                        stringPassword += password[i]; //puts the password into a String for the DB
                        System.out.print("*"); //sets the characters as '*' instead of blank spaces
                    }

                    System.out.println(messages.getString("prompt.checkNewPassword"));
                    char[] passwordCheck = console.readPassword(messages.getString("login.password"));
                    String stringPasswordCheck = "";
                    for (int i = 0; i < passwordCheck.length; i++) {
                        stringPasswordCheck += passwordCheck[i]; //puts the password into a String for the DB
                        System.out.print("*"); //sets the characters as '*' instead of blank spaces
                    }

                    boolean isValid = (stringPassword.equals(passwordCheck)) ? true : false;

                    if (isValid == false) { //2nd chance to change password
                        System.out.println(messages.getString("prompt.checkNewPassword"));
                        char[] passwordCheck2 = console.readPassword(messages.getString("login.password"));
                        String stringPasswordCheck2 = "";
                        for (int i = 0; i < passwordCheck2.length; i++) {
                            stringPasswordCheck2 += passwordCheck2[i]; //puts the password into a String for the DB
                            System.out.print("*"); //sets the characters as '*' instead of blank spaces
                        }

                        isValid = (stringPassword.equals(stringPasswordCheck2)) ? true : false;

                        if (isValid) {
                            model.changePassword(stringPassword);
                        } else {
                            break;
                        }
                    } else {
                        model.changePassword(stringPassword);
                    }
                    break;
                case "M" :
                    currentState = (model instanceof Librarian) ? MenuState.LIBRARIAN_MAIN : MenuState.SETTINGS;
                    return;
                case "X" :
                    console.writer().print("\033[H\033[2J");
                    console.flush();
                    currentState = MenuState.EXIT;
                    break;
                default :
                    view.setErrorMessage("invalid choice, please try again");
                    currentState = MenuState.SETTINGS;
                    break;
            }
        }
    }

    protected void studentMenu (Student student) {
        Console console = System.console();
        appHeader();
        System.out.println(messages.getString("menu.title") + model.getName()
                + messages.getString("menu.title.exclamation") + "\n\n");
        System.out.println(messages.getString("menu.student.borrow") + "               "
                + messages.getString("menu.student.borrowedList"));
        System.out.println(messages.getString("menu.student.return") + "               "
                + messages.getString("menu.student.searchBook"));

        String ans = console.readLine().toUpperCase().charAt(0) + "";
        while (true) {
            console.writer().print("\033[H\033[2J");
            console.flush();
            appHeader();
            switch (ans) {
                case "1" : //borrow a book
                    System.out.println(messages.getString("prompt.isbn"));
                    String inputIsbn = console.readLine();
                    model.borrowBook(inputIsbn, model.getUserID());
                    break;
                case "2" : //return a book
                    System.out.println(messages.getString("prompt.isbn"));
                    inputIsbn = console.readLine();
                    model.returnBook(inputIsbn, model.getUserID());
                    break;
                case "3" : //see borrowed books list
                    student.seeBorrowedBooksList();
                    break;
                case "4" : //search book
                    System.out.println(messages.getString("prompt.information"));
                    System.out.println(messages.getString("prompt.isbn"));
                    inputIsbn = console.readLine();
                    System.out.println(messages.getString("prompt.title"));
                    String inputTitle = console.readLine();
                    System.out.println(messages.getString("prompt.author"));
                    String inputAuthor = console.readLine();

                    model.findBook(inputIsbn, inputTitle, inputAuthor);
                    break;
                case "S" : //settings
                    currentState = MenuState.SETTINGS;
                    break;
                case "X" : //exit
                    console.writer().print("\033[H\033[2J");
                    console.flush();
                    handleLogout();
                    break;
                default :
                    view.setErrorMessage("invalid choice, please try again");
                    currentState = MenuState.STUDENT_MAIN;
                    break;
            }
        }
    }

    protected void librarianMenu (Librarian librarian) {
        Console console = System.console();
        appHeader();
        System.out.println(messages.getString("menu.title") + model.getName()
                + messages.getString("menu.title.exclamation") + "\n");

        System.out.println(messages.getString("menu.librarian.add") + "                          "
                + messages.getString("menu.librarian.addUser") + "                         "
                + messages.getString("menu.librarian.seeBookCatalog"));

        System.out.println(messages.getString("menu.librarian.remove") + "                       "
                + messages.getString("menu.librarian.removeUser") + "                     "
                + messages.getString("menu.librarian.seeUserCatalog"));

        System.out.println(messages.getString("menu.librarian.borrowForUser") + "              "
                + messages.getString("menu.librarian.returnBookFroUser") + "             "
                + messages.getString("menu.librarian.searchBook"));
        System.out.print("\n->  ");

        String ans = console.readLine().toUpperCase().charAt(0) + "";
        while (true) {
            console.writer().print("\033[H\033[2J");
            console.flush();
            appHeader();
            switch (ans) {
                case "1" : //add a book

                    break;
                case "2" : //remove a book
                    break;
                case "3" : //see book catalog
                    System.out.print(messages.getString("filter.main.prompt"));
                    ans = console.readLine().toUpperCase().charAt(0) + "";

                    if (ans.equals("Y")) {
                        System.out.println(messages.getString("filter.availableBooks"));
                        System.out.println(messages.getString("filter.borrowedBooks"));
                        ans = console.readLine().toUpperCase().charAt(0) + "";

                        if (ans.equals("1")) {
                            LibraryDatabase.selectAllAvailableBooks();
                        } else if (ans.equals("2")) {
                            LibraryDatabase.selectAllBorrowedBooks();
                        } else {
                            view.setErrorMessage("invalid choice");
                            console.writer().print("\033[H\033[2J");
                            console.flush(); //makes the console empty for better clarity
                            currentState = MenuState.LIBRARIAN_MAIN;
                            break;
                        }
                    }
                    LibraryDatabase.selectAllBooks();
                    break;
                case "4" : //add a user
                    break;
                case "5" : //remove a user
                    break;
                case "6" : //see user catalog
                    System.out.print(messages.getString("filter.main.prompt"));
                    ans = console.readLine().toUpperCase().charAt(0) + "";

                    if (ans.equals("Y")) {
                        System.out.println(messages.getString("filter.students"));
                        System.out.println(messages.getString("filter.librarians"));
                        ans = console.readLine().toUpperCase().charAt(0) + "";

                        if (ans.equals("1")) {
                            LibraryDatabase.getUserListFromRole("STUDENT");
                        } else if (ans.equals("2")) {
                            LibraryDatabase.getUserListFromRole("LIBRARIAN");
                        } else {
                            view.setErrorMessage("invalid choice");
                            console.writer().print("\033[H\033[2J");
                            console.flush(); //makes the console empty for better clarity
                            currentState = MenuState.LIBRARIAN_MAIN;
                            break;
                        }
                    }
                    LibraryDatabase.selectAllUsers();
                    break;
                case "7" : //borrow book for user
                    System.out.print(messages.getString("prompt.student.id"));
                    String studentId = console.readLine();
                    for (char c : studentId.toCharArray()) {
                        if (Character.isLetter(c)) {
                            view.setErrorMessage("Student id cannot contain letters");
                            currentState = MenuState.STUDENT_MAIN;
                            break;
                        }
                    }

                    System.out.print(messages.getString("prompt.isbn"));
                    String isbn = console.readLine();
                    for (char c : isbn.toCharArray()) {
                        if (Character.isLetter(c)) {
                            view.setErrorMessage("ISBN cannot contain letters");
                            currentState = MenuState.STUDENT_MAIN;
                            break;
                        }
                    }

                    int inputStudentId = Integer.parseInt(studentId);
                    librarian.borrowBook(isbn, inputStudentId);
                    break;
                case "8" : //return book for user
                    break;
                case "9" : //find book
                    System.out.println(messages.getString("prompt.information"));
                    System.out.println(messages.getString("prompt.isbn"));
                    String inputIsbn = console.readLine();
                    System.out.println(messages.getString("prompt.title"));
                    String inputTitle = console.readLine();
                    System.out.println(messages.getString("prompt.author"));
                    String inputAuthor = console.readLine();

                    model.findBook(inputIsbn, inputTitle, inputAuthor);
                    break;
                case "S" : //settings
                    console.writer().print("\033[H\033[2J");
                    currentState = MenuState.SETTINGS;
                    break;
                case "X" : //exit
                    console.flush();
                    currentState = MenuState.EXIT;
                    break;
                default :
                    view.setErrorMessage("invalid choice, please try again");
                    currentState = MenuState.LIBRARIAN_MAIN;
                    break;
            }
        }
    }


}
