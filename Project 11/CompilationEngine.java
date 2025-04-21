import java.io.*;

public class CompilationEngine {
    private JackTokenizer tokenizer;
    private VMWriter vmWriter;
    private SymbolTable symbolTable;
    private String curClass;
    private String curSubroutine;
    private int labelIndex = 0;

    public CompilationEngine(File input, File output) {
        tokenizer = new JackTokenizer(input);
        vmWriter = new VMWriter(output);
        symbolTable = new SymbolTable();
    }

    private String currentFunction() {
        if (curClass.length() != 0 && curSubroutine.length() != 0)
            return curClass + "." + curSubroutine;
        return "";
    }

    public String compileType() {
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.KEYWORD && (tokenizer.keyWord() == JackTokenizer.KEYWORD.INT || tokenizer.keyWord() == JackTokenizer.KEYWORD.CHAR || tokenizer.keyWord() == JackTokenizer.KEYWORD.BOOLEAN)) {
            return tokenizer.getCurrentToken();
        }
        
        if (tokenizer.tokenType() == JackTokenizer.TYPE.IDENTIFIER) {
            return tokenizer.identifier();
        }

        throw new IllegalStateException("expected in, char, boolean, or className");
        // return ""; // unreachable
    }

    public void compileClass() {
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.KEYWORD || tokenizer.keyWord() != JackTokenizer.KEYWORD.CLASS) {
            throw new IllegalStateException("expected 'class' keyword, but found: " + tokenizer.getCurrentToken());
        }

        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
            throw new IllegalStateException("classname");
        }
        curClass = tokenizer.identifier();
        // System.out.println("Compiling class: " + curClass);

        requireSymbol('{');
        compileClassVarDec();
        compileSubroutine();
        requireSymbol('}');

        if (tokenizer.hasMoreTokens()) {
            throw new IllegalStateException("unexpected tokens after class definition");
        }

        vmWriter.close();
    }

    private void compileClassVarDec() {
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == '}') {
            tokenizer.pointerBack();
            return;
        }
        if (tokenizer.tokenType() != JackTokenizer.TYPE.KEYWORD) {
            throw new IllegalStateException("keywords");
        }
        if (tokenizer.keyWord() == JackTokenizer.KEYWORD.CONSTRUCTOR || tokenizer.keyWord() == JackTokenizer.KEYWORD.FUNCTION || tokenizer.keyWord() == JackTokenizer.KEYWORD.METHOD) {
            tokenizer.pointerBack();
            return;
        }
        if (tokenizer.keyWord() != JackTokenizer.KEYWORD.STATIC && tokenizer.keyWord() != JackTokenizer.KEYWORD.FIELD) {
            throw new IllegalStateException("static or field keyword");
        }

        Symbol.KIND kind = null;
        String type = "";
        String name = "";

        switch (tokenizer.keyWord()) {
            case STATIC:
                kind = Symbol.KIND.STATIC;
                break;
            case FIELD:
                kind = Symbol.KIND.FIELD;
                break;
        }

        type = compileType();

        do {
            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
                throw new IllegalStateException("identifier");
            }
            name = tokenizer.identifier();
            symbolTable.define(name, type, kind);
            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ';')) {
                throw new IllegalStateException("expected ',' or ';'");
            }
            if (tokenizer.symbol() == ';')
                break;
        }while(true);
        compileClassVarDec();
    }

    private void compileSubroutine() {
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == '}') {
            tokenizer.pointerBack();
            return;
        }
        if (tokenizer.tokenType() != JackTokenizer.TYPE.KEYWORD || (tokenizer.keyWord() != JackTokenizer.KEYWORD.CONSTRUCTOR && tokenizer.keyWord() != JackTokenizer.KEYWORD.FUNCTION && tokenizer.keyWord() != JackTokenizer.KEYWORD.METHOD)) {
            throw new IllegalStateException("expected constructor, function, or method keyword");
        }
        
        JackTokenizer.KEYWORD keyword = tokenizer.keyWord();
        symbolTable.startSubroutine();
        if (tokenizer.keyWord() == JackTokenizer.KEYWORD.METHOD)
            symbolTable.define("this", curClass, Symbol.KIND.ARG);
        String type = "";

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.KEYWORD && tokenizer.keyWord() == JackTokenizer.KEYWORD.VOID) {
            type = "void";
        }
        else {
            tokenizer.pointerBack();
            type = compileType();
        }

        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
            throw new IllegalStateException("expected subroutine name");
        }

        curSubroutine = tokenizer.identifier();

        requireSymbol('(');
        compileParameterList();
        requireSymbol(')');
        compileSubroutineBody(keyword);
        compileSubroutine();
    }

    private void compileSubroutineBody(JackTokenizer.KEYWORD keyword) {
        requireSymbol('{');
        compileVarDec();
        // System.out.println("compiling subrouting body: " + currentFunction() + keyword);
        writeFunctionDec(keyword);
        compileStatement();
        requireSymbol('}');
    }

    private void writeFunctionDec(JackTokenizer.KEYWORD keyword) {
        vmWriter.writeFunction(currentFunction(), symbolTable.varCount(Symbol.KIND.VAR));
        if (keyword == JackTokenizer.KEYWORD.METHOD) {
            vmWriter.writePush(VMWriter.SEGMENT.ARG, 0);
            vmWriter.writePop(VMWriter.SEGMENT.POINTER, 0);
        }
        else if (keyword == JackTokenizer.KEYWORD.CONSTRUCTOR) {
            vmWriter.writePush(VMWriter.SEGMENT.CONST, symbolTable.varCount(Symbol.KIND.FIELD));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(VMWriter.SEGMENT.POINTER, 0);
        }
    }

    private void compileStatement() {
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == '}') {
            tokenizer.pointerBack();
            return;
        }
        if (tokenizer.tokenType() != JackTokenizer.TYPE.KEYWORD) {
            throw new IllegalStateException("expected a keyword");
        }
        else {
            switch (tokenizer.keyWord()) {
                case JackTokenizer.KEYWORD.LET:
                    compileLet();
                    break;
                case JackTokenizer.KEYWORD.IF:
                    compileIf();
                    break;
                case JackTokenizer.KEYWORD.WHILE:
                    compileWhile();
                    break;
                case JackTokenizer.KEYWORD.DO:
                    compileDo();
                    break;
                case JackTokenizer.KEYWORD.RETURN:
                    compileReturn();
                    break;
                default:
                    throw new IllegalStateException("expected let, if, while, do, or return keyword, but got " + tokenizer.keyWord());
            }
        }
        compileStatement();
    }

    private void compileParameterList() {
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == ')') {
            tokenizer.pointerBack();
            return;
        }
        String type = "";
        tokenizer.pointerBack();
        do {
            type = compileType();
            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
                throw new IllegalStateException("expected identifier");
            }

            symbolTable.define(tokenizer.identifier(), type, Symbol.KIND.ARG);

            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ')')) {
                throw new IllegalStateException("expected ',' or ')'");
            }
            if (tokenizer.symbol() == ')') {
                tokenizer.pointerBack();
                break;
            }
        }while(true);
    }


    private void compileVarDec() {
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.KEYWORD || tokenizer.keyWord() != JackTokenizer.KEYWORD.VAR) {
            tokenizer.pointerBack();
            return;
        }

        String type = compileType();

        do {
            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
                throw new IllegalStateException("expected identifier");
            }
            
            symbolTable.define(tokenizer.identifier(), type, Symbol.KIND.VAR);

            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ';')) {
                throw new IllegalStateException("expected ',' or ';'");
            }

            if (tokenizer.symbol() == ';') {
                break;
            }
        }while(true);
        compileVarDec();
    }

    private void compileDo() {
        compileSubroutineCall();
        requireSymbol(';');
        vmWriter.writePop(VMWriter.SEGMENT.TEMP, 0);
    }

    private void compileLet() {
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
            throw new IllegalStateException("expected varName");
        }

        String varName = tokenizer.identifier();
        
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.SYMBOL || (tokenizer.symbol() != '[' && tokenizer.symbol() != '=')) {
            throw new IllegalStateException("expected '[' or '='");
        }
        
        boolean expression = false;
        if (tokenizer.symbol() == '[') {
            expression = true;
            vmWriter.writePush(getSeg(symbolTable.kindOf(varName)), symbolTable.indexOf(varName));
            compileExpression();
            requireSymbol(']');
            vmWriter.writeArithmetic(VMWriter.COMMAND.ADD);
        }

        if (expression)
            tokenizer.advance();

        compileExpression();
        requireSymbol(';');

        if (expression) {
            vmWriter.writePop(VMWriter.SEGMENT.TEMP, 0);
            vmWriter.writePop(VMWriter.SEGMENT.POINTER, 1);
            vmWriter.writePush(VMWriter.SEGMENT.TEMP, 0);
            vmWriter.writePop(VMWriter.SEGMENT.THAT, 0);
        }
        else {
            vmWriter.writePop(getSeg(symbolTable.kindOf(varName)), symbolTable.indexOf(varName));
        }
    }

    private VMWriter.SEGMENT getSeg(Symbol.KIND kind) {
        switch (kind) {
            case FIELD:
                return VMWriter.SEGMENT.THIS;
            case STATIC:
                return VMWriter.SEGMENT.STATIC;
            case ARG:
                return VMWriter.SEGMENT.ARG;
            case VAR:
                return VMWriter.SEGMENT.LOCAL;
            default:
                return  VMWriter.SEGMENT.NONE;
        }
    }

    private void compileWhile() {
        String continueLabel = newLabel();
        String topLabel = newLabel();
        vmWriter.writeLabel(topLabel);

        requireSymbol('(');
        compileExpression();
        requireSymbol(')');

        vmWriter.writeArithmetic(VMWriter.COMMAND.NOT);
        vmWriter.writeIf(continueLabel);

        requireSymbol('{');
        compileStatement();
        requireSymbol('}');

        vmWriter.writeGoto(topLabel);
        vmWriter.writeLabel(continueLabel);
    }

    private String newLabel() {
        return "LABEL_" + (labelIndex++);
    }

    private void compileReturn() {
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == ';') {
            vmWriter.writePush(VMWriter.SEGMENT.CONST, 0);
        }
        else {
            tokenizer.pointerBack();
            compileExpression();
            requireSymbol(';');
        }
        vmWriter.writeReturn(); 
    }

    private void compileIf() {
        String elseLabel = newLabel();
        String endLabel = newLabel();

        requireSymbol('(');
        compileExpression();
        requireSymbol(')');

        vmWriter.writeArithmetic(VMWriter.COMMAND.NOT);
        vmWriter.writeIf(elseLabel);

        requireSymbol('{');
        compileStatement();
        requireSymbol('}');
        
        vmWriter.writeGoto(endLabel);
        vmWriter.writeLabel(elseLabel);

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.KEYWORD && tokenizer.keyWord() == JackTokenizer.KEYWORD.ELSE) {
            requireSymbol('{');
            compileStatement();
            requireSymbol('}');
        }
        else {
            tokenizer.pointerBack();
        }
        vmWriter.writeLabel(endLabel);
    }

    private void compileTerm() {
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.IDENTIFIER) {
            String temp = tokenizer.identifier();
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == '[') {
                vmWriter.writePush(getSeg(symbolTable.kindOf(temp)), symbolTable.indexOf(temp));
                compileExpression();
                requireSymbol(']');
                vmWriter.writeArithmetic(VMWriter.COMMAND.ADD);
                vmWriter.writePop(VMWriter.SEGMENT.POINTER, 1);
                vmWriter.writePush(VMWriter.SEGMENT.THAT, 0);
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.')) {
                tokenizer.pointerBack();
                tokenizer.pointerBack();
                compileSubroutineCall();
            }
            else {
                tokenizer.pointerBack();
                vmWriter.writePush(getSeg(symbolTable.kindOf(temp)), symbolTable.indexOf(temp));
            }
        }
        else {
            if (tokenizer.tokenType() == JackTokenizer.TYPE.INT_CONST) {
                vmWriter.writePush(VMWriter.SEGMENT.CONST, tokenizer.intVal());
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.STRING_CONST) {
                String string_val = tokenizer.stringVal();
                vmWriter.writePush(VMWriter.SEGMENT.CONST, string_val.length());
                vmWriter.writeCall("String.new", 1);
                for (int i = 0; i < string_val.length(); i++) {
                    vmWriter.writePush(VMWriter.SEGMENT.CONST, (int)string_val.charAt(i));
                    vmWriter.writeCall("String.appendChar", 2);
                }
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.KEYWORD && tokenizer.keyWord() == JackTokenizer.KEYWORD.TRUE) {
                vmWriter.writePush(VMWriter.SEGMENT.CONST, 0);
                vmWriter.writeArithmetic(VMWriter.COMMAND.NOT);
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.KEYWORD && tokenizer.keyWord() == JackTokenizer.KEYWORD.THIS) {
                vmWriter.writePush(VMWriter.SEGMENT.POINTER, 0);
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.KEYWORD && (tokenizer.keyWord() == JackTokenizer.KEYWORD.FALSE || tokenizer.keyWord() == JackTokenizer.KEYWORD.NULL)) {
                vmWriter.writePush(VMWriter.SEGMENT.CONST, 0);
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == '(') {
                compileExpression();
                requireSymbol(')');
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~')) {
                char sym = tokenizer.symbol();
                compileTerm();
                if (sym == '-')
                    vmWriter.writeArithmetic(VMWriter.COMMAND.NEG);
                else
                    vmWriter.writeArithmetic(VMWriter.COMMAND.NOT);
            }
            else {
                throw new IllegalStateException("expected integerConstant, stringConstant, keyword, '(' expression ')', or unary operator");
            }
        }
    }

    private void compileSubroutineCall() {
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
            throw new IllegalStateException("expected identifier");
        }

        String name = tokenizer.identifier();
        int numberOfArgs = 0;

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == '(') {
            vmWriter.writePush(VMWriter.SEGMENT.POINTER, 0);
            numberOfArgs = compileExpressionList() + 1;
            requireSymbol(')');
            vmWriter.writeCall(curClass + '.' + name, numberOfArgs);
        }
        else if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == '.') {
            String objectName = name;
            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
                throw new IllegalStateException("identifier" + tokenizer.tokenType() + tokenizer.keyWord());
            }
            name = tokenizer.identifier();
            String type = symbolTable.typeOf(objectName);
            if (type.equals("int") || type.equals("boolean") || type.equals("char") || type.equals("void"))
                throw new IllegalStateException("no built-in type for " + type);
            else if (type.equals(""))
                name = objectName + "." + name;
            else {
                numberOfArgs = 1;
                vmWriter.writePush(getSeg(symbolTable.kindOf(objectName)), symbolTable.indexOf(objectName));
                name = symbolTable.typeOf(objectName) + "." + name;
            }
            requireSymbol('(');
            numberOfArgs += compileExpressionList();
            requireSymbol(')');
            vmWriter.writeCall(name, numberOfArgs);
        }
        else {
            throw new IllegalStateException("expected '(' or '.'");
        }
    }

    private void compileExpression() {
        compileTerm();
        do {
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.isOp()) {
                String op = "";
                switch (tokenizer.symbol()) {
                    case '+':
                        op = "add";
                        break;
                    case '-':
                        op = "sub";
                        break;
                    case '*':
                        op = "call Math.multiply 2";
                        break;
                    case '/':
                        op = "call Math.divide 2";
                        break;
                    case '<':
                        op = "lt";
                        break;
                    case '>':
                        op = "gt";
                        break;
                    case '=':
                        op = "eq";
                        break;
                    case '&':
                        op = "and";
                        break;
                    case '|':
                        op = "or";
                        break;
                    default:
                        throw new IllegalStateException("unknown op: " + tokenizer.symbol());
                }
                compileTerm();
                vmWriter.writeCommand(op, "", "");
            }
            else {
                tokenizer.pointerBack();
                break;
            }
        }while(true);
    }

    private int compileExpressionList() {
        int numberOfArgs = 0;
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == ')') {
            tokenizer.pointerBack();
        }
        else {
            numberOfArgs = 1;
            tokenizer.pointerBack();
            compileExpression();
            do {
                tokenizer.advance();
                if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == ',') {
                    compileExpression();
                    numberOfArgs++;
                }
                else {
                    tokenizer.pointerBack();
                    break;
                }
            }while(true);
        }
        return numberOfArgs;
    }

    private void requireSymbol(char symbol) {
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.SYMBOL || tokenizer.symbol() != symbol) {
            throw new IllegalStateException("expected '" + symbol + "'");
        }
    }
}