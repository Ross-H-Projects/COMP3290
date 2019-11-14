package rossH.CD19.codegen;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolDataType;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import java.beans.Expression;
import java.util.List;

public class StatementGenerator {
    public static void generateCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        if (treeNode == null) {
            return;
        }

        // code gen for single statement
        if (treeNode.getNodeType() != TreeNodeType.NSTATS) {
            generateStatementCode(treeNode, codeGenerator);
            return;
        }

        // gen for left
        generateCode(treeNode.getLeft(), codeGenerator);

        // gen for right
        generateCode(treeNode.getRight(), codeGenerator);
    }

    public static void generateStatementCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {

        if (treeNode.getNodeType() == TreeNodeType.NASGN) {
            generateNASGNCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NPLEQ) {
            generateNPLEQCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NPRLN) {
            generateNPRLNCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NPRINT) {
            generateNPRINTCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NINPUT) {
            generateNINPUTCode(treeNode, codeGenerator);
        }

    }

    public static void generateNASGNCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {

        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) {
            codeGenerator.generateNSIVMCode(treeNode.getLeft());
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) {
            codeGenerator.generateNARRVCode(treeNode.getLeft());
        }

        // todo
        //  need to eventually implement for bool, real, boolean etc
        //  NEED TO EVENTUALLY CHANGE THIS TO BoolGenerator
        ExpressionGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // do a store at the end
        codeGenerator.addToOpCodes("43");
    }

    public static void generateNPLEQCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // todo (MAYBE? - not sure if NARRV supports this type of operation)
        //  need to eventually implement for NARRV

        // load address we are storing result into
        codeGenerator.generateNSIVMCode(treeNode.getLeft());

        // load value of the address we want to add to
        codeGenerator.generateLoadVariableCode(treeNode.getLeft());

        // todo
        //  need to eventually implement for bool, real, boolean etc
        //  NEED TO EVENTUALLY CHANGE THIS TO BoolGenerator
        ExpressionGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // do an add
        codeGenerator.addToOpCodes("11");

        // do a store at the end
        codeGenerator.addToOpCodes("43");
    }

    public static void generateNPRLNCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // todo
        //  support string print

        if (treeNode.getLeft().getNodeType() == TreeNodeType.NPRLST) {
            generateNPRLSTCode(treeNode.getLeft(), codeGenerator);
        } else { // expression
            ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);
            // VALPR
            codeGenerator.addToOpCodes("62");
        }

        // NEWLN
        codeGenerator.addToOpCodes("65");
    }

    public static void generateNPRINTCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // todo
        //  support string print

        if (treeNode.getLeft().getNodeType() == TreeNodeType.NPRLST) {
            generateNPRLSTCode(treeNode.getLeft(), codeGenerator);
        } else { // expression
            ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);
            // VALPR
            codeGenerator.addToOpCodes("62");
        }
    }

    public static void generateNPRLSTCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // todo
        //  currently only supports printing of on variable, not a list of variables / literals
        //  need to support more like actual expressions and string

        // left
        // todo
        //  support string printing
        ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);

        if (treeNode.getRight() == null) {
            return;
        }

        // right
        if (treeNode.getRight().getNodeType() == TreeNodeType.NPRLST) {
            generateNPRLSTCode(treeNode.getRight(), codeGenerator);
        } else { // expression
            ExpressionGenerator.generateCode(treeNode.getRight(), codeGenerator);
            // VALPR
            codeGenerator.addToOpCodes("62");
        }
    }

    public static void generateNINPUTCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        if (treeNode == null) {
            return;
        }

        // todo
        //  add support for reading into array elements
        if (treeNode.getNodeType() == TreeNodeType.NSIMV) {
            codeGenerator.generateNSIVMCode(treeNode);

            SymbolDataType dt = treeNode.getSymbolRecord().getSymbolDataType();

            if (dt == SymbolDataType.Integer) {
                // READI  - read integer
                codeGenerator.addToOpCodes("61");
            } else if (dt == SymbolDataType.Real) {
                // READF  - read float
                codeGenerator.addToOpCodes("60");
            }

            codeGenerator.addToOpCodes("43");
            return;
        }

        // getting hee implies we are in a NVLIST
        generateNINPUTCode(treeNode.getLeft(), codeGenerator);
        generateNINPUTCode(treeNode.getRight(), codeGenerator);
    }
}
