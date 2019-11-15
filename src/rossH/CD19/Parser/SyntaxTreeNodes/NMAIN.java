package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Scanner.Token;


public class NMAIN {

    // <mainbody> --> main <slist> begin <stats> end CD19 <id>
    public static TreeNode generateTreeNode (CD19Parser p, String programId) {
        TreeNode NMAINNode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        // main
        if (!p.currentTokenIs(Token.TMAIN)) {
            p.generateSyntaxError("Expected the keyword 'main'");
            // prematurely end parsing due to irrecoverable error
            return NMAINNode;
        }
        p.moveToNextToken();

        // <slist>
        TreeNode slist = NSDLST.generateTreeNode(p, true);
        // list is a necessary section of NMAIN
        // thus if we fail to parse it, then we must fail parsing
        // the entirety of NMAIN
        if (slist != null && slist.getNodeType() == TreeNodeType.NUNDEF) {
            // prematurely end parsing due to irrecoverable error
            return NMAINNode;
        }

        p.setAmountOfDeclarationsInMainBody(NSDLST.countAmountOfDeclarations (slist, 0));

        // begin
        if (!p.currentTokenIs(Token.TBEGN)) {
            p.generateSyntaxError("Expected the keyword 'begin'");
            // prematurely end parsing due to irrecoverable error
            return NMAINNode;
        }
        p.moveToNextToken();

        // <stats>
        TreeNode stats = NSTATS.generateTreeNode(p);
        // <stats> are a necessary section of NMAIN, so if we fail to parse
        // <stats> we fail to parse the entirety  of NMAIN
        if (stats != null && stats.getNodeType() == TreeNodeType.NUNDEF) {
            return NMAINNode;
        }

        // end
        if (!p.currentTokenIs(Token.TEND)) {
            p.generateSyntaxError("Expected the keyword 'end'");
            // prematurely end parsing due to irrecoverable error
            return NMAINNode;
        }
        p.moveToNextToken();

        // CD19
        if (!p.currentTokenIs(Token.TCD19)) {
            p.generateSyntaxError("Expected the keyword 'CD19'");
            // prematurely end parsing due to irrecoverable error
            return NMAINNode;
        }
        p.moveToNextToken();

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected a program name identifier");
            // prematurely end parsing due to irrecoverable error
            return NMAINNode;
        }
        // insert program id identifier into symbol table
        currentToken = p.getCurrentToken();
        SymbolTableRecord stRec = p.insertSymbolIdentifier(currentToken);
        NMAINNode.setSymbolRecord(stRec);
        p.moveToNextToken();

        // semantic analysis
        if (NMAINNode.getSymbolRecord().getLexeme() != programId) {
            p.generateSemanticError("Expected program ids to match");
        }

        // construct actual tree node
        NMAINNode = new TreeNode(TreeNodeType.NMAIN, slist, stats);
        return NMAINNode;
    }
}