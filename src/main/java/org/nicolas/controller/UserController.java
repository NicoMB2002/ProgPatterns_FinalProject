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
import java.util.*;

public class UserController {
    private User model;
    private UserView view;
    private ResourceBundle messages;
    private MenuState currentState = MenuState.LOGIN; //base menu-state : login page
    
    //CONSTRUCTOR///////////////////////////////////////////////////////////////////////////////////////////////////////
    public UserController(User currentUser, UserView view, ResourceBundle bundle) {
        this.model = currentUser;
        this.view = view;
        this.messages = bundle;
    }

    public User getLoggedInUser() { return model; }

    /**
     * State + singleton design pattern to avoid recursive calls enum to prevent more than one state of the menu : 
     * instead of continuously calling the methods within themselves : just changing the state of the menu
     */
    private enum MenuState {
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

    //BASE STATE HANDLING///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * handles the state of the application, making sure only one state is called at the time
     * @param console the console the application will run on
     */
    public void runApplication(Console console) {
        while (currentState != MenuState.EXIT) { //checks the menu state, if is exit, exits the application
            clearScreenSequence(console);

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
            clearScreenSequence(console);

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

    public void handleLogout () { //exit sequence
        System.out.println(messages.getString("logout"));
        System.out.println(messages.getString("app.Exit"));
        System.out.println(messages.getString("goodbye"));
        System.exit(0);
    }

    //APP BASE DISPLAY//////////////////////////////////////////////////////////////////////////////////////////////////
    protected void appHeader () {
        System.out.println("--------------------------------------------------------------------------------------------");
        Locale bundleLocale = messages.getLocale();

        // Adjusts the menu depending on the language
        if (bundleLocale.getLanguage().equals("fr")) {
            System.out.println("                                                             "
                    + messages.getString("logoutOption"));
            System.out.println("                                                                             "
                    + messages.getString("menu.settings"));
        } else {
            System.out.println("                                                                     "
                    + messages.getString("logoutOption"));
            System.out.println("                                                                             "
                    + messages.getString("menu.settings"));
        }
    }

    protected void appFooter () {
        System.out.println("                                                                       "
                + messages.getString("menu.settings.returnToMain"));
        System.out.print("->  ");
    }
    
    public void clearScreenSequence (Console console) {
        console.writer().print("\033[H\033[2J");
        console.flush();
    }

    //BASE MENUS////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected void settingsMenu (Console console) { //base call for the settings menu
        clearScreenSequence(console);

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
                    //going back to main menu, if the user is a librarian = librarian Main, else, student Main
                    // (student main always the default to prevent admin powers to a random user
                    currentState = (model instanceof Librarian) ? MenuState.LIBRARIAN_MAIN : MenuState.STUDENT_MAIN;
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

    protected void studentMenu (Student student, Console console) { //base studend app
        //front end : menu display
        appHeader();
        System.out.println(messages.getString("menu.title") + model.getName()
                + messages.getString("menu.title.exclamation") + "\n\n");
        Locale bundleLocale = messages.getLocale();

        // Adjusts the menu depending on the language
        if (bundleLocale.getLanguage().equals("fr")) {
            System.out.println(messages.getString("menu.student.borrow") + "               "
                    + messages.getString("menu.student.borrowedList"));
            System.out.println(messages.getString("menu.student.return") + "               "
                    + messages.getString("menu.student.searchBook"));
        } else {
            System.out.println(messages.getString("menu.student.borrow") + "               "
                    + messages.getString("menu.student.borrowedList"));
            System.out.println(messages.getString("menu.student.return") + "               "
                    + messages.getString("menu.student.searchBook"));
        }

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
                clearScreenSequence(console);
                handleLogout();
                break;
            default :
                view.setErrorMessage(messages.getString("error.message.invalidChoice")
                        + messages.getString("error.message.tryAgain"));
                currentState = MenuState.STUDENT_MAIN;
                break;
        }
    }

    protected void librarianMenu (Librarian librarian, Console console) {
        appHeader();
        System.out.println(messages.getString("menu.title") + model.getName()
                + messages.getString("menu.title.exclamation") + "\n");

        Locale bundleLocale = messages.getLocale();

        // Adjusts the menu depending on the language
        if (bundleLocale.getLanguage().equals("fr")) {
            System.out.println(messages.getString("menu.librarian.add") + "                               "
                    + messages.getString("menu.librarian.returnBookFroUser"));

            System.out.println(messages.getString("menu.librarian.remove") + "                             "
                    + messages.getString("menu.librarian.seeBookCatalog"));

            System.out.println(messages.getString("menu.librarian.borrowForUser") + "         "
                    + messages.getString("menu.librarian.seeUserCatalog"));

            System.out.println(messages.getString("menu.librarian.addUser") + "                         "
                    + messages.getString("menu.librarian.searchBook"));

            System.out.println(messages.getString("menu.librarian.removeUser"));
        } else {
            System.out.println(messages.getString("menu.librarian.add") + "                          "
                    + messages.getString("menu.librarian.addUser") + "                         "
                    + messages.getString("menu.librarian.seeBookCatalog"));

            System.out.println(messages.getString("menu.librarian.remove") + "                       "
                    + messages.getString("menu.librarian.removeUser") + "                     "
                    + messages.getString("menu.librarian.seeUserCatalog"));

            System.out.println(messages.getString("menu.librarian.borrowForUser") + "              "
                    + messages.getString("menu.librarian.returnBookFroUser") + "             "
                    + messages.getString("menu.librarian.searchBook"));
        }

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
                currentState = MenuState.LIBRARIAN_REMOVE_BOOK;
                break;
            case "3" : //see book catalog
                currentState = MenuState.LIBRARIAN_BORROW;
                break;
            case "4" : //add a user
                currentState = MenuState.LIBRARIAN_ADD_USER;
                break;
            case "5" : //remove a user
                currentState = MenuState.LIBRARIAN_REMOVE_USER;
                break;
            case "6" : //see user catalog
                currentState = MenuState.LIBRARIAN_RETURN;
                break;
            case "7" : //borrow book for user
                currentState = MenuState.LIBRARIAN_BOOK_CATALOG;
                break;
            case "8" : //return book for user
                currentState = MenuState.LIBRARIAN_USER_CATALOG;
                break;
            case "9" : //find book
                currentState = MenuState.FIND_BOOK;
                break;
            case "S" : //settings
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

    //SETTINGS HELPER METHODS///////////////////////////////////////////////////////////////////////////////////////////

    //never actually displaying > seems like a threading problem >>> cannot resolve issue
    private void settingsChangeName (Console console) { //menu state to change the name
        console.writer().print("\033[H\033[2J");
        console.flush();
        appHeader();
        console.writer().print("IS this working please");
        System.out.println(messages.getString("prompt.newName"));
        String newName = console.readLine();
        System.out.println(newName + "  " + messages.getString("prompt.choice") + "?");
        String ans = console.readLine().toUpperCase().charAt(0) + "";

        if (ans.equals("Y")) {
            model.changeName(newName); //calling the model that will call the DB : enforcing MVC design
        } else {
            System.out.println(messages.getString("error.message.operationAborted"));
            currentState = MenuState.SETTINGS;
        }
        currentState = MenuState.SETTINGS;
    }

    //also not displaying, like name
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

    //STUDENT HELPER METHODS////////////////////////////////////////////////////////////////////////////////////////////

    private void studentBorrow(Console console) {
        System.out.println(messages.getString("prompt.isbn"));
        String inputIsbn = console.readLine();
        model.borrowBook(inputIsbn, model.getUserID(), console);

        //Go back to main menu after borrowing
        currentState = MenuState.STUDENT_MAIN;
    }

    private void studentReturn (Console console) {
        System.out.println(messages.getString("prompt.isbn"));
        String inputIsbn = console.readLine();
        model.returnBook(inputIsbn, model.getUserID());
        currentState = MenuState.STUDENT_MAIN;
    }

    private void findBook (Console console) {
        System.out.println(messages.getString("prompt.information"));
        String inputIsbn = console.readLine(messages.getString("prompt.isbn"));
        String inputTitle = console.readLine(messages.getString("prompt.title"));
        String inputAuthor = console.readLine(messages.getString("prompt.author"));

        model.findBook(inputIsbn, inputTitle, inputAuthor);
        currentState = (model instanceof Librarian) ? MenuState.LIBRARIAN_MAIN : MenuState.STUDENT_MAIN;
    }

    //LIBRARIAN HELPER METHODS//////////////////////////////////////////////////////////////////////////////////////////

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

        String title = console.readLine(messages.getString("prompt.title"));
        String author = console.readLine(messages.getString("prompt.author"));
        String tryAns = console.readLine(messages.getString("prompt.copies"));

        for (char c : tryAns.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongNumCopies"));
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }

        int copies = Integer.parseInt(tryAns);
        librarian.addBook(isbn, title, author, copies, console, messages);

        currentState = MenuState.LIBRARIAN_MAIN;
        return;
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
        System.out.println("\n" + messages.getString("prompt.removeCopiesOption"));
        System.out.println(messages.getString("prompt.deleteOption"));
        int ans = Integer.parseInt(console.readLine().charAt(0) + "");

        if (ans == 1) {
            System.out.println("\n" + messages.getString("prompt.copies"));
            ans = Integer.parseInt(console.readLine().charAt(0) + "");
            librarian.changeCopies(isbn, ans);
            currentState = MenuState.LIBRARIAN_MAIN;
            return;
        } else if (ans == 2) {
            System.out.println(messages.getString("prompt.delete.verification"));
            String tryAns = console.readLine().toUpperCase().charAt(0) + "";

            if (tryAns.equals("Y")) {
                librarian.removeBook(isbn);
                System.out.println(messages.getString("error.message.success"));

            } else if (tryAns.equals("N")) {
                System.out.println(messages.getString("error.message.operationAborted"));
                clearScreenSequence(console); //makes the console empty for better clarity
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            } else {
                view.setErrorMessage(messages.getString("error.message.invalidChoice"));
                clearScreenSequence(console); //makes the console empty for better clarity
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }


        currentState = MenuState.LIBRARIAN_MAIN;
    }

    private void librarianBorrow (Librarian librarian, Console console) {
        System.out.print(messages.getString("prompt.student.id"));
        String studentId = console.readLine();
        for (char c : studentId.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongStudID"));
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }

        System.out.print(messages.getString("prompt.isbn"));
        String isbn = console.readLine();
        for (char c : isbn.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongISBN"));
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }

        int inputStudentId = Integer.parseInt(studentId);
        librarian.borrowBook(isbn, inputStudentId, console);
        currentState = MenuState.LIBRARIAN_MAIN;
    }

    private void librarianReturn (Librarian librarian, Console console) {
        System.out.print(messages.getString("prompt.student.id"));
        String studentId = console.readLine();
        for (char c : studentId.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongStudID"));
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }

        System.out.print(messages.getString("prompt.isbn"));
        String isbn = console.readLine();
        for (char c : isbn.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongISBN"));
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }

        int inputStudentId = Integer.parseInt(studentId);
        librarian.returnBook(isbn, inputStudentId);
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
            clearScreenSequence(console); //makes the console empty for better clarity
            currentState = MenuState.LIBRARIAN_MAIN;
            return;
        } else {
            view.setErrorMessage(messages.getString("error.message.invalidChoice"));
            clearScreenSequence(console); //makes the console empty for better clarity
            currentState = MenuState.LIBRARIAN_MAIN;
            return;
        }
        currentState = MenuState.LIBRARIAN_MAIN;
    }

    private void librarianBookCatalog (Console console) {
        System.out.println(messages.getString("filter.main.prompt"));
        String ans = console.readLine().toUpperCase().charAt(0) + "";

        if (ans.equals("Y")) {
            System.out.println(messages.getString("filter.availableBooks"));
            System.out.println(messages.getString("filter.borrowedBooks"));
            ans = console.readLine().toUpperCase().charAt(0) + "";

            if (ans.equals("1")) {
                console.writer().println(LibraryDatabase.selectAllAvailableBooks());
                clearScreenSequence(console);
                currentState = MenuState.LIBRARIAN_MAIN;
            } else if (ans.equals("2")) {
                console.writer().println(LibraryDatabase.selectAllBorrowedBooks());
                clearScreenSequence(console);
                currentState = MenuState.LIBRARIAN_MAIN;
            } else {
                view.setErrorMessage(messages.getString("error.message.invalidChoice"));
                clearScreenSequence(console);
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        } else {
            console.writer().println(LibraryDatabase.selectAllBooks());
            currentState = MenuState.LIBRARIAN_MAIN;
        }
    }

    private void librarianUserCatalog (Console console) {
        System.out.println(messages.getString("filter.main.prompt"));
        String ans = console.readLine().toUpperCase().charAt(0) + "";

        if (ans.equals("Y")) {
            System.out.println(messages.getString("filter.students"));
            System.out.println(messages.getString("filter.librarians"));
            ans = console.readLine().toUpperCase().charAt(0) + "";

            if (ans.equals("1")) {
                console.writer().println(LibraryDatabase.getUserListFromRole("STUDENT"));
                clearScreenSequence(console);
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            } else if (ans.equals("2")) {
                console.writer().println(LibraryDatabase.getUserListFromRole("LIBRARIAN"));
                clearScreenSequence(console);
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            } else {
                view.setErrorMessage(messages.getString("error.message.invalidChoice"));
                clearScreenSequence(console); //makes the console empty for better clarity
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }
        console.writer().println(LibraryDatabase.selectAllUsers());
        currentState = MenuState.LIBRARIAN_MAIN;
    }
}
