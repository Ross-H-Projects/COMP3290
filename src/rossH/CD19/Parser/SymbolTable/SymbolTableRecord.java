package rossH.CD19.Parser.SymbolTable;

import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Scanner.Token;

public class SymbolTableRecord {
    private int symbolTableKey;
    private int tokenType;
    private String lexeme;
    private SymbolDataType symbolDataType;
    private int baseRegister;
    private int offset;

    public SymbolTableRecord () {
        this.tokenType = Token.TUNDF;
        this.lexeme = "";
        this.symbolDataType = null;


    }

    public SymbolTableRecord (int tokenType, String lexeme) {
        this.tokenType = tokenType;
        this.lexeme = lexeme;
        this.symbolTableKey = lexeme.hashCode();
        this.symbolDataType = null;
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

    public SymbolDataType getSymbolDataType () {
        return this.symbolDataType;
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
        int result = symbolTableKey;
        result = 31 * result + tokenType;
        result = 31 * result + (lexeme != null ? lexeme.hashCode() : 0);
        //result = 31 * result + (scope != null ? scope.hashCode() : 0);
        return result;

    }

    public void setDataType(Token currentToken, int baseRegister, int offset) {
        this.baseRegister = baseRegister;
        this.offset = offset;

        if (currentToken.value() == Token.TINTG) {
            this.symbolDataType = SymbolDataType.Integer;
        } else if (currentToken.value() == Token.TREAL) {
            this.symbolDataType = SymbolDataType.Real;
        } else if (currentToken.value() == Token.TBOOL) {
            this.symbolDataType = SymbolDataType.Boolean;
        } else {
            this.symbolDataType = SymbolDataType.Void;
        }
    }

    public int getBaseRegister () {
        return this.baseRegister + 90;
    }

    public int getOffset () {
        return this.offset;
    }
}