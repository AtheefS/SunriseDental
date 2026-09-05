package com.sunrise.dao;

import com.sunrise.config.DatabaseConnection;
import com.sunrise.model.Treatment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAO {

    public List<Treatment> getAllTreatments() {

        List<Treatment> treatments = new ArrayList<>();

        String sql = """
                SELECT treatment_id, treatment_name, treatment_cost
                FROM treatments
                ORDER BY treatment_name
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Treatment treatment = new Treatment(
                        resultSet.getInt("treatment_id"),
                        resultSet.getString("treatment_name"),
                        resultSet.getBigDecimal("treatment_cost")
                );

                treatments.add(treatment);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return treatments;
    }
}