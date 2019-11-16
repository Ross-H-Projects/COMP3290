
package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;
import rossH.CD19.Scanner.Token;

// RULES:
// NARRV: <var> --> <id>[<expr>].<id>
public class NARRV {
    public static TreeNode generateTreeNode (CD19Parser p, SymbolTable symbolTable) {
        TreeNode NARRVNode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an identifier.");
            return NARRVNode;
        }
        TreeNode id1 = NSIVM.generateTreeNode(p, symbolTable);

        // [
        if (!p.currentTokenIs(Token.TLBRK)) {
            p.generateSyntaxError("Expected the character '['.");
            return NARRVNode;
        }
        p.moveToNextToken();

        // <expr>
        TreeNode expr = NBOOL.expr(p, symbolTable);
        if (expr.getNodeType() == TreeNodeType.NUNDEF) {
            return NARRVNode;
        }

        // ]
        if (!p.currentTokenIs(Token.TRBRK)) {
            p.generateSyntaxError("Expected the character ']'.");
            return NARRVNode;
        }
        p.moveToNextToken();

        // .
        if (!p.currentTokenIs(Token.TDOT)) {
            p.generateSyntaxError("Expected the character '.'.");
            return NARRVNode;
        }
        p.moveToNextToken();

        // <id>
        TreeNode id2 = NSIVM.generateTreeNode(p, symbolTable);
        if (id2.getNodeType() == TreeNodeType.NUNDEF) {
            return NARRVNode;
        }

        NARRVNode.setValue(TreeNodeType.NARRV);
        NARRVNode.setLeft(id1);
        NARRVNode.setMiddle(expr);
        NARRVNode.setRight(id2);
        return NARRVNode;
    }
}
