package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;
import rossH.CD19.Scanner.Token;

// RULES:
// <var> --> <id>
public class NSIVM {
    public static TreeNode generateTreeNode (CD19Parser p, SymbolTable symbolTable) {
        TreeNode NSIVMNode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an identifer");
            return NSIVMNode;
        }
        currentToken = p.getCurrentToken();
        p.moveToNextToken();
        SymbolTableRecord stRec;
        if (symbolTable == null) {
            stRec = p.insertSymbolIdentifier(currentToken);
        } else {
            stRec = p.insertSymbolIdentifier(currentToken, symbolTable);
        }
        NSIVMNode.setSymbolRecord(stRec);
        NSIVMNode.setValue(TreeNodeType.NSIMV);
        return NSIVMNode;
    }
}
