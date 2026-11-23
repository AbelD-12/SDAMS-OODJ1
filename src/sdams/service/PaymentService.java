package sdams.service;

import sdams.model.Payment;

/**
 * Loads and saves payments from data/payments.txt.
 * getAll(), getById(), add(), update() and delete() come from BaseService.
 */
public class PaymentService extends BaseService<Payment> {

    @Override
    protected String getFileName() {
        return "data/payments.txt";
    }

    @Override
    protected Payment parse(String line) {
        return Payment.fromFileString(line);
    }
}