package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <vlist>     --> <var> , <vlist>
    <vlist>     --> <var>

    NEW RULES:
    <vlist>     --> <var> <opt_vlist>
    <opt_vlist> --> , <vlist> | ε
*/

public class NVLIST {

    // <vlist>     --> <var> <opt_vlist>
    public static TreeNode generateTreeNode (CD19Parser p, SymbolTable symbolTable) {
        TreeNode NVLISTNode = new TreeNode(TreeNodeType.NUNDEF);

        // <var>
        TreeNode var = new TreeNode(TreeNodeType.NUNDEF);
        if (p.getTokenAhead(1).value() == Token.TLBRK) { // NARRV: <var> --> <id>[<expr>].<id>
            var = NARRV.generateTreeNode(p, symbolTable);
        } else { // NISVM: <var> --> <id>
            var = NSIVM.generateTreeNode(p, symbolTable);
        }
        
        if (var.getNodeType() == TreeNodeType.NUNDEF) {
            try {
                errorRecovery(p);
            } catch (Exception e) {
                return NVLISTNode;
            }
        }

        // <opt_vlist>
        TreeNode vlistOptional =  vlistOptional(p, symbolTable);
        // var properly defined AND vlistOptional either non-existant or contains errors
        // so we will just return var
        if (var.getNodeType() != TreeNodeType.NUNDEF &&
                (vlistOptional == null || vlistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return var;
        }

        // var contains errors AND vlistOptional properly defined
        // so we will just return vlistOptional
        if (vlistOptional != null && vlistOptional.getNodeType() != TreeNodeType.NUNDEF
                && var.getNodeType() == TreeNodeType.NUNDEF) {
            return vlistOptional;
        }

        // var contains errors and vlistOptional either non-existant or contains errors
        if (var.getNodeType() == TreeNodeType.NUNDEF &&
                (vlistOptional == null || vlistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return NVLISTNode;
        }

        // getting here implies both var and vlistOptional
        // were successfully defined
        NVLISTNode.setValue(TreeNodeType.NVLIST);
        NVLISTNode.setLeft(var);
        NVLISTNode.setRight(vlistOptional);
        return NVLISTNode;
    }

    // <opt_vlist> --> , <vlist> | ε
    public static TreeNode vlistOptional (CD19Parser p, SymbolTable symbolTable) {
        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <vlist>
        TreeNode vlist = generateTreeNode(p, symbolTable);
        return vlist;
    }
    
    public static void errorRecovery (CD19Parser p) throws  Exception {
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
