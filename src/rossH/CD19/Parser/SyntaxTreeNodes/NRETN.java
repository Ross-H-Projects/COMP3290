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
            p.generateSyntaxError("expected the keyword 'return'.");
            return NRETNNode;
        }
        p.moveToNextToken();



        // <opt_exp>
        TreeNode exprOptional = exprOptional(p);
        if (exprOptional == null) {
            NRETNNode.setValue(TreeNodeType.NRETN);
            return NRETNNode;
        } else if (exprOptional.getNodeType() == TreeNodeType.NUNDEF) {
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
