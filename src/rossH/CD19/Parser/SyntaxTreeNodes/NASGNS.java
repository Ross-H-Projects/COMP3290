package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <alist>     --> <asgnstat> , <alist>
    <aslist>    --> <asgnstat>

    NEW RULES:
    <alist>     --> <asgnstat> <opt_alist>
    <opt_alist> --> , <alist> | ε

*/

public class NASGNS {
    // <alist>  --> <asgnstat> <opt_alist>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NASGNSNode = new TreeNode(TreeNodeType.NUNDEF);

        // <asgnstat>
        TreeNode asgnStat = NSTATS.asgnStat(p);
        if (asgnStat.getNodeType() == TreeNodeType.NUNDEF) {
            try {
                errorRecovery(p);
            } catch (Exception e) {
                return NASGNSNode;
            }
        }

        // <opt_alist>
        TreeNode alistOptional = alistOptional(p);

        // asgnStat properly defined AND alistOptional either non-existant or contains errors
        // so we will just return asgnStat
        if (asgnStat.getNodeType() != TreeNodeType.NUNDEF &&
                (alistOptional == null || alistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return asgnStat;
        }

        // asgnStat contains errors AND alistOptional properly defined
        // so we will just return alistOptional
        if (alistOptional != null && alistOptional.getNodeType() != TreeNodeType.NUNDEF
                && asgnStat.getNodeType() == TreeNodeType.NUNDEF) {
            return alistOptional;
        }

        // asgnStat contains errors and alistOptional either non-existant or contains errors
        if (asgnStat.getNodeType() == TreeNodeType.NUNDEF &&
                (alistOptional == null || alistOptional.getNodeType() == TreeNodeType.NUNDEF)) {
            return NASGNSNode;
        }

        // getting here implies both asgnStat and alistOptional
        // were successfully defined
        NASGNSNode.setValue(TreeNodeType.NASGNS);
        NASGNSNode.setLeft(asgnStat);
        NASGNSNode.setRight(alistOptional);
        return NASGNSNode;
    }

    // <opt_alist> --> , <alist> | ε
    public static TreeNode alistOptional (CD19Parser p) {

        // ε
        if (!p.currentTokenIs(Token.TCOMA)) {
            return null;
        }

        // ,
        p.moveToNextToken();

        // <alist>
        TreeNode alist = generateTreeNode(p);
        return alist;
    }

    public static void errorRecovery (CD19Parser p) throws Exception {
        // recoverying from a invalid nasgnstat entails going to the next ','
        // s.t. the ',' occurs before the characters ';' or ')'

        int nextComma = p.nextTokenOccursAt(Token.TCOMA);

        int nextMinViableTokenBesidesComma = -1;
        int nextSemiComma = p.nextTokenOccursAt(Token.TSEMI);
        int nextLeftParanthesis = p.nextTokenOccursAt(Token.TLPAR);
        if (nextSemiComma != -1) {
            nextMinViableTokenBesidesComma = nextSemiComma;
        }
        if (nextLeftParanthesis != -1 && nextLeftParanthesis < nextMinViableTokenBesidesComma) {
            nextMinViableTokenBesidesComma = nextLeftParanthesis;
        }

        // if there is no necessary token for
        // NREPT or NFOR then just fail anyway
        // as it will fail NREPT or NFOR ultimately
        if (nextMinViableTokenBesidesComma == -1) {
            throw new Exception("Unable to recover.");
        }

        // there is no next comma
        // i.e. we have to jump the parser
        // the next sensible section
        if (nextComma == -1) {
            // there is a next comma but it doesn't occur
            // in the <initlist>. thus we can only attempt to jump
            // the parser to the next sensisble section
            if (nextMinViableTokenBesidesComma != -1) {
                p.tokensJumpTo(nextMinViableTokenBesidesComma);
            } else {
                throw new Exception("Unable to recover");
            }
            return;
        }

        // if next comma occurs before the next ';' or ')'
        // jump there
        if (nextComma < nextMinViableTokenBesidesComma) {
            p.tokensJumpTo(nextComma);
            return;
        }


        throw new Exception("Unable to recover");
    }
}
