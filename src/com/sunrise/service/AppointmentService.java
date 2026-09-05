package com.sunrise.service;

import com.sunrise.dao.AppointmentDAO;
import com.sunrise.dao.PatientDAO;
import com.sunrise.model.Appointment;
import com.sunrise.model.Patient;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentService {

    private final AppointmentDAO appointmentDAO;
    private final PatientDAO patientDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
    }

    public String registerAppointment(
            String patientName,
            String address,
            String contactNumber,
            int dentistId,
            int treatmentId,
            LocalDate appointmentDate,
            LocalTime appointmentTime) {

        // Basic validation
        if (patientName == null || patientName.trim().isEmpty()) {
            return "Patient name is required.";
        }

        if (address == null || address.trim().isEmpty()) {
            return "Address is required.";
        }

        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            return "Contact number is required.";
        }

        if (appointmentDate == null || appointmentTime == null) {
            return "Appointment date and time are required.";
        }

        if (dentistId <= 0 || treatmentId <= 0) {
            return "Please select a valid dentist and treatment.";
        }

        // Create patient
        Patient patient = new Patient(
                patientName.trim(),
                address.trim(),
                contactNumber.trim()
        );

        int patientId = patientDAO.addPatient(patient);

        if (patientId == -1) {
            return "Unable to register patient.";
        }

        // Generate unique appointment number
        String appointmentNumber =
                "APT-" + System.currentTimeMillis();

   Appointment appointment = new Appointment();

appointment.setAppointmentNumber(appointmentNumber);
appointment.setPatientId(patientId);
appointment.setDentistId(dentistId);
appointment.setTreatmentId(treatmentId);
appointment.setAppointmentDate(appointmentDate);
appointment.setAppointmentTime(appointmentTime);
appointment.setStatus("BOOKED");
        boolean success = appointmentDAO.addAppointment(appointment);

        if (!success) {
            return "Appointment could not be created. The dentist may already be booked at this time.";
        }

        return appointmentNumber;
    }
}