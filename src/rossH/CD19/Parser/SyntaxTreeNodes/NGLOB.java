package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;


public class NGLOB {

    // <globals>        --> <consts> <types> <arrays>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NGLOBNode = new TreeNode(TreeNodeType.NUNDEF);

        // <consts>
        TreeNode consts = consts(p);

        // <types>
        TreeNode types = types(p);

        // <arrays>
        TreeNode arrays = arrays(p);

        if (consts == null && types == null && arrays == null) {
            return null;
        }

        NGLOBNode.setValue(TreeNodeType.NGLOB);

        if (consts != null) {
            NGLOBNode.setLeft(consts);
        }

        if (types != null) {
            NGLOBNode.setMiddle(types);
        }

        if (arrays != null) {
            NGLOBNode.setRight(arrays);
        }

        return NGLOBNode;
    }

    // <consts>         --> constants <initlist> | ε
    public static TreeNode consts (CD19Parser p) {

        // ε
        if (!p.currentTokenIs(Token.TCONS)) {
            return null;
        }

        // constants
        p.moveToNextToken();

        // <intlist>
        TreeNode initlist =  NILIST.generateTreeNode(p);
        return initlist;
    }

    // <types>      --> types <typelist> | ε
    public static TreeNode types (CD19Parser p) {
        // ε
        if (!p.currentTokenIs(Token.TTYPS)) {
            return null;
        }

        // types
        p.moveToNextToken();

        TreeNode typelist = NTYPEL.generateTreeNode(p);
        return typelist;
    }

    // <types>      --> arrays <arrdecls> | ε
    public static TreeNode arrays (CD19Parser p) {

        // ε
        if (!p.currentTokenIs(Token.TARRS)) {
            return null;
        }

        // arrays
        p.moveToNextToken();

        TreeNode arrdecls = NALIST.generateTreeNode(p);
        return arrdecls;
    }
}