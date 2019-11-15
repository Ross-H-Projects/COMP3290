package rossH.CD19.codegen;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolDataType;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import java.beans.Expression;
import java.util.List;

public class BooleanGenerator {
    public static void generateCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {


        if (treeNode.getNodeType() == TreeNodeType.NBOOL) {
            generateNBOOLcode(treeNode, codeGenerator);
        }  else if (treeNode.getNodeType() == TreeNodeType.NNOT) {
            generateNNOTCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NEQL) {
            generateNEQLCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NNEQ) {
            generateNNEQCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NGRT) {
            generateNGRTCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NLEQ) {
            generateNLEQCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NLSS) {
            generateNLSSCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NGEQ) {
            generateNGEQCode(treeNode, codeGenerator);
        } else  {
            ExpressionGenerator.generateCode(treeNode, codeGenerator);
        }
    }

    public static void generateNBOOLcode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // NBOOL : <bool> -> <bool> <logop> <rel>

        // <bool>
        generateCode(treeNode.getLeft(), codeGenerator);

        // <logop> <rel>
        generateLogOpCode(treeNode.getRight(), codeGenerator);

    }

    public static void generateLogOpCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
         if (treeNode.getNodeType() == TreeNodeType.NAND) {
            generateNANDCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NOR) {
            generateNORCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NXOR) {
            generateNXORCode(treeNode, codeGenerator);
        }
    }

    public static  void generateNNOTCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        generateCode(treeNode.getMiddle(), codeGenerator);

        // NOT
        codeGenerator.addToOpCodes("34");
    }

    public static void generateNANDCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        generateCode(treeNode.getLeft(), codeGenerator);

        if (treeNode.getRight() != null) {
            generateLogOpCode(treeNode.getRight(), codeGenerator);
        }

        // and
        codeGenerator.addToOpCodes("31");
    }

    public static void generateNORCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        generateCode(treeNode.getLeft(), codeGenerator);

        if (treeNode.getRight() != null) {
            generateLogOpCode(treeNode.getRight(), codeGenerator);
        }

        // or
        codeGenerator.addToOpCodes("32");
    }

    public static void generateNXORCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        generateCode(treeNode.getLeft(), codeGenerator);

        if (treeNode.getRight() != null) {
            generateLogOpCode(treeNode.getRight(), codeGenerator);
        }

        // xor
        codeGenerator.addToOpCodes("33");
    }

    public static void generateNEQLCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);
        ExpressionGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // sub
        codeGenerator.addToOpCodes("12");
        // eq
        codeGenerator.addToOpCodes("25");
    }

    public static void generateNNEQCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);
        ExpressionGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // sub
        codeGenerator.addToOpCodes("12");
        // eq
        codeGenerator.addToOpCodes("26");
    }


    public static void generateNGRTCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);
        ExpressionGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // sub
        codeGenerator.addToOpCodes("12");
        // gt
        codeGenerator.addToOpCodes("21");
    }

    public static void generateNGEQCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);
        ExpressionGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // sub
        codeGenerator.addToOpCodes("12");
        // ge
        codeGenerator.addToOpCodes("22");
    }

    public static void generateNLEQCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);
        ExpressionGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // sub
        codeGenerator.addToOpCodes("12");
        // le
        codeGenerator.addToOpCodes("24");
    }

    public static void generateNLSSCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);
        ExpressionGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // sub
        codeGenerator.addToOpCodes("12");
        // lt
        codeGenerator.addToOpCodes("23");
    }
}


