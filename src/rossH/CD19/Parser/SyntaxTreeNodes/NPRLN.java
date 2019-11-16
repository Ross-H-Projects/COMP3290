package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Scanner.Token;

// <iostat>     --> printline <prlist>
public class NPRLN {
    public static TreeNode generateTreeNode (CD19Parser p, SymbolTable symbolTable) {
        TreeNode NPRlNNode = new TreeNode(TreeNodeType.NUNDEF);

        if (!p.currentTokenIs(Token.TPRLN)) {
            p.generateSyntaxError("expected the keyword 'printline'.");
            return NPRlNNode;
        }
        p.moveToNextToken();

        // <prlist>
        TreeNode prlist = NPRLST.generateTreeNode(p, symbolTable);
        if (prlist.getNodeType() == TreeNodeType.NUNDEF) {
            return NPRlNNode;
        }

        NPRlNNode.setValue(TreeNodeType.NPRLN);
        NPRlNNode.setLeft(prlist);
        return NPRlNNode;
    }
}
