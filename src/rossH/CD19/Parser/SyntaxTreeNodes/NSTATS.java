package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;
import rossH.CD19.Scanner.Token;

public class NSTATS {

    /*
     OLD RULES:
     <stats>    --> <stat>; <stats>
     <stats>    --> <stat>;
     <stats>    --> <strstat> <stats>
     <stats>    --> <strstat>
     <strstat>  --> <forstat> | <ifstat>
     <stat>     --> <reptstat> | <asgnstat> | <iostat> | <callstat> | <returnstat>


     NEW RULES:
     <stats>    --> <stat>; <opt_stats>
     <op_stats> --> <stats> | ɛ
     <stats>    --> <strstat> <opt_stats>
     <strstat>  --> <forstat> | <ifstat>
     <stat>     --> <reptstat> | <asgnstat> | <iostat> | <callstat> | <returnstat>

     */

    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NSTATSNode = new TreeNode(TreeNodeType.NUNDEF);

        // <stat>
        TreeNode stat = stat(p);
        if (stat != null && stat.getNodeType() == TreeNodeType.NUNDEF) {
            System.out.println("stat :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }

        // ;
        if (p.currentTokenIs(Token.TSEMI)) {
            p.generateSyntaxError("Expected a semi comma");
            // prematurely end parsing due to irrecoverable error
            return NSTATSNode;
        }
        p.moveToNextToken();

        // <opt_stats>
        TreeNode statsOptions = opt_stats(p);
        if (statsOptions == null) {
            // NSTATS with single stat
        }

    }

    private static TreeNode stat (CD19Parser p) {

    }

    private static TreeNode opt_stats (CD19Parser p) {

    }

}