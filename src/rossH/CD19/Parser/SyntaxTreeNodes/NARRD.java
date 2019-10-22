package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Scanner.Token;

public class NARRD {
    // <arrdecl>      --> <id> : <typeid>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NARRDNode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an identifier in array declaration.");
            // prematurely end parsing due to irrecoverable error
            return NARRDNode;
        }
        // insert program id identifier into symbol table
        currentToken = p.getCurrentToken();
        SymbolTableRecord stRec = p.insertSymbolIdentifier(currentToken);
        NARRDNode.setSymbolRecord(stRec);
        p.moveToNextToken();

        // :
        if (!p.currentTokenIs(Token.TCOLN)) {
            p.generateSyntaxError("Expected the character ':'");
            // prematurely end parsing due to irrecoverable error
            return NARRDNode;
        }
        p.moveToNextToken();

        // <typeid>
        TreeNode typeid = NSIVM.generateTreeNode(p);
        if (typeid.getNodeType() == TreeNodeType.NUNDEF) {
            return NARRDNode;
        }

        NARRDNode.setValue(TreeNodeType.NARRD);
        NARRDNode.setLeft(typeid);
        return NARRDNode;
    }
}
