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

        // opt_typelist
        TreeNode typelistOptional = typelistOptional(p);
        if (typelistOptional == null) {
            return type;
        }

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
}
