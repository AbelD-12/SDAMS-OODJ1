package sdams.model;

import sdams.enums.PaymentStatus;
import sdams.util.Storable;

public class Payment implements Storable {

    private String id;
    private String studentId;
    private String bookingId;
    private double amount;
    private String method;   // CASH, CARD, ONLINE ... kept as text
    private PaymentStatus status;
    private String date;     // written as yyyy-MM-dd, e.g. 2026-01-15

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

    @Override
    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

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