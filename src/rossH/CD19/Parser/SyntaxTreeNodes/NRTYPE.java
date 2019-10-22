package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Scanner.Token;

public class NRTYPE {
    // <structid> is <fields> end
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NRTYPENode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        // <structid>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an array name identifier.");
            // prematurely end parsing due to irrecoverable error
            return NRTYPENode;
        }
        // insert struct identifier into symbol table
        currentToken = p.getCurrentToken();
        SymbolTableRecord stRec = p.insertSymbolIdentifier(currentToken);
        NRTYPENode.setSymbolRecord(stRec);
        p.moveToNextToken();

        // is
        if (!p.currentTokenIs(Token.TIS)) {
            p.generateSyntaxError("expected keyword 'is'.");
            // prematurely end parsing due to irrecoverable error
            return NRTYPENode;
        }
        p.moveToNextToken();

        // <fields>
        TreeNode fields = NFLIST.generateTreeNode(p);
        if (fields.getNodeType() == TreeNodeType.NUNDEF) {
            return NRTYPENode;
        }

        // end
        if (!p.currentTokenIs(Token.TEND)) {
            p.generateSyntaxError("expected the keyword 'end'.");
            // prematurely end parsing due to irrecoverable error
            return NRTYPENode;
        }
        p.moveToNextToken();


        NRTYPENode.setValue(TreeNodeType.NRTYPE);
        NRTYPENode.setLeft(fields);
        return NRTYPENode;
    }
}
