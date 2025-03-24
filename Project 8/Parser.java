import java.io.*;
import java.util.*;

public class Parser {
    private Scanner scanner;
    private String cmd;
    private String argType;
    private String arg1;
    private int arg2;
    private static String[] ARITH_CMDS = new String[] {"add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"};

    public Parser(File input) throws FileNotFoundException {
        argType = ""; arg1 = ""; arg2 = -1;
        scanner = new Scanner(input);
        String line = "";
        String cleaned = "";
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            // handle comments
            int commentStart = line.indexOf("//");
            if (commentStart != -1)
                line = line.substring(0, commentStart);
            line = line.trim();

            // if line is not empty, add it to the cleaned string
            if (!line.isEmpty())
                cleaned += line + "\n";
        }
        scanner = new Scanner(cleaned.trim());
    }

    public boolean hasMoreCommands() {
        return scanner.hasNextLine();
    }

    public void advance() {
        cmd = scanner.nextLine();
        arg1 = ""; arg2 = -1; // reset arguments
        String[] parts = cmd.split(" ");

        // Check if the command is an arithmetic command
        if (Arrays.asList(ARITH_CMDS).contains(parts[0])) {
            argType = "arithmetic";
            arg1 = parts[0];
        }
        else if (parts[0].equals("return")) {
            argType = "return";
            arg1 = parts[0];
        }
        else {
            arg1 = parts[1];
            switch (parts[0]) {
                case "push":
                    argType = "push";
                    break;
                case "pop":
                    argType = "pop";
                    break;
                case "label":
                    argType = "label";
                    break;
                case "goto":
                    argType = "goto";
                    break;
                case "if-goto":
                    argType = "if-goto";
                    break;
                case "function":
                    argType = "function";
                    break;
                case "call":
                    argType = "call";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid command: " + parts[0]);
            }

            if (argType == "push" || argType == "pop" || argType == "function" || argType == "call") {
                try {
                    arg2 = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid index for command: " + parts[0]);
                }
            }
        }
    }

    public String commandType() {
        if (argType != "") {
            return argType;}
        else
            throw new IllegalStateException("No command type found.");
    }

    public String arg1() {
        if (commandType() != "return")
            return arg1;
        else
            throw new IllegalStateException("No argument for return command.");
    }

    public int arg2() {
        if (commandType() != "push" && commandType() != "pop" && commandType() != "function" && commandType() != "call")
            throw new IllegalStateException("No second argument for command type: " + commandType());
        return arg2;
    }
}
