package rossH.CD19.Parser.SymbolTable;

import rossH.CD19.Scanner.Token;

public class SymbolTableRecord {
    private int symbolTableKey;
    private int tokenType;
    private String lexeme;

    public SymbolTableRecord () {
        this.tokenType = Token.TUNDF;
        this.lexeme = "";
    }

    public SymbolTableRecord (int tokenType, String lexeme) {
        this.tokenType = tokenType;
        this.lexeme = lexeme;
        this.symbolTableKey = lexeme.hashCode();
    }

    public void setSymbolTableKey (int key) {
        this.symbolTableKey = key;
    }

    public int getSymbolTableKey () {
        return this.symbolTableKey;
    }

    public void setTokenType (int tokenType) {
        this.tokenType = tokenType;
    }

    public int getTokenType () {
        return this.tokenType;
    }

    public void setLexeme (String lexeme) {
        this.lexeme = lexeme;
    }

    public String getLexeme () {
        return this.lexeme;
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }

    @Override
    public int hashCode() {
        return lexeme.hashCode();
    }
}