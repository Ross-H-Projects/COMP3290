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
        TreeNode type;

        // <structid>
        // <typeid>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected an identifier =");
            System.out.println("NTYPEL :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }

        // is
        if (p.getTokenAhead(1).value() != Token.TIS) {
            Token offendingToken = p.getTokenAhead(1);
            p.generateSyntaxError("expected the keyword 'is'", offendingToken.getLn());
            System.out.println("NTYPEL :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }

        // array
        if (p.getTokenAhead(2).value() == Token.TARAY) {
            type = NATYPE.generateTreeNode(p);
            return type;
        }

        // struct
        type = NRTYPE.generateTreeNode(p);
        return type;
    }

    public static TreeNode typelistOptional (CD19Parser p) {
        // criteria under which more types are not being defined is when
        // the next token is not an identifier
        if (!p.currentTokenIs(Token.TIDEN)) {
            return null;
        }
        TreeNode typelist = generateTreeNode(p);
        return typelist;
    }
}
