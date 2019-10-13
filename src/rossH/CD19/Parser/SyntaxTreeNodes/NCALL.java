package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

// <callstat> --> <id> ( <elist> ) | <id> ( )
public class NCALL {
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NCALLNode = new TreeNode(TreeNodeType.NUNDEF);

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected an identifer");
            System.out.println("NCALL :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        TreeNode id = NSIVM.generateTreeNode(p);

        // (
        if (!p.currentTokenIs(Token.TLPAR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected (");
            System.out.println("NCALL :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        NCALLNode.setValue(TreeNodeType.NCALL);
        NCALLNode.setLeft(id);

        // implies we are parsing the grammar: <id> ( )
        if (p.currentTokenIs(Token.TRPAR)) {
            p.moveToNextToken();
            return NCALLNode;
        }

        // <elist>
        TreeNode elist = NEXPL.generateTreeNode(p);

        // )
        if (!p.currentTokenIs(Token.TRPAR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected ) after elist");
            System.out.println("NCALL :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        NCALLNode.setValue(TreeNodeType.NCALL);
        NCALLNode.setRight(elist);
        return NCALLNode;
    }

}
