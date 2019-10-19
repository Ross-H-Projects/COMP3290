package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <arrdecls>      --> <arrdecl> , <arrdecls>
    <arrdecls>      --> <arrdecl>

    NEW RULES:
    <arrdecls>      --> <arrdecl> <opt_arrdecls>
    <opt_arrdecls>  --> , <arrdecls> | ε
 */

public class NALIST {
    // <arrdecls>      --> <arrdecl> <opt_arrdecls>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NALISTNode = new TreeNode(TreeNodeType.NUNDEF);

        // <arrdecl>
        TreeNode arrdecl = NARRD.generateTreeNode(p);

        // <opt_arrdecls>
        TreeNode arrdeclsOptional = arrdeclsOptional(p);
        if (arrdeclsOptional == null) {
            return arrdecl;
        }

        NALISTNode.setValue(TreeNodeType.NALIST);
        NALISTNode.setLeft(arrdecl);
        NALISTNode.setRight(arrdeclsOptional);
        return NALISTNode;
    }

    // <opt_arrdecls>  --> , <arrdecls> | ε
    public static TreeNode arrdeclsOptional(CD19Parser p) {

        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // arrdecls
        TreeNode arrdecls = generateTreeNode(p);
        return arrdecls;
    }
}
