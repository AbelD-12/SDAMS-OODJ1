package sdams.service;

import sdams.enums.Role;
import sdams.model.RevenueRecord;
import sdams.model.User;
import sdams.util.FileHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    private static final String PAYMENTS_FILE = "data/payments.txt";

    // Position of each value in one line of payments.txt
    // paymentId|studentId|bookingId|amount|method|status|date
    private static final int COL_AMOUNT = 3;
    private static final int COL_STATUS = 5;
    private static final int COL_DATE = 6;

    private UserService userService = new UserService();

    private String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    // ------------------------------------------------------ Revenue

    // One record for each month of the given year
    public List<RevenueRecord> getMonthlyRevenue(int year) {

        List<RevenueRecord> records = new ArrayList<>();
        List<String[]> paid = readPaidPayments();

        for (int month = 1; month <= 12; month++) {

            String prefix = year + "-" + twoDigits(month);

            int registrations = 0;
            double total = 0;

            for (String[] payment : paid) {

                if (payment[COL_DATE].trim().startsWith(prefix)) {
                    registrations++;
                    total += Double.parseDouble(payment[COL_AMOUNT].trim());
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
        List<String[]> paid = readPaidPayments();

        // find every year in the file
        List<String> years = new ArrayList<>();

        for (String[] payment : paid) {

            String year = payment[COL_DATE].trim().substring(0, 4);

            if (!years.contains(year)) {
                years.add(year);
            }
        }

        Collections.sort(years);

        for (String year : years) {

            int registrations = 0;
            double total = 0;

            for (String[] payment : paid) {

                if (payment[COL_DATE].trim().startsWith(year)) {
                    registrations++;
                    total += Double.parseDouble(payment[COL_AMOUNT].trim());
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

    private boolean isStaff(Role role) {

        return role == Role.ADMINISTRATOR
                || role == Role.INSTRUCTOR
                || role == Role.FACILITIES_OFFICER
                || role == Role.ATTENDANCE_OFFICER;
    }

    // Every PAID line of payments.txt, split into its values.
    // PENDING and REFUNDED lines are left out.
    private List<String[]> readPaidPayments() {

        List<String[]> paid = new ArrayList<>();

        for (String line : FileHandler.readLines(PAYMENTS_FILE)) {

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\|");

            // ignore lines that do not have the 7 values
            if (parts.length != 7) {
                continue;
            }

            if (parts[COL_STATUS].trim().equals("PAID")) {
                paid.add(parts);
            }
        }

        return paid;
    }

    // 5 -> "05", 11 -> "11"
    private String twoDigits(int number) {

        if (number < 10) {
            return "0" + number;
        }

        return "" + number;
    }
}