package org.nicolas.model;

public class Librarian extends User {

    //-------PROPOSING THIS CLASS AS THE ADMIN CLASS-------

    public Librarian(int userID, String name, String password) {
        super(userID, name, password);
    }

    @Override
    public void borrowBook(String isbn) {
        System.out.println("Librarians are not allowed to borrow books");
    }
}
