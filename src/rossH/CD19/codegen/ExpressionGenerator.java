package rossH.CD19.codegen;

import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import java.util.List;

public class ExpressionGenerator {

    public static void generateCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {

        if (treeNode.getNodeType() == TreeNodeType.NILIT) {
            generateNILITCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NFLIT) {
            generateNFLITCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NSIMV) {
            codeGenerator.generateLoadVariableCode(treeNode);
        } else if (treeNode.getNodeType() == TreeNodeType.NADD) {
            generateNADDCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NSUB) {
            generateSUBCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NMUL) {
            generateNMULCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NDIV) {
            generateNDIVCode(treeNode, codeGenerator);
        }

    }


    public static void generateNILITCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        String integerLexeme = treeNode.getSymbolRecord().getLexeme();

        codeGenerator.addToOpCodes("80");
        int opCodePosForConstantLoad = codeGenerator.getAmountOfOpCodes() - 1;

        // just add nothing for now, we will replace this later when we generate constants section
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");

        codeGenerator.addToIntegerConstants(integerLexeme, opCodePosForConstantLoad);
    }


    public static void generateNFLITCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        String floatLexeme = treeNode.getSymbolRecord().getLexeme();

        codeGenerator.addToOpCodes("80");
        int opCodePosForConstantLoad = codeGenerator.getAmountOfOpCodes() - 1;

        // just add nothing for now, we will replace this later when we generate constants section
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");


        codeGenerator.addToFloatConstants(floatLexeme, opCodePosForConstantLoad);
    }


    public static void generateNADDCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {

        // gen for left
        generateCode(treeNode.getLeft(), codeGenerator);

        // gen for right
        generateCode(treeNode.getRight(), codeGenerator);

        // do an add op code at the end
        codeGenerator.addToOpCodes("11");
    }

    public static void generateSUBCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // gen for left
        generateCode(treeNode.getLeft(), codeGenerator);

        // gen for right
        generateCode(treeNode.getRight(), codeGenerator);

        // do a subtraction op code at the end
        codeGenerator.addToOpCodes("12");
    }

    public static void generateNMULCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // gen for left
        generateCode(treeNode.getLeft(), codeGenerator);

        // gen for right
        generateCode(treeNode.getRight(), codeGenerator);

        // do a multiplication op code at the end
        codeGenerator.addToOpCodes("13");
    }

    public static void generateNDIVCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // gen for left
        generateCode(treeNode.getLeft(), codeGenerator);

        // gen for right
        generateCode(treeNode.getRight(), codeGenerator);

        // do a division op code at the end
        codeGenerator.addToOpCodes("14");
    }

    public static void generateNMODCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // gen for left
        generateCode(treeNode.getLeft(), codeGenerator);

        // gen for right
        generateCode(treeNode.getRight(), codeGenerator);

        // do a modulo op code at the end
        codeGenerator.addToOpCodes("15");
    }

}
