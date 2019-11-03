
package rossH.CD19.codegen;

import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import java.util.LinkedList;
import java.util.List;




public class CD19CodeGenerator {
    List<int[]> opCodeBytes;
    int byteLineAmount;
    int currentOpCodeLine;

    public CD19CodeGenerator () {
        opCodeBytes = new LinkedList<int[]>();
        byteLineAmount = 0;
        currentOpCodeLine = 0;
    }

    /*

    CD19 test
    main
        x : integer
    begin
        x = 1;
    end
    CD19 test

     */


    int lines = 0;

    public String generateCode (TreeNode programNode) {

        // we need to traverse the tree via post order
        TreeNode mainbodyNode = programNode.getRight();


        // we need to generate code for declarations before main body
        TreeNode slistNode = mainbodyNode.getLeft();
        List<Integer> slistOpcodes = generateDeclarations(slistNode);

        // we need to generate code for statements in main body
        TreeNode statsNode = mainbodyNode.getRight();
        generateMainBodyStatements(statsNode, slistOpcodes);

        System.out.println("\n\n");

        for (int i = 0; i < slistOpcodes.size(); i++) {
            System.out.print(slistOpcodes.get(i));
            System.out.print(" ");
            if ((i + 1) % 8 == 0) {
                System.out.println();
            }
        }

        return "";
    }

    public List<Integer> generateDeclarations (TreeNode declarations) {
        // curently only supporting:
        // integer declarations
        LinkedList<Integer> opCodes = new LinkedList<Integer>();

        int noOfDeclarations = generateDeclarationsRecursive(declarations, opCodes, 0);

        // need to add allocation of stack memory backwards
        // 42  00  03  52
        opCodes.addFirst(52);
        opCodes.addFirst(noOfDeclarations);
        // todo
        //  add support for larger amount of declarations
        //  instead of just 00 here
        opCodes.addFirst(00);
        opCodes.addFirst(42);

        return opCodes;
    }

    public int generateDeclarationsRecursive (TreeNode declarations, List<Integer> opCodes, int noOfDeclarationsSofar) {
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
            int declarationBaseRegister = declarations.getSymbolRecord().getBaseRegister();
            int declarationOffSet = declarations.getSymbolRecord().getOffset();
            // load address
            opCodes.add(declarationBaseRegister);
            // todo
            //  convert large offsets to  bytes properly
            opCodes.add(0);
            opCodes.add(0);
            opCodes.add(0);
            opCodes.add(declarationOffSet);
            // initializes the variable to 0
            opCodes.add(42);
            opCodes.add(0);
            opCodes.add(0);
            // store
            opCodes.add(43);
            return 1;
        }
    }

    public List<Integer> generateMainBodyStatements (TreeNode statements, List<Integer> opCodes) {
        // currently only supporting:
        // a = x
        // x -> int literal | variables | addition of variabls / literals
        // ie currently only supports nstats, nasgn, nsimv, nilit,
        StatementGenerator.generateCode(statements, opCodes);

        return opCodes;
    }
}