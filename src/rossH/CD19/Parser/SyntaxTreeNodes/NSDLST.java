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
        TreeNode slist = new TreeNode(TreeNodeType.NUNDEF);

        // <sdecl>
        TreeNode sdecl = sdecl(p);
        if (sdecl != null && sdecl.getNodeType() == TreeNodeType.NUNDEF) {
            try {
                errorRecovery(p);
            } catch (Exception e) {
                return new TreeNode(TreeNodeType.NUNDEF);
            }
        }

        // <opt_slist>
        TreeNode sdeclOptional = optSlist(p);

        // sdecl properly defined AND sdecl either non-existant or contains errors
        // so we will just return sdecl
        if (sdecl.getNodeType() != TreeNodeType.NUNDEF &&
                (sdeclOptional == null ||sdeclOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return sdecl;
        }

        // sdecl contains errors AND sdecllistOptional properly defined
        // so we will just return sdecllistOptional
        if (sdeclOptional != null && sdeclOptional.getNodeType() != TreeNodeType.NUNDEF
                && sdecl.getNodeType() == TreeNodeType.NUNDEF) {
            return sdeclOptional;
        }

        // sdecl contains errors and typelistOptional either non-existant or contains errors
        if (sdecl.getNodeType() == TreeNodeType.NUNDEF &&
                (sdeclOptional == null || sdeclOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return slist;
        }

        // getting here implies both sdecl and sdeclOptional
        // were successfully defined
        slist.setValue(TreeNodeType.NSDLST);
        slist.setLeft(sdecl);
        slist.setRight(sdeclOptional);
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
            return sdecl;
        }
        sdecl.setSymbolRecordDataType(p.getCurrentToken(), 1, p.getBaseReigtserOffset(1));
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

    public static void errorRecovery (CD19Parser p) throws Exception {
        // we need to go to the next comma, and the next comma
        // needs to occur before the next 'begin' token

        int nextComma = p.nextTokenOccursAt(Token.TCOMA);
        int nextBegin = p.nextTokenOccursAt(Token.TBEGN);

        if (nextBegin != -1 && nextComma < nextBegin) {
            p.tokensJumpTo(nextComma);
            return;
        }

        if (nextBegin != 1) {
            p.tokensJumpTo(nextBegin);
            return;
        }

        throw new Exception("Unable to Recover");
    }
}