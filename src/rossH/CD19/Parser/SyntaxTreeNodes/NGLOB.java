package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Scanner.Token;


public class NGLOB {

    // <globals>        --> <consts> <types> <arrays>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NGLOBNode = new TreeNode(TreeNodeType.NUNDEF);

        // <consts>
        TreeNode consts = consts(p);

        p.setAmountOfCounts(countAmountOfConstants (consts, 0));

        // <types>
        TreeNode types = types(p);

        // <arrays>
        TreeNode arrays = arrays(p);

        if (consts == null && types == null && arrays == null) {
            return null;
        }

        NGLOBNode.setValue(TreeNodeType.NGLOB);

        if (consts != null) {
            NGLOBNode.setLeft(consts);
        }

        if (types != null) {
            NGLOBNode.setMiddle(types);
        }

        if (arrays != null) {
            NGLOBNode.setRight(arrays);
        }

        return NGLOBNode;
    }

    // <consts>         --> constants <initlist> | ε
    public static TreeNode consts (CD19Parser p) {

        // ε
        if (!p.currentTokenIs(Token.TCONS)) {
            return null;
        }

        // constants
        p.moveToNextToken();

        // <intlist>
        TreeNode initlist =  NILIST.generateTreeNode(p);
        if (initlist.getNodeType() != TreeNodeType.NUNDEF) {
            return initlist;
        }

        // errors occured while attempting to parse the initlist
        return null;
    }

    // <types>      --> types <typelist> | ε
    public static TreeNode types (CD19Parser p) {
        // ε
        if (!p.currentTokenIs(Token.TTYPS)) {
            return null;
        }

        // types
        p.moveToNextToken();

        // <typelist>
        TreeNode typelist = NTYPEL.generateTreeNode(p);
        if (typelist.getNodeType() != TreeNodeType.NUNDEF) {
            return  typelist;
        }

        // errors occured while attempting to parse the typelist
        return null;
    }

    // <types>      --> arrays <arrdecls> | ε
    public static TreeNode arrays (CD19Parser p) {

        // ε
        if (!p.currentTokenIs(Token.TARRS)) {
            return null;
        }

        // arrays
        p.moveToNextToken();

        // <arrdecls>
        TreeNode arrdecls = NALIST.generateTreeNode(p);
        if (arrdecls.getNodeType() != TreeNodeType.NUNDEF) {
            return  arrdecls;
        }

        // errors occured while attempting to parse the arrdecls
        return null;
    }

    public static void fixArrayOffsets (TreeNode nglobNode, int amountOfConstantsAndDeclarationsInMainBody) {

        if (nglobNode == null) {
            return;
        }

        TreeNode nalistNode = nglobNode.getRight();
        if (nalistNode == null) {
            return;
        }

        fixArrayOffsetsRecursive(nalistNode, amountOfConstantsAndDeclarationsInMainBody, 0);
    }

    public static int fixArrayOffsetsRecursive (TreeNode treeNode, int amountOfConstantsAndDeclarationsInMainBody, int arrayDeclarationsEncounteredSoFar) {
        if (treeNode == null) {
            return arrayDeclarationsEncounteredSoFar;
        }

        if (treeNode.getNodeType() == TreeNodeType.NARRD) {
            treeNode.getSymbolRecord().setOffset(amountOfConstantsAndDeclarationsInMainBody * 8 + arrayDeclarationsEncounteredSoFar * 8);
            arrayDeclarationsEncounteredSoFar++;
            return arrayDeclarationsEncounteredSoFar;
        }

        // implis it is an nalist node

        arrayDeclarationsEncounteredSoFar = fixArrayOffsetsRecursive(treeNode.getLeft(), amountOfConstantsAndDeclarationsInMainBody, arrayDeclarationsEncounteredSoFar);
        arrayDeclarationsEncounteredSoFar = fixArrayOffsetsRecursive(treeNode.getRight(), amountOfConstantsAndDeclarationsInMainBody, arrayDeclarationsEncounteredSoFar);

        return arrayDeclarationsEncounteredSoFar;
    }

    public static int countAmountOfConstants (TreeNode treeNode, int amountOfConstantsSoFar) {
        if (treeNode == null) {
            return amountOfConstantsSoFar;
        }

        if (treeNode.getNodeType() == TreeNodeType.NINIT) {
            amountOfConstantsSoFar++;
            return amountOfConstantsSoFar;
        }

        // getting here implies the node is NILIST
        amountOfConstantsSoFar = countAmountOfConstants(treeNode.getLeft(), amountOfConstantsSoFar);
        amountOfConstantsSoFar = countAmountOfConstants(treeNode.getRight(), amountOfConstantsSoFar);

        return amountOfConstantsSoFar;
    }
}