package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

public class NFUNCS {
    // <funcs>      --> <func> <funcs> | ε
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NFUNCS = new TreeNode(TreeNodeType.NUNDEF);

        // criteria under which <funcs> --> ε
        // is when the next token is not the keyword 'function'
        if (!p.currentTokenIs(Token.TFUNC)) {
            return null;
        }

        // <func>
        TreeNode func = NFUND.generateTreeNode(p);
        if (func.getNodeType() == TreeNodeType.NUNDEF) {
            try {
                errorRecovery(p);
            } catch (Exception e) {
                return NFUNCS;
            }
        }

        // <funcs>
        TreeNode funcs = generateTreeNode(p);

        // func properly defined AND funcs either non-existant or contains errors
        // so we will just return func
        if (func.getNodeType() != TreeNodeType.NUNDEF &&
                (funcs == null || funcs.getNodeType() == TreeNodeType.NUNDEF)) {
            return func;
        }

        // func contains errors AND funcs properly defined
        // so we will just return funcs
        if (funcs != null && funcs.getNodeType() != TreeNodeType.NUNDEF
                && func.getNodeType() == TreeNodeType.NUNDEF) {
            return funcs;
        }

        // func contains errors and funcs either non-existant or contains errors
        if (func.getNodeType() == TreeNodeType.NUNDEF &&
                (funcs == null || funcs.getNodeType() == TreeNodeType.NUNDEF)) {
            return NFUNCS;
        }

        // getting here implies both func and funcs
        // were successfully defined
        NFUNCS.setValue(TreeNodeType.NFUNCS);
        NFUNCS.setLeft(func);
        return NFUNCS;
    }

    public static void errorRecovery (CD19Parser p) throws Exception {
        // we need to find the next 'function' token, or in failing to do
        // that then we need to jump the parser to the next sensisble section
        // of code

        int nextFunction = p.nextTokenOccursAt(Token.TFUNC);
        int nextMain = p.nextTokenOccursAt(Token.TMAIN);

        if (nextFunction == -1) {
            if (nextMain != -1) {
                p.tokensJumpTo(nextMain);
                return;
            }
            throw new Exception("Unable to recover");
        }

        if (nextMain != -1 && nextFunction < nextMain) {
            p.tokensJumpTo(nextFunction);
            return;
        }

        throw new Exception("Unable to recover");
    }
}
