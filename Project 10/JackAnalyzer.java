import java.util.*;
import java.io.*;

public class JackAnalyzer {

    public static ArrayList<File> getFiles(File directory) {
        File[] files = directory.listFiles();
        ArrayList<File> jackFiles = new ArrayList<File>();
        // return if empty directory
        if (files == null)
            return jackFiles;
        // add all .jack files to the array list
        for (File f:files)
            if (f.getName().endsWith(".jack"))
                jackFiles.add(f);
        return jackFiles;
    }

    public static void main(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("Feed the directory containing .jack files as an argument.");
        
        String inputPath = args[0];
        File input = new File(inputPath);
        String outputPath = "";
        String tokenFileOutputPath = "";
        File output, tokenFile;
        ArrayList<File> jackFiles = new ArrayList<File>();

        if (input.isFile()) {
            String path = input.getAbsolutePath();
            if (!path.endsWith(".jack"))
                throw new IllegalArgumentException("needs to be .jack file.");
            jackFiles.add(input);
        }
        else if (input.isDirectory()) {
            jackFiles = getFiles(input);
            if (jackFiles.isEmpty())
                throw new IllegalArgumentException("no .jack files in the directory.");
        }

        for (File f:jackFiles) {
            outputPath = f.getAbsolutePath().replace(".jack", ".xml");
            tokenFileOutputPath = f.getAbsolutePath().replace(".jack", "T.xml");
            output = new File(outputPath);
            tokenFile = new File(tokenFileOutputPath);
    
            CompilationEngine compilationEngine = new CompilationEngine(f, output, tokenFile);
            compilationEngine.compileClass();
    
            System.out.println("Compiled " + f.getName() + " to " + output.getName() + " and " + tokenFile.getName());
        }
    }
}