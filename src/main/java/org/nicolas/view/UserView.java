package org.nicolas.view;

import org.nicolas.controller.UserController;
import org.nicolas.model.User;
import org.nicolas.model.UserType;

import java.util.ArrayList;
import java.util.Scanner;

public class UserView {

    // Main menu for the UserView
    public void showMenu(UserController controller) {
        System.out.println("\nWelcome to the User Management System:");

        while (true) {
            System.out.println("\n1. Login\n2. Change Name\n3. Change Password\n4. Exit");

            Scanner console = new Scanner(System.in);

            int choice = console.nextInt();
            console.nextLine(); // Consume the newline character after the integer input

            switch (choice) {
                case 1:
                    controller.handleLogin();

                    break;
                case 2:

                    break;
                case 3:

                    break;
                case 4:
                    System.out.println("\nThank you for using the User Management System!");
                    return; // Exit the program
                default:
                    System.out.println("\nInvalid choice, please try again.");
            }
        }
    }


}
