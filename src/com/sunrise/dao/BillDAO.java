package com.sunrise.dao;

import com.sunrise.config.DatabaseConnection;
import com.sunrise.model.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BillDAO {

    public boolean addBill(Bill bill) {

        String sql = """
                INSERT INTO bills
                (appointment_id, consultation_fee, treatment_cost, total_amount)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bill.getAppointmentId());
            statement.setBigDecimal(2, bill.getConsultationFee());
            statement.setBigDecimal(3, bill.getTreatmentCost());
            statement.setBigDecimal(4, bill.getTotalAmount());

            statement.executeUpdate();

            return true;

        } catch (Exception e) {
            System.out.println("Unable to create bill.");
            e.printStackTrace();
            return false;
        }
    }

    public Bill findByAppointmentId(int appointmentId) {

        String sql = """
                SELECT bill_id, appointment_id,
                       consultation_fee, treatment_cost,
                       total_amount, bill_date
                FROM bills
                WHERE appointment_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, appointmentId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                return new Bill(
                        resultSet.getInt("bill_id"),
                        resultSet.getInt("appointment_id"),
                        resultSet.getBigDecimal("consultation_fee"),
                        resultSet.getBigDecimal("treatment_cost"),
                        resultSet.getBigDecimal("total_amount"),
                        resultSet.getTimestamp("bill_date").toLocalDateTime()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}