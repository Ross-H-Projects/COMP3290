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
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NPRLN) {
            generateNPRLNCode(treeNode.getLeft(), opCodes);
        }

        if (treeNode.getRight() == null ) {
            return;
        }

        if (treeNode.getRight().getNodeType() == TreeNodeType.NSTATS) {
            generateCode(treeNode.getRight(), opCodes);
        } else if (treeNode.getRight().getNodeType() == TreeNodeType.NASGN) {
            generateNASGNCode(treeNode.getRight(), opCodes);
        } else if (treeNode.getRight().getNodeType() == TreeNodeType.NPRLN) {
            generateNPRLNCode(treeNode.getRight(), opCodes);
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
            generateNADDCode(treeNode.getRight(), opCodes);
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

    public static void generateNADDCode (TreeNode treeNode, List<Integer> opCodes) {
        // currently only supports addition of literals / variables
        // so we need:
        // todo
        //  add capability of expressions to be added

        // gen for left
        if (treeNode.getLeft().getNodeType() == TreeNodeType.NILIT) {
            generateNILITCode(treeNode.getLeft(), opCodes);
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) {
            generateLoadVariableCode(treeNode.getLeft(), opCodes);
        }

        // gen for right
        if (treeNode.getRight().getNodeType() == TreeNodeType.NILIT) {
            generateNILITCode(treeNode.getRight(), opCodes);
        } else if (treeNode.getRight().getNodeType() == TreeNodeType.NSIMV) {
            generateLoadVariableCode(treeNode.getRight(), opCodes);
        }

        // do an add op code at the end
        opCodes.add(11);
    }

    public static void generateLoadVariableCode (TreeNode treeNode, List<Integer> opCodes) {
        int baseAddressValueInstruction = treeNode.getSymbolRecord().getBaseRegisterForValue();
        int offset = treeNode.getSymbolRecord().getOffset();
        opCodes.add(baseAddressValueInstruction);
        // specify offset
        // todo
        //  eventually support larger address size
        opCodes.add(0);
        opCodes.add(0);
        opCodes.add(0);
        opCodes.add(offset);
    }

    public static void generateNPRLNCode (TreeNode treeNode, List<Integer> opCodes) {
        // todo
        //  currently only supports printing of on variable, not a list of variables / literals
        //  need to support more

        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) {
            generateLoadVariableCode(treeNode.getLeft(), opCodes);
            // VALPR
            opCodes.add(62);
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NILIT) {
            generateNILITCode(treeNode.getLeft(), opCodes);
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NPRLST) {
            generateNPRLSTCode(treeNode.getLeft(), opCodes);
        }

        // NEWLN
        opCodes.add(65);
    }

    public static void generateNPRINTCode (TreeNode treeNode, List<Integer> opCodes) {
        // todo
        //  currently only supports printing of on variable, not a list of variables / literals
        //  need to support more like actual expressions and string

        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) {
            generateLoadVariableCode(treeNode.getLeft(), opCodes);
            // VALPR
            opCodes.add(62);
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NILIT) {
            generateNILITCode(treeNode.getLeft(), opCodes);
            // VALPR
            opCodes.add(62);
        }  else if (treeNode.getLeft().getNodeType() == TreeNodeType.NPRLST) {
            generateNPRLSTCode(treeNode.getLeft(), opCodes);
        }
    }

    public static void generateNPRLSTCode (TreeNode treeNode, List<Integer> opCodes) {
        // todo
        //  currently only supports printing of on variable, not a list of variables / literals
        //  need to support more like actual expressions and string

        // left
        if (treeNode.getLeft().getNodeType() == TreeNodeType.NSIMV) {
            generateLoadVariableCode(treeNode.getLeft(), opCodes);
            // VALPR
            opCodes.add(62);
        } else if (treeNode.getLeft().getNodeType() == TreeNodeType.NILIT) {
            generateNILITCode(treeNode.getLeft(), opCodes);
            // VALPR
            opCodes.add(62);
        }

        if (treeNode.getRight() == null) {
            return;
        }

        // right
        if (treeNode.getRight().getNodeType() == TreeNodeType.NSIMV) {
            generateLoadVariableCode(treeNode.getRight(), opCodes);
            // VALPR
            opCodes.add(62);
        } else if (treeNode.getRight().getNodeType() == TreeNodeType.NILIT) {
            generateNILITCode(treeNode.getRight(), opCodes);
            // VALPR
            opCodes.add(62);
        } else if (treeNode.getRight().getNodeType() == TreeNodeType.NPRLST) {
            generateNPRLSTCode(treeNode.getRight(), opCodes);
        }
    }

}
