package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;
import rossH.CD19.Scanner.Token;

import rossH.CD19.Parser.SyntaxTreeNodes.NGLOB;
import rossH.CD19.Parser.SyntaxTreeNodes.NMAIN;

public class NPROG {

    // <progam> --> CD19 <id> <globals> <funcs> <mainbody>
    public static TreeNode generateTreeNode (CD19Parser p) {
        // initially assume we can't match anything
        TreeNode NPROGNode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        // CD19
        if (!p.currentTokenIs(Token.TCD19)) {
            p.generateSyntaxError("Expected the keyword 'CD19'");
            // prematurely end parsing due to irrecoverable error
            return NPROGNode;
        }
        p.moveToNextToken();

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected a program name identifier");
            // prematurely end parsing due to irrecoverable error
            return NPROGNode;
        }
        // insert program id identifier into symbol table
        currentToken = p.getCurrentToken();
        SymbolTableRecord stRec = p.insertSymbolIdentifier(currentToken);
        NPROGNode.setSymbolRecord(stRec);
        p.moveToNextToken();

        // <globals>
        TreeNode globals = NGLOB.generateTreeNode(p);
        if (globals != null && globals.getNodeType() == TreeNodeType.NUNDEF) {
            // error recovery will have already taken place within some node
            // within globals (NILIST, NTYPEL, or NALIST)
            globals = null;

        }

        // <funcs>
        TreeNode funcs = NFUNCS.generateTreeNode(p);
        if (funcs != null && funcs.getNodeType() == TreeNodeType.NUNDEF) {
            // error recovery will have already taken place within some node
            // within NFUNCS or some child node of NFUNCS (e.g. NFUND)
            funcs = null;
        }


        // <mainbody>
        TreeNode main = NMAIN.generateTreeNode(p, NPROGNode.getSymbolRecord().getLexeme());
        // <mainbody> is a necessary section of NPROG, so if we fail to parse
        // hat then we will fail to parse NPROG entirely
        if (main != null && main.getNodeType() == TreeNodeType.NUNDEF) {
            return NPROGNode;
        }

        NGLOB.fixArrayOffsets(globals, p.getAmountOfDeclarationsInMainBody() + p.getAmountOfConstants());

        // construct actual tree node
        NPROGNode.setValue(TreeNodeType.NPROG);
        NPROGNode.setLeft(globals);
        NPROGNode.setMiddle(funcs);
        NPROGNode.setRight(main);
        return NPROGNode;
    }


}