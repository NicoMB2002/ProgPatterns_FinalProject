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

        Scanner scanner = new Scanner(System.in);


        System.out.print("Select language / Sélectionnez votre langue (en/fr): ");
        String lang = scanner.nextLine();
        Locale locale = new Locale(lang);
        ResourceBundle bundle = ResourceBundle.getBundle("messages.messages", locale);

        UserView view = new UserView();
        User student = new Student(1, "Nicolas", "hello");
        UserController controller = new UserController(student, view, bundle);
        view.mainMenu(controller, bundle); //do this in the main
    }
}