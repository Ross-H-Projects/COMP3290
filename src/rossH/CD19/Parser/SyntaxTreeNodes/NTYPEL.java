package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <typelist>      -> <type> <typelist>
    <typelist>      -> <type>

    NEW RULES:
    <typelist>      -> <type> <opt_typelist>
    <opt_typelist>  -> <typelst> | ε
 */
public class NTYPEL {

    // <typelist>      -> <type> <opt_typelist>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NTYPELNode = new TreeNode(TreeNodeType.NUNDEF);

        // <type>
        TreeNode type = structOrArrayType(p);
        if (type.getNodeType() == TreeNodeType.NUNDEF) {
            try {
               errorRecovery(p);
            } catch (Exception e) {
                return NTYPELNode;
            }
        }

        // opt_typelist
        TreeNode typelistOptional = typelistOptional(p);

        // type properly defined AND typelistOptional either non-existant or contains errors
        // so we will just return type
        if (type.getNodeType() != TreeNodeType.NUNDEF &&
                (typelistOptional == null || typelistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return type;
        }

        // type contains errors AND typelistOptional properly defined
        // so we will just return typelistOptional
        if (typelistOptional != null && typelistOptional.getNodeType() != TreeNodeType.NUNDEF
                && type.getNodeType() == TreeNodeType.NUNDEF) {
            return typelistOptional;
        }

        // type contains errors and typelistOptional either non-existant or contains errors
        if (type.getNodeType() == TreeNodeType.NUNDEF &&
                (typelistOptional == null || typelistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return NTYPELNode;
        }

        // getting here implies both type and typelistOptional
        // were successfully defined
        NTYPELNode.setValue(TreeNodeType.NTYPEL);
        NTYPELNode.setLeft(type);
        NTYPELNode.setRight(typelistOptional);
        return NTYPELNode;
    }

    /*
        NRTYPE <type> --> <structid> is <fields> end
        NATYPE <type> --> <typeid> is array [ <expr> ] of <structid>
     */
    public static TreeNode structOrArrayType (CD19Parser p) {
        TreeNode type = new TreeNode(TreeNodeType.NUNDEF);

        // <structid>
        // <typeid>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected an identifier.");
            return type;
        }

        // is
        if (p.getTokenAhead(1).value() != Token.TIS) {
            Token offendingToken = p.getTokenAhead(1);
            p.generateSyntaxError("expected the keyword 'is'", offendingToken.getLn());
            return type;
        }

        // array
        if (p.getTokenAhead(2).value() == Token.TARAY) {
            TreeNode arrayType = NATYPE.generateTreeNode(p);
            return arrayType;
        }

        // struct
        TreeNode structType = NRTYPE.generateTreeNode(p);
        return structType;
    }

    // <opt_typelist>  -> <typelst> | ε
    public static TreeNode typelistOptional (CD19Parser p) {
        // criteria under which more types are not being defined is when
        // the next token is not an identifier
        // ε
        if (!p.currentTokenIs(Token.TIDEN)) {
            return null;
        }

        // <typelist>
        TreeNode typelist = generateTreeNode(p);
        return typelist;
    }

    public static void errorRecovery (CD19Parser p) throws Exception {
        // we need to go to the next 'is' (and move back one next token)
        // if we fail to do this we need to exit the types section
        // complete
        // the next 'is' also needs to be before the next 'arrays', 'function',
        // or 'main' token

        int nextIs = p.nextTokenOccursAt(Token.TIS);
        int nextArrays = p.nextTokenOccursAt(Token.TARRS);
        int nextFunction = p.nextTokenOccursAt(Token.TFUNC);
        int nextMain = p.nextTokenOccursAt(Token.TMAIN);

        // there is a next comma but it doesn't occur
        // in the <typelist>. thus we can only jump
        // the parser to the next sensisble section
        if (nextIs == -1) {
            if (nextArrays != -1) {
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

        // arrays was defined in the program AND
        // is occurs before arrays section is entered
        if (nextArrays != -1 && nextIs < nextArrays) {
            p.tokensJumpTo(nextIs - 1);
            return;
        }

        // a function was defined in the program AND
        // is occurs before function section is entered
        if (nextFunction != -1 && nextIs < nextFunction) {
            p.tokensJumpTo(nextIs - 1);
            return;
        }

        // main was defined in the program AND
        // is occurs before main section is entered
        if (nextMain != -1 && nextIs < nextMain) {
            p.tokensJumpTo(nextIs - 1);
            return;
        }

        throw new Exception("Unable to recover");
    }
}
