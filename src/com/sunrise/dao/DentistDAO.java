package com.sunrise.dao;

import com.sunrise.config.DatabaseConnection;
import com.sunrise.model.Dentist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO {

    public List<Dentist> getAllDentists() {

        List<Dentist> dentists = new ArrayList<>();

        String sql = """
                SELECT dentist_id, dentist_name
                FROM dentists
                ORDER BY dentist_name
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Dentist dentist = new Dentist(
                        resultSet.getInt("dentist_id"),
                        resultSet.getString("dentist_name")
                );

                dentists.add(dentist);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dentists;
    }
}