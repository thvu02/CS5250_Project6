import java.io.*;

public class CompilationEngine {
    private JackTokenizer tokenizer;
    private PrintWriter printWriter;
    private PrintWriter tokenPrintWriter;

    public CompilationEngine(File input, File output, File tokenFile) {
        try {
            tokenizer = new JackTokenizer(input);
            printWriter = new PrintWriter(output);
            tokenPrintWriter = new PrintWriter(tokenFile);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void compileType() {
        tokenizer.advance();
        boolean isType = false;
        if (tokenizer.tokenType() == JackTokenizer.TYPE.KEYWORD && (tokenizer.keyWord() == JackTokenizer.KEYWORD.INT || tokenizer.keyWord() == JackTokenizer.KEYWORD.CHAR || tokenizer.keyWord() == JackTokenizer.KEYWORD.BOOLEAN)) {
            isType = true;
            printWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");
            tokenPrintWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");
        }
        
        if (tokenizer.tokenType() == JackTokenizer.TYPE.IDENTIFIER) {
            isType = true;
            printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        }

        if (!isType) {
            throw new IllegalStateException("expected type, but found: " + tokenizer.getCurrentToken());
        }
    }

    public void compileClass() {
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.KEYWORD || tokenizer.keyWord() != JackTokenizer.KEYWORD.CLASS) {
            throw new IllegalStateException("expected 'class' keyword, but found: " + tokenizer.getCurrentToken());
        }
        printWriter.print("<class>\n");
        tokenPrintWriter.print("<tokens>\n");

        printWriter.print("<keyword>class</keyword>\n");
        tokenPrintWriter.print("<keyword>class</keyword>\n");

        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
            throw new IllegalStateException("classname");
        }
        printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

        requireSymbol('{');
        compileClassVarDec();
        compileSubroutine();
        requireSymbol('}');

        if (tokenizer.hasMoreTokens()) {
            throw new IllegalStateException("unexpected tokens after class definition");
        }

        printWriter.print("</class>\n");
        tokenPrintWriter.print("</tokens>\n");

        printWriter.close();
        tokenPrintWriter.close();
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

        printWriter.print("<classVarDec>\n");

        if (tokenizer.keyWord() != JackTokenizer.KEYWORD.STATIC && tokenizer.keyWord() != JackTokenizer.KEYWORD.FIELD) {
            throw new IllegalStateException("static or field keyword");
        }

        printWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");
        tokenPrintWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");

        compileType();

        do {
            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
                throw new IllegalStateException("identifier");
            }
            printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ';')) {
                throw new IllegalStateException("expected ',' or ';'");
            }
            if (tokenizer.symbol() == ',') {
                printWriter.print("<symbol>,</symbol>\n");
                tokenPrintWriter.print("<symbol>,</symbol>\n");
            }
            else {
                printWriter.print("<symbol>;</symbol>\n");
                tokenPrintWriter.print("<symbol>;</symbol>\n");
                break;
            }
        }while(true);
        printWriter.print("</classVarDec>\n");
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
        printWriter.print("<subroutineDec>\n");
        printWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");
        tokenPrintWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.KEYWORD && tokenizer.keyWord() == JackTokenizer.KEYWORD.VOID) {
            printWriter.print("<keyword>void</keyword>\n");
            tokenPrintWriter.print("<keyword>void</keyword>\n");
        }
        else {
            tokenizer.pointerBack();
            compileType();
        }

        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
            throw new IllegalStateException("expected subroutine name");
        }

        printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

        requireSymbol('(');
        printWriter.print("<parameterList>\n");
        compileParameterList();
        printWriter.print("</parameterList>\n");
        requireSymbol(')');
        compileSubroutineBody();
        printWriter.print("</subroutineDec>\n");
        compileSubroutine();
    }

    private void compileSubroutineBody() {
        printWriter.print("<subroutineBody>\n");
        requireSymbol('{');
        compileVarDec();
        printWriter.print("<statements>\n");
        compileStatement();
        printWriter.print("</statements>\n");
        requireSymbol('}');
        printWriter.print("</subroutineBody>\n");
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
        tokenizer.pointerBack();
        do {
            compileType();
            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
                throw new IllegalStateException("expected identifier");
            }
            printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ')')) {
                throw new IllegalStateException("expected ',' or ')'");
            }
            if (tokenizer.symbol() == ',') {
                printWriter.print("<symbol>,</symbol>\n");
                tokenPrintWriter.print("<symbol>,</symbol>\n");
            }
            else {
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

        printWriter.print("<varDec>\n");
        printWriter.print("<keyword>var</keyword>\n");
        tokenPrintWriter.print("<keyword>var</keyword>\n");

        compileType();
        do {
            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
                throw new IllegalStateException("expected identifier");
            }
            printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");

            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ';')) {
                throw new IllegalStateException("expected ',' or ';'");
            }

            if (tokenizer.symbol() == ',') {
                printWriter.print("<symbol>,</symbol>\n");
                tokenPrintWriter.print("<symbol>,</symbol>\n");
            }
            else {
                printWriter.print("<symbol>;</symbol>\n");
                tokenPrintWriter.print("<symbol>;</symbol>\n");
                break;
            }
        }while(true);
        printWriter.print("</varDec>\n");
        compileVarDec();
    }

    private void compileDo() {
        printWriter.print("<doStatement>\n");
        printWriter.print("<keyword>do</keyword>\n");
        tokenPrintWriter.print("<keyword>do</keyword>\n");
        compileSubroutineCall();
        requireSymbol(';');
        printWriter.print("</doStatement>\n");
    }

    private void compileLet() {
        printWriter.print("<letStatement>\n");
        printWriter.print("<keyword>let</keyword>\n");
        tokenPrintWriter.print("<keyword>let</keyword>\n");

        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
            throw new IllegalStateException("expected varName");
        }
        printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.SYMBOL || (tokenizer.symbol() != '[' && tokenizer.symbol() != '=')) {
            throw new IllegalStateException("expected '[' or '='");
        }
        
        boolean expression = false;
        if (tokenizer.symbol() == '[') {
            expression = true;
            printWriter.print("<symbol>[</symbol>\n");
            tokenPrintWriter.print("<symbol>[</symbol>\n");
            
            compileExpression();

            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == ']') {
                printWriter.print("<symbol>]</symbol>\n");
                tokenPrintWriter.print("<symbol>]</symbol>\n");
            }
            else {
                throw new IllegalStateException("expected ']'");
            }
        }

        if (expression)
            tokenizer.advance();

        printWriter.print("<symbol>=</symbol>\n");
        tokenPrintWriter.print("<symbol>=</symbol>\n");
        compileExpression();
        requireSymbol(';');
        printWriter.print("</letStatement>\n");
    }

    private void compileWhile() {
        printWriter.print("<whileStatement>\n");
        printWriter.print("<keyword>while</keyword>\n");
        tokenPrintWriter.print("<keyword>while</keyword>\n");
        requireSymbol('(');
        compileExpression();
        requireSymbol(')');
        requireSymbol('{');
        printWriter.print("<statements>\n");
        compileStatement();
        printWriter.print("</statements>\n");
        requireSymbol('}');
        printWriter.print("</whileStatement>\n");
    }

    private void compileReturn() {
        printWriter.print("<returnStatement>\n");
        printWriter.print("<keyword>return</keyword>\n");
        tokenPrintWriter.print("<keyword>return</keyword>\n");

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == ';') {
            printWriter.print("<symbol>;</symbol>\n");
            tokenPrintWriter.print("<symbol>;</symbol>\n");
            printWriter.print("</returnStatement>\n");
            return;
        }
        tokenizer.pointerBack();
        compileExpression();
        requireSymbol(';');
        printWriter.print("</returnStatement>\n");
    }

    private void compileIf() {
        printWriter.print("<ifStatement>\n");
        printWriter.print("<keyword>if</keyword>\n");
        tokenPrintWriter.print("<keyword>if</keyword>\n");
        requireSymbol('(');
        compileExpression();
        requireSymbol(')');
        requireSymbol('{');
        printWriter.print("<statements>\n");
        compileStatement();
        printWriter.print("</statements>\n");
        requireSymbol('}');
        
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.KEYWORD && tokenizer.keyWord() == JackTokenizer.KEYWORD.ELSE) {
            printWriter.print("<keyword>else</keyword>\n");
            tokenPrintWriter.print("<keyword>else</keyword>\n");
            requireSymbol('{');
            printWriter.print("<statements>\n");
            compileStatement();
            printWriter.print("</statements>\n");
            requireSymbol('}');
        }
        else {
            tokenizer.pointerBack();
        }
        printWriter.print("</ifStatement>\n");
    }

    private void compileTerm() {
        printWriter.print("<term>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.IDENTIFIER) {
            String temp = tokenizer.identifier();
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == '[') {
                printWriter.print("<identifier>" + temp + "</identifier>\n");
                tokenPrintWriter.print("<identifier>" + temp + "</identifier>\n");
                printWriter.print("<symbol>[</symbol>\n");
                tokenPrintWriter.print("<symbol>[</symbol>\n");
                compileExpression();
                requireSymbol(']');
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.')) {
                tokenizer.pointerBack();
                tokenizer.pointerBack();
                compileSubroutineCall();
            }
            else {
                printWriter.print("<identifier>" + temp + "</identifier>\n");
                tokenPrintWriter.print("<identifier>" + temp + "</identifier>\n");
                tokenizer.pointerBack();
            }
        }
        else {
            if (tokenizer.tokenType() == JackTokenizer.TYPE.INT_CONST) {
                printWriter.print("<integerConstant>" + tokenizer.intVal() + "</integerConstant>\n");
                tokenPrintWriter.print("<integerConstant>" + tokenizer.intVal() + "</integerConstant>\n");
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.STRING_CONST) {
                printWriter.print("<stringConstant>" + tokenizer.stringVal() + "</stringConstant>\n");
                tokenPrintWriter.print("<stringConstant>" + tokenizer.stringVal() + "</stringConstant>\n");
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.KEYWORD && (tokenizer.keyWord() == JackTokenizer.KEYWORD.TRUE || 
                    tokenizer.keyWord() == JackTokenizer.KEYWORD.FALSE || tokenizer.keyWord() == JackTokenizer.KEYWORD.NULL || tokenizer.keyWord() == JackTokenizer.KEYWORD.THIS)) {
                printWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");
                tokenPrintWriter.print("<keyword>" + tokenizer.getCurrentToken() + "</keyword>\n");
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == '(') {
                printWriter.print("<symbol>(</symbol>\n");
                tokenPrintWriter.print("<symbol>(</symbol>\n");
                compileExpression();
                requireSymbol(')');
            }
            else if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~')) {
                printWriter.print("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                tokenPrintWriter.print("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                compileTerm();
            }
            else {
                throw new IllegalStateException("expected integerConstant, stringConstant, keyword, '(' expression ')', or unary operator");
            }
        }
        printWriter.print("</term>\n");
    }

    private void compileSubroutineCall() {
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
            throw new IllegalStateException("expected identifier");
        }
        printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == '(') {
            printWriter.print("<symbol>(</symbol>\n");
            tokenPrintWriter.print("<symbol>(</symbol>\n");
            printWriter.print("<expressionList>\n");
            compileExpressionList();
            printWriter.print("</expressionList>\n");
            requireSymbol(')');
        }
        else if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == '.') {
            printWriter.print("<symbol>.</symbol>\n");
            tokenPrintWriter.print("<symbol>.</symbol>\n");
            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TYPE.IDENTIFIER) {
                throw new IllegalStateException("identifier");
            }
            printWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            tokenPrintWriter.print("<identifier>" + tokenizer.identifier() + "</identifier>\n");
            requireSymbol('(');
            printWriter.print("<expressionList>\n");
            compileExpressionList();
            printWriter.print("</expressionList>\n");
            requireSymbol(')');
        }
        else {
            throw new IllegalStateException("expected '(' or '.'");
        }
    }

    private void compileExpression() {
        printWriter.print("<expression>\n");
        compileTerm();
        do {
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.isOp()) {
                if (tokenizer.symbol() == '>') {
                    printWriter.print("<symbol>&gt;</symbol>\n");
                    tokenPrintWriter.print("<symbol>&gt;</symbol>\n");
                }
                else if (tokenizer.symbol() == '<') {
                    printWriter.print("<symbol>&lt;</symbol>\n");
                    tokenPrintWriter.print("<symbol>&lt;</symbol>\n");
                }
                else if (tokenizer.symbol() == '&') {
                    printWriter.print("<symbol>&amp;</symbol>\n");
                    tokenPrintWriter.print("<symbol>&amp;</symbol>\n");
                }
                else {
                    printWriter.print("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                    tokenPrintWriter.print("<symbol>" + tokenizer.symbol() + "</symbol>\n");
                }
                compileTerm();
            }
            else {
                tokenizer.pointerBack();
                break;
            }
        }while(true);
        printWriter.print("</expression>\n");
    }

    private void compileExpressionList() {
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == ')') {
            tokenizer.pointerBack();
        }
        else {
            tokenizer.pointerBack();
            compileExpression();
            do {
                tokenizer.advance();
                if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == ',') {
                    printWriter.print("<symbol>,</symbol>\n");
                    tokenPrintWriter.print("<symbol>,</symbol>\n");
                    compileExpression();
                }
                else {
                    tokenizer.pointerBack();
                    break;
                }
            }while(true);
        }
    }

    private void requireSymbol(char symbol) {
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TYPE.SYMBOL && tokenizer.symbol() == symbol) {
            printWriter.print("<symbol>" + symbol + "</symbol>\n");
            tokenPrintWriter.print("<symbol>" + symbol + "</symbol>\n");
        }
        else {
            throw new IllegalStateException("expected '" + symbol + "'");
        }
    }
}