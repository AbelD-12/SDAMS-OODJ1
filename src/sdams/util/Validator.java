package sdams.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validator {

    private Validator() {
        // Utility class
    }

    public static void requireNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(
                    fieldName + " cannot be empty."
            );
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        return Pattern.matches(regex, email);
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }

        return phone.matches("\\d{10,11}");
    }

    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    public static boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }

        try {
            LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFutureOrToday(LocalDate date) {
        if (date == null) {
            return false;
        }

        return !date.isBefore(LocalDate.now());
    }

    public static boolean isIntInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean noPipe(String value) {
        if (value == null) {
            return true;
        }

        if (value.contains("|")) {
            throw new InvalidInputException(
                    "Input cannot contain the pipe character (|)."
            );
        }

        return true;
    }
}