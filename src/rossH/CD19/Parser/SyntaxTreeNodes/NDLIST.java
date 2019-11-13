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
        if (decl.getNodeType() == TreeNodeType.NUNDEF) {
            try {
                errorRecovery(p);
            } catch (Exception e) {
                return NDLISTNode;
            }
        }

        // <opt_dlist>
        TreeNode dlistOptional = dlistOptional(p);
        // decl properly defined AND dlistOptional either non-existant or contains errors
        // so we will just return decl
        if (decl.getNodeType() != TreeNodeType.NUNDEF &&
                (dlistOptional == null || dlistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return decl;
        }

        // decl contains errors AND dlistOptional properly defined
        // so we will just return dlistOptional
        if (dlistOptional != null && dlistOptional.getNodeType() != TreeNodeType.NUNDEF
                && decl.getNodeType() == TreeNodeType.NUNDEF) {
            return dlistOptional;
        }

        // decl contains errors and dlistOptional either non-existant or contains errors
        if (decl.getNodeType() == TreeNodeType.NUNDEF &&
                (dlistOptional == null || dlistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return NDLISTNode;
        }

        // getting here implies both decl and dlistOptional
        // were successfully defined
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
            p.generateSyntaxError("Expected an identifier");
            // prematurely end parsing due to irrecoverable error
            return decl;
        }

        // :
        if (p.getTokenAhead(1).value() != Token.TCOLN) {
            p.generateSyntaxError("Expected the character ':'.");
            // prematurely end parsing due to irrecoverable error
            return decl;
        }

        // sdecl
        int potentialStype = p.getTokenAhead(2).value();
        if (potentialStype == Token.TINTG || potentialStype == Token.TREAL || potentialStype == Token.TBOOL) {
            TreeNode sdecl = NSDLST.sdecl(p, false);
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

    private static void errorRecovery (CD19Parser p) throws Exception {
        // we need to find the next comma token before the next 'begin' token
        // BUT we will not jump to that begin token as that will be handled in the NFUND errorRecovery for dlist

        int nextComma = p.nextTokenOccursAt(Token.TCOMA);
        int nextBegin = p.nextTokenOccursAt(Token.TBEGN);

        if (nextComma == -1) {
            throw new Exception("Unable to recover");
        }

        if (nextBegin != -1 && nextComma < nextBegin) {
            p.tokensJumpTo(nextComma);
            return;
        }

        throw new Exception("Unable to recover");
    }

}
