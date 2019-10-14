package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

public class NFOR {

    // for ( <asgnlist> ; <bool> ) <stats> end
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NFORNode = new TreeNode(TreeNodeType.NUNDEF);

        // for
        if (!p.currentTokenIs(Token.TFOR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'for'");
            System.out.println("NFOR :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // (
        if (!p.currentTokenIs(Token.TLPAR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character (");
            System.out.println("NFOR :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <asgnlist>
        TreeNode asgnList = new TreeNode(TreeNodeType.NUNDEF);
        if (p.currentTokenIs(Token.TIDEN)) {
            asgnList = NASGNS.generateTreeNode(p);
        }

        // ;
        if (!p.currentTokenIs(Token.TSEMI)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character ;");
            System.out.println("NFOR :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <bool>
        TreeNode bool = NBOOL.generateTreeNode(p);

        // )
        if (!p.currentTokenIs(Token.TRPAR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character )");
            System.out.println("NFOR :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <stats>
        TreeNode stats = NSTATS.generateTreeNode(p);

        // end
        if (!p.currentTokenIs(Token.TEND)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'end'");
            System.out.println("NFOR :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        NFORNode.setValue(TreeNodeType.NFOR);
        NFORNode.setLeft(asgnList);
        NFORNode.setMiddle(bool);
        NFORNode.setRight(stats);
        return NFORNode;
    }
}
