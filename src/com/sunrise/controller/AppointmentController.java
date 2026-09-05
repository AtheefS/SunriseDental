package com.sunrise.controller;

import com.sunrise.service.AppointmentService;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController() {
        this.appointmentService = new AppointmentService();
    }

    public String registerAppointment(
            String patientName,
            String address,
            String contactNumber,
            int dentistId,
            int treatmentId,
            LocalDate appointmentDate,
            LocalTime appointmentTime) {

        return appointmentService.registerAppointment(
                patientName,
                address,
                contactNumber,
                dentistId,
                treatmentId,
                appointmentDate,
                appointmentTime
        );
    }
}