package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

public class NFUNCS {
    // <funcs>      --> <func> <funcs> | ε
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NFUNCS = new TreeNode(TreeNodeType.NUNDEF);

        // criteria under which <funcs> --> ε
        // is when the next token is not the keyword 'function'
        if (!p.currentTokenIs(Token.TFUNC)) {
            return null;
        }

        // <func>
        TreeNode func = NFUND.generateTreeNode(p);

        // <funcs>
        TreeNode funcs = generateTreeNode(p);
        if (funcs != null) {
            NFUNCS.setRight(funcs);
        }

        NFUNCS.setValue(TreeNodeType.NFUNCS);
        NFUNCS.setLeft(func);
        return NFUNCS;

    }
}
