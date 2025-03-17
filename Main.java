import java.io.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String inputDir = "input";
        String outputDir = "output";

        // Iterate through each file in the input directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(inputDir))) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    String inputFile = entry.toString();
                    String outputFile = outputDir + "/" + entry.getFileName().toString().replaceFirst("[.][^.]+$", "") + ".hack";
                    File assembly = new File(inputFile);
                    File binary = new File(outputFile);

                    // Create Assembler object
                    Assembler assembler = new Assembler(assembly, binary);
                    // Run first pass
                    assembler.firstPass();
                    // Run second pass
                    assembler.secondPass();
                }
            }
        } catch (IOException | DirectoryIteratorException ex) {
            System.err.println("Error processing files: " + ex.getMessage());
        }
    }
}