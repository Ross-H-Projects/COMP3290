package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <dlist>     --> <decl> , <dlist>
    <dlist>     --> <decl>

    NEW RULES:
    <dlist>     --> <decl> <opt_dlist>
    <opt_dlist> --> , <dlist> | ε
 */
public class NDLIST {
    // <dlist>     --> <decl> <opt_dlist>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NDLISTNode = new TreeNode(TreeNodeType.NUNDEF);

        // <decl>
        TreeNode decl = sdeclOrArrdecl(p);

        // <opt_dlist>
        TreeNode dlistOptional = dlistOptional(p);
        if (dlistOptional == null) {
            return decl;
        }

        NDLISTNode.setValue(TreeNodeType.NDLIST);
        NDLISTNode.setLeft(decl);
        NDLISTNode.setRight(dlistOptional);
        return NDLISTNode;
    }

    //  <opt_dlist> --> , <dlist> | ε
    public static TreeNode dlistOptional (CD19Parser p) {
        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <dlist>
        TreeNode dlist = generateTreeNode(p);
        return dlist;
    }

    // <decl>        --> <arrdecl>
    // <decl>        --> <sdecl>
    public static TreeNode sdeclOrArrdecl (CD19Parser p) {
        TreeNode decl = new TreeNode(TreeNodeType.NUNDEF);
        // <arrdecl>    --> <id> : <typeid>
        // <sdecl>      --> <id> : <stype>

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            System.out.println("NDLIST :: sdeclOArrdecl :: expected identifier ::  ERROR RECOVERY - exiting...");
            System.exit(1);
        }

        // :
        if (p.getTokenAhead(1).value() != Token.TCOLN) {
            System.out.println("NDLIST :: sdeclOArrdecl :: expected ':' :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }

        // sdecl
        int potentialStype = p.getTokenAhead(2).value();
        if (potentialStype == Token.TINTG || potentialStype == Token.TREAL || potentialStype == Token.TBOOL) {
            TreeNode sdecl = NSDLST.sdecl(p);
            return sdecl;
        }

        // arrdecl
        if (potentialStype == Token.TIDEN) {
            TreeNode arrdecl = NARRD.generateTreeNode(p);
            return arrdecl;
        }

        // if the token after <id> : ... is not an identifier (typeid)
        // or integer, real, or boolean, then we've experienced an error
        p.generateSyntaxError("expected 'integer', 'real', 'boolean', or an identifer in locals declaration of function.");
        return decl;
    }
}
