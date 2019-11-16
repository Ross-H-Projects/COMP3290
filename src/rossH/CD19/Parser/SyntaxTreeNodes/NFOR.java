package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Scanner.Token;

public class NFOR {

    // for ( <asgnlist> ; <bool> ) <stats> end
    public static TreeNode generateTreeNode (CD19Parser p, SymbolTable symbolTable) {
        TreeNode NFORNode = new TreeNode(TreeNodeType.NUNDEF);

        // for
        if (!p.currentTokenIs(Token.TFOR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'for'.");
            return NFORNode;
        }
        p.moveToNextToken();

        // (
        if (!p.currentTokenIs(Token.TLPAR)) {
            errorRecoveryToEnd(p);
            p.getCurrentToken();
            p.generateSyntaxError("expected character '('.");
            return NFORNode;
        }
        p.moveToNextToken();

        // <asgnlist>
        TreeNode asgnList = new TreeNode(TreeNodeType.NUNDEF);
        if (p.currentTokenIs(Token.TIDEN)) {
            asgnList = NASGNS.generateTreeNode(p, symbolTable);
            // <asgnlist> is not a necessary part for NFOR
            // so just set it null
            if (asgnList.getNodeType() == TreeNodeType.NUNDEF) {
                asgnList = null;
            }
        }

        // ;
        if (!p.currentTokenIs(Token.TSEMI)) {
            errorRecoveryToEnd(p);
            p.generateSyntaxError("expected character ';'.");
            return NFORNode;
        }
        p.moveToNextToken();

        // <bool>
        TreeNode bool = NBOOL.generateTreeNode(p, symbolTable);
        // <bool> is a necessary part of NFOR
        // so if it fails, fail the entire NFOR
        if (bool.getNodeType() == TreeNodeType.NUNDEF) {
            errorRecoveryToEnd(p);
            return NFORNode;
        }

        // )
        if (!p.currentTokenIs(Token.TRPAR)) {
            errorRecoveryToEnd(p);
            p.getCurrentToken();
            p.generateSyntaxError("expected character ')'");
            return NFORNode;
        }
        p.moveToNextToken();

        // <stats>
        TreeNode stats = NSTATS.generateTreeNode(p, symbolTable);
        // <stats> is a necessary part of NFOR
        // so if it fails, fail the entire NFOR
        if (stats.getNodeType() == TreeNodeType.NUNDEF) {
            errorRecoveryToEnd(p);
            return NFORNode;
        }

        // end
        if (!p.currentTokenIs(Token.TEND)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'end'.");
            return NFORNode;
        }
        p.moveToNextToken();

        // getting here imples all necessary parts of
        // NFOR were parsed correctly
        NFORNode.setValue(TreeNodeType.NFOR);
        NFORNode.setLeft(asgnList);
        NFORNode.setMiddle(bool);
        NFORNode.setRight(stats);
        return NFORNode;
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
