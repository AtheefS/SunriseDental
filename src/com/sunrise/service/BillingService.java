package com.sunrise.service;

import com.sunrise.dao.BillDAO;
import com.sunrise.dao.TreatmentDAO;
import com.sunrise.model.Bill;
import com.sunrise.model.Treatment;

import java.math.BigDecimal;
import java.util.List;

public class BillingService {


    private static final BigDecimal CONSULTATION_FEE =
            new BigDecimal("1000.00");

    private final BillDAO billDAO;
    private final TreatmentDAO treatmentDAO;

    public BillingService() {
        this.billDAO = new BillDAO();
        this.treatmentDAO = new TreatmentDAO();
    }

    public BigDecimal calculateTotal(BigDecimal treatmentCost) {

        if (treatmentCost == null || treatmentCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Treatment cost cannot be negative."
            );
        }

        return CONSULTATION_FEE.add(treatmentCost);
    }

    public Bill createBill(int appointmentId, int treatmentId) {

        if (appointmentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid appointment ID."
            );
        }

        if (treatmentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid treatment ID."
            );
        }

        List<Treatment> treatments = treatmentDAO.getAllTreatments();

        for (Treatment treatment : treatments) {

            if (treatment.getTreatmentId() == treatmentId) {

                BigDecimal treatmentCost = treatment.getTreatmentCost();

                BigDecimal total = calculateTotal(treatmentCost);

                Bill bill = new Bill(
                        appointmentId,
                        CONSULTATION_FEE,
                        treatmentCost,
                        total
                );

                if (!billDAO.addBill(bill)) {
                    throw new RuntimeException(
                            "Unable to save bill."
                    );
                }

                return bill;
            }
        }

        throw new IllegalArgumentException(
                "Treatment not found."
        );
    }
}