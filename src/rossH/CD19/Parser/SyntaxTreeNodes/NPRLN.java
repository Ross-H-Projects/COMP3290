package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

// <iostat>     --> printline <prlist>
public class NPRLN {
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NPRlNNode = new TreeNode(TreeNodeType.NUNDEF);

        if (!p.currentTokenIs(Token.TPRLN)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'printline'");
            System.out.println("NPRINT :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <prlist>
        TreeNode prlist = NPRLST.generateTreeNode(p);

        NPRlNNode.setValue(TreeNodeType.NPRLN);
        NPRlNNode.setLeft(prlist);
        return NPRlNNode;
    }
}
