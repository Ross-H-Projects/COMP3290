/*
    Despite This file being named NIFTH it returns both NIFTH and NIFTE

*/

package rossH.CD19.Parser.SyntaxTreeNodes;

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
            p.generateSyntaxError("expected the keyword 'if'.");
            errorRecoveryToEnd(p);
            return NIFTHNode;
        }
        p.moveToNextToken();

        // (
        if (!p.currentTokenIs(Token.TLPAR)) {
            p.generateSyntaxError("expected character '('.");
            errorRecoveryToEnd(p);
            return NIFTHNode;
        }
        p.moveToNextToken();

        // <bool>
        TreeNode bool = NBOOL.generateTreeNode(p);
        if (bool.getNodeType() == TreeNodeType.NUNDEF) {
            errorRecoveryToEnd(p);
            return NIFTHNode;
        }

        // )
        if (!p.currentTokenIs(Token.TRPAR)) {
            p.generateSyntaxError("expected character ')'.");
            errorRecoveryToEnd(p);
            return NIFTHNode;
        }
        p.moveToNextToken();

        // <stats>
        TreeNode stats = NSTATS.generateTreeNode(p);
        if (stats.getNodeType() == TreeNodeType.NUNDEF) {
            errorRecoveryToEnd(p);
            return NIFTHNode;
        }

        // <opt_else>
        TreeNode  elseStats = elseIfOptional(p);
        if (elseStats != null && elseStats.getNodeType() == TreeNodeType.NUNDEF) {
            errorRecoveryToEnd(p);
            return NIFTHNode;
        }

        // end
        if (!p.currentTokenIs(Token.TEND)) {
            p.generateSyntaxError("expected the keyword 'end'.");
            return NIFTHNode;
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

    public static void errorRecoveryToEnd (CD19Parser p) {
        int nextEndOccurence = p.nextTokenOccursAt(Token.TEND);
        try {
            if (nextEndOccurence != -1) {
                p.tokensJumpTo(nextEndOccurence);
            }
        } catch (Exception e) {

        }
    }
}
