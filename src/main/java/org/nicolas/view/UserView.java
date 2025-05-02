package org.nicolas.view;

import org.nicolas.Main;
import org.nicolas.controller.UserController;
import org.nicolas.model.User;
import org.nicolas.model.UserType;

import java.io.Console;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

public class UserView {
    public void setErrorMessage (String message) {
        System.out.println("[ERROR : " + message + "]");
    }

    // Main menu for the User
    public void mainMenu (UserController controller, ResourceBundle bundle) {
        Console console = System.console();
        console.flush(); //ensures the console is empty
        final int MAX_ATTEMPTS = 3;
        int tryCounter = 0;

        while (tryCounter <= MAX_ATTEMPTS) {
            System.out.println(bundle.getString("welcome") + "\n\n");
            System.out.println(bundle.getString("menu.main.login"));
            System.out.println(bundle.getString("menu.main.exit"));
            System.out.print("\n->  ");

            String ans = console.readLine().toUpperCase().charAt(0) + "";
            console.writer().print("\033[H\033[2J");
            console.flush();

            switch (ans) {
                case "1" :
                    console.writer().print("\033[H\033[2J");
                    console.flush();
                    controller.handleLogin(console);
                    if (controller.getLoggedInUser() != null) {
                        return;
                    }
                    break;
                case "2" :
                    System.out.println(bundle.getString("app.Exit"));
                    System.exit(0);
                    break;
                case "X" :
                    System.out.println(bundle.getString("goodbye"));
                    System.exit(0);
                    break;
                default:
                    tryCounter++;
                    setErrorMessage(bundle.getString("invalid.choice"));
                    break;
            }
        }
        setErrorMessage(bundle.getString("too.many.attempts"));
        System.exit(1); //Exception Termination
    }
}
