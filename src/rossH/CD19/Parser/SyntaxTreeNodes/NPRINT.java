package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

// <iostat>     --> print <prlist>
public class NPRINT {
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NPRINTNode = new TreeNode(TreeNodeType.NUNDEF);

        if (!p.currentTokenIs(Token.TPRIN)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'print'");
            System.out.println("NPRINT :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <prlist>
        TreeNode prlist = NPRLST.generateTreeNode(p);

        NPRINTNode.setValue(TreeNodeType.NPRINT);
        NPRINTNode.setLeft(prlist);
        return NPRINTNode;
    }
}
