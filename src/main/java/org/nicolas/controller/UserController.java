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

    public User getLoggedInUser() {
        return model;
    }

    private enum MenuState { //State + singleton design pattern to avoid recursive calls
        /*enum to prevent more than one state of the menu : instead of continuously calling the methods within themsleves : just changing the state of the menu*/
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

    private MenuState currentState = MenuState.LOGIN; //base menu-state : login page

    public void runApplication() {
        Console console = System.console(); //creates the console for the entire system
        if (console == null) {
            setErrorMessage("No console available");
            System.exit(1);
        }

        while (currentState != MenuState.EXIT) { //checks the menu state, if is is exit, exits the application : this prevents recursive or infinite calls
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
                    librarianRemoveBook((Librarian) model, console);
                    break;
                case LIBRARIAN_ADD_USER:
                    librarianAddUser((Librarian) model, console);
                    break;
                case LIBRARIAN_REMOVE_USER:
                    librarianRemoveUser((Librarian) model, console);
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
        handleLogout(); //exit sequence
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
                return;
            } else {
                currentState = MenuState.STUDENT_MAIN;
                return;
            }
        } else {
            view.setErrorMessage(messages.getString("login.failure"));
        }
    }

//SETTINGS//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected void settingsMenu (Console console) { //base call for the settings menu
        console.writer().print("\033[H\033[2J");
        console.flush();

        //settings menu display
        appHeader();
        System.out.println(messages.getString("menu.settings.title") + "\n");
        System.out.println(messages.getString("menu.settings.changeName"));
        System.out.println(messages.getString("menu.settings.changePassword"));
        appFooter();

        String ans = console.readLine().toUpperCase().charAt(0) + "";
        console.writer().print("\033[H\033[2J");
        console.flush();
        appHeader();

        //settings menu back-end code for next option
        while (true) {
            switch (ans) {
                case "1":
                    //changin the name
                    currentState = MenuState.SETTINGS_CHANGE_NAME;
                    break;
                case "2" :
                    //changing the password 
                    currentState = MenuState.SETTINGS_CHANGE_PASSWORD;
                    break;
                case "M" :
                    //going back to main menu, if the user is a librarian = librarian Main, else, student Main (student main always the default to prevent admin powers to random user
                    currentState = (model instanceof Librarian) ? MenuState.LIBRARIAN_MAIN : MenuState.SETTINGS;
                    return;
                case "X" :
                    //exit application completly
                    console.writer().print("\033[H\033[2J");
                    console.flush();
                    currentState = MenuState.EXIT;
                    break;
                default :
                    //goes back to settings base menu (this menu) again until there is a valid choice , no counter allowed 
                    view.setErrorMessage("invalid choice, please try again");
                    currentState = MenuState.SETTINGS;
                    break;
            }
        }
    }

    private void settingsChangeName (Console console) { //menu state to change the name
        System.out.println(messages.getString("prompt.newName"));
        String newName = console.readLine();
        model.changeName(newName); //calling the model that will call the DB : enforcing MVC desing
    }

    private void settingsChangePassword (Console console) {
        System.out.println(messages.getString("prompt.newPassword")); //first request of the new password
        char[] password = console.readPassword(messages.getString("login.password"));
        String stringPassword = "";
        for (int i = 0; i < password.length; i++) {
            stringPassword += password[i]; //puts the password into a String for the DB
            //System.out.print("*"); //sets the characters as '*' instead of blank spaces
        }

        System.out.println(messages.getString("prompt.checkNewPassword")); //validation of the new password (asks to input it again)
        char[] passwordCheck = console.readPassword(messages.getString("login.password"));
        String stringPasswordCheck = "";
        for (int i = 0; i < passwordCheck.length; i++) {
            stringPasswordCheck += passwordCheck[i]; //puts the password into a String for the DB
            //System.out.print("*"); //sets the characters as '*' instead of blank spaces
        }

        boolean isValid = (stringPassword.equals(passwordCheck)) ? true : false; //checks if the first password and the validation are the same

        if (isValid == false) { //2nd chance to change password
            System.out.println(messages.getString("prompt.checkNewPassword"));
            char[] passwordCheck2 = console.readPassword(messages.getString("login.password"));
            String stringPasswordCheck2 = "";
            for (int i = 0; i < passwordCheck2.length; i++) {
                stringPasswordCheck2 += passwordCheck2[i]; //puts the password into a String for the DB
                //System.out.print("*"); //sets the characters as '*' instead of blank spaces
            }

            isValid = (stringPassword.equals(stringPasswordCheck2)) ? true : false; //checks if first password and 2nd validation attempt are the same

            if (isValid) {
                model.changePassword(stringPassword); //call the change password method of user to enfore MVC model
            } else {
                System.out.println(messages.getString("error.message.operationAborted")); //operation aborted because too many attemps
                currentState = MenuState.SETTINGS; //both attempts failed return to main settings menu
                return; 
            }
        } else {
            model.changePassword(stringPassword); //first attempt was ok, therefore changed password right away by calling model to enforce MVC design pattern
        }
    }

    //STUDENT///////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected void studentMenu (Student student, Console console) { //base studend app
        //front end : menu display
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
        appHeader(); //back end menu display

        //back end
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
                view.setErrorMessage(messages.getString("error.message.invalidChoice")
                        + messages.getString("error.message.tryAgain"));
                currentState = MenuState.STUDENT_MAIN;
                break;
        }
    }

    private void studentBorrow(Console console) {
        System.out.println(messages.getString("prompt.isbn"));
        String inputIsbn = console.readLine();
        model.borrowBook(inputIsbn, model.getUserID());

        //Go back to main menu after borrowing
        currentState = MenuState.STUDENT_MAIN;
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
                view.setErrorMessage(messages.getString("error.message.invalidChoice")
                        + messages.getString("error.message.tryAgain"));
                currentState = MenuState.LIBRARIAN_MAIN;
                break;
        }
    }

    private void librarianBorrow (Librarian librarian, Console console) {
        System.out.print(messages.getString("prompt.student.id"));
        String studentId = console.readLine();
        for (char c : studentId.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongStudID"));
                currentState = MenuState.STUDENT_MAIN;
                break;
            }
        }

        System.out.print(messages.getString("prompt.isbn"));
        String isbn = console.readLine();
        for (char c : isbn.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongISBN"));
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
                view.setErrorMessage(messages.getString("error.message.wrongStudID"));
                currentState = MenuState.STUDENT_MAIN;
                break;
            }
        }

        System.out.print(messages.getString("prompt.isbn"));
        String isbn = console.readLine();
        for (char c : isbn.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongISBN"));
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
                view.setErrorMessage(messages.getString("error.message.wrongISBN"));
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }

        System.out.print(messages.getString("prompt.title"));
        String title = console.readLine();
        System.out.print(messages.getString("prompt.author"));
        String author = console.readLine();
        System.out.print(messages.getString("prompt.copies"));
        String tryAns = console.readLine();

        for (char c : tryAns.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongNumCopies"));
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }

        int copies = Integer.parseInt(tryAns);
        librarian.addBook(isbn, title, author, copies);
    }

    private void librarianRemoveBook (Librarian librarian, Console console) {
        System.out.print(messages.getString("prompt.isbn"));
        String isbn = console.readLine();
        for (char c : isbn.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongISBN"));
                currentState = MenuState.STUDENT_MAIN;
                break;
            }
        }

        LibraryDatabase.getBookThroughISBN(isbn);
        System.out.println(messages.getString("prompt.delete.verification"));
        System.out.print("->  ");
        String ans = console.readLine().toUpperCase().charAt(0) + "";

        if (ans.equals("Y")) {
            librarian.removeBook(isbn);
        } else if (ans.equals("N")) {
            System.out.println(messages.getString("error.message.operationAborted"));
            console.writer().print("\033[H\033[2J");
            console.flush(); //makes the console empty for better clarity
            currentState = MenuState.LIBRARIAN_MAIN;
            return;
        } else {
            view.setErrorMessage(messages.getString("error.message.invalidChoice"));
            console.writer().print("\033[H\033[2J");
            console.flush(); //makes the console empty for better clarity
            currentState = MenuState.LIBRARIAN_MAIN;
            return;
        }
        currentState = MenuState.LIBRARIAN_MAIN;
    }

    private void librarianAddUser (Librarian librarian, Console console) {
        System.out.print(messages.getString("prompt.studentName"));
        String newStudentName = console.readLine();
        System.out.print(messages.getString("prompt.studentPassword"));
        String newStudentPassword = console.readLine();
        librarian.addUser(newStudentName, newStudentPassword);
        currentState = MenuState.LIBRARIAN_MAIN;
    }

    private void librarianRemoveUser (Librarian librarian, Console console) {
        System.out.print(messages.getString("prompt.student.id"));
        String studentId = console.readLine();
        for (char c : studentId.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongStudID"));
                currentState = MenuState.LIBRARIAN_MAIN;
                break;
            }
        }

        int idToDelete = Integer.parseInt(studentId);
        LibraryDatabase.getStudentFromId(idToDelete);
        System.out.println(messages.getString("prompt.delete.verification"));
        System.out.print("->  ");
        String ans = console.readLine().toUpperCase().charAt(0) + "";

        if (ans.equals("Y")) {
            librarian.removeUser(idToDelete);
        } else if (ans.equals("N")) {
            System.out.println(messages.getString("error.message.operationAborted"));
            console.writer().print("\033[H\033[2J");
            console.flush(); //makes the console empty for better clarity
            currentState = MenuState.LIBRARIAN_MAIN;
            return;
        } else {
            view.setErrorMessage(messages.getString("error.message.invalidChoice"));
            console.writer().print("\033[H\033[2J");
            console.flush(); //makes the console empty for better clarity
            currentState = MenuState.LIBRARIAN_MAIN;
            return;
        }
        currentState = MenuState.LIBRARIAN_MAIN;
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
                view.setErrorMessage(messages.getString("error.message.invalidChoice"));
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
                view.setErrorMessage(messages.getString("error.message.invalidChoice"));
                console.writer().print("\033[H\033[2J");
                console.flush(); //makes the console empty for better clarity
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }
        LibraryDatabase.selectAllUsers();
    }

}
