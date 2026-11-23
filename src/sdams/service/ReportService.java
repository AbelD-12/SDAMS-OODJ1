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

/**
 * Builds the Manager's reports: monthly and yearly revenue
 * (from PaymentService) and the staff report (from UserService).
 */
public class ReportService {

    private UserService userService = new UserService();
    private PaymentService paymentService = new PaymentService();

    private String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    // ------------------------------------------------------ Revenue

    /**
     * Revenue for every month of a year (always 12 records).
     * Only PAID payments are counted.
     *
     * @param year the year, e.g. 2026
     * @return one RevenueRecord for each month, January to December
     */
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

    /**
     * Revenue for one month only (overloaded version of the method above).
     *
     * @param year   the year, e.g. 2026
     * @param month  the month, 1 (January) to 12 (December)
     * @return the RevenueRecord of that month
     */
    public RevenueRecord getMonthlyRevenue(int year, int month) {

        return getMonthlyRevenue(year).get(month - 1);
    }

    /**
     * Revenue for every year that has PAID payments, oldest year first.
     *
     * @return one RevenueRecord for each year
     */
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

    /**
     * Counts the staff of each staff role.
     * Managers and students are not staff, so they are not counted.
     *
     * @return a map from role to the number of users with that role
     */
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

    /**
     * Staff filtered by role and by name.
     *
     * @param roleOrNull  the role to show, or null for all staff roles
     * @param name        part of the name to look for ("" = any name)
     * @return the staff that match both filters
     */
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

    /**
     * Staff filtered by role only (overloaded version of the method above).
     *
     * @param roleOrNull  the role to show, or null for all staff roles
     * @return the staff with that role
     */
    public List<User> searchStaff(Role roleOrNull) {

        return searchStaff(roleOrNull, "");
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

    // the payment amount, e.g. 150.0
    private double amountOf(Payment payment) {
        return payment.getAmount();
    }

    // the payment date as text, like "2026-01-15"
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