package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Scanner.Token;

public class NINPUT {
    // <iostat> --> input <vlist>
    public static TreeNode generateTreeNode (CD19Parser p, SymbolTable symbolTable) {
        TreeNode NINPUTNode = new TreeNode(TreeNodeType.NUNDEF);

        // input
        if (!p.currentTokenIs(Token.TINPT)) {
            p.generateSyntaxError("expected the keyword 'input'.");
            return NINPUTNode;
        }
        p.moveToNextToken();

        // <vlist>
        TreeNode vlist = NVLIST.generateTreeNode(p, symbolTable);
        if (vlist.getNodeType() == TreeNodeType.NUNDEF) {
            return NINPUTNode;
        }

        NINPUTNode.setValue(TreeNodeType.NINPUT);
        NINPUTNode.setLeft(vlist);
        return NINPUTNode;
    }
}
