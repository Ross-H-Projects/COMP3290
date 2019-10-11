package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;
import rossH.CD19.Scanner.Token;

// RULES:
// <var> --> <id>
public class NSIVM {
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NSIVMNode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        if (p.currentTokenIs(Token.TIDEN)) {
            System.out.println("NISVM :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        currentToken = p.getCurrentToken();
        SymbolTableRecord stRec = p.insertSymbolIdentifier(currentToken);
        NSIVMNode.setSymbolRecord(stRec);

        return NSIVMNode;
    }
}
