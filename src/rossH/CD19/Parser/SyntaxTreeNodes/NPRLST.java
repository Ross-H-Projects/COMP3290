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
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Scanner.Token;


public class NPRLST {

    // <prlist>        --> <printitem> <opt_prlist>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode PRLSTNode = new TreeNode(TreeNodeType.NUNDEF);

        // <printitem>
        TreeNode printitem = printItem(p);

        PRLSTNode.setValue(TreeNodeType.NPRLST);
        PRLSTNode.setLeft(printitem);

        TreeNode prlistOptional = prlistOptional(p);
        if (prlistOptional == null) {
            return PRLSTNode;
        }

        PRLSTNode.setRight(prlistOptional);
        return PRLSTNode;
    }

    // <opt_prlist>    --> , <prlist> | ε
    public static TreeNode prlistOptional (CD19Parser p) {

        // ε
        if (p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <prlist>
        TreeNode prlist = generateTreeNode(p);
        return prlist;
    }

    // <printitem> -->  <expr>
    // <printitem>  --> <string>
    public static TreeNode printItem (CD19Parser p) {

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

    }
}
