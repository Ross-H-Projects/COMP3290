
package rossH.CD19.codegen;

import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

        // todo
        //  generate integer constants section AND fix instruction section
        //  to refer to integer constants section

        //  generate float constants section AND fix instruction section
        //  to refer to float constants section
        generateFloatConsants();

        // todo
        //  generate string constants section AND fix instruction section
        //  to refer to string constants section

        String instructionSection = convertOpCodesListToModFile(opCodes);
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

    public void addToFloatConstants (String f, int opCodePosForConstant) {

        int newFloatConstantPos = 0;
        if (!floatConstants.contains(f)) {
            floatConstants.add(f);
            newFloatConstantPos = floatConstants.size() - 1;
        } else {
            newFloatConstantPos = floatConstants.indexOf(f);
        }

        floatConstantToInstructionMapping.put(opCodePosForConstant, newFloatConstantPos);
    }

    public String generateFloatConsants () {
        int sizeOfFloatConstants = floatConstants.size();
    }

    public String convertOpCodesListToModFile (List<String> opCodes) {
        String modFileContents = "";
        int modFileLength = (int) Math.ceil( ((double) opCodes.size()) / 8.0);

        modFileContents += modFileLength + "\n  ";

        int opsThisLine = 0;
        for (int i = 0; i < opCodes.size(); i++) {
            opsThisLine++;
            modFileContents += convertToByteRepresentation(opCodes.get(i)) + "  ";
            if (((i + 1) % 8 == 0) && (i != opCodes.size() - 1)) {
                opsThisLine = 0;
                modFileContents += "\n  ";
            }
        }

        // figure out if we need to pad the last line
        if ((opsThisLine != 0) &&(opsThisLine != 8)) {
            for (int i = 0; i < (8 - opsThisLine); i++) {
                modFileContents += "00  ";
            }
        }

        return modFileContents;
    }

    public String convertToByteRepresentation (String o) {
        if (o.length() == 1) {
            o = "0" + o;
        }

        return o;
    }
}