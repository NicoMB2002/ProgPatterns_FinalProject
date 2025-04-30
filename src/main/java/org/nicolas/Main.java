package org.nicolas;

import org.nicolas.controller.UserController;
import org.nicolas.database.LibraryDatabase;
import org.nicolas.model.Student;
import org.nicolas.model.User;
import org.nicolas.view.UserView;

public class Main {
    public static void main(String[] args) {

        UserView view = new UserView();
        User student = new Student(1, "Nicolas", "hello");
        UserController controller = new UserController(student, view);

        view.showMainMenu(controller);


    }
}