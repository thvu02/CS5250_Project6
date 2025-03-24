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

            if (input.isDirectory()) {
                vmFiles = getFiles(input);
                if (vmFiles.isEmpty())
                    throw new IllegalArgumentException("No vm files found in the directory.");
            } else if (input.isFile()) {
                if (!input.getName().endsWith(".vm")) {
                    throw new IllegalArgumentException("Not a vm file");
                }
                vmFiles.add(input);
            }

            for (File f : vmFiles) {
                Parser parser = new Parser(f);
                String type = "";

                File output = new File(f.getAbsolutePath().replace(".vm", ".asm"));
                codeWriter = new CodeWriter(output);

                while (parser.hasMoreCommands()) {
                    parser.advance();
                    type = parser.commandType();
                    if (type == "arithmetic")
                        codeWriter.writeArithmetic(parser.arg1());
                    else if (type == "push" || type == "pop")
                        codeWriter.writePushPop(type, parser.arg1(), parser.arg2());
                }
                codeWriter.close();
                System.out.println("Output written to: " + output.getAbsolutePath());
            }
        }
    }
}
