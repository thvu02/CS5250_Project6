import java.io.*;

public class CodeWriter {
    private int arithmeticJumpFlag = 0;
    private BufferedWriter outPrinter;

    public CodeWriter(File output) throws IOException{
        this.outPrinter = new BufferedWriter(new FileWriter(output));
    }

    public void setFileName(File fileOut) throws IOException {
        // This method can be used to set the file name for the output, if needed.
        // Currently, it does not do anything as we are already writing to a specific file.
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
                    this.outPrinter.write("@R5\nD=M\n@" + (index+5) + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "pointer":
                    if (index == 0)
                        this.outPrinter.write("@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n"); 
                    if (index == 1)
                        this.outPrinter.write("@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "static":
                    this.outPrinter.write("@" + String.valueOf(16+index) + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
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
                    this.outPrinter.write("@R5\nD=M\n@" + (index+5) + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                    break;
                case "pointer":
                    if (index == 0)
                        this.outPrinter.write("@THIS\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                    if (index == 1)
                        this.outPrinter.write("@THAT\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                    break;
                case "static":
                    this.outPrinter.write("@" + (String.valueOf(16+index)) + "\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
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
}
