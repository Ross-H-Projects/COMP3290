package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;
import rossH.CD19.Scanner.Token;

// <rel>             -> not <expr> <relop> <expr>
public class NNOT {
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NNOTNode = new TreeNode(TreeNodeType.NUNDEF);

        if (!p.currentTokenIs(Token.TNOT)) {
            p.generateSyntaxError("expected keyword 'not'");
            System.out.println("NNOT :: error recovery :: ending program..");
            System.exit(1);
        }
        p.moveToNextToken();

        // <expr>
        TreeNode expr1 = NBOOL.expr(p);

        // <relop>
        TreeNode relop = NBOOL.relop(p);
        if (relop == null) {
            System.out.println("NNOT :: relop :: error recovery :: ending program..");
            System.exit(1);
        }
        p.moveToNextToken();

        // <expr>
        TreeNode expr2 = NBOOL.rel(p);

        NNOTNode.setValue(TreeNodeType.NNOT);
        NNOTNode.setMiddle(relop);
        relop.setLeft(expr1);
        relop.setRight(expr2);
        return NNOTNode;
    }
}
