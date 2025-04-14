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

    public static void createTables() {
        String userTable = """
        CREATE TABLE IF NOT EXISTS User (
            user_id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            role TEXT NOT NULL,
            password TEXT NOT NULL
        );
        """;

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

        String borrowedBooksTable = """
        CREATE TABLE IF NOT EXISTS BorrowedBooks (
            borrowedBook_id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            book_id TEXT,
            borrow_date TEXT,
            return_date TEXT,
            return_status TEXT,
            FOREIGN KEY(user_id) REFERENCES User(user_id),
            FOREIGN KEY(book_id) REFERENCES Books(isbn)
        );
        """;

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            //execute the Create table statements
            stmt.execute(userTable);
            stmt.execute(booksTable);
            stmt.execute(borrowedBooksTable);
            System.out.println("\nTables created successfully.\n");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

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

    public static void insertIntoUser(String name, String role, String password) {
        String sql = "INSERT INTO user VALUES(?, ?, ?)"; //Insert query with '?' placeholders

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setString(3, password);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
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
    }
}
