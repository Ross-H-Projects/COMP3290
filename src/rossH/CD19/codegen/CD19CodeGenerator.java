
package rossH.CD19.codegen;

import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CD19CodeGenerator {
    LinkedList<String> opCodes;


    LinkedList<String> integerConstants;
    LinkedList<String> floatConstants;
    LinkedList<String> stringConstants;

    HashMap<Integer, Integer> integerConstantToInstructionMapping;
    HashMap<Integer, Integer> floatConstantToInstructionMapping;
    HashMap<Integer, Integer> stringConstantToInstructionMapping;

    public CD19CodeGenerator () {
        opCodes = new LinkedList<String>();

        integerConstants = new LinkedList<String>();
        floatConstants = new LinkedList<String>();
        stringConstants = new LinkedList<String>();

        integerConstantToInstructionMapping = new HashMap<Integer, Integer>();
        floatConstantToInstructionMapping = new HashMap<Integer, Integer>();
        stringConstantToInstructionMapping = new HashMap<Integer, Integer>();
    }

    public String generateCode (TreeNode programNode) {

        TreeNode mainbodyNode = programNode.getRight();

        // we need to generate code for declarations before main body
        TreeNode slistNode = mainbodyNode.getLeft();
        generateDeclarations(slistNode);

        // we need to generate code for statements in main body
        TreeNode statsNode = mainbodyNode.getRight();
        generateMainBodyStatements(statsNode);


        //  generate integer constants section AND fix instruction section
        //  to refer to integer constants section
        resolveIntegerConstants();

        //  generate float constants section AND fix instruction section
        //  to refer to float constants section
        resolveFloatConsants();

        // todo
        //  generate string constants section AND fix instruction section
        //  to refer to string constants section

        String instructionSection = convertOpCodesListToModFile(opCodes);
        return instructionSection;
    }

    public void generateDeclarations (TreeNode declarations) {
        // curently only supporting:
        // integer declarations

        String noOfDeclarations = "" + generateDeclarationsRecursive(declarations, opCodes, 0);

        // need to add allocation of stack memory backwards
        // 42  00  03  52
        opCodes.addFirst("52");
        opCodes.addFirst(noOfDeclarations);
        // todo
        //  add support for larger amount of declarations
        //  instead of just 00 here
        opCodes.addFirst("00");
        opCodes.addFirst("42");
    }

    public int generateDeclarationsRecursive (TreeNode declarations, List<String> opCodes, int noOfDeclarationsSofar) {
        if (declarations == null) {
            return 0;
        }
        // declaratiosn will either be NSDLST or NSDECL

        if (declarations.getNodeType() == TreeNodeType.NSDLST) { // NSDLST
            int noOfDeclarationsLeft = generateDeclarationsRecursive(declarations.getLeft(), opCodes, noOfDeclarationsSofar);
            int noOfDeclarationsRight = generateDeclarationsRecursive(declarations.getRight(), opCodes, noOfDeclarationsSofar);
            return noOfDeclarationsSofar + noOfDeclarationsLeft + noOfDeclarationsRight;
        } else { // NSDECL
            // example declaration initialization
            // where the declaration lies in base register 1
            // and the offset here is 16 :
            // "91  00  00  00 16  42  00  00 43"
            String declarationBaseRegister = "" + declarations.getSymbolRecord().getBaseRegister();
            String declarationOffSet = "" + declarations.getSymbolRecord().getOffset();
            // load address
            opCodes.add(declarationBaseRegister);
            // todo
            //  convert large offsets to  bytes properly
            opCodes.add("00");
            opCodes.add("00");
            opCodes.add("00");
            opCodes.add(declarationOffSet);
            // initializes the variable to 0
            opCodes.add("42");
            opCodes.add("00");
            opCodes.add("00");
            // store
            opCodes.add("43");
            return 1;
        }
    }

    public void generateMainBodyStatements (TreeNode statements) {
        // currently only supporting:
        // a = x
        // x -> int literal | variables | addition of variabls / literals
        // ie currently only supports nstats, nasgn, nsimv, nilit,
        StatementGenerator.generateCode(statements, this);

        // figure out if we need to pad the instruction op codes
        if (opCodes.size() % 8 != 0) {
            int amountToPadBy = 8 - (opCodes.size() % 8);
            for (int i = 0; i < amountToPadBy; i++) {
                opCodes.add("00");
            }
        }
    }

    public void generateNSIVMCode (TreeNode treeNode) {
        // load address
        // specific to base address
        String baseAddressLoadInstruction = "" + treeNode.getSymbolRecord().getBaseRegister();
        String offset = "" + treeNode.getSymbolRecord().getOffset();
        opCodes.add(baseAddressLoadInstruction);
        // specify offset
        // todo
        //  eventually support larger address size
        opCodes.add("00");
        opCodes.add("00");
        opCodes.add("00");
        opCodes.add(offset);
    }

    public void generateLoadVariableCode (TreeNode treeNode) {
        String baseAddressValueInstruction = "" + treeNode.getSymbolRecord().getBaseRegisterForValue();
        String offset = "" +  treeNode.getSymbolRecord().getOffset();
        opCodes.add(baseAddressValueInstruction);
        // specify offset
        // todo
        //  eventually support larger address size
        opCodes.add("00");
        opCodes.add("00");
        opCodes.add("00");
        opCodes.add(offset);
    }

    public void addToOpCodes(String opCode) {
        opCodes.add(opCode);
    }

    public int getAmountOfOpCodes () {
        return opCodes.size();
    }

    public void addToIntegerConstants (String i, int opCodePosForInteger) {

        int newIntegerConstantPos = 0;
        if (!integerConstants.contains(i)) {
            integerConstants.add(i);
            newIntegerConstantPos = integerConstants.size();
        } else {
            newIntegerConstantPos = integerConstants.indexOf(i) + 1;
        }

        integerConstantToInstructionMapping.put(opCodePosForInteger, newIntegerConstantPos);
    }

    public void addToFloatConstants (String f, int opCodePosForConstant) {

        int newFloatConstantPos = 0;
        if (!floatConstants.contains(f)) {
            floatConstants.add(f);
            newFloatConstantPos = floatConstants.size();
        } else {
            newFloatConstantPos = floatConstants.indexOf(f) + 1;
        }

        floatConstantToInstructionMapping.put(opCodePosForConstant, newFloatConstantPos);
    }

    public void resolveIntegerConstants () {
        // by this time all instruction op codes have been added, i.e. we know the size of
        // the instruction op codes
        int integerConstantsStartingPos = opCodes.size();

        // instruction op code pos  ->   relative pos in integer constants
        for (Map.Entry<Integer, Integer> entry : integerConstantToInstructionMapping.entrySet()) {
            int instructionOpCodePos = entry.getKey();
            int relativeIntegerConstantPos = entry.getValue();

            int actualIntegerConstantPos = (relativeIntegerConstantPos - 1) * 8 + integerConstantsStartingPos;
            String intCosPosByteRep[] = convertAdressToByteRep(actualIntegerConstantPos);

            // resolve the instruction space address to use the integer constant
            opCodes.set(instructionOpCodePos + 1, intCosPosByteRep[0]);
            opCodes.set(instructionOpCodePos + 2, intCosPosByteRep[1]);
            opCodes.set(instructionOpCodePos + 3, intCosPosByteRep[2]);
            opCodes.set(instructionOpCodePos + 4, intCosPosByteRep[3]);
        }
    }

    public void resolveFloatConsants () {
        // by this time all instruction op codes have been added AND
        // we know how many integer literals will be needed
        // i.e. we know the starting pos for our float constants
        int integerConstantsStartingPos = opCodes.size();

        // instruction op code pos  ->   relative pos in integer constants
        for (Map.Entry<Integer, Integer> entry : integerConstantToInstructionMapping.entrySet()) {
            int instructionOpCodePos = entry.getKey();
            int relativeIntegerConstantPos = entry.getValue();

            int actualIntegerConstantPos = (relativeIntegerConstantPos - 1) * 8 + integerConstantsStartingPos;
            String intCosPosByteRep[] = convertAdressToByteRep(actualIntegerConstantPos);

            // resolve the instruction space address to use the integer constant
            opCodes.set(instructionOpCodePos + 1, intCosPosByteRep[0]);
            opCodes.set(instructionOpCodePos + 2, intCosPosByteRep[1]);
            opCodes.set(instructionOpCodePos + 3, intCosPosByteRep[2]);
            opCodes.set(instructionOpCodePos + 4, intCosPosByteRep[3]);
        }
    }

    public String convertOpCodesListToModFile (List<String> opCodes) {
        String modFileContents = "";

        // op code section
        int modFileLength = (int) Math.ceil( ((double) opCodes.size()) / 8.0);

        modFileContents += modFileLength + "\n  ";

        for (int i = 0; i < opCodes.size(); i++) {
            modFileContents += convertToByteRepresentation(opCodes.get(i)) + "  ";
            if (((i + 1) % 8 == 0) && (i != opCodes.size() - 1)) {
                modFileContents += "\n  ";
            }
        }

        // integer constants section
        modFileContents += "\n";
        modFileContents += integerConstants.size() + "\n";
        for (int i = 0; i < integerConstants.size(); i++) {
            modFileContents += "  " + integerConstants.get(i) + "\n";
        }

        // float constants section
        modFileContents += "0\n";
        // todo

        // string constants section
        modFileContents += "0\n";
        // todo

        return modFileContents;
    }

    public String convertToByteRepresentation (String o) {
        if (o.length() == 1) {
            o = "0" + o;
        }

        return o;
    }

    public String[] convertAdressToByteRep (int actualIntegerConstantPos) {
        String[] byteRep = new String[4];

        // we can cheat a little bit since we know we are only working with 64 Kbits of memory
        byteRep[0] = "00";
        byteRep[1] = "00";

        if (actualIntegerConstantPos <= 255) {
            byteRep[2] = "00";
            byteRep[3] = "" + actualIntegerConstantPos;
            return byteRep;
        }

        // the number to represent is >= 256

        // get the lower byte first as we need it to calculate the hiher byte
        int lowerByte = actualIntegerConstantPos % 256;

        // calculate the higher byte
        int higherByte = (actualIntegerConstantPos - lowerByte) / 256;

        byteRep[2] = "" + higherByte;
        byteRep[3] = "" + lowerByte;
        return byteRep;
    }
}