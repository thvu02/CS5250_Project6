import java.util.HashMap;

public class SymbolTable {
    // constants for memory addresses
    private static int PROGRAM_START_ADDR = 0;
    private static int PROGRAM_END_ADDR = 32676;
    private static int DATA_START_ADDR = 16;
    private static int DATA_END_ADDR = 16384;
    // initialize class-wide variables
    private HashMap<String, Integer> symbolTable;
    private int programAddr;
    private int dataAddr;
    

    public SymbolTable() {
        // initialize symbol table
        this.symbolTable = new HashMap<>();
        this.fillSymbolTable();
        // initialize tracker variables
        this.programAddr = PROGRAM_START_ADDR;
        this.dataAddr = DATA_START_ADDR;
    }

    private void fillSymbolTable() {
        this.symbolTable.put("SP", 0);
        this.symbolTable.put("LCL", 1);
        this.symbolTable.put("ARG", 2);
        this.symbolTable.put("THIS", 3);
        this.symbolTable.put("THAT", 4);
        this.symbolTable.put("R0", 0);
        this.symbolTable.put("R1", 1);
        this.symbolTable.put("R2", 2);
        this.symbolTable.put("R3", 3);
        this.symbolTable.put("R4", 4);
        this.symbolTable.put("R5", 5);
        this.symbolTable.put("R6", 6);
        this.symbolTable.put("R7", 7);
        this.symbolTable.put("R8", 8);
        this.symbolTable.put("R9", 9);
        this.symbolTable.put("R10", 10);
        this.symbolTable.put("R11", 11);
        this.symbolTable.put("R12", 12);
        this.symbolTable.put("R13", 13);
        this.symbolTable.put("R14", 14);
        this.symbolTable.put("R15", 15);
        this.symbolTable.put("SCREEN", 16384);
        this.symbolTable.put("KBD", 24576); 
    }

    public void addEntry(String symbol, int address) {
        this.symbolTable.put(symbol, Integer.valueOf(address));
    }

    public boolean contains(String symbol) {
        return (this.symbolTable.containsKey(symbol));
    }

    public int getAddress(String symbol) {
        // returns null if the symbol is not in the table
        return (this.symbolTable.get(symbol));
    }

    public void incrementProgramAddr() {
        this.programAddr++;
        if (this.programAddr > PROGRAM_END_ADDR) {
            throw new RuntimeException("Program address out of bounds");
        }
    }

    public int getProgramAddr() {
        return this.programAddr;
    }

    public void incrementDataAddr() {
        this.dataAddr++;
        if (this.dataAddr > DATA_END_ADDR) {
            throw new RuntimeException("Data address out of bounds");
        }
    }

    public int getDataAddr() {
        return this.dataAddr;
    }
}
