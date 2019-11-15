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
    private List<String> semanticErrors;
    private SymbolTable symbolTableIdentifiers;
    private int tokenPos = 0;

    private boolean isSemanticallyValid;

    private int currentBaseRegister0Offset;
    private int currentBaseRegister1Offset;
    private int currentBaseRegister2Offset;

    private int amountOfConstants;
    private int amountOfDeclarationsInMainBody;


    public CD19Parser (List<Token> tokens) {
        this.tokens = tokens;
        this.symbolTableIdentifiers = new SymbolTable();
        syntaxErrors = new LinkedList<String>();
        semanticErrors = new LinkedList<String>();
        isSemanticallyValid = true;
        currentBaseRegister0Offset = 0;
        currentBaseRegister1Offset = 0;
        currentBaseRegister2Offset = 0;

        amountOfConstants = 0;
        amountOfDeclarationsInMainBody = 0;

        TreeNode.setup();
    }

    public TreeNode parse () {
        // we starting parsing by attempting to generate a token for the root node
        return NPROG.generateTreeNode(this);
    }

    public SymbolTableRecord insertSymbolIdentifier(Token token) {

        // Add to symbol Table or get Symbol Table reference
        SymbolTableRecord stRec = new SymbolTableRecord(token.value(), token.getStr());

        if (symbolTableIdentifiers.contains(stRec)) {
            // MAY NEED KEEP TRACK OF symbol table record in Token??
            stRec = symbolTableIdentifiers.getSymbolTableRecord(stRec);
        }

        symbolTableIdentifiers.setSymbolTableRecord(stRec);
        return stRec;
    }

    public void moveToNextToken () {
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
        String syntaxErrorString = "Error occured at line ";
        syntaxErrorString += " " + tokens.get(tokenPos).getLn() + ":\n\t";
        syntaxErrorString += s;
        syntaxErrors.add(syntaxErrorString);
    }

    public void generateSyntaxError (String s, int lineNo) {
        String syntaxErrorString = "Error occured at line " + lineNo + ":\n\t";
        syntaxErrorString += s;
        syntaxErrors.add(syntaxErrorString);
    }

    public void generateSemanticError (String s) {
        String semanticErrorString = "Error (semantic) occured at line";
        semanticErrorString += " " + tokens.get(tokenPos).getLn() + ":\n\t";
        semanticErrorString += s;
        semanticErrors.add(semanticErrorString);
        isSemanticallyValid = false;
    }

    public void generateSemanticError (String s, int lineNo) {
        String semanticErrorString = "Error (semantic) occured at line " + lineNo + ":\n\t";
        semanticErrorString += s;
        semanticErrors.add(semanticErrorString);
        isSemanticallyValid = false;
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

    public int getBaseReigtserOffset(int i) {
        int oldBaseRegisterOffset = -1;
        if (i == 0) {
            oldBaseRegisterOffset = currentBaseRegister0Offset;
            currentBaseRegister0Offset += 8;
        } else if (i == 1) {
            oldBaseRegisterOffset = currentBaseRegister1Offset;
            currentBaseRegister1Offset += 8;
        } else if (i == 2) {
            oldBaseRegisterOffset = currentBaseRegister2Offset;
            currentBaseRegister2Offset += 8;
        }

        return oldBaseRegisterOffset;
    }

    public void setAmountOfCounts (int amount) {
        this.amountOfConstants = amount;
    }

    public int getAmountOfConstants () {
        return this.amountOfConstants;
    }

    public void setAmountOfDeclarationsInMainBody (int amount) {
        this.amountOfDeclarationsInMainBody = amount;
    }

    public int getAmountOfDeclarationsInMainBody () {
        return this.amountOfDeclarationsInMainBody;
    }


}
