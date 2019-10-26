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

        // handle invalid strStat
        if (isStrStat && strStat.getNodeType() == TreeNodeType.NUNDEF) {
            try {
                errorRecoveryStrStat(p);
            } catch (Exception e) {
                return NSTATSNode;
            }
        }

        if (!isStrStat) {
            // <stat>
            stat = stat(p);
            if (stat.getNodeType() == TreeNodeType.NUNDEF) {
                try {
                    errorRecovery(p);
                } catch (Exception e) {
                    return NSTATSNode;
                }
            }

            // ;
            if (stat.getNodeType() != TreeNodeType.NUNDEF) {
                if (!p.currentTokenIs(Token.TSEMI)) {
                    stat = new TreeNode(TreeNodeType.NUNDEF);
                    p.generateSyntaxError("Statements must end with a semi-comma");
                    try {
                        errorRecovery(p);
                    } catch (Exception e) {
                        return NSTATSNode;
                    }
                } else {
                    p.moveToNextToken();
                }
            }
        }

        // simplify code
        if (isStrStat) {
            stat = strStat;
        }

        // <opt_stats>
        TreeNode statsOptions = optStats(p);

        // stat properly defined AND statsOptions either non-existant or contains errors
        // so we will just return stat
        if (stat.getNodeType() != TreeNodeType.NUNDEF &&
                (statsOptions == null || statsOptions.getNodeType() == TreeNodeType.NUNDEF)) {
            return stat;
        }

        // stat contains errors AND statsOptions properly defined
        // so we will just return statsOptions
        if (statsOptions != null && statsOptions.getNodeType() != TreeNodeType.NUNDEF
                && stat.getNodeType() == TreeNodeType.NUNDEF) {
            return statsOptions;
        }

        // stat contains errors and statsOptions either non-existant or contains errors
        if (stat.getNodeType() == TreeNodeType.NUNDEF &&
                (statsOptions == null || statsOptions.getNodeType() == TreeNodeType.NUNDEF)) {
            return NSTATSNode;
        }

        // getting here implies both (stat OR strStat) and statsOptions
        // are both defined properly
        NSTATSNode.setValue(TreeNodeType.NSTATS);
        NSTATSNode.setLeft(stat);
        NSTATSNode.setRight(statsOptions);
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
        // current token is 'end', 'else', or 'until'

        // end, or else, or until
        if (p.currentTokenIs(Token.TEND) || p.currentTokenIs(Token.TELSE) || p.currentTokenIs(Token.TUNTL)) {
            return null;
        }

        TreeNode optStats = generateTreeNode(p);
        return optStats;
    }

    // <asgnOrCallStat> --> <asgnstat> | <callstat>
    public static TreeNode asgnOrCallStat (CD19Parser p) {
        // asgnstat and callstat both start with an identifier
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an identifer");
            return new TreeNode(TreeNodeType.NUNDEF);
        }

        // <callstat>
        if (p.getTokenAhead(1).value() == Token.TLPAR) {
            TreeNode callstat = NCALL.generateTreeNode(p);
            return callstat;
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
            var = NARRV.generateTreeNode(p);
        } else { // NISVM: <var> --> <id>
            var = NSIVM.generateTreeNode(p);
        }

        // handle var error recovery
        if (var.getNodeType() == TreeNodeType.NUNDEF) {
            return var;
        }

        // <asgnop>
        TreeNode asgnop = asgnop(p);
        if (asgnop == null) {
            p.generateSyntaxError("Expected an assignment operator: '=', '+=', '-=', '*=', or '/='.");
            return new TreeNode(TreeNodeType.NUNDEF);
        }
        p.moveToNextToken();

        // <bool>
        TreeNode bool = NBOOL.generateTreeNode(p);
        // handle bool error recovery
        if (bool.getNodeType() == TreeNodeType.NUNDEF) {
            return bool;
        }

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

    private static void errorRecovery (CD19Parser p) throws Exception {
        // we need to move to the earliest next occurence of any of the following tokens
        // ';' (and one more past that). 'repeat', 'for', 'if', 'input', 'print', 'printline', 'return',
        // 'end', 'else', or 'until'

        int nextViableTokenOccurence[] = new int[11];
        nextViableTokenOccurence[0] = p.nextTokenOccursAt(Token.TSEMI);

        // handle going one past the semi colon if it occurs
        if (nextViableTokenOccurence[0] != -1) {
            nextViableTokenOccurence[0] += 1;
        }

        nextViableTokenOccurence[1] = p.nextTokenOccursAt(Token.TREPT);
        nextViableTokenOccurence[2] = p.nextTokenOccursAt(Token.TFOR);
        nextViableTokenOccurence[3] = p.nextTokenOccursAt(Token.TIFTH);
        nextViableTokenOccurence[4] = p.nextTokenOccursAt(Token.TINPT);
        nextViableTokenOccurence[5] = p.nextTokenOccursAt(Token.TPRIN);
        nextViableTokenOccurence[6] = p.nextTokenOccursAt(Token.TPRIN);
        nextViableTokenOccurence[7] = p.nextTokenOccursAt(Token.TRETN);
        nextViableTokenOccurence[8]= p.nextTokenOccursAt(Token.TEND);
        nextViableTokenOccurence[9] = p.nextTokenOccursAt(Token.TELSE);
        nextViableTokenOccurence[10] = p.nextTokenOccursAt(Token.TUNTL);

        int minNextViableOccurence = -1;
        boolean atleastOneViableOccurenceFound = false;
        for (int i = 0; i < nextViableTokenOccurence.length; i++) {
            if (!atleastOneViableOccurenceFound && nextViableTokenOccurence[i] != -1) {
                atleastOneViableOccurenceFound = true;
                minNextViableOccurence = nextViableTokenOccurence[i];
            } else if (atleastOneViableOccurenceFound && nextViableTokenOccurence[i] != -1 &&
                    nextViableTokenOccurence[i] < minNextViableOccurence) {
                minNextViableOccurence = nextViableTokenOccurence[i];
            }
        }



        if (!atleastOneViableOccurenceFound) {
            throw new Exception("Unable to recover");
        } else {
            p.tokensJumpTo(minNextViableOccurence);
        }
    }

    public static void errorRecoveryStrStat (CD19Parser p) throws Exception {
        // when recovering from an error that resulted in
        // in attempting to parse a strStat we need to consider
        // that we have already gone to the next occurence of the token 'end',
        // so we need to jump the parser to the next occurence of any of the following tokens:
        // 'repeat', 'for', 'if', 'input', 'print', 'printline', 'return', or an
        //  identifier
        //  ALSO which ever occurs first we will jump to

        int nextViableTokenOccurence[] = new int[8];

        nextViableTokenOccurence[0] = p.nextTokenOccursAt(Token.TREPT);
        nextViableTokenOccurence[1] = p.nextTokenOccursAt(Token.TFOR);
        nextViableTokenOccurence[2] = p.nextTokenOccursAt(Token.TIFTH);
        nextViableTokenOccurence[3] = p.nextTokenOccursAt(Token.TINPT);
        nextViableTokenOccurence[4] = p.nextTokenOccursAt(Token.TPRIN);
        nextViableTokenOccurence[5] = p.nextTokenOccursAt(Token.TPRIN);
        nextViableTokenOccurence[6] = p.nextTokenOccursAt(Token.TRETN);
        nextViableTokenOccurence[7] = p.nextTokenOccursAt(Token.TIDEN);

        int minNextViableOccurence = -1;
        boolean atleastOneViableOccurenceFound = false;
        for (int i = 0; i < nextViableTokenOccurence.length; i++) {
            if (!atleastOneViableOccurenceFound && nextViableTokenOccurence[i] != -1) {
                atleastOneViableOccurenceFound = true;
                minNextViableOccurence = nextViableTokenOccurence[i];
            } else if (atleastOneViableOccurenceFound && nextViableTokenOccurence[i] != -1 &&
                    nextViableTokenOccurence[i] < minNextViableOccurence) {
                minNextViableOccurence = nextViableTokenOccurence[i];
            }
        }


        if (!atleastOneViableOccurenceFound) {
            throw new Exception("Unable to recover");
        } else {
            p.tokensJumpTo(minNextViableOccurence);
        }
    }

}