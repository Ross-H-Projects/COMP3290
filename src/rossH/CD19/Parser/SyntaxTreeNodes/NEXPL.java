package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <elist>     --> <bool> , <elist>
    <elist>     --> <bool>

    NEW RULES:
    <elist>     --> <bool> <opt_elist>
    <opt_elist> --> , <elist> | ε
*/

public class NEXPL {

    // <elist>     --> <bool> <opt_elist>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NEXPLNode = new TreeNode(TreeNodeType.NUNDEF);

        // <bool>
        TreeNode bool = NBOOL.generateTreeNode(p);
        if (bool.getNodeType() == TreeNodeType.NUNDEF) {
            try {
                errorRecovery(p);
            } catch (Exception e) {
                return NEXPLNode;
            }
        }

        // <opt_elist>
        TreeNode elistOptional = elistOptional(p);

        // bool properly defined AND elistOptional either non-existant or contains errors
        // so we will just return bool
        if (bool.getNodeType() != TreeNodeType.NUNDEF &&
                (elistOptional == null || elistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return bool;
        }

        // bool contains errors AND elistOptional properly defined
        // so we will just return elistOptional
        if (elistOptional != null && elistOptional.getNodeType() != TreeNodeType.NUNDEF
                && bool.getNodeType() == TreeNodeType.NUNDEF) {
            return elistOptional;
        }

        // bool contains errors and elistOptional either non-existant or contains errors
        if (bool.getNodeType() == TreeNodeType.NUNDEF &&
                (elistOptional == null || elistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return NEXPLNode;
        }

        // getting here implies both bool and elistOptional
        // were successfully defined
        NEXPLNode.setValue(TreeNodeType.NEXPL);
        NEXPLNode.setLeft(bool);
        NEXPLNode.setRight(elistOptional);
        return NEXPLNode;
    }

    // <opt_elist> --> , <elist> | ε
    public static TreeNode elistOptional (CD19Parser p) {

        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <elist>
        TreeNode elist = generateTreeNode(p);
        return elist;
    }

    public static void errorRecovery (CD19Parser p) throws Exception {
        // we need to go to the next ',' token or the next ')' token
        // which ever occurs first

        int nextComma = p.nextTokenOccursAt(Token.TCOMA);
        int nextRightParanthesis = p.nextTokenOccursAt(Token.TRPAR);

        if (nextRightParanthesis != -1 && nextComma != -1 && nextComma < nextRightParanthesis) {
            p.tokensJumpTo(nextComma);
        } else if (nextRightParanthesis != -1) {
            p.tokensJumpTo(nextRightParanthesis);
        } else {

            throw new Exception("Unable to recover.");
        }
    }
}
