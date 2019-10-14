package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;


public class NGLOB {

    // <globals>        --> <consts> <types> <arrays>
    public static TreeNode generateTreeNode (CD19Parser p) {

        // <consts>

        // <types>

        // <arrays>
    }

    // <consts>         --> constants <initlist> | ε
    public static TreeNode consts (CD19Parser p) {

        // ε
        if (p.currentTokenIs(Token.TCONS)) {
            return null;
        }

        // constants
    }
}