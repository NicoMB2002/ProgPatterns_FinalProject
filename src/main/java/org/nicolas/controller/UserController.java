package org.nicolas.controller;

import org.nicolas.database.LibraryDatabase;
import org.nicolas.exceptions.InvalidUserException;
import org.nicolas.model.Librarian;
import org.nicolas.model.Student;
import org.nicolas.model.User;
import org.nicolas.view.UserView;

import java.util.ArrayList;
import java.util.Scanner;

public class UserController {
    private User model;
    private UserView view;

    public UserController(User currentUser, UserView view) {
        this.model = currentUser;
        this.view = view;
    }

    public void handleLogin() {
        Scanner console = new Scanner(System.in);
        System.out.println("Enter Your User ID");
        int id = console.nextInt();
        console.nextLine();
        System.out.println("Enter your password");
        String password = console.nextLine();
        model.login(id, password);
    }


}
