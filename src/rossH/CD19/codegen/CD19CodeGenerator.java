
package rossH.CD19.codegen;

import rossH.CD19.Parser.SymbolTable.SymbolDataType;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import javax.lang.model.type.ArrayType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CD19CodeGenerator {

    public class TypeField {
        public String name;
        public int offSet;
        public SymbolDataType dataType;

        TypeField (String s, int o, SymbolDataType d) {
            this.name = s;
            this.offSet = o;
            this.dataType = d;
        }

        @Override
        public int hashCode () {
            return name.hashCode();
        }
    }

    public class ArrayType {
        public String name;
        public TreeNode expr;
        public String typeId;

        ArrayType (String s, TreeNode e, String t) {
            this.name = s;
            this.expr = e;
            this.typeId = t;
        }

        @Override
        public int hashCode () {
            return name.hashCode();
        }
    }


    LinkedList<String> opCodes;


    LinkedList<String> integerConstants;
    LinkedList<String> floatConstants;
    LinkedList<String> stringConstants;

    HashMap<Integer, Integer> integerConstantToInstructionMapping;
    HashMap<Integer, Integer> floatConstantToInstructionMapping;
    HashMap<Integer, Integer> stringConstantToInstructionMapping;

    HashMap<String, HashMap<String, TypeField> > types;
    HashMap<String, ArrayType> arrays;
    HashMap<String, String> arrayToArrayTypeMappings;


    public CD19CodeGenerator () {
        opCodes = new LinkedList<String>();

        integerConstants = new LinkedList<String>();
        floatConstants = new LinkedList<String>();
        stringConstants = new LinkedList<String>();

        integerConstantToInstructionMapping = new HashMap<Integer, Integer>();
        floatConstantToInstructionMapping = new HashMap<Integer, Integer>();
        stringConstantToInstructionMapping = new HashMap<Integer, Integer>();

        types = new HashMap<String, HashMap<String, TypeField> >();
        arrays = new HashMap<String, ArrayType>();
        arrayToArrayTypeMappings = new HashMap<String, String>();

    }

    public String generateCode (TreeNode programNode) {



        TreeNode globalsNode = programNode.getLeft();
        TreeNode mainbodyNode = programNode.getRight();
        TreeNode slistNode = mainbodyNode.getLeft();

        // we need to generate constants first
        // constants
        if (globalsNode != null && globalsNode.getLeft() != null) {
            GlobalsGenerator.generateConstantsSection(globalsNode.getLeft(), this);
        }

        // we need to generate code for declarations before main body
        generateDeclarations(slistNode);

        GlobalsGenerator.generateCode(globalsNode, this);




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

        opCodes.add("42");
        int opCodeStartPosForDeclaration = opCodes.size();
        // we will replace the blank 00 00 with an actual count of the declaratiosn needed after we have generated and counted the
        // declarations needed
        opCodes.add("00");
        opCodes.add("00");
        opCodes.add("52");
        int noOfDeclarations = generateDeclarationsRecursive(declarations, opCodes, 0);

        String[] noOFDeclarationsByteRep = convertAddressToByteRep(noOfDeclarations);

        opCodes.set(opCodeStartPosForDeclaration, noOFDeclarationsByteRep[2]);
        opCodes.set(opCodeStartPosForDeclaration + 1, noOFDeclarationsByteRep[3]);
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

            if (declarations.getSymbolRecord().getSymbolDataType() == SymbolDataType.Real) {
                // turn the top of the stack to real / float
                //opCodes.add("09");
            }

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
        int offset = treeNode.getSymbolRecord().getOffset();
        String[] offsetByteRep = convertAddressToByteRep(offset);

        opCodes.add(baseAddressLoadInstruction);
        opCodes.add(offsetByteRep[0]);
        opCodes.add(offsetByteRep[1]);
        opCodes.add(offsetByteRep[2]);
        opCodes.add(offsetByteRep[3]);

    }

    public void generateNARRVCode (TreeNode treeNode) {

        // load the array descripton

        String arrayDescriptionAddressLoadInstruction = "" + treeNode.getLeft().getSymbolRecord().getBaseRegisterForValue();
        int arrayDescriptionOffset = treeNode.getLeft().getSymbolRecord().getOffset();
        String[] arrayDescriptionOffsetByteRep = convertAddressToByteRep(arrayDescriptionOffset);

        opCodes.add(arrayDescriptionAddressLoadInstruction);
        opCodes.add(arrayDescriptionOffsetByteRep[0]);
        opCodes.add(arrayDescriptionOffsetByteRep[1]);
        opCodes.add(arrayDescriptionOffsetByteRep[2]);
        opCodes.add(arrayDescriptionOffsetByteRep[3]);

        // get the amount of fields for the type this array uses as an element
        String arrayTypeId = arrayToArrayTypeMappings.get(treeNode.getLeft().getSymbolRecord().getLexeme());
        ArrayType arrayType = arrays.get(arrayTypeId);
        String typeId = arrayType.typeId;
        HashMap<String, TypeField> fieldsForType = types.get(typeId);
        String[] sizeOfEachElementByteRep = convertAddressToByteRep(fieldsForType.size());

        // evaluate expression
        ExpressionGenerator.generateCode(treeNode.getMiddle(), this);

        // muliply the expression by the amount of fields per element
        opCodes.add("42");
        opCodes.add(sizeOfEachElementByteRep[2]);
        opCodes.add(sizeOfEachElementByteRep[3]);
        opCodes.add("13");

        // we also need to consider which fields we are trying to access
        String fieldId = treeNode.getRight().getSymbolRecord().getLexeme();
        TypeField typeField = fieldsForType.get(fieldId);
        String[] fieldOffsetByteRep = convertAddressToByteRep(typeField.offSet / 8);
        opCodes.add("42");
        opCodes.add(fieldOffsetByteRep[2]);
        opCodes.add(fieldOffsetByteRep[3]);
        opCodes.add("11");

        // run INDEX which will consume the array description load address
        // and the result of the expression * amount of fields per element
        opCodes.add("54");
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

    public void generateLoadArrayElementCode (TreeNode treeNode) {
        generateNARRVCode(treeNode);
        opCodes.add("40");
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
            String intCosPosByteRep[] = convertAddressToByteRep(actualIntegerConstantPos);

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
        int floatConstantsStartingPos = opCodes.size() + (integerConstants.size() * 8);

        // instruction op code pos  ->   relative pos in integer constants
        for (Map.Entry<Integer, Integer> entry : floatConstantToInstructionMapping.entrySet()) {
            int instructionOpCodePos = entry.getKey();
            int relativeFloatConstantPos = entry.getValue();

            int actualFloatConstantPos = (relativeFloatConstantPos - 1) * 8 + floatConstantsStartingPos;
            String floatCosPosByteRep[] = convertAddressToByteRep(actualFloatConstantPos);

            // resolve the instruction space address to use the integer constant
            opCodes.set(instructionOpCodePos + 1, floatCosPosByteRep[0]);
            opCodes.set(instructionOpCodePos + 2, floatCosPosByteRep[1]);
            opCodes.set(instructionOpCodePos + 3, floatCosPosByteRep[2]);
            opCodes.set(instructionOpCodePos + 4, floatCosPosByteRep[3]);
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
        modFileContents += floatConstants.size() + "\n";
        for (int i = 0; i < floatConstants.size(); i++) {
            modFileContents += "  " + floatConstants.get(i) + "\n";
        }

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

    public String[] convertAddressToByteRep (int actualIntegerConstantPos) {
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

    public void setOpCodes (int index, String s) {
        opCodes.set(index, s);
    }

    public void createTypes (TreeNode treeNode) {
        if (treeNode == null) {
            return;
        }

        if (treeNode.getNodeType() == TreeNodeType.NRTYPE) {
            HashMap<String, TypeField> fields = new HashMap<>();
            createFieldsForType(treeNode.getLeft(), fields, 0);

            types.put(treeNode.getSymbolRecord().getLexeme(), fields);
        } else if (treeNode.getNodeType() == TreeNodeType.NATYPE) {
            ArrayType arrayType = new ArrayType(treeNode.getSymbolRecord().getLexeme(), treeNode.getLeft(), treeNode.getRight().getSymbolRecord().getLexeme());

            arrays.put(treeNode.getSymbolRecord().getLexeme(), arrayType);
        }

        createTypes(treeNode.getLeft());
        createTypes(treeNode.getRight());
    }

    public int createFieldsForType (TreeNode treeNode, HashMap<String, TypeField> fields, int currentOffset) {
        if (treeNode == null) {
            return currentOffset;
        }

        if (treeNode.getNodeType() == TreeNodeType.NSDECL) {
            fields.put(treeNode.getSymbolRecord().getLexeme(),  new TypeField(treeNode.getSymbolRecord().getLexeme(), currentOffset, treeNode.getSymbolRecord().getSymbolDataType()));
            currentOffset += 8;
        }

        currentOffset = createFieldsForType(treeNode.getLeft(), fields, currentOffset);
        currentOffset = createFieldsForType(treeNode.getRight(), fields, currentOffset);

        return currentOffset;

    }

    public String getConvertOpCodeForSymbolDataType (SymbolDataType sdt) {
        if (sdt == SymbolDataType.Integer) {
            return "08";
        }

        // implies it is real
        return "09";
    }

    public int countDeclarationsInMain (TreeNode treeNode, int amountOfDeclarationsSoFar) {
        if (treeNode == null) {
            return amountOfDeclarationsSoFar;
        }

        if (treeNode.getNodeType() == TreeNodeType.NSDECL) {
            amountOfDeclarationsSoFar++;
            return amountOfDeclarationsSoFar;
        }

        amountOfDeclarationsSoFar = countDeclarationsInMain(treeNode.getLeft(), amountOfDeclarationsSoFar);
        amountOfDeclarationsSoFar = countDeclarationsInMain(treeNode.getRight(), amountOfDeclarationsSoFar);

        return amountOfDeclarationsSoFar;
    }




}