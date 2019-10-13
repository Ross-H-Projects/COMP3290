package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

public class NINPUT {
    // <iostat> --> input <vlist>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NINPUTNode = new TreeNode(TreeNodeType.NUNDEF);

        // input
        if (!p.currentTokenIs(Token.TINPT)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'input'");
            System.out.println("NINPUT :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        NINPUTNode.setValue(TreeNodeType.NINPUT);

        // <vlist>
        TreeNode vlist = NVLIST.generateTreeNode(p);

        NINPUTNode.setLeft(vlist);
        return NINPUTNode;
    }
}
