package rossH.CD19.Parser.SyntaxTreeNodes;

/*
    OLD RULES:
    <prlist>        --> <printitem> , <prlist>
    <prlist>        --> <printitem>

    NEW RULES:
    <prlist>        --> <printitem> <opt_prlist>
    <opt_prlist>    --> , <prlist> | ε

*/

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Scanner.Token;


public class NPRLST {

    // <prlist>        --> <printitem> <opt_prlist>
    public static TreeNode generateTreeNode (CD19Parser p, SymbolTable symbolTable) {
        TreeNode PRLSTNode = new TreeNode(TreeNodeType.NUNDEF);

        // <printitem>
        TreeNode printitem = printItem(p, symbolTable);
        if (printitem.getNodeType() == TreeNodeType.NUNDEF) {
            try {
                errorRecovery(p);
            } catch (Exception e) {
                return PRLSTNode;
            }
        }

        TreeNode prlistOptional = prlistOptional(p, symbolTable);

        // printitem properly defined AND prlistOptional either non-existant or contains errors
        // so we will just return printitem
        if (printitem.getNodeType() != TreeNodeType.NUNDEF &&
                (prlistOptional == null || prlistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return printitem;
        }

        // printitem contains errors AND prlistOptional properly defined
        // so we will just return prlistOptional
        if (prlistOptional != null && prlistOptional.getNodeType() != TreeNodeType.NUNDEF
                && printitem.getNodeType() == TreeNodeType.NUNDEF) {
            return prlistOptional;
        }

        // printitem contains errors and prlistOptional either non-existant or contains errors
        if (printitem.getNodeType() == TreeNodeType.NUNDEF &&
                (prlistOptional == null || prlistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return PRLSTNode;
        }

        // getting here implies both printitem and prlistOptional
        // were successfully defined
        PRLSTNode.setValue(TreeNodeType.NPRLST);
        PRLSTNode.setLeft(printitem);
        PRLSTNode.setRight(prlistOptional);
        return PRLSTNode;
    }

    // <opt_prlist>    --> , <prlist> | ε
    public static TreeNode prlistOptional (CD19Parser p, SymbolTable symbolTable) {
        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <prlist>
        TreeNode prlist = generateTreeNode(p, symbolTable);
        return prlist;
    }

    // <printitem> -->  <expr>
    // <printitem>  --> <string>
    public static TreeNode printItem (CD19Parser p, SymbolTable symbolTable) {
        // <string>
        if (p.currentTokenIs(Token.TSTRG)) {
            Token stringToken = p.getCurrentToken();
            p.moveToNextToken();
            TreeNode NSTRGNode = new TreeNode(TreeNodeType.NSTRG);
            SymbolTableRecord stRec = p.insertSymbolIdentifier(stringToken);
            NSTRGNode.setSymbolRecord(stRec);
            return NSTRGNode;
        }

        // <expr>
        TreeNode expr = NBOOL.expr(p, symbolTable);
        return expr;
    }

    public static void errorRecovery (CD19Parser p) throws Exception {
        // we need to go to the next ',' token or the next ';' token
        // which ever occurs first

        int nextComma = p.nextTokenOccursAt(Token.TCOMA);
        int nextSemiComma = p.nextTokenOccursAt(Token.TSEMI);

        if (nextSemiComma != -1 && nextComma != -1 && nextComma < nextSemiComma) {
            p.tokensJumpTo(nextComma);
        } else if (nextSemiComma != -1) {
            p.tokensJumpTo(nextSemiComma);
        } else {
            throw new Exception("Unable to recover.");
        }
    }
}
