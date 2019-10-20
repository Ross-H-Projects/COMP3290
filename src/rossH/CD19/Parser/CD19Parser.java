package rossH.CD19.Parser;

import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Parser.SyntaxTreeNodes.NPROG;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Scanner.Token;
import rossH.CD19.Parser.SyntaxTreeNodes.NPROG;

import javax.swing.plaf.synth.SynthMenuBarUI;
import java.util.LinkedList;
import java.util.List;

public class CD19Parser {
    private List<Token> tokens;
    private List<String> syntaxErrors;
    private SymbolTable symbolTableIdentifiers;
    private int tokenPos = 0;

    public CD19Parser (List<Token> tokens) {
        this.tokens = tokens;
        this.symbolTableIdentifiers = new SymbolTable();
        syntaxErrors = new LinkedList<String>();
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
    }

    public void moveToNextToken () {
        System.out.print("Current Token is: " + tokens.get(tokenPos).getTokenObjString());
        System.out.print(" || Next Token is: " + tokens.get(tokenPos + 1).getTokenObjString());
        System.out.println();
        tokenPos++;

    }

    public List<String> getSyntaxErrors () {
        return syntaxErrors;
    }

    public Token getCurrentToken () {
        return tokens.get(tokenPos);
    }

    public Token getTokenAhead (int ahead) {
        if (tokenPos + ahead > (tokens.size() - 1)) {
            return null;
        }
        return tokens.get(tokenPos + ahead);
    }

    public boolean currentTokenIs (int tokenValue) {
        Token currentToken = tokens.get(tokenPos);

        return (currentToken.value() == tokenValue);
    }

    public void generateSyntaxError (String s) {
        System.out.println(s);

        String syntaxErrorString = "Error occured at line ";
        syntaxErrorString += " " + tokens.get(tokenPos).getLn() + ":\n\t";
        syntaxErrorString += s;
        syntaxErrors.add(syntaxErrorString);
    }

    public void generateSyntaxError (String s, int lineNo) {
        System.out.println(s);

        String syntaxErrorString = "Error occured at line " + lineNo + ":\n\t";
        syntaxErrorString += s;
        syntaxErrors.add(syntaxErrorString);
    }

    public void tokensJumpTo (int pos) {
        tokenPos = pos;
    }

    public int nextTokenOccursAt (int tokenValue) {
        for (int i = tokenPos; i < tokens.size(); i++) {
            if (tokens.get(i).value() == tokenValue) {
                return i;
            }
        }
        return -1;
    }
}
