package sdams.service;

import sdams.model.Payment;

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