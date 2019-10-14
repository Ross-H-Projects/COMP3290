package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <alist>     --> <asgnstat> , <alist>
    <aslist>    --> <asgnstat>

    NEW RULES:
    <alist>     --> <asgnstat> <opt_alist>
    <opt_alist> --> , <alist> | ε

*/

public class NASGNS {
    // <alist>  --> <asgnstat> <opt_alist>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NASGNSNode = new TreeNode(TreeNodeType.NUNDEF);

        // <asgnstat>
        TreeNode asgnStat = NSTATS.asgnStat(p);

        // <opt_alist>
        TreeNode alistOptional = alistOptional(p);
        if (alistOptional == null) {
            return asgnStat;
        }

        NASGNSNode.setValue(TreeNodeType.NASGNS);
        NASGNSNode.setLeft(asgnStat);
        NASGNSNode.setRight(alistOptional);
        return NASGNSNode;
    }

    // <opt_alist> --> , <alist> | ε
    public static TreeNode alistOptional (CD19Parser p) {

        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <alist>
        TreeNode alist = generateTreeNode(p);
        return alist;
    }
}
