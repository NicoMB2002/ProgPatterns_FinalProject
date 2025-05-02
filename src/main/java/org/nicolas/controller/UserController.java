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
        LOGIN,
        FIND_BOOK,
        STUDENT_MAIN,
            STUDENT_BORROW,
            STUDENT_RETURN,
        LIBRARIAN_MAIN,
            LIBRARIAN_ADD_BOOK,
            LIBRARIAN_REMOVE_BOOK,
            LIBRARIAN_BORROW,
            LIBRARIAN_RETURN,
            LIBRARIAN_ADD_USER,
            LIBRARIAN_REMOVE_USER,
            LIBRARIAN_BOOK_CATALOG,
            LIBRARIAN_USER_CATALOG,
        SETTINGS,
            SETTINGS_CHANGE_PASSWORD,
            SETTINGS_CHANGE_NAME,
        EXIT
    }

    private MenuState currentState = MenuState.LOGIN;

    public void runApplication() {
        Console console = System.console();

        while (currentState != MenuState.EXIT) {
            console.writer().print("\033[H\033[2J");
            console.flush();

            switch (currentState) {
                case LOGIN:
                    handleLogin(console);
                    break;
                case STUDENT_MAIN:
                    studentMenu((Student) model, console);
                    break;
                case STUDENT_BORROW:
                    studentBorrow(console);
                    break;
                case STUDENT_RETURN:
                    studentReturn(console);
                    break;
                case FIND_BOOK:
                    findBook(console);
                    break;
                case LIBRARIAN_MAIN:
                    librarianMenu((Librarian) model, console);
                    break;
                case LIBRARIAN_BORROW:
                    librarianBorrow((Librarian) model, console);
                    break;
                case LIBRARIAN_RETURN:
                    librarianReturn((Librarian) model, console);
                    break;
                case LIBRARIAN_ADD_BOOK:
                    librarianAddBook((Librarian) model, console);
                    break;
                case LIBRARIAN_REMOVE_BOOK:
                    librarianRemoveBook(console);
                    break;
                case LIBRARIAN_ADD_USER:
                    librarianAddUser(console);
                    break;
                case LIBRARIAN_REMOVE_USER:
                    librarianRemoveUser(console);
                    break;
                case LIBRARIAN_BOOK_CATALOG:
                    librarianBookCatalog(console);
                    break;
                case LIBRARIAN_USER_CATALOG:
                    librarianUserCatalog(console);
                    break;
                case SETTINGS:
                    settingsMenu(console);
                    break;
                case SETTINGS_CHANGE_NAME:
                    settingsChangeName(console);
                    break;
                case SETTINGS_CHANGE_PASSWORD:
                    settingsChangePassword(console);
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

    public void handleLogin(Console console) {
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
                currentState = MenuState.LIBRARIAN_MAIN;
            } else {
                currentState = MenuState.LIBRARIAN_MAIN;
            }
        } else {
            view.setErrorMessage(messages.getString("login.failure"));
        }
    }

//SETTINGS//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected void settingsMenu (Console console) {
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
                    currentState = MenuState.SETTINGS_CHANGE_NAME;
                    break;
                case "2" :
                    currentState = MenuState.SETTINGS_CHANGE_PASSWORD;
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

    private void settingsChangeName (Console console) {
        System.out.println(messages.getString("prompt.newName"));
        String newName = console.readLine();
        model.changeName(newName);
    }

    private void settingsChangePassword (Console console) {
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
                return;
            }
        } else {
            model.changePassword(stringPassword);
        }
    }

    //STUDENT///////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected void studentMenu (Student student, Console console) {
        appHeader();
        System.out.println(messages.getString("menu.title") + model.getName()
                + messages.getString("menu.title.exclamation") + "\n\n");
        System.out.println(messages.getString("menu.student.borrow") + "               "
                + messages.getString("menu.student.borrowedList"));
        System.out.println(messages.getString("menu.student.return") + "               "
                + messages.getString("menu.student.searchBook"));

        String ans = console.readLine().toUpperCase().charAt(0) + "";
        console.writer().print("\033[H\033[2J");
        console.flush();
        appHeader();
        switch (ans) {
            case "1" : //borrow a book
                currentState = MenuState.STUDENT_BORROW;
                break;
            case "2" : //return a book
                currentState = MenuState.STUDENT_RETURN;
                break;
            case "3" : //see borrowed books list
                student.seeBorrowedBooksList();
                break;
            case "4" : //search book
                currentState = MenuState.FIND_BOOK;
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

    private void studentBorrow (Console console) {
        System.out.println(messages.getString("prompt.isbn"));
        String inputIsbn = console.readLine();
        model.borrowBook(inputIsbn, model.getUserID());
    }

    private void studentReturn (Console console) {
        System.out.println(messages.getString("prompt.isbn"));
        String inputIsbn = console.readLine();
        model.returnBook(inputIsbn, model.getUserID());
    }

    private void findBook (Console console) {
        System.out.println(messages.getString("prompt.information"));
        System.out.println(messages.getString("prompt.isbn"));
        String inputIsbn = console.readLine();
        System.out.println(messages.getString("prompt.title"));
        String inputTitle = console.readLine();
        System.out.println(messages.getString("prompt.author"));
        String inputAuthor = console.readLine();

        model.findBook(inputIsbn, inputTitle, inputAuthor);
    }

    //LIBRARIAN/////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected void librarianMenu (Librarian librarian, Console console) {
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
        console.writer().print("\033[H\033[2J");
        console.flush();
        appHeader();
        switch (ans) {
            case "1" : //add a book
                currentState = MenuState.LIBRARIAN_ADD_BOOK;
                break;
            case "2" : //remove a book
                break;
            case "3" : //see book catalog
                currentState = MenuState.LIBRARIAN_BOOK_CATALOG;
                break;
            case "4" : //add a user
                currentState = MenuState.LIBRARIAN_ADD_USER;
                break;
            case "5" : //remove a user
                currentState = MenuState.LIBRARIAN_REMOVE_USER;
                break;
            case "6" : //see user catalog
                currentState = MenuState.LIBRARIAN_USER_CATALOG;
                break;
            case "7" : //borrow book for user
                currentState = MenuState.LIBRARIAN_BORROW;
                break;
            case "8" : //return book for user
                currentState = MenuState.LIBRARIAN_RETURN;
                break;
            case "9" : //find book
                currentState = MenuState.FIND_BOOK;
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

    private void librarianBorrow (Librarian librarian, Console console) {
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
    }

    private void librarianReturn (Librarian librarian, Console console) {
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
        librarian.returnBook(isbn, inputStudentId);
    }

    private void librarianAddBook (Librarian librarian, Console console) {
        System.out.print(messages.getString("prompt.isbn"));
        String isbn = console.readLine();
        for (char c : isbn.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage("ISBN cannot contain letters");
                currentState = MenuState.STUDENT_MAIN;
                break;
            }
        }

        System.out.print(messages.getString("prompt.title"));
        String title = console.readLine();
        System.out.print(messages.getString("prompt.author"));
        String author = console.readLine();
        System.out.print(messages.getString("prompt.title"));
        int copies = Integer.parseInt(console.readLine());

        librarian.addBook(isbn, title, author, copies);
    }

    private void librarianRemoveBook (Console console) {
        System.out.print(messages.getString("prompt.isbn"));
        String isbn = console.readLine();
        for (char c : isbn.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage("ISBN cannot contain letters");
                currentState = MenuState.STUDENT_MAIN;
                break;
            }
        }
        //TODO this and thay
    }

    private void librarianAddUser (Console console) {
        System.out.print(messages.getString("prompt.studentName"));
        String newStudentName = console.readLine();
        System.out.print(messages.getString("prompt.studentPassword"));
        String newStudentPassword = console.readLine();
        LibraryDatabase.insertIntoUser(newStudentName, "STUDENT", newStudentPassword);
        LibraryDatabase.selectLastInsertedStudent();
    }

    private void librarianRemoveUser (Console console) {
        System.out.print(messages.getString("prompt.isbn"));
        String isbn = console.readLine();
        for (char c : isbn.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage("ISBN cannot contain letters");
                currentState = MenuState.STUDENT_MAIN;
                break;
            }
        }
        //TODO this
    }

    private void librarianBookCatalog (Console console) {
        System.out.print(messages.getString("filter.main.prompt"));
        String ans = console.readLine().toUpperCase().charAt(0) + "";

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
                return;
            }
        }
        LibraryDatabase.selectAllBooks();
    }

    private void librarianUserCatalog (Console console) {
        System.out.print(messages.getString("filter.main.prompt"));
        String ans = console.readLine().toUpperCase().charAt(0) + "";

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
                return;
            }
        }
        LibraryDatabase.selectAllUsers();
    }

}
