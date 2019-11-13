package rossH.CD19.codegen;

import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalsGenerator {



    public static void generateCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        if (treeNode == null) {
            return;
        }

        // constants
        generateConstantsSection(treeNode.getLeft(), codeGenerator);

        // types
        // types are not actually represented in the memory op codes
        // as directly as arrays or primitives are, since they are only
        // used for arrays, we compute all types, their fields and each fields offsets
        // so that during compilation we can resolve calls to array[index].field
        codeGenerator.createTypes(treeNode.getMiddle());

        // arrays
    }



    public static void generateConstantsSection (TreeNode treeNode,  CD19CodeGenerator codeGenerator) {
        if (treeNode == null) {
            return;
        }

        codeGenerator.addToOpCodes("42");
        int opCodeStartPosForConstants = codeGenerator.getAmountOfOpCodes();
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("52");
        int noOfConstants = generateConstants(treeNode, codeGenerator, 0);

        // we will replace the blank 00 00 with an actual count of the constants needed after we have generated and counted the
        // constants needed

        String[] noOFConstantsByteRep = codeGenerator.convertAdressToByteRep(noOfConstants);

        codeGenerator.setOpCodes(opCodeStartPosForConstants, noOFConstantsByteRep[2]);
        codeGenerator.setOpCodes(opCodeStartPosForConstants + 1, noOFConstantsByteRep[3]);
    }

    public static int generateConstants (TreeNode treeNode, CD19CodeGenerator codeGenerator, int noOfConstantsSoFar) {
        if (treeNode == null) {
            return noOfConstantsSoFar;
        }

        if (treeNode.getNodeType() == TreeNodeType.NINIT) {
            // load the address of the constant
            codeGenerator.generateNSIVMCode(treeNode);

            // instantiate the constant
            // todo
            //  we may need to change this to accomodate float and booleans ??
            codeGenerator.addToOpCodes("42");
            codeGenerator.addToOpCodes("00");
            codeGenerator.addToOpCodes("00");
            codeGenerator.addToOpCodes("43");

            // load the address of the constant again so that we can store the result of the expression to it
            codeGenerator.generateNSIVMCode(treeNode);

            // generate the code needed to assign a value to the constant
            ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);

            codeGenerator.addToOpCodes("43");

            noOfConstantsSoFar++;
            return noOfConstantsSoFar;
        }

        noOfConstantsSoFar = generateConstants(treeNode.getLeft(), codeGenerator, noOfConstantsSoFar);
        noOfConstantsSoFar = generateConstants(treeNode.getRight(), codeGenerator, noOfConstantsSoFar);

        return noOfConstantsSoFar;
    }

    public static void generateArraysSection (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        
    }


}