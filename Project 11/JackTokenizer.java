import java.io.*;
import java.util.*;
import java.util.regex.*;

public class JackTokenizer {
    public static enum TYPE {KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST, NONE};
    public static enum KEYWORD {CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN,
                                CHAR, VOID, VAR, STATIC, FIELD, LET, DO, IF, ELSE,
                                WHILE, RETURN, TRUE, FALSE, NULL, THIS};

    private String curToken;
    private TYPE curTokenType;
    private int pointer;
    private ArrayList<String> tokens;

    private static Pattern tokenPatterns;
    private static String keywordReg;
    private static String symbolReg;
    private static String intReg;
    private static String stringReg;
    private static String idReg;

    private static HashMap<String, KEYWORD> keywordMap = new HashMap<String, KEYWORD>();
    private static HashSet<Character> opSet = new HashSet<Character>();

    static {
        keywordMap.put("class", KEYWORD.CLASS); keywordMap.put("constructor", KEYWORD.CONSTRUCTOR); keywordMap.put("function", KEYWORD.FUNCTION);
        keywordMap.put("method", KEYWORD.METHOD); keywordMap.put("field", KEYWORD.FIELD); keywordMap.put("static", KEYWORD.STATIC);
        keywordMap.put("var", KEYWORD.VAR); keywordMap.put("int", KEYWORD.INT); keywordMap.put("char", KEYWORD.CHAR);
        keywordMap.put("boolean", KEYWORD.BOOLEAN); keywordMap.put("void", KEYWORD.VOID); keywordMap.put("true", KEYWORD.TRUE);
        keywordMap.put("false", KEYWORD.FALSE); keywordMap.put("null", KEYWORD.NULL); keywordMap.put("this", KEYWORD.THIS);
        keywordMap.put("let", KEYWORD.LET); keywordMap.put("do", KEYWORD.DO); keywordMap.put("if", KEYWORD.IF);
        keywordMap.put("else", KEYWORD.ELSE); keywordMap.put("while", KEYWORD.WHILE); keywordMap.put("return", KEYWORD.RETURN);

        opSet.add('+'); opSet.add('-'); opSet.add('*'); opSet.add('/'); opSet.add('&'); opSet.add('|');
        opSet.add('<'); opSet.add('>'); opSet.add('=');
    }

    public JackTokenizer(File input) {
        try {
            Scanner scanner = new Scanner(input);
            String preprocessed = "";
            String line = "";

            // remove comments and empty lines
            while(scanner.hasNext()) {
                line = scanner.nextLine();
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex);
                    line = line.trim();
                }
                else
                    line = line.trim();
                if (line.length() > 0)
                    preprocessed += line + "\n";
            }

            // remove block comments
            int blockStart = preprocessed.indexOf("/*");
            if (blockStart == -1) 
                preprocessed = preprocessed.trim();
            else {
                String temp = preprocessed;
                int blockEnd = preprocessed.indexOf("*/");
                while (blockStart != -1) {
                    if (blockEnd == -1) {
                        preprocessed = preprocessed.substring(0, blockStart-1);
                        preprocessed = preprocessed.trim();
                        break;
                    }
                    else {
                        temp = temp.substring(0, blockStart) + temp.substring(blockEnd + 2);
                        blockStart = temp.indexOf("/*");
                        blockEnd = temp.indexOf("*/");
                    }
                }
                // if break, do nothing; else set to trimmed temp
                if (blockStart == -1)
                    preprocessed = temp.trim();
            }

            // initialize regex
            keywordReg = "";
            for (String key: keywordMap.keySet())
                keywordReg += key + "|";
            symbolReg = "[\\&\\*\\+\\(\\)\\.\\/\\,\\-\\]\\;\\~\\}\\|\\{\\>\\=\\[\\<]";
            intReg = "[0-9]+";
            stringReg = "\"[^\"\n]*\"";
            idReg = "[a-zA-Z_]\\w*";
            tokenPatterns = Pattern.compile(idReg + "|" + keywordReg + symbolReg + "|" + intReg + "|" + stringReg);
            
            Matcher m = tokenPatterns.matcher(preprocessed);
            tokens = new ArrayList<String>();
            pointer = 0;

            while (m.find())
                tokens.add(m.group());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }  
        curToken = "";
        curTokenType = TYPE.NONE;
    }

    public boolean hasMoreTokens() {
        return pointer < tokens.size();
    }

    public void advance() {
        if (hasMoreTokens()) {
            curToken = tokens.get(pointer);
            pointer++;
        }
        else {
            throw new IllegalStateException("no more tokens to advance");
        }
        if (curToken.matches(keywordReg))
            curTokenType = TYPE.KEYWORD;
        else if (curToken.matches(symbolReg))
            curTokenType = TYPE.SYMBOL;
        else if (curToken.matches(intReg))
            curTokenType = TYPE.INT_CONST;
        else if (curToken.matches(stringReg))
            curTokenType = TYPE.STRING_CONST;
        else if (curToken.matches(idReg))
            curTokenType = TYPE.IDENTIFIER;
        else
            throw new IllegalArgumentException("unknown token type: " + curToken);
    }

    public String getCurrentToken() {
        return curToken;
    }

    public TYPE tokenType() {
        return curTokenType;
    }

    public KEYWORD keyWord() {
        if (curTokenType != TYPE.KEYWORD) {
            throw new IllegalStateException("current token isn't a keyword: " + curToken);
        }
        return keywordMap.get(curToken);
    }

    public char symbol() {
        if (curTokenType != TYPE.SYMBOL) {
            throw new IllegalStateException("current token isn't a symbol: " + curToken);
        }
        return curToken.charAt(0);
    }

    public String identifier() {
        if (curTokenType != TYPE.IDENTIFIER) {
            throw new IllegalStateException("current token isn't an indentifier: " + curToken);
        }
        return curToken;
    }

    public int intVal() {
        if (curTokenType != TYPE.INT_CONST) {
            throw new IllegalStateException("current token isn't an integer constant: " + curToken);
        }
        return Integer.parseInt(curToken);
    }

    public String stringVal() {
        if (curTokenType != TYPE.STRING_CONST) {
            throw new IllegalStateException("current token isn't an string constant: " + curToken);
        }
        return curToken.substring(1, curToken.length()-1);
    }

    public void pointerBack() {
        if (pointer > 0) {
            pointer--;
            curToken = tokens.get(pointer);
        }
    }

    public boolean isOp() {
        return opSet.contains(symbol());
    }
}