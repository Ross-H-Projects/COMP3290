package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <bool>   -> <bool> <logop> <rel>
    <bool>   -> <rel>

    NEW RULES:
    <bool>   -> <rel> <bool_r>
    <bool_r> -> <logop> <rel> <bool_r>

 */

public class NBOOL {
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NBOOLNode = new TreeNode(TreeNodeType.NUNDEF);

        // <rel>
        TreeNode rel;

        // <bool_r>
        TreeNode boolR = boolR(p);

        // construct the actual TreeNode
        NBOOLNode.setLeft(rel);
        if (boolR != null) {
            NBOOLNode.setValue(TreeNodeType.NBOOL);
            NBOOLNode.setLeft(rel);
            NBOOLNode.setRight(boolR);
        }

        

    }

    public static TreeNode rel (CD19Parser p) {

    }

    public static TreeNode boolR (CD19Parser p) {

    }
}
