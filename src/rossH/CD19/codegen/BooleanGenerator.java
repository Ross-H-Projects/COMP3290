package rossH.CD19.codegen;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolDataType;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import java.beans.Expression;
import java.util.List;

public class BooleanGenerator {
    public static void generateCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {


        if (treeNode.getNodeType() == TreeNodeType.NNOT) {

        } else if (treeNode.getNodeType() == TreeNodeType.NAND) {

        } else if (treeNode.getNodeType() == TreeNodeType.NOR) {

        } else if (treeNode.getNodeType() == TreeNodeType.NXOR) {

        } else if (treeNode.getNodeType() == TreeNodeType.NEQL) {

        } else if (treeNode.getNodeType() == TreeNodeType.NNEQ) {

        } else if (treeNode.getNodeType() == TreeNodeType.NGRT) {
            generateNGRTCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NLEQ) {

        } else if (treeNode.getNodeType() == TreeNodeType.NLSS) {

        } else if (treeNode.getNodeType() == TreeNodeType.NGEQ) {

        } else  {
            ExpressionGenerator.generateCode(treeNode, codeGenerator);
        }
    }

    public static void generateNGRTCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);
        ExpressionGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // sub
        codeGenerator.addToOpCodes("12");
        // gt
        codeGenerator.addToOpCodes("21");
    }
}
