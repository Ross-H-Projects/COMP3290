package rossH.CD19.codegen;

import com.sun.source.tree.Tree;
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
        } else if (treeNode.getNodeType() == TreeNodeType.NMNEQ) {
            generateNMNEQCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NSTEQ) {
            generateNSTEQCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NDVEQ) {
            generateNDVEQCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NPRLN) {
            generateNPRLNCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NPRINT) {
            generateNPRINTCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NINPUT) {
            generateNINPUTCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NIFTE) {
            generateNIFTECode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NIFTH) {
            generateNIFTHCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NREPT) {
            generateNREPTCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NFOR) {
            generateNPFORCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NCALL) {
            generateNCALLCode(treeNode, codeGenerator);
        } else if (treeNode.getNodeType() == TreeNodeType.NRETN) {
            generateNRETNCode(treeNode, codeGenerator);
        }

    }

    public static void generateNASGNCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        String convertTopOfStackToOpCode = "";


        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) {
            codeGenerator.generateNSIVMCode(treeNode.getLeft());
            convertTopOfStackToOpCode = codeGenerator.getConvertOpCodeForSymbolDataType(treeNode.getLeft().getSymbolRecordDataType());
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NARRV) {
            codeGenerator.generateNARRVCode(treeNode.getLeft());
            convertTopOfStackToOpCode = codeGenerator.getConvertOpCodeForSymbolDataType(treeNode.getLeft().getRight().getSymbolRecordDataType());
        }

        BooleanGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // do an appopriate data conversion for the variable we are assigning to
        codeGenerator.addToOpCodes(convertTopOfStackToOpCode);

        // do a store at the end
        codeGenerator.addToOpCodes("43");
    }

    public static void generateNPLEQCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        String convertTopOfStackToOpCode = "";

        // load address we are storing result into
        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) { // simple var
            codeGenerator.generateNSIVMCode(treeNode.getLeft());
            convertTopOfStackToOpCode = codeGenerator.getConvertOpCodeForSymbolDataType(treeNode.getLeft().getSymbolRecordDataType());
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NARRV) { // array index
            codeGenerator.generateNARRVCode(treeNode.getLeft());
            convertTopOfStackToOpCode = codeGenerator.getConvertOpCodeForSymbolDataType(treeNode.getLeft().getRight().getSymbolRecordDataType());
        }


        // load value of the address we want to add to
        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) { // simple var
            codeGenerator.generateLoadVariableCode(treeNode.getLeft());
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NARRV) { // array index
            codeGenerator.generateLoadArrayElementCode(treeNode.getLeft());
        }


        BooleanGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // do an add
        codeGenerator.addToOpCodes("11");

        // do an appopriate data conversion for the variable we are assigning to
        codeGenerator.addToOpCodes(convertTopOfStackToOpCode);

        // do a store at the end
        codeGenerator.addToOpCodes("43");
    }

    public static void generateNMNEQCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        String convertTopOfStackToOpCode = "";

        // load address we are storing result into
        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) { // simple var
            codeGenerator.generateNSIVMCode(treeNode.getLeft());
            convertTopOfStackToOpCode = codeGenerator.getConvertOpCodeForSymbolDataType(treeNode.getLeft().getSymbolRecordDataType());
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NARRV) { // array index
            codeGenerator.generateNARRVCode(treeNode.getLeft());
            convertTopOfStackToOpCode = codeGenerator.getConvertOpCodeForSymbolDataType(treeNode.getLeft().getRight().getSymbolRecordDataType());
        }


        // load value of the address we want to add to
        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) { // simple var
            codeGenerator.generateLoadVariableCode(treeNode.getLeft());
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NARRV) { // array index
            codeGenerator.generateLoadArrayElementCode(treeNode.getLeft());
        }


        BooleanGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // do a sub
        codeGenerator.addToOpCodes("12");

        // do an appopriate data conversion for the variable we are assigning to
        codeGenerator.addToOpCodes(convertTopOfStackToOpCode);

        // do a store at the end
        codeGenerator.addToOpCodes("43");
    }

    public static void generateNSTEQCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        String convertTopOfStackToOpCode = "";

        // load address we are storing result into
        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) { // simple var
            codeGenerator.generateNSIVMCode(treeNode.getLeft());
            convertTopOfStackToOpCode = codeGenerator.getConvertOpCodeForSymbolDataType(treeNode.getLeft().getSymbolRecordDataType());
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NARRV) { // array index
            codeGenerator.generateNARRVCode(treeNode.getLeft());
            convertTopOfStackToOpCode = codeGenerator.getConvertOpCodeForSymbolDataType(treeNode.getLeft().getRight().getSymbolRecordDataType());
        }


        // load value of the address we want to add to
        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) { // simple var
            codeGenerator.generateLoadVariableCode(treeNode.getLeft());
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NARRV) { // array index
            codeGenerator.generateLoadArrayElementCode(treeNode.getLeft());
        }


        BooleanGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // do a mul
        codeGenerator.addToOpCodes("13");

        // do an appopriate data conversion for the variable we are assigning to
        codeGenerator.addToOpCodes(convertTopOfStackToOpCode);

        // do a store at the end
        codeGenerator.addToOpCodes("43");
    }

    public static void generateNDVEQCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        String convertTopOfStackToOpCode = "";

        // load address we are storing result into
        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) { // simple var
            codeGenerator.generateNSIVMCode(treeNode.getLeft());
            convertTopOfStackToOpCode = codeGenerator.getConvertOpCodeForSymbolDataType(treeNode.getLeft().getSymbolRecordDataType());
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NARRV) { // array index
            codeGenerator.generateNARRVCode(treeNode.getLeft());
            convertTopOfStackToOpCode = codeGenerator.getConvertOpCodeForSymbolDataType(treeNode.getLeft().getRight().getSymbolRecordDataType());
        }


        // load value of the address we want to add to
        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) { // simple var
            codeGenerator.generateLoadVariableCode(treeNode.getLeft());
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NARRV) { // array index
            codeGenerator.generateLoadArrayElementCode(treeNode.getLeft());
        }


        BooleanGenerator.generateCode(treeNode.getRight(), codeGenerator);

        // do a div
        codeGenerator.addToOpCodes("14");

        // do an appopriate data conversion for the variable we are assigning to
        codeGenerator.addToOpCodes(convertTopOfStackToOpCode);

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
        if (treeNode == null) {
            return;
        }

        if (treeNode.getNodeType() == TreeNodeType.NPRLST) {
            generateNPRLSTCode(treeNode.getLeft(), codeGenerator);
            generateNPRLSTCode(treeNode.getRight(), codeGenerator);
            return;
        }

        // todo
        //  support printing strings

        // implies we have a var, expression, or string to print
        ExpressionGenerator.generateCode(treeNode, codeGenerator);

        // valpr
        codeGenerator.addToOpCodes("62");
    }

    public static void generateNINPUTCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        if (treeNode == null) {
            return;
        }


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
        } else if (treeNode.getNodeType() == TreeNodeType.NARRV) {
            codeGenerator.generateNARRVCode(treeNode);

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

    public static void generateNIFTHCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {

        codeGenerator.addToOpCodes("90");
        int fillWithInstructionToJumpProgramCounter = codeGenerator.getAmountOfOpCodes();
        // we will fill this with the proper address to jump to after we have
        // added op codes for evaluating the bool and the instruction area
        // if the bool evaluates to be true;
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");

        BooleanGenerator.generateCode(treeNode.getLeft(), codeGenerator);

        // BF
        codeGenerator.addToOpCodes("36");

        // generate statement op codes to execute if bool evaluates to true
        generateCode(treeNode.getRight(), codeGenerator);
        int instructionAddressToJumpTo = codeGenerator.getAmountOfOpCodes();
        String[] instructionAddressToJumpToByteRep = codeGenerator.convertAddressToByteRep(instructionAddressToJumpTo);

        codeGenerator.setOpCodes(fillWithInstructionToJumpProgramCounter, instructionAddressToJumpToByteRep[0]);
        codeGenerator.setOpCodes(fillWithInstructionToJumpProgramCounter + 1, instructionAddressToJumpToByteRep[1]);
        codeGenerator.setOpCodes(fillWithInstructionToJumpProgramCounter + 2, instructionAddressToJumpToByteRep[2]);
        codeGenerator.setOpCodes(fillWithInstructionToJumpProgramCounter + 3, instructionAddressToJumpToByteRep[3]);
    }

    public static void generateNIFTECode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        codeGenerator.addToOpCodes("90");
        int fillWithInstructionToJumpToForElseProgramCounter = codeGenerator.getAmountOfOpCodes();
        // we will fill this with the proper address to jump to after we have
        // added op codes for evaluating the bool and the instruction area
        // if the bool evaluates to be true;
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");

        BooleanGenerator.generateCode(treeNode.getLeft(), codeGenerator);

        // BF
        codeGenerator.addToOpCodes("36");

        // generate statement op codes to execute if bool evaluates to true
        generateCode(treeNode.getMiddle(), codeGenerator);

        codeGenerator.addToOpCodes("90");
        int fillWithInstructionToJumpToForEndProgramCounter = codeGenerator.getAmountOfOpCodes();
        // we will fill this with the proper address to jump to after we have
        // added the statement op codes for if and else sections
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");

        // br
        codeGenerator.addToOpCodes("37");

        int instructionAddressToJumpToForElse = codeGenerator.getAmountOfOpCodes();
        String[] instructionAddressToJumpToForElseByteRep = codeGenerator.convertAddressToByteRep(instructionAddressToJumpToForElse);

        codeGenerator.setOpCodes(fillWithInstructionToJumpToForElseProgramCounter, instructionAddressToJumpToForElseByteRep[0]);
        codeGenerator.setOpCodes(fillWithInstructionToJumpToForElseProgramCounter + 1, instructionAddressToJumpToForElseByteRep[1]);
        codeGenerator.setOpCodes(fillWithInstructionToJumpToForElseProgramCounter + 2, instructionAddressToJumpToForElseByteRep[2]);
        codeGenerator.setOpCodes(fillWithInstructionToJumpToForElseProgramCounter+ 3, instructionAddressToJumpToForElseByteRep[3]);

        // generate statement op codes to executeif bool evaluates to false
        generateCode(treeNode.getRight(), codeGenerator);

        int instructionAddressToJumpToForEnd = codeGenerator.getAmountOfOpCodes();
        String[] instructionAddressToJumpToForEndByteRep = codeGenerator.convertAddressToByteRep(instructionAddressToJumpToForEnd);

        codeGenerator.setOpCodes(fillWithInstructionToJumpToForEndProgramCounter, instructionAddressToJumpToForEndByteRep[0]);
        codeGenerator.setOpCodes(fillWithInstructionToJumpToForEndProgramCounter + 1, instructionAddressToJumpToForEndByteRep[1]);
        codeGenerator.setOpCodes(fillWithInstructionToJumpToForEndProgramCounter + 2, instructionAddressToJumpToForEndByteRep[2]);
        codeGenerator.setOpCodes(fillWithInstructionToJumpToForEndProgramCounter + 3, instructionAddressToJumpToForEndByteRep[3]);
    }

    public static void generateNREPTCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // create op codes for asgnlist
        generateAsgnListCode(treeNode.getLeft(), codeGenerator);

        int opCodeToJumpToIfBoolFalse = codeGenerator.getAmountOfOpCodes();

        // generate op codes for statements within repeat
        generateCode(treeNode.getMiddle(), codeGenerator);

        // generate load address op codes that we will jump the program counter to
        // if boolean evaluates false
        String[] opCodeToJumpToIfBoolFalseByteRep = codeGenerator.convertAddressToByteRep(opCodeToJumpToIfBoolFalse);
        codeGenerator.addToOpCodes("90");
        codeGenerator.addToOpCodes(opCodeToJumpToIfBoolFalseByteRep[0]);
        codeGenerator.addToOpCodes(opCodeToJumpToIfBoolFalseByteRep[1]);
        codeGenerator.addToOpCodes(opCodeToJumpToIfBoolFalseByteRep[2]);
        codeGenerator.addToOpCodes(opCodeToJumpToIfBoolFalseByteRep[3]);

        // generate op codes to evaluate boolean

        BooleanGenerator.generateCode(treeNode.getRight(), codeGenerator);

        codeGenerator.addToOpCodes("36");
    }

    public static void generateAsgnListCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        if (treeNode == null) {
            return;
        }

        if (treeNode.getNodeType() == TreeNodeType.NASGN) {
            generateNASGNCode(treeNode, codeGenerator);
            return;
        } else if (treeNode.getNodeType() == TreeNodeType.NPLEQ) {
            generateNPLEQCode(treeNode, codeGenerator);
            return;
        } else if (treeNode.getNodeType() == TreeNodeType.NMNEQ) {
            generateNMNEQCode(treeNode, codeGenerator);
            return;
        } else if (treeNode.getNodeType() == TreeNodeType.NSTEQ) {
            generateNSTEQCode(treeNode, codeGenerator);
            return;
        } else if (treeNode.getNodeType() == TreeNodeType.NDVEQ) {
            generateNDVEQCode(treeNode, codeGenerator);
            return;
        }

        // getting here implies we are in NASGNS node
        generateAsgnListCode(treeNode.getLeft(), codeGenerator);
        generateAsgnListCode(treeNode.getRight(), codeGenerator);
    }

    public static void generateNPFORCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // create op codes for asgnlist
        generateAsgnListCode(treeNode.getLeft(), codeGenerator);

        int opCodeToJumpToForLoop = codeGenerator.getAmountOfOpCodes();


        codeGenerator.addToOpCodes("90");
        int fillWithOpCodeToJumpToIfBoolTruePos = codeGenerator.getAmountOfOpCodes();
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");

        // gneerate op codes for bool
        BooleanGenerator.generateCode(treeNode.getMiddle(), codeGenerator);

        // bf
        codeGenerator.addToOpCodes("36");

        // generate op codes for statements
        generateCode(treeNode.getRight(), codeGenerator);


        String[] opCodeToJumpToForLoopByteRep = codeGenerator.convertAddressToByteRep(opCodeToJumpToForLoop);
        codeGenerator.addToOpCodes("90");
        codeGenerator.addToOpCodes(opCodeToJumpToForLoopByteRep[0]);
        codeGenerator.addToOpCodes(opCodeToJumpToForLoopByteRep[1]);
        codeGenerator.addToOpCodes(opCodeToJumpToForLoopByteRep[2]);
        codeGenerator.addToOpCodes(opCodeToJumpToForLoopByteRep[3]);

        // br
        codeGenerator.addToOpCodes("37");

        int opCodeToJumpToIfBoolTruePos = codeGenerator.getAmountOfOpCodes();
        String[] opCodeToJumpToIfBoolTruePosByteRep = codeGenerator.convertAddressToByteRep(opCodeToJumpToIfBoolTruePos);

        codeGenerator.setOpCodes(fillWithOpCodeToJumpToIfBoolTruePos, opCodeToJumpToIfBoolTruePosByteRep[0]);
        codeGenerator.setOpCodes(fillWithOpCodeToJumpToIfBoolTruePos + 1, opCodeToJumpToIfBoolTruePosByteRep[1]);
        codeGenerator.setOpCodes(fillWithOpCodeToJumpToIfBoolTruePos + 2, opCodeToJumpToIfBoolTruePosByteRep[2]);
        codeGenerator.setOpCodes(fillWithOpCodeToJumpToIfBoolTruePos + 3, opCodeToJumpToIfBoolTruePosByteRep[3]);
    }

    public static void generateNCALLCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {

        // allocate a space for return type if needed
        if (treeNode.getLeft().getSymbolRecord().getSymbolDataType() != SymbolDataType.Void) {
            // LB
            codeGenerator.addToOpCodes("42");
            codeGenerator.addToOpCodes("00");
            codeGenerator.addToOpCodes("00");
        }

        // generate params
        int noOfParams = evaluateParams(treeNode.getRight(), codeGenerator, 0);
        String[] noOfParamsByteRep = codeGenerator.convertAddressToByteRep(noOfParams);

        // LH for no of params
        codeGenerator.addToOpCodes("42");
        codeGenerator.addToOpCodes(noOfParamsByteRep[2]);
        codeGenerator.addToOpCodes(noOfParamsByteRep[3]);

        // LA for function start pos

        codeGenerator.addToOpCodes("90");
        // we do not know the op code start pos for functions until after main body is
        // has op codes generated, so we will just save the position and the name of the function that is
        // needed to be called here
        int whereToFillWithFunctionAddress = codeGenerator.getAmountOfOpCodes();
        codeGenerator.addCallStatOpCodePosToFunctionNameMapping(whereToFillWithFunctionAddress, treeNode.getLeft().getSymbolRecord().getLexeme());

        // leave blank for now, will be resolved later after both main and function bodies are generated
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");
        codeGenerator.addToOpCodes("00");

        // JS2
        codeGenerator.addToOpCodes("72");
    }

    public static int evaluateParams (TreeNode treeNode, CD19CodeGenerator codeGenerator, int noOfParamsSoFar) {
        if (treeNode == null) {
            return noOfParamsSoFar;
        }


        if (treeNode.getNodeType() != TreeNodeType.NEXPL) {
            BooleanGenerator.generateCode(treeNode, codeGenerator);

            noOfParamsSoFar++;
            return noOfParamsSoFar;
        }

        noOfParamsSoFar = evaluateParams(treeNode.getLeft(), codeGenerator, noOfParamsSoFar);
        noOfParamsSoFar = evaluateParams(treeNode.getRight(), codeGenerator, noOfParamsSoFar);

        return noOfParamsSoFar;
    }

    public static void generateNRETNCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // implies we are returning void
        if (treeNode.getLeft() == null) {
            // RETN
            codeGenerator.addToOpCodes("71");
            return;
        }

        ExpressionGenerator.generateCode(treeNode.getLeft(), codeGenerator);
        codeGenerator.addToOpCodes("70");
        codeGenerator.addToOpCodes("71");
    }
}
