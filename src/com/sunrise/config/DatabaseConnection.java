package com.sunrise.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/sunrise_dental_db?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";


    private static final String PASSWORD = "Atheef@123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}