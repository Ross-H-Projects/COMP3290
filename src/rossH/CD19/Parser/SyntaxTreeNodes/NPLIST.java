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
        if (param.getNodeType() == TreeNodeType.NUNDEF) {
            try {
                errorRecovery(p);
            } catch (Exception e) {
                return NPLISTNode;
            }

        }

        // <opt_params>
        TreeNode paramsOptional = paramsOptional(p);

        // param properly defined AND paramsOptional either non-existant or contains errors
        // so we will just return param
        if (param.getNodeType() != TreeNodeType.NUNDEF &&
                (paramsOptional == null || paramsOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return param;
        }

        // param contains errors AND paramsOptionalproperly defined
        // so we will just return paramsOptional
        if (paramsOptional != null && paramsOptional.getNodeType() != TreeNodeType.NUNDEF
                && param.getNodeType() == TreeNodeType.NUNDEF) {
            return paramsOptional;
        }

        // param contains errors and paramsOptionaleither non-existant or contains errors
        if (param.getNodeType() == TreeNodeType.NUNDEF &&
                (paramsOptional == null || paramsOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return NPLISTNode;
        }

        // getting here implies both param and paramOptional
        // were successfully defined

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
        if (p.currentTokenIs(Token.TCNST)) {
            p.moveToNextToken();
            return constantArrDecl(p);
        }

        // <param>        --> <arrdecl>
        // <param>        --> <sdecl>
        return sdeclOrArrdecl(p);
    }

    public static TreeNode constantArrDecl (CD19Parser p) {
        TreeNode decl = new TreeNode(TreeNodeType.NARRC);

        TreeNode arrdecl = NARRD.generateTreeNode(p);
        if (arrdecl.getNodeType() == TreeNodeType.NUNDEF) {
            return arrdecl;
        }

        decl.setLeft(arrdecl);
        return decl;
    }

    // <param>        --> <arrdecl>
    // <param>        --> <sdecl>
    public static TreeNode sdeclOrArrdecl (CD19Parser p) {
        TreeNode decl = new TreeNode(TreeNodeType.NUNDEF);
        // <arrdecl>    --> <id> : <typeid>
        // <sdecl>      --> <id> : <stype>

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an identifer.");
            return decl;
        }

        // :
        if (p.getTokenAhead(1).value() != Token.TCOLN) {
            p.generateSyntaxError("Expected an the character ':'.");
            return decl;
        }

        // sdecl
        int potentialStype = p.getTokenAhead(2).value();
        if (potentialStype == Token.TINTG || potentialStype == Token.TREAL || potentialStype == Token.TBOOL) {
            decl.setValue(TreeNodeType.NSIMP);
            TreeNode sdecl = NSDLST.sdecl(p);
            decl.setLeft(sdecl);
            return decl;
        }

        // arrdecl
        if (potentialStype == Token.TIDEN) {
            decl.setValue(TreeNodeType.NARRP);
            TreeNode arrdecl = NARRD.generateTreeNode(p);
            decl.setLeft(arrdecl);
            return decl;
        }

        // if the token after <id> : ... is not an identifier (typeid)
        // or integer, real, or boolean, then we've experienced an error
        p.generateSyntaxError("expected 'integer', 'real', 'boolean', or an type identifer after start of array / variable function parameter declaration.");
        return decl;
    }

    // <opt_paramas>   --> , <params>  | ε
    public static TreeNode paramsOptional (CD19Parser p) {
        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <params>
        TreeNode params = generateTreeNode(p);
        return params;
    }

    private static void errorRecovery (CD19Parser p) throws Exception {
        // we need to find the next occurrence of a comma token
        // s.t. it occurs before the right paranthesis next

        int nextComma = p.nextTokenOccursAt(Token.TCOMA);
        int nextRightParanthesis = p.nextTokenOccursAt(Token.TRPAR);

        if (nextComma == -1) {
            if (nextRightParanthesis != -1) {
                p.tokensJumpTo(nextRightParanthesis);
                return;
            }
            throw new Exception("Unable to recover.");
        }

        if (nextRightParanthesis !=1 && nextComma < nextRightParanthesis) {
            p.tokensJumpTo(nextComma);
            return;
        }
        throw new Exception("Unable to recover.");

    }
}
