package com.sunrise.controller;

import com.sunrise.model.Bill;
import com.sunrise.service.BillingService;

public class BillController {

    private final BillingService billingService;

    public BillController() {
        this.billingService = new BillingService();
    }

    public Bill generateBill(int appointmentId, int treatmentId) {
        return billingService.createBill(
                appointmentId,
                treatmentId
        );
    }
}