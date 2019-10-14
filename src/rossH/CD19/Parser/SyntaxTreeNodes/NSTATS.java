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
        if (statsOptions == null) {
            if (strStat != null) {
                return strStat;
            }
            return stat;
        }

        NSTATSNode.setRight(statsOptions);


        if (strStat != null) {
            NSTATSNode.setLeft(strStat);
        } else {
            NSTATSNode.setLeft(stat);
        }

        NSTATSNode.setValue(TreeNodeType.NSTATS);
        return NSTATSNode;
    }

    // <stat>     --> <reptstat> | <iostat> | <returnstat>
    // <stat>     --> <asgnOrCallStat>
    public static TreeNode stat (CD19Parser p) {


        // <repstat>
        if (p.currentTokenIs(Token.TREPT)) {
            return NREPT.generateTreeNode(p);
        }


        // <iostat>
        if (p.currentTokenIs(Token.TINPT)) { // input
            return NINPUT.generateTreeNode(p);
        } else if (p.currentTokenIs(Token.TPRIN)) { // print
            return NPRINT.generateTreeNode(p);
        } else if (p.currentTokenIs(Token.TPRLN)) { // printline
            return NPRLN.generateTreeNode(p);
        }

        // <returnstat>
        if (p.currentTokenIs(Token.TRETN)) {
            return NRETN.generateTreeNode(p);
        }

        // <asgnstat>
        // <callstat>
        // getting to this point implies the next grammar is either <asgnstat>, <callstat>,
        // or we are traversing invalid code
        TreeNode stat = asgnOrCallStat(p);
        return stat;
    }

    // <op_stats> --> <stats> | ɛ
    public static TreeNode optStats (CD19Parser p) {
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
    public static TreeNode asgnOrCallStat (CD19Parser p) {
        // asgnstat and callstat both being with an identifier
        if (!p.currentTokenIs(Token.TIDEN)) {
            System.out.println("NSTATS :: asgOrCallStat :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }

        // <callstat>
        if (p.getTokenAhead(1).value() == Token.TLPAR) {
            return NCALL.generateTreeNode(p);
        }

        // <asgnstat>
        TreeNode asgnStat = asgnStat(p);
        return asgnStat;
    }

    // <asgnstat> --> <var> <asgnop> <bool>
    public static TreeNode asgnStat (CD19Parser p) {

        // <var>
        TreeNode var = new TreeNode(TreeNodeType.NUNDEF);
        if (p.getTokenAhead(1).value() == Token.TLBRK) { // NARRV: <var> --> <id>[<expr>].<id>
            System.out.println(" <var> --> <id>[<expr>].<id>");
            var = NARRV.generateTreeNode(p);
        } else { // NISVM: <var> --> <id>
            System.out.println("<var> --> <id>");
            var = NSIVM.generateTreeNode(p);
        }

        // <asgnop>
        TreeNode asgnop = asgnop(p);
        if (asgnop == null) {
            System.out.println("NSTATS :: asgOrCallStat :: asgnop :: ERROR RECOVERY - exiting...");
            System.exit(1);
        }
        p.moveToNextToken();

        // <bool>
        TreeNode bool = NBOOL.generateTreeNode(p);

        asgnop.setLeft(var);
        asgnop.setRight(bool);
        return asgnop;
    }

    public static TreeNode asgnop (CD19Parser p) {

        if (p.currentTokenIs(Token.TEQUL)) { // =
            return new TreeNode(TreeNodeType.NASGN);
        } else if (p.currentTokenIs(Token.TPLEQ)) { // +=
            return new TreeNode(TreeNodeType.NPLEQ);
        } else if (p.currentTokenIs(Token.TMNEQ)) { // -=
            return new TreeNode(TreeNodeType.NMNEQ);
        } else if (p.currentTokenIs(Token.TSTEQ)) { // *=
            return new TreeNode(TreeNodeType.NSTEQ);
        } else if (p.currentTokenIs(Token.TDVEQ)) { // /=
            return new TreeNode(TreeNodeType.NDVEQ);
        }

        return null;
    }

}