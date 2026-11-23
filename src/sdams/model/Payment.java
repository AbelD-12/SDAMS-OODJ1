package sdams.model;

import sdams.enums.PaymentStatus;
import sdams.util.Storable;

/**
 * A payment made by a student. Stored in data/payments.txt as:
 * paymentId|studentId|bookingId|amount|method|status|date
 */
public class Payment implements Storable {

    private String id;
    private String studentId;
    private String bookingId;
    private double amount;
    private String method;   // CASH, CARD, ONLINE ... kept as text
    private PaymentStatus status;
    private String date;     // written as yyyy-MM-dd, e.g. 2026-01-15

    /**
     * Creates a payment.
     *
     * @param id         payment ID, e.g. PAY001
     * @param studentId  ID of the student who paid
     * @param bookingId  ID of the booking that was paid
     * @param amount     amount paid
     * @param method     how it was paid (CASH, CARD, ONLINE ...)
     * @param status     PAID, PENDING or REFUNDED
     * @param date       payment date as yyyy-MM-dd
     */
    public Payment(String id, String studentId, String bookingId,
                   double amount, String method,
                   PaymentStatus status, String date) {

        this.id = id;
        this.studentId = studentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.date = date;
    }

    /** Returns the payment ID. */
    @Override
    public String getId() {
        return id;
    }

    /** Returns the ID of the student who paid. */
    public String getStudentId() {
        return studentId;
    }

    /** Returns the ID of the booking that was paid. */
    public String getBookingId() {
        return bookingId;
    }

    /** Returns the amount paid. */
    public double getAmount() {
        return amount;
    }

    /** Returns the payment method as text. */
    public String getMethod() {
        return method;
    }

    /** Returns the payment status (PAID, PENDING or REFUNDED). */
    public PaymentStatus getStatus() {
        return status;
    }

    /** Returns the payment date as yyyy-MM-dd. */
    public String getDate() {
        return date;
    }

    /** Converts this payment into one line for payments.txt. */
    @Override
    public String toFileString() {
        return id + "|" +
                studentId + "|" +
                bookingId + "|" +
                amount + "|" +
                method + "|" +
                status + "|" +
                date;
    }

    /**
     * Builds a Payment from one line of payments.txt.
     *
     * @param line a line with 7 values separated by |
     * @return the Payment described by the line
     * @throws IllegalArgumentException if the line is not valid
     */
    public static Payment fromFileString(String line) {

        String[] parts = line.split("\\|", -1);

        if (parts.length != 7) {
            throw new IllegalArgumentException(
                    "Invalid payment record: " + line
            );
        }

        String id = parts[0].trim();
        String studentId = parts[1].trim();
        String bookingId = parts[2].trim();
        double amount = Double.parseDouble(parts[3].trim());
        String method = parts[4].trim();
        PaymentStatus status = PaymentStatus.valueOf(parts[5].trim());
        String date = parts[6].trim();

        return new Payment(id, studentId, bookingId,
                amount, method, status, date);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", studentId='" + studentId + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", date='" + date + '\'' +
                '}';
    }
}