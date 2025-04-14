package org.nicolas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
