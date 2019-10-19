package rossH.CD19.Parser.SyntaxTreeNodes;

import com.sun.source.tree.Tree;
import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <params>        --> <param> , <params>
    <params>        --> <param>

    NEW RULES:
    <params>        --> <param> <opt_params>
    <opt_paramas>   --> , <params>  | ε
 */
public class NPLIST {
    // <params>        --> <param> <opt_params>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NPLISTNode = new TreeNode(TreeNodeType.NUNDEF);

        // <param>
        TreeNode param = param(p);

        // <opt_params>
        TreeNode paramsOptional = paramsOptional(p);
        if (paramsOptional == null) {
            return param;
        }

        NPLISTNode.setValue(TreeNodeType.NPLIST);
        NPLISTNode.setLeft(param);
        NPLISTNode.setRight(paramsOptional);
        return NPLISTNode;
    }

    // <param>        --> <sdecl>
    // <param>        --> <arrdecl>
    // <param>        --> const <arrdecl>
    public static TreeNode param (CD19Parser p) {
        // determine which piece of grammar we are parsing

        // <param>      --> const <arrdecl>
        if (p.currentTokenIs(Token.TCONS)) {
            return constantArrDecl(p);
        }

        // <param>        --> <arrdecl>
        // <param>        --> <sdecl>
        return sdeclOrArrdecl(p);
    }

    public static TreeNode constantArrDecl (CD19Parser p) {

    }

    // <param>        --> <arrdecl>
    // <param>        --> <sdecl>
    public static TreeNode sdeclOrArrdecl (CD19Parser p) {
        TreeNode decl = new TreeNode(TreeNodeType.NUNDEF);
        // <arrdecl>    --> <id> : <typeid>
        // <sdecl>      --> <id> : <stype>

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            System.out.println("NPLIST :: sdeclOArrdecl :: expected identifier ::  ERROR RECOVERY - exiting...");
            System.exit(1);
        }

        // :
        if (!p.getTokenAhead(1).value() != Token.TSEMI) {
            System.out.println("NPLIST :: sdeclOArrdecl :: expected ':' :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }

        // sdecl
        int potentialStype = p.getTokenAhead(2).value();
        if (potentialStype == Token.TINTG || potentialStype == Token.TREAL || potentialStype == Token.TBOOL) {
            decl
        }

        // arrdecl

        // error
    }

    // <opt_paramas>   --> , <params>  | ε
    public static TreeNode paramsOptional (CD19Parser p) {
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        TreeNode params = generateTreeNode(p);
        return params;
    }
}
