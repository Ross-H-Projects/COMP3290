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
            p.generateSyntaxError("Expected an array type name identifier.");
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
            p.generateSyntaxError("Expected the keyword 'is'.");
            // prematurely end parsing due to irrecoverable error
            return NATYPENode;
        }
        p.moveToNextToken();

        // array
        if (!p.currentTokenIs(Token.TARAY)) {
            p.generateSyntaxError("expected keyword 'array'");
            // prematurely end parsing due to irrecoverable error
            return NATYPENode;
        }
        p.moveToNextToken();

        // [
        if (!p.currentTokenIs(Token.TLBRK)) {
            p.generateSyntaxError("expected character '['.");
            // prematurely end parsing due to irrecoverable error
            return NATYPENode;
        }
        p.moveToNextToken();

        // <expr>
        TreeNode expr = NBOOL.expr(p);
        if (expr.getNodeType() == TreeNodeType.NUNDEF) {
            // TODO expression errorrecovery i.e. try to go to ']'
            //  OR if we fail to do that we need to exit types section entirely
            return NATYPENode;
        }

        // ]
        if (!p.currentTokenIs(Token.TRBRK)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character ']'.");
            // prematurely end parsing due to irrecoverable error
            return NATYPENode;
        }
        p.moveToNextToken();

        // of
        if (!p.currentTokenIs(Token.TOF)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected keyword 'of'.");
            // prematurely end parsing due to irrecoverable error
            return NATYPENode;
        }
        p.moveToNextToken();

        // <structid>
        TreeNode structId = NSIVM.generateTreeNode(p);
        if (structId.getNodeType() == TreeNodeType.NUNDEF) {
            return NATYPENode;
        }

        NATYPENode.setValue(TreeNodeType.NATYPE);
        NATYPENode.setLeft(expr);
        NATYPENode.setRight(structId);
        return NATYPENode;
    }
}
