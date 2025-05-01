package org.nicolas;

import org.nicolas.controller.UserController;
import org.nicolas.database.LibraryDatabase;
import org.nicolas.model.Student;
import org.nicolas.model.User;
import org.nicolas.view.UserView;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

//        LibraryDatabase.insertIntoBooks("2109312037362", "Alice in Wonderland", "J.K Rowling", 22);
//        LibraryDatabase.createUserTable();
//        LibraryDatabase.insertIntoUser("nicolas", "librarian", "12345");
//        LibraryDatabase.insertIntoUser("helene", "student", "54321");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Select language (en/fr): ");
        String lang = scanner.nextLine();
        Locale locale = new Locale(lang);
        ResourceBundle bundle = ResourceBundle.getBundle("messages.messages", locale);

        UserView view = new UserView();
        User student = new Student(1, "Nicolas", "hello");
        UserController controller = new UserController(student, view, bundle);

        view.showMainMenu(controller, bundle);


    }
}