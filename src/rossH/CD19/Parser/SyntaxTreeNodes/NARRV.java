
package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;
import rossH.CD19.Scanner.Token;

// RULES:
// NARRV: <var> --> <id>[<expr>].<id>
public class NARRV {
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NARRVNode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.getCurrentToken();
            System.out.println("NARRV :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        TreeNode id1 = NSIVM.generateTreeNode(p);

        // [
        if (!p.currentTokenIs(Token.TLBRK)) {
            System.out.println("NARRV :: missing left bracket ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();;

        // <expr>
        TreeNode expr = NBOOL.expr(p);

        // ]
        if (!p.currentTokenIs(Token.TRBRK)) {
            System.out.println("NARRV :: missing right bracket ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // .
        if (!p.currentTokenIs(Token.TDOT)) {
            System.out.println("NARRV :: missing dot operator ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <id>
        TreeNode id2 = NSIVM.generateTreeNode(p);

        NARRVNode.setValue(TreeNodeType.NARRV);
        NARRVNode.setLeft(id1);
        NARRVNode.setMiddle(expr);
        NARRVNode.setRight(id2);
        return NARRVNode;
    }
}
