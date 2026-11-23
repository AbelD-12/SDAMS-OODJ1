package sdams.service;

import sdams.enums.PaymentStatus;
import sdams.enums.Role;
import sdams.model.Payment;
import sdams.model.RevenueRecord;
import sdams.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    private UserService userService = new UserService();
    private PaymentService paymentService = new PaymentService();

    private String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    // ------------------------------------------------------ Revenue

    // One record for each month of the given year
    public List<RevenueRecord> getMonthlyRevenue(int year) {

        List<RevenueRecord> records = new ArrayList<>();
        List<Payment> paid = getPaidPayments();

        for (int month = 1; month <= 12; month++) {

            String prefix = year + "-" + twoDigits(month);

            int registrations = 0;
            double total = 0;

            for (Payment payment : paid) {

                if (dateOf(payment).startsWith(prefix)) {
                    registrations++;
                    total += amountOf(payment);
                }
            }

            String period = monthNames[month - 1] + " " + year;

            records.add(new RevenueRecord(period, registrations, total));
        }

        return records;
    }

    // One record for each year that has PAID payments (oldest first)
    public List<RevenueRecord> getYearlyRevenue() {

        List<RevenueRecord> records = new ArrayList<>();
        List<Payment> paid = getPaidPayments();

        // find every year that has a payment
        List<String> years = new ArrayList<>();

        for (Payment payment : paid) {

            String year = dateOf(payment).substring(0, 4);

            if (!years.contains(year)) {
                years.add(year);
            }
        }

        Collections.sort(years);

        for (String year : years) {

            int registrations = 0;
            double total = 0;

            for (Payment payment : paid) {

                if (dateOf(payment).startsWith(year)) {
                    registrations++;
                    total += amountOf(payment);
                }
            }

            records.add(new RevenueRecord(year, registrations, total));
        }

        return records;
    }

    // ---------------------------------------------------------- Staff

    // Number of staff for each of the 4 staff roles
    public Map<Role, Integer> countStaffByRole() {

        Map<Role, Integer> counts = new HashMap<>();

        counts.put(Role.ADMINISTRATOR, 0);
        counts.put(Role.INSTRUCTOR, 0);
        counts.put(Role.FACILITIES_OFFICER, 0);
        counts.put(Role.ATTENDANCE_OFFICER, 0);

        for (User user : userService.getAll()) {

            if (isStaff(user.getRole())) {
                counts.put(user.getRole(), counts.get(user.getRole()) + 1);
            }
        }

        return counts;
    }

    // Staff filtered by role (null = all roles) and by name
    public List<User> searchStaff(Role roleOrNull, String name) {

        List<User> results = new ArrayList<>();

        if (name == null) {
            name = "";
        }

        String searchName = name.trim().toLowerCase();

        for (User user : userService.getAll()) {

            if (!isStaff(user.getRole())) {
                continue;
            }

            if (roleOrNull != null && user.getRole() != roleOrNull) {
                continue;
            }

            if (user.getName().toLowerCase().contains(searchName)) {
                results.add(user);
            }
        }

        return results;
    }

    // ------------------------------------------------------- Helpers

    // Only PAID payments count. PENDING and REFUNDED are left out.
    private List<Payment> getPaidPayments() {

        List<Payment> paid = new ArrayList<>();

        for (Payment payment : paymentService.getAll()) {

            if (payment.getStatus() == PaymentStatus.PAID) {
                paid.add(payment);
            }
        }

        return paid;
    }

    // ---- If Member 4's Payment class uses other getter names,
    // ---- these two methods are the ONLY lines you need to change.

    // the payment amount, e.g. 150.0
    private double amountOf(Payment payment) {
        return payment.getAmount();
    }

    // the payment date as text, like "2026-01-15"
    // (toString() works for both a String and a LocalDate)
    private String dateOf(Payment payment) {
        return payment.getDate().toString();
    }

    private boolean isStaff(Role role) {

        return role == Role.ADMINISTRATOR
                || role == Role.INSTRUCTOR
                || role == Role.FACILITIES_OFFICER
                || role == Role.ATTENDANCE_OFFICER;
    }

    // 5 -> "05", 11 -> "11"
    private String twoDigits(int number) {

        if (number < 10) {
            return "0" + number;
        }

        return "" + number;
    }
}