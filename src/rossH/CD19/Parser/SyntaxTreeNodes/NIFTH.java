/*
    Despite This file being named NIFTH it returns both NIFTH and NIFTE

*/

package rossH.CD19.Parser.SyntaxTreeNodes;

import com.sun.source.tree.Tree;
import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    NIFTH :: <ifstat>      --> if ( <bool> ) <stats> end
    NIFTE :: <ifstat>      --> if ( <bool>) <stats> else <stats> end

    NEW RULES:
    NIFTH :: <ifstat>                --> if ( <bool> ) <stats> <opt_else> end
    NIFTE :: <opt_else>              --> else <stats> | ε

 */

public class NIFTH {
    // <ifstat>                --> if ( <bool> ) <stats> <opt_else> end
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NIFTHNode = new TreeNode(TreeNodeType.NUNDEF);

        // if
        if (!p.currentTokenIs(Token.TIFTH)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'if'");
            System.out.println("NIFTH :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // (
        if (!p.currentTokenIs(Token.TLPAR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character (");
            System.out.println("NIFTH :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <bool>
        TreeNode bool = NBOOL.generateTreeNode(p);

        // )
        if (!p.currentTokenIs(Token.TRPAR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character )");
            System.out.println("NIFTH :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <stats>
        TreeNode stats = NSTATS.generateTreeNode(p);

        // <opt_else>
        TreeNode  elseStats = elseIfOptional(p);

        // end
        if (!p.currentTokenIs(Token.TEND)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'end'");
            System.out.println("NIFTH :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        if (elseStats != null) {
            TreeNode NIFTENode = new TreeNode(TreeNodeType.NIFTE);
            NIFTENode.setLeft(bool);
            NIFTENode.setMiddle(stats);
            NIFTENode.setRight(elseStats);
            return NIFTENode;
        }

        NIFTHNode.setValue(TreeNodeType.NIFTH);
        NIFTHNode.setLeft(bool);
        NIFTHNode.setRight(stats);
        return NIFTHNode;
    }

    // <opt_else>              --> else <stats> | ε
    public static TreeNode elseIfOptional (CD19Parser p) {

        // ε
        if (!p.currentTokenIs(Token.TELSE)) {
            return null;
        }

        // else
        p.moveToNextToken();

        // <stats>
        TreeNode stats = NSTATS.generateTreeNode(p);
        return stats;
    }
}
