package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;


public class NREPT {
    // <repstat>    --> repeat ( <asgnlist>  ) <stats> until <bool>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NREPTNode = new TreeNode(TreeNodeType.NUNDEF);

        // repeat
        if (!p.currentTokenIs(Token.TREPT)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected the keyword 'repeat'.");
            return NREPTNode;
        }
        p.moveToNextToken();

        // (
        if (!p.currentTokenIs(Token.TLPAR)) {
            p.getCurrentToken();
            p.generateSyntaxError("expected character '('.");
            return NREPTNode;
        }
        p.moveToNextToken();

        // <asgnlist>
        TreeNode asgnList = new TreeNode(TreeNodeType.NUNDEF);
        if (p.currentTokenIs(Token.TIDEN)) {
            asgnList = NASGNS.generateTreeNode(p);
            // handle error recovery in asgnList,
            // asgnList can be null so if it did not parse
            // just set it as null
            if (asgnList.getNodeType() == TreeNodeType.NUNDEF) {
                asgnList = null;
            }
        }

        // )
        if (!p.currentTokenIs(Token.TRPAR)) {
            p.generateSyntaxError("expected character )");
            return NREPTNode;
        }
        p.moveToNextToken();

        // <stats>
        TreeNode stats = NSTATS.generateTreeNode(p);
        // <stats> is a necessary section of
        // NREPT, so if we failed to parse that then we are
        // failing parse NREPT entirely
        if (stats.getNodeType() == TreeNodeType.NUNDEF) {
            return NREPTNode;
        }

        // until
        if (!p.currentTokenIs(Token.TUNTL)) {
            p.generateSyntaxError("expected the keyword 'until'");
            return  NREPTNode;
        }
        p.moveToNextToken();

        // <bool>
        TreeNode bool = NBOOL.generateTreeNode(p);
        // <bool> is a necessary section of
        // NREPT, so if it failed to parse that then we are
        // failing to parse NREPT entirely
        if (bool.getNodeType() == TreeNodeType.NUNDEF) {
            return NREPTNode;
        }

        // getting here implies that
        // that we have successfully parsed all necessary sections
        // of NREPT
        NREPTNode.setValue(TreeNodeType.NREPT);
        NREPTNode.setLeft(asgnList);
        NREPTNode.setMiddle(stats);
        NREPTNode.setRight(bool);
        return NREPTNode;
    }
}
