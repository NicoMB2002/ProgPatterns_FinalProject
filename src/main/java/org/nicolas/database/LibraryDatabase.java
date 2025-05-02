package org.nicolas.database;

import org.nicolas.model.Book;
import org.nicolas.model.Date;
import org.nicolas.model.Librarian;
import org.nicolas.model.Student;
import org.nicolas.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class LibraryDatabase {

    public static Connection connect() {
        String basePath = "jdbc:sqlite:src/main/resources/LibraryDatabase/"; //created LibraryDatabase in resources
        String dbPath = basePath + "library.db";

        Connection connection;
        try {
            //try to connect to the database
            connection = DriverManager.getConnection(dbPath);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    //CREATING DB TABLES////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * creates the table user if it is not already created
     */
    public static void createUserTable() {
        // USER_ID | name | role | password
        String userTable = """
        CREATE TABLE IF NOT EXISTS User (
            user_id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            role TEXT NOT NULL,
            password TEXT NOT NULL
        );
        """;

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            //execute the Create table statement
            stmt.execute(userTable);
            System.out.println("\nUser Table created successfully.\n");
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * creates the table Books if it is not already created
     */
    public static void createBooksTable() {
        // ISBN | title | author | no_copies | borrowed_books | available_copies
        String booksTable = """
        CREATE TABLE IF NOT EXISTS Books (
            isbn TEXT PRIMARY KEY,
            title TEXT NOT NULL,
            author TEXT NOT NULL,
            no_copies INTEGER NOT NULL,
            borrowed_books INTEGER NOT NULL,
            available_copies INTEGER NOT NULL,
            CHECK (available_copies = no_copies - borrowed_books)
        );
        """; //TODO check the check option : make a trigger instead?
        /*
        CREATE TRIGGER CheckCopies
        ON Books
        INSTEAD OF INSERT
        AS
        BEGIN
            IF (available_copies = no_copies - borrowed_books) BEGIN
                INSERT INTO Books VALUES (?, ?, ?, ?, ?, ?)
            END;
            ELSE BEGIN
                RAISEERROR ("The number of copies doesn't match);
                ROLLBACK TRANSACTION;
            END;
        END;
         */
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            //execute the Create table statement
            stmt.execute(booksTable);
            System.out.println("\nBooks Table created successfully.\n");
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * creates the table BorrowedBooks if it is not already created
     */
    public static void createBorrowedBooksTable() {
        // BB_ID | USER_ID | ISBN | borrow_date | return_date | return_status
        String borrowedBooksTable = """
        CREATE TABLE IF NOT EXISTS BorrowedBooks (
            borrowedBook_id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            isbn TEXT,
            borrow_date TEXT,
            return_date TEXT,
            return_status TEXT,
            FOREIGN KEY(user_id) REFERENCES User(user_id),
            FOREIGN KEY(isbn) REFERENCES Books(isbn)
        );
        """;
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            //execute the Create table statement
            stmt.execute(borrowedBooksTable);
            System.out.println("\nBorrowedBooks Table created successfully.\n");
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //MODIFY TABLES/////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * adds a column to a specified table
     * @param tableName the table to update
     * @param columnName the new column's name
     * @param columnType the data type of the new column
     */
    public static void addColumn(String tableName, String columnName, String columnType) {
        String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType;

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql); // Alter the table to add a new column
            System.out.println("Column " + columnName + " added successfully to table " + tableName + ".");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Failed to add column: " + e.getMessage());
        }
    }

    /**
     * Removes a table from the database
     * ! This action is unchangable : proceed with caution
     * @param tableName the table to remove
     */
    public static void dropTable(String tableName) {

        String sql = "DROP TABLE IF EXISTS " + tableName;

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql); // Drop the table
            System.out.println("Table " + tableName + " dropped successfully.");
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //INSERT STATEMENTS/////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * inserts a new record with a new user
     * @param name the new user's name
     * @param role the user's role within the system
     * @param password the user's password
     */
    public static void insertIntoUser(String name, String role, String password) {
        String sql = "INSERT INTO User (name, role, password) VALUES(?, ?, ?)"; //Insert query with '?' placeholders

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setString(3, password);
            pstmt.executeUpdate(); //execute insert
            System.out.println();
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * inserts a new record with a new book
     * @param isbn the book's ISBN (as a PK)
     * @param title the book's title
     * @param author the author of the book
     * @param no_Copies the number of copies in the system
     */
    public static void insertIntoBooks(String isbn, String title, String author, int no_Copies) {
        String sql = "INSERT INTO Books VALUES(?, ?, ?, ?, ?, ?)"; //Insert query with '?' placeholders
        int borrowed_Books = 0; //sets the value to 0 to avoid any error (because nobody could have borrowed the book)
        int available_Copies = no_Copies; //simple check : it should always initialize == no_Copies

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setInt(4, no_Copies);
            pstmt.setInt(5, borrowed_Books);
            pstmt.setInt(6, available_Copies);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * inserts a book into the borrowed books table when a user borrows it
     * @param userID the user that borrowed the book
     * @param isbn the book's unique identifier
     * @param borrow_date the borrow date
     */
    public static void insertIntoBorrowedBooks(int userID, String isbn, Date borrow_date) {
        String sql = "INSERT INTO borrowedBooks (user_id, isbn, borrow_date, return_date, return_status) VALUES (?, ?, ?, ?, ?);"; //Insert query with '?' placeholders


        Date return_date = borrow_date;
        return_date.setDay(return_date.getDay() + 14); //automatically sets the return date to two weeks from the borrow date
        String return_status = "Borrowed";   //automatically sets the status to borrowed

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userID);
            pstmt.setString(2, isbn);
            pstmt.setString(3, borrow_date.toString());
            pstmt.setString(4, return_date.toString());
            pstmt.setString(5, return_status);
            pstmt.executeUpdate(); //execute insert
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    //SELECT * STATEMENTS///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * selects the entire user table
     * @return the id, name, and role of the user (not their password)
     */
    public static String selectAllUsers() {
        String sql = "SELECT * FROM User ORDER BY role, user_id";
        StringBuilder builder = new StringBuilder();
        //using StringBuilder to build (or append) String

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            int counter = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String role = rs.getString("role");
                builder.append(String.format("%d.  %d  %s  ROLE : %s", counter, id, name, role));
                counter++;
            }
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }

    /**
     * selects the entirety of the books table
     * @return books with their isbn, title, author, no of copies total, no of borrowed copies, and no of copies available
     */
    public static String selectAllBooks() {
        String sql = "SELECT * FROM Books";
        StringBuilder builder = new StringBuilder();

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int noCopies = rs.getInt("no_copies");
                int borrowedBooks = rs.getInt("borrowed_books");
                int booksAvailable = noCopies - borrowedBooks;

                builder.append(String.format("%s  %s,  %s  COPIES: %d  [BORROWED: %d  AVAILABLE: %d]%n",
                        isbn, title, author, noCopies, borrowedBooks, booksAvailable));
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }

    /**
     * Selects books from the Book table in the database
     * @return ArrayList of all the books
     */
    public static ArrayList<Book> returnListOfBooks() {
        String sql = "SELECT * FROM Books";
        ArrayList<Book> books = new ArrayList<>();

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int noCopies = rs.getInt("no_copies");
                int borrowedBooks = rs.getInt("borrowed_books");
                int availableCopies = rs.getInt("available_copies");

                Book book = new Book(isbn, title, author, noCopies, availableCopies, borrowedBooks);
                books.add(book);
            }

            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return books;
    }

    /**
     * selects the entirety of the borrowed books table
     * @return the borrowed book ID, userID, book's ISBN, borrowDate, returnDate, returnStatus
     */
    public static String selectAllBorrowedBooks() {
        String sql = "SELECT * FROM BorrowedBooks";
        StringBuilder builder = new StringBuilder();
        //using StringBuilder to build (or append) String

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int borrowedBookId = rs.getInt("borrowedBook_id");
                int userId = rs.getInt("user_id");
                String isbn = rs.getString("isbn");
                String borrowDate = rs.getString("borrow_date");
                String returnDate = rs.getString("return_date");
                String returnStatus = rs.getString("return_status");

                builder.append(String.format("Borrowed Book ID: %d%nUser ID: %d%nISBN: %s%nBorrow Date: %s%n" +
                        "Return Date: %s%nReturn Status: %s%n",
                        borrowedBookId, userId, isbn, borrowDate, returnDate, returnStatus));
            }
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }

    //SPECIALIZED STATEMENTS////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * searches through the database for books matching filters
     * @param isbnFilter the isbn filter (if no information is provided, contains '---')
     * @param titleFilter the title filter (if no information is provided, contains '---')
     * @param authorFilter the author filter (if no information is provided, contains '---')
     * @return
     */
    public static ArrayList<Book> getFilteredBooks (String isbnFilter, String titleFilter, String authorFilter) {
        //TODO check in the library system and implement a value for null strings
        isbnFilter = (isbnFilter.isEmpty() || isbnFilter.isBlank() || isbnFilter == null) ? "---" : isbnFilter;
        titleFilter = (titleFilter.isEmpty() || titleFilter.isBlank() || titleFilter == null) ? "---" : titleFilter;
        authorFilter = (authorFilter.isEmpty() || authorFilter.isBlank() || authorFilter == null) ? "---" : authorFilter;

        //TODO would it be select *distinct* to not have duplicates??
        String sqlQuery = "SELECT * FROM Books WHERE isbn = '" + isbnFilter + "' OR title = '" + titleFilter
                + "' OR author = '" + authorFilter + "'";

        StringBuilder builder = new StringBuilder();
        ArrayList<Book> booksFound = new ArrayList<>();

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);

            int counter = 0; //counter to create a list to later be able to select a book
            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int copies = rs.getInt("no_copies");

                Book tempBook = new Book(isbn, title, author, copies, 0, 0);
                booksFound.add(counter, tempBook);
                builder.append(String.format("%d.  %s  %s  %s  COPIES : %d", counter, isbn, title, author, copies));
                counter++;
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //return builder.toString();
        return booksFound;
    }

    /**
     * filter the users by a specified role
     * @param role the role to filter for
     * @return the user_id, name, and role of the filtered users
     */
    public static String getUserListFromRole (String role) {
        String sqlQuery = "SELECT * FROM User WHERE role = '" + role.toUpperCase() + "'";
        StringBuilder builder = new StringBuilder();

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);

            int counter = 0; //counter to create a list to later be able to select a book
            while (rs.next()) {
                int userID = rs.getInt("user_id");
                String name = rs.getString("name");
                String role1 = rs.getString("role");

                builder.append(String.format("%d.  %d  %s  %s", userID, name, role1));
                counter++;
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }

    public static Student getStudentFromId (int userId) {
        String sqlQuery = "SELECT * FROM User WHERE user_id = " + userId + " AND role = 'STUDENT'";
        Student returnedUser = null;

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);

            while (rs.next()) {
                int userID = rs.getInt("user_id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                returnedUser = new Student(userID, name, password);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return returnedUser;
    }

    /**
     * gets the entirety of the information for a specified user
     * @param userId the input user_id
     * @return the user_id, name, role, and password of the input user
     */
    public static String getUserFullInfo (int userId) {
        String sqlQuery = "SELECT * FROM User WHERE user_id = " + userId;
        StringBuilder builder = new StringBuilder();

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);

            while (rs.next()) {
                int userID = rs.getInt("user_id");
                String name = rs.getString("name");
                String role = rs.getString("role");
                String password = rs.getString("password");

                builder.append(String.format("%d.  %d  %s  %s  PASSWORD : %s", userID, name, role, password));
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }

    /**
     * gets a specific book through the isbn
     * @param inputISBN the book's isbn
     * @return the book's information (isbn, title, author, no_copies, borrowed_copies, available_copies)
     */
    public static Book getBookThroughISBN(String inputISBN) {
        String sql = "SELECT * FROM Books WHERE isbn = ?";
        Book book = null; // Return null if not found

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, inputISBN);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String isbn = rs.getString("isbn");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    int totalCopies = rs.getInt("no_copies");
                    int borrowedCopies = rs.getInt("borrowed_books");
                    int availableCopies = rs.getInt("available_copies");

                    book = new Book(isbn, title, author, totalCopies, availableCopies, borrowedCopies);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving book: " + e.getMessage());
        }
        return book;
    }

    public static void updateBookCopies(Book inputBook) {
        String sql = "UPDATE Books SET no_copies = ?, borrowed_books = ? WHERE isbn = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, inputBook.getCopies());
            pstmt.setInt(2, inputBook.getBorrowedCopies());
            pstmt.setString(3, inputBook.getISBN());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Book copy counts updated successfully.");
            } else {
                System.out.println("No book found with the given ISBN.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }

    /**
     * updates the role of a specified user
     * @param userID the user to modify
     * @param newRole the new role of the input user
     */
    public static void updateUserRole (int userID, String newRole) {
        String sqlQuery = "UPDATE user WHERE user_id = " + userID + " SET role = '" + newRole + "'";
        //TODO check if the update statement is correct

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sqlQuery);
            System.out.println("User role update successfully");
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * updates the name of a specified user
     * @param userID the user's id
     * @param newName the new name to update
     */
    public static void updateUserName (int userID, String newName) {
        String sqlQuery = "UPDATE user WHERE user_id = " + userID + " SET name = '" + newName + "'";
        //TODO check if the update statement is correct

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sqlQuery);
            System.out.println("User name update successfully");
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * updates a specified user's password
     * @param userID the input user's id
     * @param newPassword the new password to update
     */
    public static void updateUserPassword (int userID, String newPassword) {
        String sqlQuery = "UPDATE user WHERE user_id = " + userID + " SET password = '" + newPassword + "'";
        //TODO check if the update statement is correct

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sqlQuery);
            System.out.println("User password update successfully");
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void removeBookCopies (String ISBN, int newNumOfCopies) {
        String sqlQuery = "UPDATE Book WHERE isbn = '" + ISBN + "' SET copies = " + newNumOfCopies;
        //TODO check if the update statement is correct

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sqlQuery);
            System.out.println("Copies updated successfully");
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void deleteBookByIsbn(String isbn) {
        String sql = "DELETE FROM Books WHERE isbn = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Book with ISBN " + isbn + " deleted successfully.");
            } else {
                System.out.println("No book found with ISBN " + isbn + ".");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
    }

    public static void deleteFromBorrowedBooks(int userId, String isbn) {
        String sql = "DELETE FROM borrowedBooks WHERE user_id = ? AND isbn = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, isbn);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting from borrowedBooks: " + e.getMessage());
        }
    }

    public static User findUserById(int id) {
        String sql = "SELECT * FROM User WHERE user_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {  // Ensure ResultSet is closed after use
                if (rs.next()) {
                    String name = rs.getString("name");
                    String role = rs.getString("role");
                    String password = rs.getString("password");

                    if (role.equalsIgnoreCase("student")) { // verifies if it's a student user
                        return new Student(id, name, password);
                    } else if (role.equalsIgnoreCase("librarian")) { // verifies if it's a librarian user
                        return new Librarian(id, name, password);
                    } else {
                        System.out.println("Unknown role for user.");
                        return null;
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error with ResultSet: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
        return null;
    }


    /**
     * gets the borrowed books for a specified user
     * @param userID the input user
     * @return an array of borrowed book's information
     */
    public static ArrayList<String> getUserBorrowedBooks(int userID) {
        String sql = "SELECT * FROM BorrowedBooks WHERE user_id = ? AND return_status != 'RETURNED'";
        ArrayList<String> borrowedBooksList = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);  // Set the user_id in the prepared statement

            try (ResultSet rs = pstmt.executeQuery()) {  // Ensure ResultSet is closed
                while (rs.next()) {
                    int borrowedBookId = rs.getInt("borrowedBook_id");
                    int userId = rs.getInt("user_id");
                    String isbn = rs.getString("isbn");
                    String borrowDate = rs.getString("borrow_date");
                    String returnDate = rs.getString("return_date");
                    String returnStatus = rs.getString("return_status");

                    borrowedBooksList.add(String.format("Borrowed Book ID: %d%nUser ID: %d%nISBN: %s%nBorrow Date: %s%n" +
                                    "Return Date: %s%nReturn Status: %s%n",
                            borrowedBookId, userId, isbn, borrowDate, returnDate, returnStatus));
                }
            } catch (SQLException e) {
                System.out.println("Error with ResultSet: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Error getting borrowed books: " + e.getMessage());
        }

        return borrowedBooksList;
    }

    /**
     * selects all available books
     * @return books with their isbn, title, author, no of copies total, no of borrowed copies, and no of copies available
     */
    public static String selectAllAvailableBooks() {
        String sql = "SELECT * FROM Books WHERE available_copies > 0";
        StringBuilder builder = new StringBuilder();

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int noCopies = rs.getInt("no_copies");
                int borrowedBooks = rs.getInt("borrowed_books");
                int booksAvailable = noCopies - borrowedBooks;

                builder.append(String.format("%s  %s,  %s  COPIES: %d  [BORROWED: %d  AVAILABLE: %d]%n",
                        isbn, title, author, noCopies, borrowedBooks, booksAvailable));
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }

    /**
     * selects all available books
     * @return books with their isbn, title, author, no of copies total, no of borrowed copies, and no of copies available
     */
    public static String selectLastInsertedStudent() {
        String sqlQuery = """
                SET @last_id = LAST_INSERT_ID();
                SELECT * FROM Users WHERE user_id = @last_id;
                """;

        StringBuilder builder = new StringBuilder();

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);

            while (rs.next()) {
                int userID = rs.getInt("user_id");
                String name = rs.getString("name");
                String role = rs.getString("role");
                String password = rs.getString("password");

                builder.append(String.format("%d.  %d  %s  %s  PASSWORD : %s", userID, name, role, password));
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }



        //Testing connectivity
    public static void main(String[] args) {

        try {
            Connection conn = connect();
            if (conn != null) {
                System.out.println("Connection to SQLite has been established!");
            }
        }
        catch (Exception e) {
            System.out.println("Failed to connect: " + e.getMessage());
        }

//        createBooksTable();
//        dropTable("BorrowedBooks");

//        insertIntoBooks("9781566199094", "Alice in Wonderland", "Lewis Carrol", 10, 0, 10);
        System.out.println(selectAllBooks());

//        dropTable("borrowed_books");
    }

}
