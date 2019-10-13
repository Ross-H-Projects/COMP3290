package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <vlist>     --> <var> , <vlist>
    <vlist>     --> <var>

    NEW RULES:
    <vlist>     --> <var> <opt_vlist>
    <opt_vlist> --> , <vlist> | ε
*/

public class NVLIST {

    // <vlist>     --> <var> <opt_vlist>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NVLISTNode = new TreeNode(TreeNodeType.NUNDEF);

        // <var>
        TreeNode var = new TreeNode(TreeNodeType.NUNDEF);
        if (p.getTokenAhead(1).value() == Token.TLBRK) { // NARRV: <var> --> <id>[<expr>].<id>
            System.out.println(" <var> --> <id>[<expr>].<id>");
            var = NARRV.generateTreeNode(p);
        } else { // NISVM: <var> --> <id>
            System.out.println("<var> --> <id>");
            var = NSIVM.generateTreeNode(p);
        }

        // <opt_vlist>
        TreeNode vlistOptional =  vlistOptional(p);
        if (vlistOptional == null) {
            return var;
        }

        NVLISTNode.setValue(TreeNodeType.NVLIST);
        NVLISTNode.setLeft(var);
        NVLISTNode.setRight(vlistOptional);
        return NVLISTNode;
    }

    // <opt_vlist> --> , <vlist> | ε
    public static TreeNode vlistOptional (CD19Parser p) {

        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <vlist>
        TreeNode vlist = generateTreeNode(p);
        return vlist;
    }
}
