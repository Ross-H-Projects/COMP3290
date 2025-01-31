package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
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
    public static TreeNode generateTreeNode (CD19Parser p, SymbolTable symbolTable) {
        TreeNode NBOOLNode = new TreeNode(TreeNodeType.NUNDEF);

        // <rel>
        TreeNode rel = rel(p, symbolTable);
        if (rel.getNodeType() == TreeNodeType.NUNDEF) {
            return NBOOLNode;
        }

        // <bool_r>
        TreeNode boolR = boolR(p, symbolTable);
        if (boolR != null && boolR.getNodeType() == TreeNodeType.NUNDEF) {
            return NBOOLNode;
        }

        // <bool>  -> <rel> <bool_r>
        if (boolR != null) {
            NBOOLNode.setValue(TreeNodeType.NBOOL);
            NBOOLNode.setLeft(rel);
            NBOOLNode.setRight(boolR);
            return NBOOLNode;
        }

        // <bool> -> <rel>
        return rel;

    }

    // <bool_r> -> <logop> <rel> <bool_r> | ɛ
    public static TreeNode boolR (CD19Parser p, SymbolTable symbolTable) {

        // <logop>
        TreeNode logop = logop(p);
        if (logop == null) {
            // getting here implies the rule:
            // <bool_r> -> ɛ
            return null;
        }
        p.moveToNextToken();

        // <rel>
        TreeNode rel = rel(p, symbolTable);
        if (rel.getNodeType() == TreeNodeType.NUNDEF) {
            return rel;
        }

        // <bool_r>
        TreeNode boolR = boolR(p, symbolTable);
        if (boolR != null && boolR.getNodeType() == TreeNodeType.NUNDEF) {
            return boolR;
        }

        logop.setLeft(rel);
        if (boolR != null) {
            logop.setRight(boolR);
        }
        return logop;
    }

    public static TreeNode logop (CD19Parser p) {
        if (p.currentTokenIs(Token.TAND)) {
            return new TreeNode(TreeNodeType.NAND);
        } else if (p.currentTokenIs(Token.TOR)) {
            return new TreeNode(TreeNodeType.NOR);
        } else if (p.currentTokenIs(Token.TXOR)) {
            return new TreeNode(TreeNodeType.NXOR);
        }
        return null;
    }

    // <rel>
    /*
        OLD RULES:
        <rel>             -> <expr> <relop> <rel>
        <rel>             -> not <expr> <relop> <rel>
        <rel>             -> <expr>

        NEW RULES:
        <rel>             -> <expr> <opt_relop_expr>
        <rel>             -> not <expr> <relop> <rel>
        <opt_relop_expr>  -> <relop> <expr> | ɛ
    */

    // <rel>             -> <expr> <opt_relop_expr>
    // <rel>             -> not <expr> <relop> <rel>
    public static TreeNode rel (CD19Parser p, SymbolTable symbolTable) {
        boolean notPresent = false;

        // <rel>             -> not <expr> <relop> <expr>
        if (p.currentTokenIs(Token.TNOT)) {
            TreeNode NNOTNode = NNOT.generateTreeNode(p, symbolTable);
            return NNOTNode;
        }

        // <expr>
        TreeNode expr = expr(p, symbolTable);
        if (expr.getNodeType() == TreeNodeType.NUNDEF) {
            return expr;
        }

        // <opt_relop_expr>
        TreeNode optRelopExpr = optRelopExpr(p, symbolTable);
        if (optRelopExpr != null && optRelopExpr.getNodeType() == TreeNodeType.NUNDEF) {
            return optRelopExpr;
        }

        if (optRelopExpr != null) {
            optRelopExpr.setLeft(expr);
            return optRelopExpr;
        }

        return expr;
    }

    // <opt_relop_expr>  -> <relop> <expr> | ɛ
    public static TreeNode optRelopExpr (CD19Parser p, SymbolTable symbolTable) {

        // <relop>
        TreeNode relop = relop(p);
        if (relop == null) {
            return null;
        }
        p.moveToNextToken();

        // <expr>
        TreeNode expr = expr(p, symbolTable);
        if (expr.getNodeType() == TreeNodeType.NUNDEF) {
            return expr;
        }

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
    public static TreeNode expr (CD19Parser p, SymbolTable symbolTable) {
        // <term>
        TreeNode term = term(p, symbolTable);
        if (term.getNodeType() == TreeNodeType.NUNDEF) {
            return term;
        }

        // <opt_add_sub>
        TreeNode optAddSub = optAddSub(p, symbolTable);
        if (optAddSub != null && optAddSub.getNodeType() == TreeNodeType.NUNDEF) {
            return optAddSub;
        }

        if (optAddSub == null) {
            return term;
        }

        optAddSub.setLeft(term);
        return optAddSub;
    }

    // <opt_add_sub>     -> + <expr> | - <expr> | ɛ
    public static TreeNode optAddSub (CD19Parser p, SymbolTable symbolTable) {
        TreeNode optAddSub = new TreeNode(TreeNodeType.NUNDEF);


        if (p.currentTokenIs(Token.TPLUS)) { // +
            optAddSub.setValue(TreeNodeType.NADD);
        } else if (p.currentTokenIs(Token.TMINS)) { // -
            optAddSub.setValue(TreeNodeType.NSUB);
        } else { // ɛ
            return null;
        }
        p.moveToNextToken();

        // <expr>
        TreeNode expr = expr(p, symbolTable);
        if (expr.getNodeType() == TreeNodeType.NUNDEF) {
            return expr;
        }

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
    public static TreeNode term (CD19Parser p, SymbolTable symbolTable) {
        // <fact>
        TreeNode fact = fact(p, symbolTable);
        if (fact.getNodeType() == TreeNodeType.NUNDEF) {
            return fact;
        }

        // <multDivModOpt>
        TreeNode multDivModNothing = multDivModNothing(p, symbolTable);
        if (multDivModNothing != null && multDivModNothing.getNodeType() == TreeNodeType.NUNDEF) {
            return multDivModNothing;
        }

        if (multDivModNothing == null) {
            return fact;
        }

        multDivModNothing.setLeft(fact);
        return multDivModNothing;
    }

    //        <multDivModOpt> -> / <term> | % <term> | * <term> | ɛ
    public static TreeNode multDivModNothing (CD19Parser p, SymbolTable symbolTable) {
        TreeNode multDivModNothing = new TreeNode(TreeNodeType.NUNDEF);

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
        TreeNode term = term(p, symbolTable);
        if (term.getNodeType() == TreeNodeType.NUNDEF) {
            return term;
        }

        multDivModNothing.setRight(term);
        return multDivModNothing;
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
    public static TreeNode fact (CD19Parser p, SymbolTable symbolTable) {

        // <exponent>
        TreeNode exponent = exponent(p, symbolTable);
        if (exponent.getNodeType() == TreeNodeType.NUNDEF) {
            return exponent;
        }

        // <fact_r>
        TreeNode factR = factR(p, symbolTable);
        if (factR != null && factR.getNodeType() == TreeNodeType.NUNDEF) {
            return factR;
        }

        if (factR == null) {
            return exponent;
        }

        factR.setLeft(exponent);
        return factR;
    }

    // <fact_r> -> ^ <fact> | ɛ
    public static TreeNode factR (CD19Parser p, SymbolTable symbolTable) {
        TreeNode factR = new TreeNode(TreeNodeType.NUNDEF);

        // ɛ
        if (!p.currentTokenIs(Token.TCART)) {
            return null;
        }

        // ^
        p.moveToNextToken();
        factR.setValue(TreeNodeType.NPOW);

        // <fact>
        TreeNode fact = fact(p, symbolTable);
        if (fact.getNodeType() == TreeNodeType.NUNDEF) {
            return fact;
        }

        factR.setRight(fact);
        return factR;
    }

    /*
        OLD RULES:
        <exponent> --> <var>
        <exponent> --> <intlit>
        <exponent> --> <reallit>
        <exponent> --> <fncall>
        <exponent> --> true
        <exponent> --> false
        <exponent> --> ( <bool> )

        NEW RULES:
        <exponent> --> <intlit>
        <exponent> --> <reallit>
        <exponent> --> <intlit>
        <exponent> --> ( <bool> )
        <exponent> --> <varOrFnCall>

        <varOrFnCall> --> <var> | <fnCall>
    */

    public static TreeNode exponent (CD19Parser p, SymbolTable symbolTable) {

        // true | false
        if (p.currentTokenIs(Token.TTRUE)) { // true
            p.moveToNextToken();
            return new TreeNode(TreeNodeType.NTRUE);
        } else if (p.currentTokenIs(Token.TFALS)) { // false
            p.moveToNextToken();
            return new TreeNode(TreeNodeType.NFALS);
        }

        boolean negativeNumberLiteral = false;
        if (p.currentTokenIs(Token.TMINS)) {
            negativeNumberLiteral = true;
            p.moveToNextToken();
        }

        // <intlit> | <realit>
        if (p.currentTokenIs(Token.TILIT) || p.currentTokenIs(Token.TFLIT)) { // <intlit>
            TreeNodeType type = (p.currentTokenIs(Token.TILIT)) ? TreeNodeType.NILIT : TreeNodeType.NFLIT;
            TreeNode litNode = new TreeNode(type);
            Token lit = p.getCurrentToken();

            if (negativeNumberLiteral) {
                lit.setLexeme( "-" + lit.getStr());
            }


            p.moveToNextToken();
            SymbolTableRecord stRec = p.insertSymbolIdentifier(lit);
            litNode.setSymbolRecord(stRec);
            return litNode;
        }

        // ( <bool> )
        if (p.currentTokenIs(Token.TLPAR)) { // (
            p.moveToNextToken();

            // <bool>
            TreeNode bool = generateTreeNode(p, symbolTable);
            if (bool.getNodeType() == TreeNodeType.NUNDEF) {
                return bool;
            }

            if (p.currentTokenIs(Token.TRPAR)) { // )
                p.moveToNextToken();
                return bool;
            }

            // getting here implies an open paranthesis
            p.generateSyntaxError("Unclosed paranthesis in expression");
            return new TreeNode(TreeNodeType.NUNDEF);
        }

        // <varOrFnCall>
        if (p.currentTokenIs(Token.TIDEN)) {
            TreeNode varOrFnCall = varOrFnCall(p, symbolTable);
            return varOrFnCall;
        }

        // nothing suitable to match for expression
        String syntaxErrorString = "Illegal start of expression:\n\t\t";
        syntaxErrorString += Token.TPRINTInverse[p.getCurrentToken().value()];
        p.generateSyntaxError(syntaxErrorString);

        return new TreeNode(TreeNodeType.NUNDEF);
    }

    // <varOrFnCall> --> <var> | <fncall>
    private static TreeNode varOrFnCall (CD19Parser p, SymbolTable symbolTable) {
        // <fncall>
        if (p.getTokenAhead(1).value() == Token.TLPAR) {
            TreeNode fncall = NFCALL.generateTreeNode(p, symbolTable);
            return fncall;
        }

        // <var>
        TreeNode var = new TreeNode(TreeNodeType.NUNDEF);
        if (p.getTokenAhead(1).value() == Token.TLBRK) { // NARRV: <var> --> <id>[<expr>].<id>
            var = NARRV.generateTreeNode(p, symbolTable);
        } else { // NISVM: <var> --> <id>
            var = NSIVM.generateTreeNode(p, symbolTable);
        }

        return var;
    }
}
