package org.nicolas;

import org.nicolas.controller.UserController;
import org.nicolas.database.LibraryDatabase;
import org.nicolas.model.Student;
import org.nicolas.model.User;
import org.nicolas.view.UserView;

import java.io.Console;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Console console = System.console();
        if (console == null) {
            //unclean and wrongful termination : there is no console on the computer, therefore the app cannot run
            System.exit(-1);
        }

        System.out.print("Select language / Sélectionnez la langue (en/fr): ");
        String lang = console.readLine();
        Locale locale = new Locale(lang);
        ResourceBundle bundle = ResourceBundle.getBundle("messages.messages", locale);

        UserView view = new UserView();
        User student = new Student(1, "Nicolas", "hello");
        UserController controller = new UserController(student, view, bundle);
        view.mainMenu(controller, bundle); //do this in the main
        controller.runApplication(console);
    }
}
