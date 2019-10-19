package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <initlist>      --> <init> , <initlist> | ε
    <initlist>      --> <init>

    NEW RULES:
    <initlist>      --> <init> <opt_initlist> | ε
    <opt_initlist>  --> , <initlist> | ε
*/

public class NILIST {

    // <initlist>      --> <init> <opt_initlist> | ε
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NILISTNode = new TreeNode(TreeNodeType.NUNDEF);

        // <init>
        TreeNode init = NINIT.generateTreeNode(p);

        // <opt_initlist>
        TreeNode initlistOptional = initlistOptional(p);
        if (initlistOptional == null) {
            return init;
        }

        NILISTNode.setValue(TreeNodeType.NILIST);
        NILISTNode.setLeft(init);
        NILISTNode.setRight(initlistOptional);
        return NILISTNode;
    }

    // <opt_initlist>  --> , <initlist> | ε
    public static TreeNode initlistOptional (CD19Parser p) {
        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <initlist>
        TreeNode initlist = generateTreeNode(p);
        return initlist;
    }
}
