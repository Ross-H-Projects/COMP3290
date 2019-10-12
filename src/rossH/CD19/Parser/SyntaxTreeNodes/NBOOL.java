package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;
import rossH.CD19.Scanner.Token;

/*
    OLD RULES:
    <bool>   -> <bool> <logop> <rel>
    <bool>   -> <rel>

    NEW RULES:
    <bool>   -> <rel> <bool_r>
    <bool_r> -> <logop> <rel> <bool_r> | ɛ

 */

public class NBOOL {
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NBOOLNode = new TreeNode(TreeNodeType.NUNDEF);

        // <rel>
        TreeNode rel = rel(p);

        // <bool_r>
        TreeNode boolR = boolR(p);

        // construct the actual TreeNode

        if (boolR != null) {
            NBOOLNode.setValue(TreeNodeType.NBOOL);
            NBOOLNode.setLeft(rel);
            NBOOLNode.setRight(boolR);

            return NBOOLNode;
        }

        return rel;

    }

    // <bool_r> -> <logop> <rel> <bool_r> | ɛ
    public static TreeNode boolR (CD19Parser p) {

        // <logop>
        TreeNode logop;
        if (p.currentTokenIs(Token.TAND)) {
            logop = new TreeNode(TreeNodeType.NAND);
        } else if (p.currentTokenIs(Token.TOR))) {
            logop = new TreeNode(TreeNodeType.NOR);
        } else if (p.currentTokenIs(Token.TXOR))) {
            logop = new TreeNode(TreeNodeType.NXOR);
        } else {
            // getting here implies the rule:
            // <bool_r> -> ɛ
            return null;
        }
        p.moveToNextToken();

        // <rel>
        TreeNode rel = rel(p);

        // <bool_r>
        TreeNode boolR = boolR(p);

        logop.setLeft(rel);
        if (boolR != null) {
            logop.setRight(boolR);
        }
        return logop;
    }

    // <rel>
    /*
        OLD RULES:
        <rel>             -> <expr> <relop> <rel>
        <rel>             -> <expr>

        NEW RULES:
        <rel>             -> <expr> <opt_relop_expr>
        <opt_relop_expr>  -> <relop> <expr> | ɛ
    */

    // <rel>             -> <expr> <opt_relop_expr>
    public static TreeNode rel (CD19Parser p) {
        // <expr>
        TreeNode expr = expr(p);

        // <opt_relop_expr>
        TreeNode optRelopExpr = optRelopExpr(p);
        if (optRelopExpr != null) {
            optRelopExpr.setLeft(expr);
            return optRelopExpr;
        }

        return expr;
    }

    // <opt_relop_expr>  -> <relop> <expr> | ɛ
    public static TreeNode optRelopExpr (CD19Parser p) {

        // <relop>
        TreeNode relop = relop(p);
        if (relop == null) {
            return null;
        }

        // <expr>
        TreeNode expr = expr(p);

        relop.setRight(expr);
        return relop;
    }

    // <relop> --> == | != | > | < | >= | <=
    public static TreeNode relop (CD19Parser p) {
        if (p.currentTokenIs(Token.TEQEQ)) { // ==
            return new TreeNode(TreeNodeType.NEQL);
        } else if (p.currentTokenIs(Token.TNEQL)) { // !=
            return new TreeNode(TreeNodeType.NNEQ);
        } else if (p.currentTokenIs(Token.TGRTR)) { // >
            return new TreeNode(TreeNodeType.NGRT);
        } else if (p.currentTokenIs(Token.TLESS)) { // <
            return new TreeNode(TreeNodeType.NLSS);
        } else if (p.currentTokenIs(Token.TGEQL)) { // >=
            return new TreeNode(TreeNodeType.NGEQ);
        } else if (p.currentTokenIs(Token.TLEQL))  { // <=
            return new TreeNode(TreeNodeType.NLEQ);
        }

        return null;
    }

    /*
        OLD RULES:
        <expr>            -> <expr> + <expr>
        <expr>            -> <expr> - <expr>
        <expr>            -> <term>

        NEW RULES:
        <expr>            -> <term> <opt_add_sub>
        <opt_add_sub>     -> + <expr> | - <expr> | ɛ

    */

    // <expr>            -> <term> <opt_add_sub>
    public static TreeNode expr (CD19Parser p) {
        // <term>
        TreeNode term;

        // <opt_add_sub>
        TreeNode optAddSub = optAddSub(p);

        if (optAddSub == null) {
            return term;
        }

        optAddSub.setLeft(term);
        return optAddSub;
    }

    // <opt_add_sub>     -> + <expr> | - <expr> | ɛ
    public static TreeNode optAddSub (CD19Parser p) {
        TreeNode optAddSub;

        if (p.currentTokenIs(Token.TPLUS)) { // +
            optAddSub.setValue(TreeNodeType.NADD);
        } else if (p.currentTokenIs(Token.TMINS)) { // -
            optAddSub.setValue(TreeNodeType.NSUB);
        } else { // ɛ
            return null;
        }
        p.moveToNextToken();

        // <expr>
        TreeNode expr = expr(p);
        optAddSub.setRight(expr);
        return optAddSub;
    }

    /*
        OLD RULES:
        <term>          -> <term> / <fact>
        <term>          -> <term> % <fact>
        <term>          -> <term> * <fact>
        <term>          -> <fact>

        NEW RULES:
        <term>          -> <fact> <multDivModOpt>
        <multDivModOpt> -> / <term>
        <multDivModOpt> -> % <term>
        <multDivModOpt> -> * <term>
        <multDivModOpt> -> ɛ
    */

    // <term>          -> <fact> * <multDivModOpt>
    public static TreeNode term (CD19Parser p) {
        // <fact>
        TreeNode fact = fact(p);

        // <multDivModOpt>
        TreeNode multDivModNothing = multDivModNothing();

        if (multDivModNothing == null) {
            return fact;
        }

        multDivModNothing.setLeft(fact);
        return multDivModNothing;
    }

    //        <multDivModOpt> -> / <term> | % <term> | * <term> | ɛ
    public static TreeNode multDivModNothing (CD19Parser p) {
        TreeNode multDivModNothing;

        if (p.currentTokenIs(Token.TDIVD)) { // /
            multDivModNothing.setValue(TreeNodeType.NDIV);
        } else if (p.currentTokenIs(Token.TPERC)) { // %
            multDivModNothing.setValue(TreeNodeType.NMOD);
        } else if (p.currentTokenIs(Token.TSTAR)) { // *
            multDivModNothing.setValue(TreeNodeType.NMUL);
        } else { // ɛ
            return null;
        }
        p.moveToNextToken();


        // <term>
        TreeNode term = term(p);
        multDivModNothing.setRight(term);
        term multDivModNothing;
    }

    /*
        OLD RULES:
        <fact>   -> <fact> ^ <exponent>
        <fact>   -> <exponent>

        NEW RULES:
        <fact>   -> <exponent <fact_r>
        <fact_r> -> ^ <fact> | ɛ
    */

    // <fact>   -> <exponent <fact_r>
    public static TreeNode fact (CD19Parser p) {

        // <exponent>
        TreeNode exponent = exponent(p);

        // <fact_r>
        TreeNode factR = factR(p);

        if (factR == null) {
            return exponent;
        }

        factR.setLeft(exponent);
        return factR;
    }

    // <fact_r> -> ^ <fact> | ɛ
    public static TreeNode factR (CD19Parser p) {
        TreeNode factR;

        if (!p.currentTokenIs(Token.TCART)) { // ɛ
            return null;
        }

        // ^
        p.moveToNextToken();
        factR.setValue(TreeNodeType.NPOW);

        // <fact>
        TreeNode fact = fact(p);
        factR.setRight(fact);
        return factR;
    }

    public static TreeNode exponent (CD19Parser p) {

        return null;
    }




}
