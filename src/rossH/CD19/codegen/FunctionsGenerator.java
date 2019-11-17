package rossH.CD19.codegen;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolDataType;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import java.beans.Expression;
import java.util.List;

public class FunctionsGenerator {
    public static void generateCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        if (treeNode == null) {
            return;
        }

        // code gen for single function
        if (treeNode.getNodeType() != TreeNodeType.NFUNCS) {
            generateFunctionCode(treeNode, codeGenerator);
            return;
        }

        // gen for left
        generateCode(treeNode.getLeft(), codeGenerator);

        // gen for right
        generateCode(treeNode.getRight(), codeGenerator);

    }

    public static void generateFunctionCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        if (treeNode == null) {
            return;
        }

        // take note of the amount of op codes for where this function body
        // is starting to be generated
        // add a mapping in CD19CodGenerator of function names to their op code pos
        int functionOpCodeStartingPos = codeGenerator.getAmountOfOpCodes();
        codeGenerator.addFunctionOpCodeStartingPosMapping(treeNode.getSymbolRecord().getLexeme(), functionOpCodeStartingPos);

        // todo
        //  CHECK IF we don't need to generate any op codes for the parameters because this is done
        //  when the function is called

        mapArrayParametersToArrayTypes(treeNode.getLeft(), codeGenerator);

        // allocate space on top of the call frame stack for the local variables
        // and initialize those variables
        generateFunctionDeclarations(treeNode.getMiddle(), codeGenerator);

        mapArrayDeclarationsToArrayTypes(treeNode.getMiddle(), codeGenerator);

        // generate code for the statements within the function
        StatementGenerator.generateCode(treeNode.getRight(), codeGenerator);
    }

    public static void generateFunctionDeclarations (TreeNode declarations, CD19CodeGenerator codeGenerator) {
        // todo
        //  support array declarations

        codeGenerator.addToOpCodes("42");
        int opCodeStartPosForDeclaration = codeGenerator.getAmountOfOpCodes();
        // we will replace the blank 00 00 with an actual count of the declaratiosn needed after we have generated and counted the
        // declarations needed
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("52");
        int noOfDeclarations = generateFunctionDeclarationsRecursive(declarations, 0, codeGenerator);

        String[] noOFDeclarationsByteRep = codeGenerator.convertAddressToByteRep(noOfDeclarations);

       codeGenerator.setOpCodes(opCodeStartPosForDeclaration, noOFDeclarationsByteRep[2]);
       codeGenerator.setOpCodes(opCodeStartPosForDeclaration + 1, noOFDeclarationsByteRep[3]);
    }

    public static int generateFunctionDeclarationsRecursive (TreeNode declarations, int noOfDeclarationsSofar, CD19CodeGenerator codeGenerator) {
        if (declarations == null) {
            return 0;
        }
        // declaratiosn will either be NSDLST or NSDECL or NARRD

        if (declarations.getNodeType() == TreeNodeType.NDLIST) { // NSDLST
            int noOfDeclarationsLeft = generateFunctionDeclarationsRecursive(declarations.getLeft(), noOfDeclarationsSofar, codeGenerator);
            int noOfDeclarationsRight = generateFunctionDeclarationsRecursive(declarations.getRight(), noOfDeclarationsSofar, codeGenerator);
            return noOfDeclarationsSofar + noOfDeclarationsLeft + noOfDeclarationsRight;
        }

        if (declarations.getNodeType() == TreeNodeType.NSDECL) { //

            String declarationBaseRegister = "" + declarations.getSymbolRecord().getBaseRegister();

            // load address
            codeGenerator.addToOpCodes(declarationBaseRegister);

            int declarationOffSet = declarations.getSymbolRecord().getOffset();
            String[] declarationOffSetByteRep = codeGenerator.convertAddressToByteRep(declarationOffSet);

            codeGenerator.addToOpCodes(declarationOffSetByteRep[0]);
            codeGenerator.addToOpCodes(declarationOffSetByteRep[1]);
            codeGenerator.addToOpCodes(declarationOffSetByteRep[2]);
            codeGenerator.addToOpCodes(declarationOffSetByteRep[3]);

            // initializes the variable to 0
            codeGenerator.addToOpCodes("42");
            codeGenerator.addToOpCodes("00");
            codeGenerator.addToOpCodes("00");
            // store
            codeGenerator.addToOpCodes("43");
        } else if (declarations.getNodeType() == TreeNodeType.NARRD) {
            GlobalsGenerator.generateArraysSection(declarations, codeGenerator);
            return 0;
        }

        return 1;
    }

    public static void mapArrayParametersToArrayTypes (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        if (treeNode == null) {
            return;
        }

        if (treeNode.getNodeType() != TreeNodeType.NPLIST) {
            if (treeNode.getNodeType() == TreeNodeType.NARRP) {
                String arrayParameterName = treeNode.getLeft().getSymbolRecord().getLexeme();
                String arrayTypeParameterName = treeNode.getLeft().getLeft().getSymbolRecord().getLexeme();

                codeGenerator.arrayToArrayTypeMappings.put(arrayParameterName, arrayTypeParameterName);
            }
            return;
        }

        mapArrayParametersToArrayTypes(treeNode.getLeft(), codeGenerator);
        mapArrayParametersToArrayTypes(treeNode.getRight(), codeGenerator);
    }

    public static void mapArrayDeclarationsToArrayTypes (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        if (treeNode == null) {
            return;
        }

        if (treeNode.getNodeType() != TreeNodeType.NDLIST) {
            if (treeNode.getNodeType() == TreeNodeType.NARRD) {
                String arrayDeclarationrName = treeNode.getSymbolRecord().getLexeme();
                String arrayTypeParameterName = treeNode.getLeft().getSymbolRecord().getLexeme();

                codeGenerator.arrayToArrayTypeMappings.put(arrayDeclarationrName, arrayTypeParameterName);
            }
            return;
        }

        mapArrayDeclarationsToArrayTypes(treeNode.getLeft(), codeGenerator);
        mapArrayDeclarationsToArrayTypes(treeNode.getRight(), codeGenerator);
    }
}