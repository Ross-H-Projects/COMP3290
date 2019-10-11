package rossH.CD19.Parser;

import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Parser.SyntaxTreeNodes.NPROG;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Scanner.Token;
import rossH.CD19.Parser.SyntaxTreeNodes.NPROG;

import javax.swing.plaf.synth.SynthMenuBarUI;
import java.util.List;

public class CD19Parser {
    private List<Token> tokens;
    private SymbolTable symbolTableIdentifiers;
    private int tokenPos = 0;

    public CD19Parser (List<Token> tokens) {
        this.tokens = tokens;
        this.symbolTableIdentifiers = new SymbolTable();
        TreeNode.setup();
    }

    public TreeNode parse () {
        // we starting parsing by attempting to generate a token for the root node
        return NPROG.generateTreeNode(this);
    }

    public SymbolTableRecord insertSymbolIdentifier(Token token) {

        // Add to symbol Table or get Symbol Table reference
        SymbolTableRecord stRec = new SymbolTableRecord(token.value(), token.getStr());

        if (!symbolTableIdentifiers.contains(stRec)) {
            // MAY NEED KEEP TRACK OF symbol table record in Token??
            stRec = symbolTableIdentifiers.getSymbolTableRecord(stRec);
        }

        symbolTableIdentifiers.setSymbolTableRecord(stRec);
        return stRec;

        /*
        if (identifiers.contains(rec)) rec = identifiers.lookup(rec);
        else identifiers.insert(rec);

        // Update token with symbol table record reference
        identifier.setSymbolTableRecord(rec);
         */
    }

    public void moveToNextToken () {
        tokenPos++;
    }

    public Token getCurrentToken () {
        return tokens.get(tokenPos);
    }

    public Token getTokenAhead (int ahead) {
        if (tokenPos + ahead > (tokens.size() - 1) {
            return null;
        }
        return tokens.get(tokenPos + ahead);
    }

    public boolean currentTokenIs(int tokenValue) {
        Token currentToken = tokens.get(tokenPos);

        return (currentToken.value() == tokenValue);
    }

    public void generateSyntaxError(String s) {
        // stub
    }
}
