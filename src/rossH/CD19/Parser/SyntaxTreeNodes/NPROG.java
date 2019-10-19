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

            System.out.println("NRPOG :: NGLOB :: ERROR RECOVERY - exiting...");
            System.exit(1);
            //try {
            //    errorRecovery(p);
            //} catch (Exception e) { p.popScope(); return nProg; }

        }



        // <funcs>
        TreeNode functions = new TreeNode(TreeNodeType.NUNDEF);
        /*
        todo
        TreeNode funcs = NProclNode.generateTreeNode(p);
        if (funcs != null && funcs.getNodeType() == ParseTreeNodeType.NUNDEF) {
            try {errorRecovery(p);}
            catch (Exception e) { p.popScope(); return nProg; }
        }
        */

        // <mainbody>
        TreeNode main = NMAIN.generateTreeNode(p);
        if (main != null && main.getNodeType() == TreeNodeType.NUNDEF) {
            System.out.println("NMAIN :: ERROR RECOVERY - exiting...");
            System.exit(1);
            //errorRecoveryToEnd(p);
        }

        // construct actual tree node
        NPROGNode.setValue(TreeNodeType.NPROG);
        NPROGNode.setLeft(globals);
        NPROGNode.setMiddle(functions);
        NPROGNode.setRight(main);
        return NPROGNode;
    }


}