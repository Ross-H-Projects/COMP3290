package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Scanner.Token;

public class NATYPE {
    // <type>   --> <typeid> is array [ <expr> ] of <structid>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NATYPENode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        // <typeid>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an array name identifier");
            // prematurely end parsing due to irrecoverable error
            return NATYPENode;
        }
        // insert array identifier into symbol table
        currentToken = p.getCurrentToken();
        SymbolTableRecord stRec = p.insertSymbolIdentifier(currentToken);
        NATYPENode.setSymbolRecord(stRec);
        p.moveToNextToken();

        // is
        if (!p.currentTokenIs(Token.TIS)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected keyword 'is'");
            System.out.println("NATYPE :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // array
        if (!p.currentTokenIs(Token.TARAY)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected keyword 'array'");
            System.out.println("NATYPE :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // [
        if (!p.currentTokenIs(Token.TLBRK)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character [");
            System.out.println("NATYPE :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <expr>
        TreeNode expr = NBOOL.expr(p);

        // ]
        if (!p.currentTokenIs(Token.TRBRK)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character ]");
            System.out.println("NATYPE :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // of
        if (!p.currentTokenIs(Token.TOF)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected keyword 'of'");
            System.out.println("NATYPE :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <structid>
        TreeNode structId = NSIVM.generateTreeNode(p);

        NATYPENode.setValue(TreeNodeType.NATYPE);
        NATYPENode.setLeft(expr);
        NATYPENode.setRight(structId);
        return NATYPENode;
    }
}
