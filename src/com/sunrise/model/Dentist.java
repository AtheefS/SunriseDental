package com.sunrise.model;

public class Dentist {

    private int dentistId;
    private String dentistName;

    public Dentist() {
    }

    public Dentist(int dentistId, String dentistName) {
        this.dentistId = dentistId;
        this.dentistName = dentistName;
    }

    public Dentist(String dentistName) {
        this.dentistName = dentistName;
    }

    public int getDentistId() {
        return dentistId;
    }

    public void setDentistId(int dentistId) {
        this.dentistId = dentistId;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }
}
