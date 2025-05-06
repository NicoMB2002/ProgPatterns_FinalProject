package org.nicolas.view;

import org.nicolas.controller.UserController;
import java.io.Console;
import java.util.ResourceBundle;

public class UserView {

    /**
     * displays the same type of error messages throughout the whole system
     * @param message the input message to be displayed
     */
    public void setErrorMessage (String message) {
        System.out.println("[ERROR : " + message + "]");
    }

    /**
     * Main menu to connect to the application
     * @param controller the application itself
     * @param bundle the language bundle
     */
    public void mainMenu (UserController controller, ResourceBundle bundle, int maxSystemAttempts) {
        Console console = System.console();

        console.writer().print("\033[H\033[2J");
        console.flush();
        int tryCounter = 0;
        String ans = "";

        while (tryCounter <= maxSystemAttempts) {
            System.out.println(bundle.getString("welcome") + "\n\n");
            System.out.println(bundle.getString("menu.main.login"));
            System.out.println(bundle.getString("menu.main.exit"));
            System.out.print("\n->  ");

            ans = console.readLine().toUpperCase().charAt(0) + "";
            console.writer().print("\033[H\033[2J");
            console.flush();

            switch (ans) {
                case "1" :
                    console.writer().print("\033[H\033[2J"); //flush sequence
                    console.flush();
                    controller.handleLogin(console, maxSystemAttempts); //call login
                    if (controller.getLoggedInUser() != null) {
                        return; //return statement to break out of loop & method
                    }
                    break;
                case "2" :
                    System.out.println(bundle.getString("app.Exit")); //exiting the app
                    System.exit(0); //exit code in system out 0 ->> rightful and ok termination
                    break;
                case "X" :
                    System.out.println(bundle.getString("goodbye"));
                    System.exit(0); //exit code in system out 0 ->> rightful and ok termination
                    break;
                default:
                    tryCounter++; //add another try to while counter
                    setErrorMessage(bundle.getString("invalid.choice"));
                    break; //break out of switch, not of loop
            }
        }
        setErrorMessage(bundle.getString("too.many.attempts"));
        System.exit(1); //Exception Termination -->> not good termination but still ok termination
    }
}
