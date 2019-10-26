package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

// <fncall> --> <id> ( <elist> ) | <id> ( )
public class NFCALL {
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NFCALLNode = new TreeNode(TreeNodeType.NUNDEF);

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("expected an identifer.");
            return NFCALLNode;
        }
        TreeNode id = NSIVM.generateTreeNode(p);

        // (
        if (!p.currentTokenIs(Token.TLPAR)) {
            p.generateSyntaxError("expected character '('");
            return NFCALLNode;
        }
        p.moveToNextToken();

        // implies we are parsing the grammar: <id> ( )
        if (p.currentTokenIs(Token.TRPAR)) {
            p.moveToNextToken();
            NFCALLNode.setValue(TreeNodeType.NFCALL);
            NFCALLNode.setLeft(id);
            return NFCALLNode;
        }

        // <elist>
        TreeNode elist = NEXPL.generateTreeNode(p);
        // handle <elist> error recovery
        if (elist.getNodeType() == TreeNodeType.NUNDEF) {
            // )
            if (!p.currentTokenIs(Token.TRPAR)) {
                p.getCurrentToken();
                p.generateSyntaxError("expected ')' in function call.");
                return NFCALLNode;
            }
            p.moveToNextToken();

            NFCALLNode.setValue(TreeNodeType.NFCALL);
            NFCALLNode.setLeft(id);
            return NFCALLNode;
        }

        // )
        if (!p.currentTokenIs(Token.TRPAR)) {
            p.generateSyntaxError("expected ')' after list of parameters for function call.");
            return NFCALLNode;
        }
        p.moveToNextToken();

        NFCALLNode.setValue(TreeNodeType.NFCALL);
        NFCALLNode.setLeft(id);
        NFCALLNode.setRight(elist);
        return NFCALLNode;
    }
}
