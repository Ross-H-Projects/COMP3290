package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <returnsStat>   --> return
    <returnStat>    --> return <expr>

    NEW RULES:
    <returnStat>    --> return <opt_expr>
    <opt_expr>      --> <expr> | ε
*/
public class NRETN {
    // <returnStat>    --> return <opt_expr>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NRETNNode  = new TreeNode(TreeNodeType.NRETN);

        // return
        if (!p.currentTokenIs(Token.TRETN)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'return'");
            System.out.println("NRETN :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        NRETNNode.setValue(TreeNodeType.NRETN);

        // <opt_exp>
        TreeNode exprOptional = exprOptional(p);
        if (exprOptional == null) {
            return NRETNNode;
        }

        NRETNNode.setLeft(exprOptional);
        return NRETNNode;
    }

    // <opt_expr>      --> <expr> | ε
    public static TreeNode exprOptional (CD19Parser p) {
        // implies we've just traversed "return ;"
        // so we're pasrsing <opt_expr> --> <expr> | ε
        if (p.currentTokenIs(Token.TSEMI)) {
            return null;
        }

        TreeNode expr = NBOOL.expr(p);
        return expr;
    }
}
