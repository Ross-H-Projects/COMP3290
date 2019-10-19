package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Scanner.Token;


public class NSDLST {

    /*
        OLD RULES:
        <slist>      --> <sdecl>, <slist>
        <slist>      --> <sdecl>

        NEW RULES:
        <slist>      --> <sdecl> <opt_slist>
        <opt_slist>  --> , <slist> | ɛ
    */


    public static TreeNode generateTreeNode (CD19Parser p) {

        // <sdecl>
        TreeNode sdecl = sdecl(p);
        if (sdecl != null && sdecl.getNodeType() == TreeNodeType.NUNDEF) {
            System.out.println("NSDLST :: sdecl :: ERROR RECOVERY - exiting...");
            System.exit(1);
            //try { errorRecovery(p); }
            //catch (Exception e) { return new TreeNode(ParseTreeNodeType.NUNDEF); }
        }

        // <opt_slist>
        TreeNode sdeclOptional = optSlist(p);
        if (sdeclOptional == null) {
            return sdecl;
        }

        TreeNode slist = new TreeNode(TreeNodeType.NSDLST, sdecl, sdeclOptional);
        //decList.setDataType(TreeNodeType.);
        return slist;
    }

    // <sdecl> --> <id> : <stype>
    public static TreeNode sdecl (CD19Parser p) {
        TreeNode sdecl = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an identifier in variable declaration.");
            // prematurely end parsing due to irrecoverable error
            return sdecl;
        }
        // insert program id identifier into symbol table
        currentToken = p.getCurrentToken();
        SymbolTableRecord stRec = p.insertSymbolIdentifier(currentToken);
        sdecl.setSymbolRecord(stRec);
        p.moveToNextToken();

        // :
        if (!p.currentTokenIs(Token.TCOLN)) {
            p.generateSyntaxError("Expected the character ':'");
            // prematurely end parsing due to irrecoverable error
            return sdecl;
        }
        p.moveToNextToken();

        // <stype> --> integer | real | boolean
        if (!p.currentTokenIs(Token.TINTG) && !p.currentTokenIs(Token.TREAL) && !p.currentTokenIs(Token.TBOOL)) {
            p.generateSyntaxError("expected the keyword 'integer', 'real', or 'boolean'");
        }
        // todo
        // add symbol reference to symbol type (sdecl stype)
        //currentToken = p.getCurrentToken();
        //p.insertSymbolIdentifier((currentToken));
        p.moveToNextToken();

        sdecl.setValue(TreeNodeType.NSDECL);
        return sdecl;
    }

    // <opt_slist>  --> , <slist> | ɛ
    public static TreeNode optSlist (CD19Parser p) {

        // ɛ
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <slist>
        TreeNode slist = generateTreeNode(p);
        return slist;

    }
}