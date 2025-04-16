package org.nicolas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

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
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createBooksTable() {
        // ISBN | title | author | no_copies | borrowed_books | available_copies
        String booksTable = """
        CREATE TABLE IF NOT EXISTS Books (
            isbn TEXT PRIMARY KEY,
            title TEXT NOT NULL,
            author TEXT NOT NULL,
            no_copies INTEGER NOT NULL,
            borrowed_books INTEGER NOT NULL,
            available_copies INTEGER NOT NULL
        );
        """;
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            //execute the Create table statement
            stmt.execute(booksTable);
            System.out.println("\nBooks Table created successfully.\n");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

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
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //MODIFY TABLES/////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void addColumn(String tableName, String columnName, String columnType) {
        String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType;

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql); // Alter the table to add a new column
            System.out.println("Column " + columnName + " added successfully to table " + tableName + ".");
        } catch (SQLException e) {
            System.out.println("Failed to add column: " + e.getMessage());
        }
    }

    public static void dropTable(String tableName) {

        String sql = "DROP TABLE IF EXISTS " + tableName;

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql); // Drop the table
            System.out.println("Table " + tableName + " dropped successfully.");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //INSERT STATEMENTS/////////////////////////////////////////////////////////////////////////////////////////////////

    public static void insertIntoUser(String name, String role, String password) {
        String sql = "INSERT INTO user VALUES(?, ?, ?)"; //Insert query with '?' placeholders

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setString(3, password);
            pstmt.executeUpdate(); //execute insert
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertIntoBooks(String isbn, String title, String author,
                                       int no_Copies, int borrowed_Books, int available_Copies) {

        String sql = "INSERT INTO books VALUES(?, ?, ?, ?, ?, ?)"; //Insert query with '?' placeholders

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, isbn);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setInt(4, no_Copies);
            pstmt.setInt(5, borrowed_Books);
            pstmt.setInt(6, available_Copies);
            pstmt.executeUpdate(); //execute insert

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertIntoBorrowedBooks(String borrowedBookID, String userID, String isbn,
                                               Date borrow_date, Date return_date, String return_status) {
        String sql = "INSERT INTO borrowedBooks VALUES(?, ?, ?, ?, ?, ?)"; //Insert query with '?' placeholders

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, borrowedBookID);
            pstmt.setString(2, userID);
            pstmt.setString(3, isbn);
            pstmt.setString(4, borrow_date.toString());
            pstmt.setString(5, return_date.toString());
            pstmt.setString(6, return_status);
            pstmt.executeUpdate(); //execute insert
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    //SELECT * STATEMENTS///////////////////////////////////////////////////////////////////////////////////////////////

    public static String selectAllUsers() {
        String sql = "SELECT * FROM user";
        StringBuilder builder = new StringBuilder();
        //using StringBuilder to build (or append) String

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
//                String password = rs.getString("password"); //To be decided
                builder.append(String.format("ID: %d%n, Name: %s%n", id, name));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }

    public static String selectAllBooks() {
        String sql = "SELECT * FROM Books";
        StringBuilder builder = new StringBuilder();
        //using StringBuilder to build (or append) String

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

                builder.append(String.format("ISBN: %s%nTitle: %s%nAuthor: %s%nNumber of copies: %d%n" +
                        "Borrowed Books: %d%nAvailable Books: %d%n",isbn, title, author, noCopies, borrowedBooks, booksAvailable));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }

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
                        "Return Date: %s%nReturn Status: %s%n",borrowedBookId, userId, isbn, borrowDate, returnDate, returnStatus));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }

    //SPECIALIZED STATEMENTS////////////////////////////////////////////////////////////////////////////////////////////

    public static String getFilteredBooks (String isbnFilter, String titleFilter, String authorFilter) {
        String sqlQuery = "SELECT * FROM Books WHERE isbn = '" + isbnFilter + "' OR title = '" + titleFilter
                + "' OR author = '" + authorFilter + "'";
        //TODO check in the library system and implement a value for null strings

        StringBuilder builder = new StringBuilder();

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

                builder.append(String.format("%d.  %s  %s  %s  COPIES : %d", counter, isbn, title, author, copies));
                counter++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }

    public static String getUserListFromRole (String role) {
        String sqlQuery = "SELECT * FROM user WHERE role = '" + role.toUpperCase() + "'";
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return builder.toString();
    }



    //Testing connectivity
    public static void main(String[] args) {

//        try {
//            Connection conn = connect();
//            if (conn != null) {
//                System.out.println("Connection to SQLite has been established!");
//            }
//        }
//        catch (Exception e) {
//            System.out.println("Failed to connect: " + e.getMessage());
//        }

//        createBooksTable();
//        dropTable("BorrowedBooks");

//        insertIntoBooks("9781566199094", "Alice in Wonderland", "Lewis Carrol", 10, 0, 10);
        System.out.println(selectAllBooks());

//        dropTable("borrowed_books");
    }
}
