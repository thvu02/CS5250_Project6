import java.io.*;
import java.util.*;

public class Main {
    public static ArrayList<File> getFiles(File dir) {
        File[] files = dir.listFiles();
        ArrayList<File> fileList = new ArrayList<File>();
        for (File f : files)
            if (f.getName().endsWith(".vm"))
                fileList.add(f);
        return fileList;
    }
    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            System.err.println("Usage: java Main.java <input file or directory>");
        else {
            File input = new File(args[0]);
            CodeWriter codeWriter;
            ArrayList<File> vmFiles = new ArrayList<File>();
            String outputPath = "";

            if (input.isDirectory()) {
                vmFiles = getFiles(input);
                if (vmFiles.isEmpty())
                    throw new IllegalArgumentException("No vm files found in the directory.");
                outputPath = input.getAbsolutePath() + File.separator + input.getName() + ".asm";
            } else if (input.isFile()) {
                if (!input.getName().endsWith(".vm")) {
                    throw new IllegalArgumentException("Not a vm file");
                }
                vmFiles.add(input);
                outputPath = input.getAbsolutePath().replace(".vm", ".asm");
            }
            
            File output = new File(outputPath);
            codeWriter = new CodeWriter(output);
            
            // if there is a Sys.vm file, we need to write the bootstrap code
            boolean fileExists = false;
            for (File f : vmFiles) {
                if (f.getName().equals("Sys.vm")) {
                    fileExists = true;
                    break;
                }
            }
            if (fileExists) {
                // Write the bootstrap code only if Sys.vm is present
                codeWriter.writeBootstrap(); 
            }

            for (File f : vmFiles) {
                codeWriter.setFileName(f);
                Parser parser = new Parser(f);
                String type = "";
                

                while (parser.hasMoreCommands()) {
                    parser.advance();
                    type = parser.commandType();
                    switch (type) {
                        case "arithmetic":
                            codeWriter.writeArithmetic(parser.arg1()); break;
                        case "push":
                        case "pop":
                            codeWriter.writePushPop(type, parser.arg1(), parser.arg2()); break;
                        case "label":
                            codeWriter.writeLabel(parser.arg1()); break;
                        case "goto":
                            codeWriter.writeGoto(parser.arg1()); break;
                        case "if-goto":
                            codeWriter.writeIf(parser.arg1()); break;
                        case "function":
                            codeWriter.writeFunction(parser.arg1(), parser.arg2()); break;
                        case "call":
                            codeWriter.writeCall(parser.arg1(), parser.arg2()); break;
                        case "return":
                            codeWriter.writeReturn(); break;
                    }
                }
            }
            System.out.println("Output written to: " + output.getAbsolutePath());
            codeWriter.close();
        }
    }
}