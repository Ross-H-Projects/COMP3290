package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Scanner.Token;

public class NINIT {

    // <init>   --> <id> = <expr>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NINITNode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        // id
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an identifier in constants initialisation.");
            // prematurely end parsing due to irrecoverable error
            return NINITNode;
        }
        // insert program id identifier into symbol table
        currentToken = p.getCurrentToken();
        SymbolTableRecord stRec = p.insertSymbolIdentifier(currentToken);
        NINITNode.setSymbolRecord(stRec);
        p.moveToNextToken();

        // =
        if (!p.currentTokenIs(Token.TEQUL)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character =");
            System.out.println("NINIT :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <expr>
        TreeNode expr = NBOOL.expr(p);

        NINITNode.setValue(TreeNodeType.NINIT);
        NINITNode.setLeft(expr);
        return NINITNode;
    }
}
