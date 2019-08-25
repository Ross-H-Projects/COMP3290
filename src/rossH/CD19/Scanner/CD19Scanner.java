package rossH.CD19.Scanner;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Vector;

public class CD19Scanner {
    private String srcCode;
    private int srcCodePos;

    private String currentLine;
    private int currentLineNo;
    private int currentColumn;

    private String lexemeBuffer;

    private boolean endOfFile;

    public CD19Scanner (String srcCode) {
        this.srcCode = srcCode;
        this.srcCodePos = 0;
        this.currentColumn = 1;
        this.currentLineNo = 1;
        this.lexemeBuffer = "";
        this.endOfFile = false;
    }

    public boolean eof () {
        return this.endOfFile;
    }

    public void printtoken (Token token) {
        String printString = "<" + token.value() + ", ";
        printString += token.getStr() + ", ";
        printString += token.getLn() + ", ";
        printString += token.getPos() + ">";
        System.out.println(printString);
    }

    public Token gettoken () {
        this.endOfFile = true;
        return new Token(Token.TIDEN, 1, 1, "cd19");
    }



}
