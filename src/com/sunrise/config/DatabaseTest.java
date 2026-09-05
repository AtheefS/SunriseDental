package com.sunrise.config;

import java.sql.Connection;

public class DatabaseTest {

    public static void main(String[] args) {

        try {
            Connection connection = DatabaseConnection.getConnection();

            System.out.println("=================================");
            System.out.println("MySQL CONNECTION SUCCESSFUL!");
            System.out.println("Sunrise Dental database connected.");
            System.out.println("=================================");

            connection.close();

        } catch (Exception e) {
            System.out.println("MySQL CONNECTION FAILED!");
            e.printStackTrace();
        }
    }
}