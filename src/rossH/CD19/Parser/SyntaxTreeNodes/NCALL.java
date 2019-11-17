package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Scanner.Token;

// <callstat> --> <id> ( <elist> ) | <id> ( )
public class NCALL {
    public static TreeNode generateTreeNode (CD19Parser p, SymbolTable symbolTable) {
        TreeNode NCALLNode = new TreeNode(TreeNodeType.NUNDEF);

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected an identifer.");
            return NCALLNode;
        }
        TreeNode id = NSIVM.generateTreeNode(p, null);
        NCALLNode.setLeft(id);

        // (
        if (!p.currentTokenIs(Token.TLPAR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected '('.");
            return NCALLNode;
        }
        p.moveToNextToken();



        // implies we are parsing the grammar: <id> ( )
        if (p.currentTokenIs(Token.TRPAR)) {
            p.moveToNextToken();
            return NCALLNode;
        }

        // <elist>
        TreeNode elist = NEXPL.generateTreeNode(p, symbolTable);
        // handle <elist> error recovery
        if (elist.getNodeType() == TreeNodeType.NUNDEF) {
            // )
            if (!p.currentTokenIs(Token.TRPAR)) {
                p.getCurrentToken();
                p.generateSyntaxError("expected ')' expression list in call.");
                return NCALLNode;
            }
            p.moveToNextToken();

            NCALLNode.setValue(TreeNodeType.NCALL);
            return NCALLNode;
        }

        // )
        if (!p.currentTokenIs(Token.TRPAR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected ')' expression list in call.");
            return NCALLNode;
        }
        p.moveToNextToken();

        NCALLNode.setValue(TreeNodeType.NCALL);
        NCALLNode.setRight(elist);
        return NCALLNode;
    }

}
