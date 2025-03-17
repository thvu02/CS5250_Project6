import java.io.*;

public class Parser {
    private BufferedReader reader;
    private String currLine;
    private String nextLine;

    public Parser(File fileName) throws IOException{
        try {
            this.reader = new BufferedReader(new FileReader(fileName));
            this.currLine = null;
            this.nextLine = this.getLine();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException e) {
            System.out.println("Error reading file: " + fileName);
        }
    }

    private String getLine() throws IOException {
        String line;

        line = this.reader.readLine();
        if (line == null)
            return null;
        while (line.trim().isEmpty() || line.trim().startsWith("//") || line == null) {
            if (line == null)
                return null;
            line = this.reader.readLine();
        }

        // remove comments if necessary
        int commentLocation = line.indexOf("//");
        if (commentLocation != -1) {
            line = line.substring(0, commentLocation);
        }
        return line;
    }

    public void close() {
        try {
            this.reader.close();
        } catch (IOException e) {
            System.out.println("Error closing file");
        }
    }

    public boolean hasMoreCommands() {
        return this.nextLine != null;
    }

    public void advance() {
        this.currLine = this.nextLine;
        try {
            this.nextLine = getLine();
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }

    public String commandType() {
        String content = this.currLine.trim();

        if (content.startsWith("(") && content.endsWith(")"))
            return "L_COMMAND";
        else if (content.startsWith("@"))
            return "A_COMMAND";
        else
            return "C_COMMAND";
    }

    public String symbol() {
        String content = this.currLine.trim();

        if (this.commandType().equals("L_COMMAND"))
            return content.substring(1, currLine.length() - 1);
        else if (this.commandType().equals("A_COMMAND"))
            return content.substring(1);
        else
            return null;
    }

    public String dest() {
        String content = this.currLine.trim();
        if (content.contains("="))
            return content.substring(0, content.indexOf("="));
        return null;
    }

    public String comp() {
        String content = this.currLine.trim();
        if (content.contains("="))
            content = content.substring(content.indexOf("=") + 1);
        if (content.contains(";"))
            return content.substring(0, content.indexOf(";"));
        return content;
    }

    public String jump() {
        String content = this.currLine.trim();
        if (content.contains(";"))
            return content.substring(content.indexOf(";") + 1);
        return null;
    }

    @Override
    public void finalize() {
        this.close();
    }
}
