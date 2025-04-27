package org.nicolas.model;

public class Admin extends User {

    public Admin(int userID, String name, String password, User currentUser) {
        super(userID, name, password, currentUser);
    }

    public void addUser (UserType userType, int userID, String name, String password) {}

    public void removeUser(int userID) {}

    public void displayUsers () {}
}
