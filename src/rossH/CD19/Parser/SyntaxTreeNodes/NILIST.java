package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <initlist>      --> <init> , <initlist> | ε
    <initlist>      --> <init>

    NEW RULES:
    <initlist>      --> <init> <opt_initlist> | ε
    <opt_initlist>  --> , <initlist> | ε
*/

public class NILIST {

    // <initlist>      --> <init> <opt_initlist> | ε
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NILISTNode = new TreeNode(TreeNodeType.NUNDEF);

        // <init>
        TreeNode init = NINIT.generateTreeNode(p);
        if (init.getNodeType() == TreeNodeType.NUNDEF) {
            // recover from error
            try {
                initErrorRecovery(p);
            } catch (Exception e) {
                return NILISTNode;
            }
        }

        // <opt_initlist>
        TreeNode initlistOptional = initlistOptional(p);

        // init properly defined AND initlistOptional either non-existant or contains errors
        // so we will just return init
        if (init.getNodeType() != TreeNodeType.NUNDEF &&
                (initlistOptional == null || initlistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return init;
        }

        // init contains errors AND initlistOptional properly defined
        // so we will just return initlistOptional
        if (initlistOptional != null && initlistOptional.getNodeType() != TreeNodeType.NUNDEF
                && init.getNodeType() == TreeNodeType.NUNDEF) {
            return initlistOptional;
        }

        // init contains errors and initlistOptional either non-existant or contains errors
        if (init.getNodeType() == TreeNodeType.NUNDEF &&
                (initlistOptional == null || initlistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return NILISTNode;
        }

        // getting here implies both init and initlistOptional
        // were successfully defined
        NILISTNode.setValue(TreeNodeType.NILIST);
        NILISTNode.setLeft(init);
        NILISTNode.setRight(initlistOptional);
        return NILISTNode;
    }

    // <opt_initlist>  --> , <initlist> | ε
    public static TreeNode initlistOptional (CD19Parser p) {
        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <initlist>
        TreeNode initlist = generateTreeNode(p);
        return initlist;
    }

    public static void initErrorRecovery (CD19Parser p) throws Exception {
        // recoverying from a invalid init entails going to the next ','
        // s.t. the ',' occurs before the keywords: 'types', 'arrays', 'function', or 'main'

        int nextComma = p.nextTokenOccursAt(Token.TCOMA);
        int nextTypes = p.nextTokenOccursAt(Token.TTYPS);
        int nextArrays = p.nextTokenOccursAt(Token.TARRS);
        int nextFunction = p.nextTokenOccursAt(Token.TFUNC);
        int nextMain = p.nextTokenOccursAt(Token.TMAIN);

        // there is no next comma
        // i.e. we have to jump the parser
        // the next sensible section
        if (nextComma == -1) {
            // there is a next comma but it doesn't occur
            // in the <initlist>. thus we can only attempt to jump
            // the parser to the next sensisble section
            if (nextTypes != -1) {
                p.tokensJumpTo(nextTypes);
            } else if (nextArrays != -1) {
                p.tokensJumpTo(nextArrays);
            } else if (nextFunction != -1) {
                p.tokensJumpTo(nextFunction);
            } else if (nextMain != -1) {
                p.tokensJumpTo(nextMain);
            } else {
                throw new Exception("Unable to recover");
            }
            return;
        }

        // types was defined in the program AND
        // comma occurs AND comma occurs before types section is entered
        if (nextTypes != -1 && nextComma < nextTypes) {
            p.tokensJumpTo(nextComma);
            return;
        }

        // arrays was defined in the program AND
        // comma occurs before arrays section is entered
        if (nextArrays != -1 && nextComma < nextArrays) {
            p.tokensJumpTo(nextComma);
            return;
        }

        // a function was defined in the program AND
        // comma occurs before function section is entered
        if (nextFunction != -1 && nextComma < nextFunction) {
            p.tokensJumpTo(nextComma);
            return;
        }

        // main was defined in the program AND
        // comma occurs before main section is entered
        if (nextMain != -1 && nextComma < nextMain) {
            p.tokensJumpTo(nextComma);
            return;
        }

        throw new Exception("Unable to recover");
    }
}
