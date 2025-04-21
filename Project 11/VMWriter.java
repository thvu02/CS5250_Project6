import java.io.*;
import java.util.*;

public class VMWriter {
    public static enum SEGMENT {CONST, ARG, LOCAL, STATIC, THIS, THAT, POINTER, TEMP, NONE};
    public static enum COMMAND {ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT};

    private static HashMap<SEGMENT, String> segmentStringHashMap = new HashMap<SEGMENT, String>();
    private static HashMap<COMMAND, String> commandStringHashMap = new HashMap<COMMAND, String>();
    private PrintWriter printWriter;

    static {
        segmentStringHashMap.put(SEGMENT.CONST, "constant");
        segmentStringHashMap.put(SEGMENT.ARG, "argument");
        segmentStringHashMap.put(SEGMENT.LOCAL, "local");
        segmentStringHashMap.put(SEGMENT.STATIC, "static");
        segmentStringHashMap.put(SEGMENT.THAT, "that");
        segmentStringHashMap.put(SEGMENT.THIS, "this");
        segmentStringHashMap.put(SEGMENT.POINTER, "pointer");
        segmentStringHashMap.put(SEGMENT.TEMP, "temp");

        commandStringHashMap.put(COMMAND.ADD, "add");
        commandStringHashMap.put(COMMAND.SUB, "sub");
        commandStringHashMap.put(COMMAND.NEG, "neg");
        commandStringHashMap.put(COMMAND.EQ, "eq");
        commandStringHashMap.put(COMMAND.GT, "gt");
        commandStringHashMap.put(COMMAND.LT, "lt");
        commandStringHashMap.put(COMMAND.AND, "and");
        commandStringHashMap.put(COMMAND.OR, "or");
        commandStringHashMap.put(COMMAND.NOT, "not");
    }

    public VMWriter(File output) {
        try {
            printWriter = new PrintWriter(output);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writePop(SEGMENT segment, int index) {
        writeCommand("pop", segmentStringHashMap.get(segment), String.valueOf(index));
    }

    public void writePush(SEGMENT segment, int index) {
        writeCommand("push", segmentStringHashMap.get(segment), String.valueOf(index));
    }

    public void writeArithmetic(COMMAND command) {
        writeCommand(commandStringHashMap.get(command), "", "");
    }

    public void writeLabel(String label) {
        writeCommand("label", label, "");
    }

    public void writeGoto(String label) {
        writeCommand("goto", label, "");
    }

    public void writeIf(String label) {
        writeCommand("if-goto", label, "");
    }

    public void writeCall(String name, int numberOfArgs) {
        writeCommand("call", name, String.valueOf(numberOfArgs));
    }

    public void writeFunction(String name, int numberOfLocals) {
        writeCommand("function", name, String.valueOf(numberOfLocals));
    }

    public void writeReturn() {
        writeCommand("return", "", "");
    }

    public void writeCommand(String command, String arg1, String arg2) {
        printWriter.print(command + " " + arg1 + " " + arg2 + "\n");
    }

    public void close() {
        printWriter.close();
    }
}
