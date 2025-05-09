package org.nicolas.controller;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.model.Book;
import org.nicolas.model.Librarian;
import org.nicolas.model.Student;
import org.nicolas.model.User;
import org.nicolas.view.UserView;

import java.io.Console;
import java.util.*;

public class UserController {
    public User model;
    public UserView view;
    public ResourceBundle messages;
    public MenuState currentState = MenuState.LOGIN; //base menu-state : login page
    public int loginAttempts = 0; // Track attempts at class level

    //CONSTRUCTOR///////////////////////////////////////////////////////////////////////////////////////////////////////
    public UserController(User currentUser, UserView view, ResourceBundle bundle) {
        this.model = currentUser;
        this.view = view;
        this.messages = bundle;
        resetLoginAttempts();
    }

    public void resetLoginAttempts() {
        this.loginAttempts = 0; // Reset when returning to main menu
    }

    public User getLoggedInUser() { return model; }

    /**
     * State + singleton design pattern to avoid recursive calls enum to prevent more than one state of the menu : 
     * instead of continuously calling the methods within themselves : just changing the state of the menu
     */
    public enum MenuState {
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
    public void runApplication(Console console, int maxSystemAttempts) {
        while (currentState != MenuState.EXIT) { //checks the menu state, if is exit, exits the application
            clearScreenSequence(console);
            switch (currentState) {
                case LOGIN:
                    handleLogin(console, maxSystemAttempts);
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

    /**
     * logins to the application by loading all the needed information from the database to the system
     * @param console the used console
     * @param maxAttempts the number of tries the user can have to login before a system exit
     */
    public void handleLogin(Console console, int maxAttempts) {
        clearScreenSequence(console); //makes the console empty for better clarity

        if (loginAttempts >= maxAttempts) {
            view.setErrorMessage("Maximum login attempts reached. Please try again later.");
            return;
        }

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
            } else {
                currentState = MenuState.STUDENT_MAIN;
            }
        } else {
            view.setErrorMessage(messages.getString("login.failure"));
        }
    }

    /**
     * logs out of the application in a safe manner
     */
    public void handleLogout () { //exit sequence
        System.out.println(messages.getString("logout"));
        System.out.println(messages.getString("app.Exit"));
        System.out.println(messages.getString("goodbye"));
        System.exit(0);
    }

    //APP BASE DISPLAY//////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * the console application header
     */
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

        System.out.println(messages.getString("menu.title") + model.getName()
                + messages.getString("menu.title.exclamation") + "\n\n");
    }

    /**
     * the console application app footer
     */
    protected void appFooter () {
        System.out.println("                                                                       "
                + messages.getString("menu.settings.returnToMain"));
    }

    /**
     * the sequence to clear the console (might need to be updated / changed depending on the console used)
     * @param console the system console
     */
    public void clearScreenSequence (Console console) {
        console.writer().print("\033[H\033[2J");
        console.flush();
    }

    //BASE MENUS////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * base settings menu
     * @param console the system console
     */
    protected void settingsMenu (Console console) { //base call for the settings menu
        clearScreenSequence(console);

        //settings menu display
        appHeader();
        System.out.println(messages.getString("menu.settings.title") + "\n");
        System.out.println(messages.getString("menu.settings.changeName"));
        System.out.println(messages.getString("menu.settings.changePassword"));
        appFooter();

        String ans = console.readLine("\n->  ").toUpperCase().charAt(0) + "";
        clearScreenSequence(console);
        //appHeader();

        //settings menu back-end code for next option
        switch (ans) {
            case "1":
                //changing the name
                currentState = MenuState.SETTINGS_CHANGE_NAME;
                return;
            case "2" :
                //changing the password
                currentState = MenuState.SETTINGS_CHANGE_PASSWORD;
                return;
            case "M" :
                //going back to main menu, if the user is a librarian = librarian Main, else, student Main
                // (student main always the default to prevent admin powers to a random user
                currentState = (model instanceof Librarian) ? MenuState.LIBRARIAN_MAIN : MenuState.STUDENT_MAIN;
                return;
            case "X" :
                //exit application completly
                clearScreenSequence(console);
                currentState = MenuState.EXIT;
                return;
            default :
                //goes back to settings base menu (this menu) again until there is a valid choice , no counter allowed
                view.setErrorMessage("invalid choice, please try again");
                currentState = MenuState.SETTINGS;
                return;
        }
    }

    /**
     * base student menu
     * @param student the student using the application
     * @param console the system console
     */
    protected void studentMenu (Student student, Console console) { //base studend app
        //front end : menu display
        appHeader();
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

        String ans = console.readLine("\n->  ").toUpperCase().charAt(0) + "";
        clearScreenSequence(console);
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

    /**
     * base librarian menu
     * @param librarian the librarian using the application
     * @param console the system console
     */
    protected void librarianMenu (Librarian librarian, Console console) {
        appHeader();
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

        String ans = console.readLine("\n->  ").toUpperCase().charAt(0) + "";
        clearScreenSequence(console);
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
    /**
     * changes the name of the logged-in user
     * @param console the sytsem console
     */
    public void settingsChangeName (Console console) { //menu state to change the name
        clearScreenSequence(console);
        appHeader();
        System.out.println(messages.getString("prompt.newName"));
        String newName = console.readLine();
        System.out.println(newName + "  " + messages.getString("prompt.choice") + "?");
        String ans = console.readLine().toUpperCase().charAt(0) + "";

        if (ans.equals("Y")) {
            model.changeName(newName); //calling the model that will call the DB : enforcing MVC design
        } else {
            view.setErrorMessage(messages.getString("error.message.operationAborted"));
            currentState = MenuState.SETTINGS;
        }
        currentState = MenuState.SETTINGS;
    }

    /**
     * changes the password of the logged-in user
     * @param console the system console
     */
    public void settingsChangePassword (Console console) {
        //first request of the new password
        String stringPassword = console.readLine(messages.getString("prompt.newPassword"));

        //validation of the new password (asks to input it again)
        String stringPasswordCheck = console.readLine(messages.getString("prompt.checkNewPassword"));

        boolean isValid = (stringPassword.equals(stringPasswordCheck)) ? true : false; //checks if the first password and the validation are the same

        if (isValid == false) { //2nd chance to change password
            view.setErrorMessage(messages.getString("error.validationFailed")
                    + messages.getString("error.message.tryAgain"));
            String stringPasswordCheck2 = console.readLine(messages.getString("prompt.checkNewPassword"));

            isValid = (stringPassword.equals(stringPasswordCheck2)) ? true : false; //checks if first password and 2nd validation attempt are the same

            if (isValid) {
                model.changePassword(stringPassword); //call the change password method of user to enfore MVC model
                currentState = MenuState.SETTINGS;
            } else {
                view.setErrorMessage(messages.getString("error.message.operationAborted")); //operation aborted because too many attemps
                currentState = MenuState.SETTINGS; //both attempts failed return to main settings menu
            }
        } else {
            model.changePassword(stringPassword); //first attempt was ok, therefore changed password right away by calling model to enforce MVC design pattern
        }
    }

    //STUDENT HELPER METHODS////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * borrows a book for the student
     * @param console the system console
     */
    public void studentBorrow(Console console) {
        System.out.println(messages.getString("prompt.isbn"));
        String inputIsbn = console.readLine();
        model.borrowBook(inputIsbn, model.getUserID(), console);

        //Go back to main menu after borrowing
        currentState = MenuState.STUDENT_MAIN;
    }

    /**
     * returns a book for the student
     * @param console the system console
     */
    public void studentReturn (Console console) {
        System.out.println(messages.getString("prompt.isbn"));
        String inputIsbn = console.readLine();
        model.returnBook(inputIsbn, model.getUserID());
        currentState = MenuState.STUDENT_MAIN;
    }

    /**
     * allows a student to find a book through filtered search
     * @param console the system console
     */
    public void findBook (Console console) {
        System.out.println(messages.getString("prompt.information"));
        String inputIsbn = console.readLine(messages.getString("prompt.isbn"));
        String inputTitle = console.readLine(messages.getString("prompt.title"));
        String inputAuthor = console.readLine(messages.getString("prompt.author"));

        model.findBook(inputIsbn, inputTitle, inputAuthor);
        currentState = (model instanceof Librarian) ? MenuState.LIBRARIAN_MAIN : MenuState.STUDENT_MAIN;
    }

    //LIBRARIAN HELPER METHODS//////////////////////////////////////////////////////////////////////////////////////////

    /**
     * adds a book to the database
     * @param librarian the librarian adding the book
     * @param console the system console
     */
    public void librarianAddBook (Librarian librarian, Console console) {
        String isbn = console.readLine(messages.getString("prompt.isbn"));
        String title = console.readLine(messages.getString("prompt.title"));
        String author = console.readLine(messages.getString("prompt.author"));
        String tryAns = console.readLine(messages.getString("prompt.copies"));
        if (isbn.isBlank() || isbn.isEmpty() || isbn == null) {
            view.setErrorMessage(messages.getString("error.message.wrongISBN"));
            currentState = MenuState.LIBRARIAN_MAIN;
            return;
        } else if (tryAns.isBlank() || tryAns.isEmpty() || tryAns == null || tryAns.equals("0")
                || title.isBlank() || title.isEmpty() || title == null
                || author.isBlank() || author.isEmpty() || author == null) {

            view.setErrorMessage(messages.getString("error.message.wrongNumCopies"));
            currentState = MenuState.LIBRARIAN_MAIN;
            return;
        }

        for (char c : isbn.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongISBN"));
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }

        for (char c : tryAns.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongNumCopies"));
                currentState = MenuState.LIBRARIAN_MAIN;
                return;
            }
        }

        int copies = Integer.parseInt(tryAns);
        librarian.addBook(isbn, title, author, copies, console);

        currentState = MenuState.LIBRARIAN_MAIN;
        return;
    }

    /**
     * removes copies of a book or the book completly in the database
     * @param librarian the librarian removing the copies / book
     * @param console the system console
     */
    public void librarianRemoveBook (Librarian librarian, Console console) {
        System.out.print(messages.getString("prompt.isbn"));
        String isbn = console.readLine();
        for (char c : isbn.toCharArray()) {
            if (Character.isLetter(c)) {
                view.setErrorMessage(messages.getString("error.message.wrongISBN"));
                currentState = MenuState.STUDENT_MAIN;
                break;
            }
        }

        Book tempBook = LibraryDatabase.getBookThroughISBN(isbn);
        System.out.println("\n" + messages.getString("prompt.removeCopiesOption"));
        System.out.println(messages.getString("prompt.deleteOption"));
        int ans = Integer.parseInt(console.readLine().charAt(0) + "");

        if (ans == 1) {
            System.out.println("\n" + messages.getString("prompt.copiesToRemove"));
            ans = Integer.parseInt(console.readLine().charAt(0) + "");
            int newNumsOfCopies = tempBook.getCopies() - ans;
            librarian.changeCopies(isbn, newNumsOfCopies);
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

    /**
     * allows a librarian to borrow a book for a student
     * @param librarian the librarian performing the borrowing
     * @param console the system console
     */
    public void librarianBorrow (Librarian librarian, Console console) {
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

    /**
     * allows a librarian to return a book copy on behalf of a student
     * @param librarian the libarian performing the returning
     * @param console the system console
     */
    public void librarianReturn (Librarian librarian, Console console) {
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

    /**
     * adds a user to the database
     * @param librarian the librarian adding the user
     * @param console the system console
     */
    public void librarianAddUser (Librarian librarian, Console console) {
        System.out.print(messages.getString("prompt.studentName"));
        String newStudentName = console.readLine();
        System.out.print(messages.getString("prompt.studentPassword"));
        String newStudentPassword = console.readLine();
        librarian.addUser(newStudentName, newStudentPassword);
        currentState = MenuState.LIBRARIAN_MAIN;
    }

    /**
     * removes a user from the database
     * @param librarian the librarian removing the user
     * @param console the system console
     */
    public void librarianRemoveUser (Librarian librarian, Console console) {
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

    /**
     * gets all the book from the system, with the option to see only available or borrowed books
     * @param console the system console
     */
    public void librarianBookCatalog (Console console) {
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

    /**
     * gets all the users from the database, with the option to filter them by role
     * @param console the system console
     */
    public void librarianUserCatalog (Console console) {
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

    public User getModel() {
        return model;
    }

    public void setModel(User model) {
        this.model = model;
    }

    public UserView getView() {
        return view;
    }

    public void setView(UserView view) {
        this.view = view;
    }

    public ResourceBundle getMessages() {
        return messages;
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

    public MenuState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(MenuState currentState) {
        this.currentState = currentState;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }
}
