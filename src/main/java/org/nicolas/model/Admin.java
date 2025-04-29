package org.nicolas.model;

public class Admin extends User {

    //-----------POTENTIALLY DELETED CLASS-------------

    public Admin(int userID, String name, String password) {
        super(userID, name, password);
    }

    public void addUser (UserType userType, int userID, String name, String password) {}

    public void removeUser(int userID) {}

    public void displayUsers () {}
}
