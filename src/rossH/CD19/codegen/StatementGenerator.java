package rossH.CD19.codegen;

import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import java.util.List;

public class StatementGenerator {
    public static void generateCode (TreeNode treeNode, List<Integer> opCodes) {
        if (treeNode == null) {
            return;
        }

        // traverse via post order
        // left, right, root

        if (treeNode.getLeft().getNodeType() == TreeNodeType.NASGN) {
            generateNASGNCode(treeNode.getLeft(), opCodes);
        }

        if (treeNode.getRight() == null ) {
            return;
        }

        if (treeNode.getRight().getNodeType() == TreeNodeType.NSTATS) {
            generateCode(treeNode.getRight(), opCodes);
        } else if (treeNode.getRight().getNodeType() == TreeNodeType.NASGN) {
            generateNASGNCode(treeNode.getRight(), opCodes);
        }
    }

    public static void generateNASGNCode (TreeNode treeNode, List<Integer> opCodes) {

        // todo
        //  need to eventually implement for NARRV
        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) {
            generateNSIVMCode(treeNode.getLeft(), opCodes);
        }

        if (treeNode.getRight().getNodeType() == TreeNodeType.NILIT) {
            generateNILITCode(treeNode.getRight(), opCodes);
        }

        if (treeNode.getRight().getNodeType() == TreeNodeType.NADD) {
            // todo
        }

        // do a store at the end
        opCodes.add(43);

    }

    public static void generateNSIVMCode (TreeNode treeNode, List<Integer> opCodes) {
        // load address
        // specific to base address
        int baseAddressLoadInstruction = treeNode.getSymbolRecord().getBaseRegister();
        int offset = treeNode.getSymbolRecord().getOffset();
        opCodes.add(baseAddressLoadInstruction);
        // specify offset
        // todo
        //  eventually support larger address size
        opCodes.add(0);
        opCodes.add(0);
        opCodes.add(0);
        opCodes.add(offset);
    }

    public static void generateNILITCode (TreeNode treeNode, List<Integer> opCodes) {
        String integerliteralLexeme = treeNode.getSymbolRecord().getLexeme();
        int integerLiteral = Integer.parseInt(integerliteralLexeme);
        // todo (maybe?)
        //  support larger integers
        opCodes.add(42);
        opCodes.add(0);
        opCodes.add(integerLiteral);
    }


}
