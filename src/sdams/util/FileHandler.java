package sdams.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private FileHandler() {
        // Utility class
    }

    public static List<String> readLines(String file) {
        try {
            Path path = Paths.get(file);

            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                return new ArrayList<>();
            }

            return Files.readAllLines(path);

        } catch (IOException e) {
            throw new DataAccessException(
                    "Unable to read file: " + file, e
            );
        }
    }

    public static void writeLines(String file, List<String> lines) {
        try {
            Path path = Paths.get(file);

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            Files.write(path, lines);

        } catch (IOException e) {
            throw new DataAccessException(
                    "Unable to write file: " + file, e
            );
        }
    }

    public static void appendLine(String file, String line) {
        try {
            Path path = Paths.get(file);

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            Files.write(
                    path,
                    (line + System.lineSeparator()).getBytes(),
                    java.nio.file.StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            throw new DataAccessException(
                    "Unable to append to file: " + file, e
            );
        }
    }
}
