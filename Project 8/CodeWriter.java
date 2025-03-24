import java.io.*;
import java.util.regex.*;

public class CodeWriter {
    private int arithmeticJumpFlag = 0;
    private int labelCount = 0;
    private BufferedWriter outPrinter;
    private String fileName = "";

    public CodeWriter(File output) throws IOException{
        this.outPrinter = new BufferedWriter(new FileWriter(output));
        fileName = output.getName();
    }

    public void setFileName(File output) throws IOException {
        fileName = output.getName();
    }

    public void writeArithmetic(String command) throws IOException {
        if (command.equals("add")) {
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("AM=M-1\n");
            this.outPrinter.write("D=M\n");
            this.outPrinter.write("A=A-1\n");
            this.outPrinter.write("M=D+M\n");
        } else if (command.equals("sub")) {
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("AM=M-1\n");
            this.outPrinter.write("D=M\n");
            this.outPrinter.write("A=A-1\n");
            this.outPrinter.write("M=M-D\n");
        } else if (command.equals("neg")) {
            this.outPrinter.write("D=0\n");
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("A=M-1\n");
            this.outPrinter.write("M=D-M\n");
        } else if (command.equals("not")) {
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("A=M-1\n");
            this.outPrinter.write("M=!M\n");
        } else if (command.equals("and")) {
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("AM=M-1\n");
            this.outPrinter.write("D=M\n");
            this.outPrinter.write("A=A-1\n");
            this.outPrinter.write("M=D&M\n");
        } else if (command.equals("or")) {
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("AM=M-1\n");
            this.outPrinter.write("D=M\n");
            this.outPrinter.write("A=A-1\n");
            this.outPrinter.write("M=D|M\n");
        } else if (command.equals("gt")) {
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("AM=M-1\n");
            this.outPrinter.write("D=M\n");
            this.outPrinter.write("A=A-1\n");
            this.outPrinter.write("D=M-D\n");
            this.outPrinter.write("@FALSE" + arithmeticJumpFlag + "\n");
            this.outPrinter.write("D;JLE\n");
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("A=M-1\n");
            this.outPrinter.write("M=-1\n");
            this.outPrinter.write("@CONTINUE" + arithmeticJumpFlag + "\n");
            this.outPrinter.write("0;JMP\n");
            this.outPrinter.write("(FALSE" + arithmeticJumpFlag + ")\n");
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("A=M-1\n");
            this.outPrinter.write("M=0\n");
            this.outPrinter.write("(CONTINUE" + arithmeticJumpFlag + ")\n");
            arithmeticJumpFlag++;
        } else if (command.equals("lt")) {
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("AM=M-1\n");
            this.outPrinter.write("D=M\n");
            this.outPrinter.write("A=A-1\n");
            this.outPrinter.write("D=M-D\n");
            this.outPrinter.write("@FALSE" + arithmeticJumpFlag + "\n");
            this.outPrinter.write("D;JGE\n");
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("A=M-1\n");
            this.outPrinter.write("M=-1\n");
            this.outPrinter.write("@CONTINUE" + arithmeticJumpFlag + "\n");
            this.outPrinter.write("0;JMP\n");
            this.outPrinter.write("(FALSE" + arithmeticJumpFlag + ")\n");
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("A=M-1\n");
            this.outPrinter.write("M=0\n");
            this.outPrinter.write("(CONTINUE" + arithmeticJumpFlag + ")\n");
            arithmeticJumpFlag++;
        } else if (command.equals("eq")) {
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("AM=M-1\n");
            this.outPrinter.write("D=M\n");
            this.outPrinter.write("A=A-1\n");
            this.outPrinter.write("D=M-D\n");
            this.outPrinter.write("@FALSE" + arithmeticJumpFlag + "\n");
            this.outPrinter.write("D;JNE\n");
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("A=M-1\n");
            this.outPrinter.write("M=-1\n");
            this.outPrinter.write("@CONTINUE" + arithmeticJumpFlag + "\n");
            this.outPrinter.write("0;JMP\n");
            this.outPrinter.write("(FALSE" + arithmeticJumpFlag + ")\n");
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("A=M-1\n");
            this.outPrinter.write("M=0\n");
            this.outPrinter.write("(CONTINUE" + arithmeticJumpFlag + ")\n");
            arithmeticJumpFlag++;
        } 
        else {
            throw new IllegalArgumentException("Invalid arithmetic command: " + command);
        }
    }

    public void writePushPop(String command, String segment, int index) throws IOException {
        if (command == "push") {
            switch (segment) {
                case "constant":
                    this.outPrinter.write("@" + index + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "local":
                    this.outPrinter.write("@LCL\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "argument":
                    this.outPrinter.write("@ARG\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "this":
                    this.outPrinter.write("@THIS\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "that":
                    this.outPrinter.write("@THAT\nD=M\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "temp":
                    this.outPrinter.write("@" + (index + 5) + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "pointer":
                    if (index == 0)
                        this.outPrinter.write("@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n"); 
                    else if (index == 1)
                        this.outPrinter.write("@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "static":
                    this.outPrinter.write("@" + fileName + index + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid segment for push: " + segment);
            }
        }
        else if (command == "pop") {
            switch (segment) {
                case "local":
                    this.outPrinter.write("@LCL\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                    break;
                case "argument":
                    this.outPrinter.write("@ARG\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                    break;
                case "this":
                    this.outPrinter.write("@THIS\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                    break;
                case "that":
                    this.outPrinter.write("@THAT\nD=M\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                    break;
                case "temp":
                    this.outPrinter.write("@" + (index + 5) + "\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                    break;
                case "pointer":
                    if (index == 0)
                        this.outPrinter.write("@SP\nAM=M-1\nD=M\n@THIS\nM=D\n");
                    else if (index == 1)
                        this.outPrinter.write("@SP\nAM=M-1\nD=M\n@THAT\nM=D\n");
                    break;
                case "static":
                    this.outPrinter.write("@" + fileName + index + "\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid segment for pop: " + segment);
            }
        } else {
            throw new IllegalArgumentException("Invalid command: " + command);
        }
    }

    public void close() throws IOException {
        this.outPrinter.close();
    }

    public void writeBootstrap() throws IOException {
        this.outPrinter.write("@256\nD=A\n@SP\nM=D\n");
        writeCall("Sys.init", 0);
    }

    public void writeCall(String func, int argCount) throws IOException{
        String label = "RETURN_LABEL" + labelCount++;
        this.outPrinter.write("@" + label + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        this.outPrinter.write("@LCL\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        this.outPrinter.write("@ARG\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        this.outPrinter.write("@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        this.outPrinter.write("@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        this.outPrinter.write("@SP\nD=M\n@5\nD=D-A\n@" + argCount + "\nD=D-A\n@ARG\nM=D\n");
        this.outPrinter.write("@SP\nD=M\n@LCL\nM=D\n@" + func + "\n0;JMP\n" + "(" + label + ")\n");
    }

    public void writeReturn() throws IOException {
        this.outPrinter.write("@LCL\nD=M\n@R11\nM=D\n@5\nA=D-A\nD=M\n@R12\nM=D\n");
        this.outPrinter.write("@ARG\nD=M\n@0\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
        this.outPrinter.write("@ARG\nD=M\n@SP\nM=D+1\n");
        this.outPrinter.write("@R11\nD=M-1\nAM=D\nD=M\n@THAT\nM=D\n");
        this.outPrinter.write("@R11\nD=M-1\nAM=D\nD=M\n@THIS\nM=D\n");
        this.outPrinter.write("@R11\nD=M-1\nAM=D\nD=M\n@ARG\nM=D\n");
        this.outPrinter.write("@R11\nD=M-1\nAM=D\nD=M\n@LCL\nM=D\n");
        this.outPrinter.write("@R12\nA=M\n0;JMP\n");
    }

    public void writeFunction(String func, int argCount) throws IOException {
        this.outPrinter.write("(" + func + ")\n");
        for (int i = 0; i < argCount; i++) {
            writePushPop("push", "constant", 0);
        }
    }

    public void writeLabel(String label) throws IOException {
        Pattern p = Pattern.compile("^[^0-9][0-9A-Za-z\\_\\:\\.\\$]+");
        Matcher m = p.matcher(label);
        if (m.find())
            this.outPrinter.write("(" + label + ")\n");
        else
            throw new IllegalArgumentException("Invalid label: " + label);
    }

    public void writeGoto(String label) throws IOException {
        Pattern p = Pattern.compile("^[^0-9][0-9A-Za-z\\_\\:\\.\\$]+");
        Matcher m = p.matcher(label);
        if (m.find())
            this.outPrinter.write("@" + label + "\n0;JMP\n");
        else
            throw new IllegalArgumentException("Invalid label: " + label);
    }

    public void writeIf(String label) throws IOException {
        Pattern p = Pattern.compile("^[^0-9][0-9A-Za-z\\_\\:\\.\\$]+");
        Matcher m = p.matcher(label);
        if (m.find()) {
            this.outPrinter.write("@SP\n");
            this.outPrinter.write("AM=M-1\n");
            this.outPrinter.write("D=M\n");
            this.outPrinter.write("@" + label + "\nD;JNE\n");
        }
        else
            throw new IllegalArgumentException("Invalid label: " + label);
    }

}
