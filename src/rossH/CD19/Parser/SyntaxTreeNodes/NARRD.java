package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Scanner.Token;

public class NARRD {
    // <arrdecl>      --> <id> : <typeid>
    public static TreeNode generateTreeNode (CD19Parser p, SymbolTable symbolTable, boolean isParameter) {
        TreeNode NARRDNode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an identifier in array declaration.");
            // prematurely end parsing due to irrecoverable error
            return NARRDNode;
        }
        // insert program id identifier into symbol table
        currentToken = p.getCurrentToken();
        SymbolTableRecord stRec;
        if (symbolTable == null) {
            stRec = p.insertSymbolIdentifier(currentToken);
        } else {
            stRec = p.insertSymbolIdentifier(currentToken, symbolTable);
        }
        NARRDNode.setSymbolRecord(stRec);
        NARRDNode.setIsArray(true);
        p.moveToNextToken();

        // :
        if (!p.currentTokenIs(Token.TCOLN)) {
            p.generateSyntaxError("Expected the character ':'");
            // prematurely end parsing due to irrecoverable error
            return NARRDNode;
        }
        p.moveToNextToken();

        // <typeid>
        // we only want to the typeid to point to a the programs symbol table
        // as types are only defined globally and not in each sub procedure
        TreeNode typeid = NSIVM.generateTreeNode(p, null);
        if (typeid.getNodeType() == TreeNodeType.NUNDEF) {
            return NARRDNode;
        }

        // we compute the offset after we have traversed the main body declarations
        // or the function parameters / locals
        if (symbolTable == null) { // it is a main program array, offset calculated in codegen
            NARRDNode.setSymbolRecordDataType(p.getCurrentToken(), 1, 0);
        } else if (isParameter) { // is parameter to sub program, offset can be calculated now
            NARRDNode.setSymbolRecordDataType(p.getCurrentToken(), 2, symbolTable.getBaseReigtser1OffsetNegative());
        } else { // it is a local in a function, we compute the offsets of these in code gen
            NARRDNode.setSymbolRecordDataType(p.getCurrentToken(), 2, 0);
        }
        NARRDNode.setValue(TreeNodeType.NARRD);
        NARRDNode.setLeft(typeid);
        return NARRDNode;
    }
}
