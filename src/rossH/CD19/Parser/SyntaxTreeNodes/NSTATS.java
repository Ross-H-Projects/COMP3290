package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
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
     <stats>    --> <strstat> <opt_stats>
     <op_stats> --> <stats> | ɛ
     <strstat>  --> <forstat> | <ifstat>
     <stat>     --> <reptstat> | <iostat> | <returnstat>
     <stat>     --> <asgnOrCallStat>
    <asgnOrCallStat> --> <asnstat> | <callstat>
     */

    // <stats>    --> <stat>; <opt_stats>
    // <stats>    --> <strstat> <opt_stats>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NSTATSNode = new TreeNode(TreeNodeType.NUNDEF);
        boolean isStrStat = false;

        TreeNode stat = new TreeNode(TreeNodeType.NUNDEF);
        TreeNode strStat = null;

        /* todo
        // <strstat> -> <forstat>
        if (p.currentTokenIs(Token.TFOR)) {
            isStrStat = true;
            strStat = NFOR.generateTreeNode(p);
        }

        // <strstat> -> <ifstat>
        if (!isStrStat && p.currentTokenIs(Token.TIFTH)) {
            isStrStat = true;
            strStat = NIFTH.generateTreeNode(p);
        }
        */

        if (!isStrStat) {
            // <stat>
            stat = stat(p);
            if (stat != null && stat.getNodeType() == TreeNodeType.NUNDEF) {
                System.out.println("NSTATS :: ERROR RECOVERY - exiting...");
                System.exit(1);
            }

            // ;
            if (!p.currentTokenIs(Token.TSEMI)) {
                System.out.println("NSTATS :: Expected a semi comma");
                System.exit(1);
            }
            p.moveToNextToken();
        }

        // <opt_stats>
        TreeNode statsOptions = optStats(p);

        // construct actual tree node
        if (strStat != null) {
            NSTATSNode.setLeft(strStat);
        } else {
            NSTATSNode.setLeft(stat);
        }

        if (statsOptions != null) {
            NSTATSNode.setRight(statsOptions);
        }

        NSTATSNode.setValue(TreeNodeType.NSTATS);
        return NSTATSNode;
    }

    // <stat>     --> <reptstat> | <iostat> | <returnstat>
    // <stat>     --> <asgnOrCallStat>
    private static TreeNode stat (CD19Parser p) {

        /* todo
        // <repstat>
        if (p.currentTokenIs(Token.TREPT)) {
            return NREPT.generateTreeNode(p);
        }

        // <iostat>
        if (p.currentTokenIs(Token.TINPT)) {
            return NINPUT.generateTreeNode(p);
        } else if (p.currentTokenIs(Token.TPRIN)) {
            return NPRINT.generateTreeNode(p);
        } else if (p.currentTokenIs(Token.TPRLN)) {
            return NPRLN.generateTreeNode(p);
        }

        // <returnstat>
        if (p.currentTokenIs(Token.TRETN)) {
            return NREPT.generateTreeNode(p);
        }
        */

        // <asgnstat>
        // <callstat>
        // getting to this point implies the next grammar is either <asgnstat>, <callstat>,
        // or we are traversing invalid code
        TreeNode stat = asgnOrCallStat(p);
        return stat;
    }

    // <op_stats> --> <stats> | ɛ
    private static TreeNode optStats (CD19Parser p) {
        // critera under which we don't need
        // another <stat> / <stats> is if
        // current token is end, else, or until

        // end, or else, or until
        if (p.currentTokenIs(Token.TEND) || p.currentTokenIs(Token.TELSE) || p.currentTokenIs(Token.TUNTL)) {
            return null;
        }

        TreeNode optStats = generateTreeNode(p);
        return optStats;
    }

    // <asgnOrCallStat> --> <asgnstat> | <callstat>
    private static TreeNode asgnOrCallStat (CD19Parser p) {
        // asgnstat and callstat both being with an identifier
        if (!p.currentTokenIs(Token.TIDEN)) {
            System.out.println("NSTATS :: asgOrCallStat :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }

        // <callstat>
        /* todo
        if (p.getTokenAhead(1).value() == Token.TLPAR) {
            return NCALL.generateTreeNode(p);
        }
        */

        // <asgnstat> --> <var> <asgnop> <bool>

        // <var>
        TreeNode var = new TreeNode(TreeNodeType.NUNDEF);
        if (p.getTokenAhead(1).value() == Token.TLBRK) { // NARRV: <var> --> <id>[<expr>].<id>
            System.out.println(" <var> --> <id>[<expr>].<id>");
            /* todo
            var = NARRV.generateTreeNode(p);
            */
        } else { // NISVM: <var> --> <id>
            System.out.println("<var> --> <id>");
            var = NSIVM.generateTreeNode(p);
        }

        // <asgnop>
        Token currToken = p.getCurrentToken();

        TreeNode asgnop = new TreeNode(TreeNodeType.NUNDEF);
        if (p.currentTokenIs(Token.TEQUL)) { // =
            asgnop = new TreeNode(TreeNodeType.NASGN);
        } else if (p.currentTokenIs(Token.TPLEQ)) { // +=
            asgnop = new TreeNode(TreeNodeType.NPLEQ);
        } else if (p.currentTokenIs(Token.TMNEQ)) { // -=
            asgnop = new TreeNode(TreeNodeType.NMNEQ);
        } else if (p.currentTokenIs(Token.TSTEQ)) { // *=
            asgnop = new TreeNode(TreeNodeType.NSTEQ);
        } else if (p.currentTokenIs(Token.TDVEQ)) { // /=
            asgnop = new TreeNode(TreeNodeType.NDVEQ);
        } else { // error
            System.out.println("NSTATS :: asgstat - asgnop :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <bool>
        TreeNode bool = NBOOL.generateTreeNode(p);

        asgnop.setLeft(var);
        asgnop.setRight(bool);
        return asgnop;
    }

}