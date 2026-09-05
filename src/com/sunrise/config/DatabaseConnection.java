package com.sunrise.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String HOST =
            System.getenv().getOrDefault("MYSQLHOST", "localhost");

    private static final String PORT =
            System.getenv().getOrDefault("MYSQLPORT", "3306");

    private static final String DATABASE =
            System.getenv().getOrDefault("MYSQLDATABASE", "sunrise_dental_db");

    private static final String USER =
            System.getenv().getOrDefault("MYSQLUSER", "root");

    private static final String PASSWORD =
            System.getenv("MYSQLPASSWORD");

    public static Connection getConnection() throws Exception {

        if (PASSWORD == null || PASSWORD.isEmpty()) {
            throw new Exception("MYSQLPASSWORD environment variable is not set.");
        }

        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                + "?useSSL=false&serverTimezone=UTC";

        return DriverManager.getConnection(url, USER, PASSWORD);
    }
}