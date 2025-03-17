import java.io.*;

public class Assembler {
    private File assembly;
    private BufferedWriter binary;
    private Code coder;
    private SymbolTable symbolTable;

    public Assembler(File assembly, File binary) throws IOException {
        this.assembly = assembly;
        this.binary = new BufferedWriter(new FileWriter(binary));
        this.coder = new Code();
        this.symbolTable = new SymbolTable();
    }

    public void firstPass() throws IOException {
        Parser parser = new Parser(this.assembly);
        while (parser.hasMoreCommands()) {
            parser.advance();
            String commandType = parser.commandType();

            if (commandType.equals("L_COMMAND")) {
                String symbol = parser.symbol();
                this.symbolTable.addEntry(symbol, this.symbolTable.getProgramAddr());
            }
            else
                this.symbolTable.incrementProgramAddr();
        }
        parser.close();
    }

    public void secondPass() throws IOException {
        Parser parser = new Parser(this.assembly);
        while (parser.hasMoreCommands()) {
            parser.advance();
            String instruction = null;
            String commandType = parser.commandType();

            if (commandType.equals("A_COMMAND")) {
                String address = null;
                String symbol = parser.symbol();
                boolean isSymbol = !Character.isDigit(symbol.charAt(0));
                if (isSymbol) {
                    if (!this.symbolTable.contains(symbol)) {
                        this.symbolTable.addEntry(symbol, this.symbolTable.getDataAddr());
                        this.symbolTable.incrementDataAddr();
                    }
                    address = Integer.toString(this.symbolTable.getAddress(symbol));
                }
                else {
                    address = symbol;
                }
                instruction = "0" + this.coder.toBinary(address);
            }
            else if (commandType.equals("C_COMMAND")) {
                String dest = parser.dest();
                String comp = parser.comp();
                String jump = parser.jump();
                instruction = "111" + this.coder.comp(comp) + this.coder.dest(dest) + this.coder.jump(jump);
            }

            if (!commandType.equals("L_COMMAND")) {
                this.binary.write(instruction+"\n");
                // this.binary.newLine(); // defautls to control character for OS; working on Windows, but nand2tetris wants Unix format
            }
        }
        
        parser.close();
        this.binary.flush();
        this.binary.close();
    }
}