package org.nicolas.view;

import org.nicolas.controller.UserController;
import org.nicolas.model.User;
import org.nicolas.model.UserType;

import java.util.ArrayList;
import java.util.Scanner;

public class UserView {

    // Main menu for the User
    public void showMainMenu(UserController controller) {
        Scanner console = new Scanner(System.in);
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Login");
            System.out.println("2. Exit");

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
