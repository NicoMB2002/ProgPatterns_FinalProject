package org.nicolas;

public class User {
    private UserType userType;
    private int userID;
    private String name;
    private String password;

    public void changeInfo () {}

    protected void changeName () {}

    protected void changePassword () {}

    public boolean login (int userID, String password) {
        return false;
    }
}
