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
        // todo
        //  currently only supports printing of on variable, not a list of variables / literals
        //  need to support more like actual expressions and string


        // todo
        //  support string printing

        // left
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


}
