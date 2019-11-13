package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <fields>        --> <sdecl> , <fields>
    <fields>        --> <sdecl>

    NEW RULES:
    <fields>        --> <sdecl> <opt_fields>
    <opt_fields>    --> , <fields> | ε
 */
public class NFLIST {
    // <fields>        --> <sdecl> <opt_fields>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NFLISTNode = new TreeNode(TreeNodeType.NUNDEF);

        // <sdecl>
        TreeNode sdecl = NSDLST.sdecl(p, false);
        if (sdecl.getNodeType() == TreeNodeType.NUNDEF) {
            return NFLISTNode;
        }

        // <opt_fields>
        TreeNode fieldsOptional = fieldsOptional(p);
        if (fieldsOptional == null) {
            return sdecl;
        } else if (fieldsOptional.getNodeType() == TreeNodeType.NUNDEF) {
            // todo
            //  maybe : complete fields instead of just prematurely ending
            return NFLISTNode;
        }

        NFLISTNode.setValue(TreeNodeType.NFLIST);
        NFLISTNode.setLeft(sdecl);
        NFLISTNode.setRight(fieldsOptional);
        return NFLISTNode;
    }

    // <opt_fields>    --> , <fields> | ε
    public static TreeNode fieldsOptional (CD19Parser p) {
        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        TreeNode fields = generateTreeNode(p);
        return fields;
    }
}
