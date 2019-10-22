package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Scanner.Token;

import java.awt.desktop.AppForegroundListener;

public class NFUND {
    // <func>       --> function <id> ( <plist> ) : <rtype> <funcbody>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NFUNDNode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        // function
        if (!p.currentTokenIs(Token.TFUNC)) {
            p.generateSyntaxError("Expected the keyword 'function'.");
            // prematurely end parsing due to irrecoverable error
            return NFUNDNode;
        }
        p.moveToNextToken();

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an identifier in function declaration.");
            // prematurely end parsing due to irrecoverable error
            return NFUNDNode;
        }
        // insert program id identifier into symbol table
        currentToken = p.getCurrentToken();
        SymbolTableRecord stRec = p.insertSymbolIdentifier(currentToken);
        NFUNDNode.setSymbolRecord(stRec);
        p.moveToNextToken();

        // (
        if (!p.currentTokenIs(Token.TLPAR)) {
            p.generateSyntaxError("Expected the chracter '('.");
            // prematurely end parsing due to irrecoverable error
            return NFUNDNode;
        }
        p.moveToNextToken();


        // <plist>
        TreeNode plist = null;
        if (!p.currentTokenIs(Token.TRPAR)) {
            plist = NPLIST.generateTreeNode(p);
        }

        // )
        if (!p.currentTokenIs(Token.TRPAR)) {
            p.generateSyntaxError("Unclosed paranthesis for function definition.");
            // prematurely end parsing due to irrecoverable error
            return NFUNDNode;
        }
        p.moveToNextToken();

        // :
        if (!p.currentTokenIs(Token.TCOLN)) {
            p.generateSyntaxError("Expected the chracter ':'.");
            // prematurely end parsing due to irrecoverable error
            return NFUNDNode;
        }
        p.moveToNextToken();

        // <rtype>
        if (!p.currentTokenIs(Token.TVOID) && !p.currentTokenIs(Token.TINTG) && !p.currentTokenIs(Token.TREAL) && !p.currentTokenIs(Token.TBOOL)) {
            p.generateSyntaxError("Expected a function reurn type: 'integer', 'real', 'boolean', 'void'.");
            return NFUNDNode;
        }
        // todo
        // add symbol reference to symbol type (function return type)
        p.moveToNextToken();


        // <funcbody>
        // <funcbody>   --> <locals> begin <stats> end

        // <locals>
        // <locals>     --> <dlist> | ε
        // <dlist>
        TreeNode dlist = null;
        if (!p.currentTokenIs(Token.TBEGN)) {
            dlist = NDLIST.generateTreeNode(p);

            if (dlist.getNodeType() == TreeNodeType.NUNDEF) {
                dlist = null;
                try {
                    errorRecoveryDlist(p);
                } catch (Exception e) {
                    return NFUNDNode;
                }
            }
        }

        // begin
        if (!p.currentTokenIs(Token.TBEGN)) {
            p.generateSyntaxError("Expected the keyword 'begin'.");
            return NFUNDNode;
        }
        p.moveToNextToken();

        // <stats>
        TreeNode stats = NSTATS.generateTreeNode(p);
        if (stats.getNodeType() == TreeNodeType.NUNDEF) {
            // stats are a necessary part of the function body
            // therefore we will just return an invalid NFUND
            return NFUNDNode;
        }

        // end
        if (!p.currentTokenIs(Token.TEND)) {
            p.generateSyntaxError("Expected the keyword 'end'.");
            return NFUNDNode;
        }
        p.moveToNextToken();

        NFUNDNode.setValue(TreeNodeType.NFUND);
        NFUNDNode.setLeft(plist);
        NFUNDNode.setMiddle(dlist);
        NFUNDNode.setRight(stats);
        return NFUNDNode;
    }

    private static void errorRecoveryDlist (CD19Parser p) throws Exception {
        // we need to go the next 'begin' token,
        // in failing that we need to go to either the next 'function' token
        // or 'main' token

        int nextBegin = p.nextTokenOccursAt(Token.TBEGN);
        int nextFunction = p.nextTokenOccursAt(Token.TFUNC);
        int nextMain = p.nextTokenOccursAt(Token.TMAIN);

        if (nextBegin == -1) {
            if (nextFunction != -1) {
                p.tokensJumpTo(nextFunction);
                return;
            } else if (nextFunction != -1) {
                p.tokensJumpTo(nextMain);
                return;
            }
            throw new Exception("Unable to recover");
        }

        if (nextFunction != -1 && nextBegin < nextFunction) {
            p.tokensJumpTo(nextBegin);
            return;
        }

        if (nextMain != -1 && nextBegin < nextMain) {
            p.tokensJumpTo(nextBegin);
            return;
        }

        throw new Exception("Unable to recover");
    }

}
