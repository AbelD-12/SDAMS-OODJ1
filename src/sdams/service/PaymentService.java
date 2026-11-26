


package sdams.service;

import sdams.enums.*;
import sdams.model.*;
import sdams.util.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Loads payments and handles simulated payment and refund operations. */
public class PaymentService extends BaseService<Payment> {
    @Override
    protected String getFileName() { return "data/payments.txt"; }

    @Override
    protected Payment parse(String line) { return Payment.fromFileString(line); }

    /** Returns the existing payment for a booking, or null. */
    public Payment getByBooking(String bookingId) {
        for (Payment p : getAll()) if (p.getBookingId().equals(bookingId)) return p;
        return null;
    }

    /** Pays for an active booking using the class fee. */
    public Payment pay(String bookingId, PaymentMethod method) {
        if (method == null) throw new InvalidInputException("Select a payment method.");
        Booking b = new BookingService().getById(bookingId);
        if (b == null || b.getStatus() != BookingStatus.BOOKED) {
            throw new InvalidInputException("Active booking not found.");
        }
        ClassSession c = new ClassService().getById(b.getClassId());
        if (c == null || c.getStatus() == ClassStatus.CANCELLED || c.getStatus() == ClassStatus.COMPLETED) {
            throw new InvalidInputException("Class is not available for payment.");
        }
        Payment previous = getByBooking(bookingId);
        if (previous != null && (!previous.getStudentId().equals(b.getStudentId())
                || previous.getStatus() != PaymentStatus.PENDING)) {
            throw new InvalidInputException("This booking already has a final payment record.");
        }
        if (!Double.isFinite(c.getFee()) || c.getFee() < 0) {
            throw new InvalidInputException("Invalid class fee.");
        }
        String id = previous == null ? IdGenerator.next("PAY", getFileName()) : previous.getId();
        // Match the existing Payment constructor and current payments.txt format.
        Payment p = new Payment(id, b.getStudentId(), bookingId, c.getFee(),
                method.name(), PaymentStatus.PAID, LocalDate.now().toString());
        if (previous == null) add(p); else update(p);
        return p;
    }

    /** Appends a record safely even if the existing file has no final newline. */
    @Override
    public void add(Payment p) {
        List<String> lines = FileHandler.readLines(getFileName());
        lines.add(p.toFileString());
        FileHandler.writeLines(getFileName(), lines);
    }

    /** Marks a paid booking's payment as refunded. */
    public void refund(String bookingId) {
        Payment p = getByBooking(bookingId);
        if (p != null && p.getStatus() == PaymentStatus.PAID) {
            update(new Payment(p.getId(), p.getStudentId(), p.getBookingId(), p.getAmount(),
                    p.getMethod(), PaymentStatus.REFUNDED, p.getDate()));
        }
    }

    /** Checks whether a booking has a PAID payment. */
    public boolean isPaid(String bookingId) {
        Payment p = getByBooking(bookingId);
        return p != null && p.getStatus() == PaymentStatus.PAID;
    }

    /** Returns payment records for a student. */
    public List<Payment> getByStudent(String studentId) {
        return filter(studentId, null, null, null);
    }

    /** Filters by optional student ID, inclusive dates and status. */
    public List<Payment> filter(String studentId, LocalDate from, LocalDate to, PaymentStatus status) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidInputException("Start date must not be after end date.");
        }
        List<Payment> results = new ArrayList<>();
        for (Payment p : getAll()) {
            LocalDate date = LocalDate.parse(p.getDate());
            if (studentId != null && !studentId.isBlank() && !studentId.equals(p.getStudentId())) continue;
            if (status != null && status != p.getStatus()) continue;
            if (from != null && date.isBefore(from)) continue;
            if (to != null && date.isAfter(to)) continue;
            results.add(p);
        }
        return results;
    }

    /** Filters payments by student and status. */
    public List<Payment> filter(String studentId, PaymentStatus status) {
        return filter(studentId, null, null, status);
    }
}

