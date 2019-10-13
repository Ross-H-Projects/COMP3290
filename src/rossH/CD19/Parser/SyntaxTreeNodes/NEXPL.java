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

        // <opt_elist>
        TreeNode elistOptional = elistOptional(p);
        if (elistOptional == null) {
            return bool;
        }

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
}
