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
        TreeNode arrdecl = NARRD.generateTreeNode(p, null, false);
        if (arrdecl.getNodeType() == TreeNodeType.NUNDEF) {
            try {
                errorRecovery(p);
            } catch (Exception e) {
                return NALISTNode;
            }
        }

        // <opt_arrdecls>
        TreeNode arrdeclsOptional = arrdeclsOptional(p);

        // arrdecl properly defined AND arrdeclsOptional either non-existant or contains errors
        // so we will just return arrdecl
        if (arrdecl.getNodeType() != TreeNodeType.NUNDEF &&
                (arrdeclsOptional == null || arrdeclsOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return arrdecl;
        }

        // arrdecl contains errors AND arrdeclsOptional properly defined
        // so we will just return arrdeclsOptional
        if (arrdeclsOptional != null && arrdeclsOptional.getNodeType() != TreeNodeType.NUNDEF
                && arrdecl.getNodeType() == TreeNodeType.NUNDEF) {
            return arrdeclsOptional;
        }

        // arrdecl contains errors and arrdeclsOptional either non-existant or contains errors
        if (arrdecl.getNodeType() == TreeNodeType.NUNDEF &&
                (arrdeclsOptional == null || arrdeclsOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return NALISTNode;
        }

        // getting here implies both arrdecl and arrdeclsOptional
        // were successfully defined
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

    private static void errorRecovery (CD19Parser p) throws Exception {
        // recoverying from a invalid arrays entails going to the next ','
        // s.t. the ',' occurs before the keywords:  'function', or 'main'

        int nextComma = p.nextTokenOccursAt(Token.TCOMA);
        int nextFunction = p.nextTokenOccursAt(Token.TFUNC);
        int nextMain = p.nextTokenOccursAt(Token.TMAIN);

        // there is no next comma
        // i.e. we have to jump the parser
        // the next sensible section
        if (nextComma == -1) {
            // there is a next comma but it doesn't occur
            // in the <arrdecls>. thus we can only attempt to jump
            // the parser to the next sensisble section
             if (nextFunction != -1) {
                p.tokensJumpTo(nextFunction);
            } else if (nextMain != -1) {
                p.tokensJumpTo(nextMain);
            } else {
                throw new Exception("Unable to recover");
            }
            return;
        }
        
        // a function was defined in the program AND
        // comma occurs before function section is entered
        if (nextFunction != -1 && nextComma < nextFunction) {
            p.tokensJumpTo(nextComma);
            return;
        }

        // main was defined in the program AND
        // comma occurs before main section is entered
        if (nextMain != -1 && nextComma < nextMain) {
            p.tokensJumpTo(nextComma);
            return;
        }

        throw new Exception("Unable to recover");
    }
}
