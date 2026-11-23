package sdams.util;

import java.util.List;

public class IdGenerator {

    private IdGenerator() {
        // Utility class
    }

    public static String next(String prefix, String fileName) {

        List<String> lines = FileHandler.readLines(fileName);

        int highest = 0;

        for (String line : lines) {

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\|");

            if (parts.length == 0) {
                continue;
            }

            String id = parts[0];

            if (!id.startsWith(prefix)) {
                continue;
            }

            try {
                String numberPart = id.substring(prefix.length());
                int number = Integer.parseInt(numberPart);

                if (number > highest) {
                    highest = number;
                }

            } catch (NumberFormatException e) {
                // Ignore invalid ID
            }
        }

        return String.format("%s%03d", prefix, highest + 1);
    }
}
