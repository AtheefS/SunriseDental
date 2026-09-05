package com.sunrise.dao;

import com.sunrise.config.DatabaseConnection;
import com.sunrise.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AppointmentDAO {

    public boolean addAppointment(Appointment appointment) {

        String sql = """
                INSERT INTO appointments
                (appointment_number, patient_id, dentist_id, treatment_id,
                 appointment_date, appointment_time, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, appointment.getAppointmentNumber());
            statement.setInt(2, appointment.getPatientId());
            statement.setInt(3, appointment.getDentistId());
            statement.setInt(4, appointment.getTreatmentId());

            statement.setDate(
                    5,
                    java.sql.Date.valueOf(
                            appointment.getAppointmentDate()
                    )
            );

            statement.setTime(
                    6,
                    java.sql.Time.valueOf(
                            appointment.getAppointmentTime()
                    )
            );

            statement.setString(7, appointment.getStatus());

            statement.executeUpdate();

            return true;

        } catch (Exception e) {

            System.out.println("Unable to create appointment.");
            e.printStackTrace();

            return false;
        }
    }


    // Find appointment by appointment number
    public Appointment findByAppointmentNumber(
            String appointmentNumber) {

        String sql = """
                SELECT appointment_id,
                       appointment_number,
                       patient_id,
                       dentist_id,
                       treatment_id,
                       appointment_date,
                       appointment_time,
                       status
                FROM appointments
                WHERE appointment_number = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, appointmentNumber);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                Appointment appointment =
                        new Appointment();

                appointment.setAppointmentId(
                        resultSet.getInt("appointment_id")
                );

                appointment.setAppointmentNumber(
                        resultSet.getString(
                                "appointment_number")
                );

                appointment.setPatientId(
                        resultSet.getInt("patient_id")
                );

                appointment.setDentistId(
                        resultSet.getInt("dentist_id")
                );

                appointment.setTreatmentId(
                        resultSet.getInt("treatment_id")
                );

                appointment.setAppointmentDate(
                        resultSet.getDate("appointment_date")
                                .toLocalDate()
                );

                appointment.setAppointmentTime(
                        resultSet.getTime("appointment_time")
                                .toLocalTime()
                );

                appointment.setStatus(
                        resultSet.getString("status")
                );

                return appointment;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }


  // Search appointment with complete patient,
// dentist and treatment details
public Appointment getAppointmentByNumber(String appointmentNumber) {

    String sql = """
            SELECT a.appointment_id,
                   a.appointment_number,
                   p.patient_name,
                   p.address,
                   p.contact_number,
                   d.dentist_name,
                   t.treatment_id,
                   t.treatment_name,
                   a.appointment_date,
                   a.appointment_time,
                   a.status
            FROM appointments a
            JOIN patients p
                ON a.patient_id = p.patient_id
            JOIN dentists d
                ON a.dentist_id = d.dentist_id
            JOIN treatments t
                ON a.treatment_id = t.treatment_id
            WHERE a.appointment_number = ?
            """;

    try (Connection connection =
                 DatabaseConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setString(1, appointmentNumber);

        ResultSet resultSet =
                statement.executeQuery();

        if (resultSet.next()) {

            Appointment appointment =
                    new Appointment();

            appointment.setAppointmentId(
                    resultSet.getInt("appointment_id")
            );

            appointment.setAppointmentNumber(
                    resultSet.getString(
                            "appointment_number")
            );

            appointment.setPatientName(
                    resultSet.getString("patient_name")
            );

            appointment.setAddress(
                    resultSet.getString("address")
            );

            appointment.setContactNumber(
                    resultSet.getString("contact_number")
            );

            appointment.setDentistName(
                    resultSet.getString("dentist_name")
            );

            appointment.setTreatmentId(
                    resultSet.getInt("treatment_id")
            );

            appointment.setTreatmentName(
                    resultSet.getString("treatment_name")
            );

            appointment.setAppointmentDate(
                    resultSet.getDate("appointment_date")
                            .toLocalDate()
            );

            appointment.setAppointmentTime(
                    resultSet.getTime("appointment_time")
                            .toLocalTime()
            );

            appointment.setStatus(
                    resultSet.getString("status")
            );

            return appointment;
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return null;
}
}