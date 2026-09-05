package com.sunrise.dao;

import com.sunrise.config.DatabaseConnection;
import com.sunrise.model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PatientDAO {

    public int addPatient(Patient patient) {

        String sql = """
                INSERT INTO patients
                (patient_name, address, contact_number)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, patient.getPatientName());
            statement.setString(2, patient.getAddress());
            statement.setString(3, patient.getContactNumber());

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();

            if (keys.next()) {
                return keys.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}