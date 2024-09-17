package arlot.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReadWrite {

    public static void main(String[] args) {
        // Change this path to your file path
        String filePath = "numbers.txt";
        Path tempFilePath = Path.of("temp_numbers.txt");

        try {
            // Read lines from the file
            List<String> lines = readFile(filePath);

            // Write lines to the file
            writeFile(tempFilePath, lines);

            // Replace the original file with the temporary file
            Files.move(tempFilePath, Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Processing completed. Output written to " + filePath);
        } catch (IOException e) {
            System.err.println("An error occurred while processing the file: " + e.getMessage());
        }
    }

    // Method to read lines from a file
    public static List<String> readFile(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            return lines.collect(Collectors.toList());
        }
    }

    // Method to write lines to a file
    public static void writeFile(Path filePath, List<String> lines) throws IOException {
        Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
