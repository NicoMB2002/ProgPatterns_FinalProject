package org.nicolas.view;

import org.nicolas.controller.UserController;
import org.nicolas.model.User;
import org.nicolas.model.UserType;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

public class UserView {

    // Main menu for the User
    public void showMainMenu(UserController controller, ResourceBundle bundle) {
        Scanner console = new Scanner(System.in);
        while (true) {
            System.out.println("\n" + bundle.getString("menu.main.title"));
            System.out.println("1. " + bundle.getString("menu.main.login"));
            System.out.println("2. " + bundle.getString("menu.main.exit"));

            int choice = console.nextInt();
            console.nextLine(); // clear newline

            switch (choice) {
                case 1:
                    controller.handleLogin(); // Ask userID and password inside controller
                    break;
                case 2:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


}
