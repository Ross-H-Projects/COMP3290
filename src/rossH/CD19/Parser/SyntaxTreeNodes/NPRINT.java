package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Scanner.Token;

// <iostat>     --> print <prlist>
public class NPRINT {
    public static TreeNode generateTreeNode (CD19Parser p, SymbolTable symbolTable) {
        TreeNode NPRINTNode = new TreeNode(TreeNodeType.NUNDEF);

        if (!p.currentTokenIs(Token.TPRIN)) {
            p.generateSyntaxError("expected the keyword 'print'.");
            return NPRINTNode;
        }
        p.moveToNextToken();

        // <prlist>
        TreeNode prlist = NPRLST.generateTreeNode(p, symbolTable);
        if (prlist.getNodeType() == TreeNodeType.NUNDEF) {
            return NPRINTNode;
        }

        NPRINTNode.setValue(TreeNodeType.NPRINT);
        NPRINTNode.setLeft(prlist);
        return NPRINTNode;
    }
}
