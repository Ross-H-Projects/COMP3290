package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;


public class NREPT {
    // <repstat>    --> repeat ( <asgnlist>  ) <stats> until <bool>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NREPTNode = new TreeNode(TreeNodeType.NUNDEF);

        // repeat
        if (!p.currentTokenIs(Token.TREPT)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'repeat'");
            System.out.println("NREPT :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // (
        if (!p.currentTokenIs(Token.TLPAR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character (");
            System.out.println("NREPT :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <asgnlist>
        TreeNode asgnList = new TreeNode(TreeNodeType.NUNDEF);
        if (p.currentTokenIs(Token.TIDEN)) {
            asgnList = NASGNS.generateTreeNode(p);
        }

        // )
        if (!p.currentTokenIs(Token.TRPAR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character )");
            System.out.println("NREPT :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <stats>
        TreeNode stats = NSTATS.generateTreeNode(p);

        // until
        if (!p.currentTokenIs(Token.TUNTL)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'until'");
            System.out.println("NREPT :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <bool>
        TreeNode bool = NBOOL.generateTreeNode(p);

        NREPTNode.setValue(TreeNodeType.NREPT);
        NREPTNode.setLeft(asgnList);
        NREPTNode.setMiddle(stats);
        NREPTNode.setRight(bool);
        return NREPTNode;
    }
}
