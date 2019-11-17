package rossH.CD19.Parser.SyntaxTreeNodes;

import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolTable;
import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;
import rossH.CD19.Scanner.Token;


public class NFUND {
    // <func>       --> function <id> ( <plist> ) : <rtype> <funcbody>
    public static TreeNode generateTreeNode (CD19Parser p) {
        TreeNode NFUNDNode = new TreeNode(TreeNodeType.NUNDEF);
        Token currentToken;

        SymbolTable symbolTable = new SymbolTable();

        // function
        if (!p.currentTokenIs(Token.TFUNC)) {
            p.generateSyntaxError("Expected the keyword 'function'.");
            // prematurely end parsing due to irrecoverable error
            return NFUNDNode;
        }
        p.moveToNextToken();

        // <id>
        if (!p.currentTokenIs(Token.TIDEN)) {
            p.generateSyntaxError("Expected an identifier in function declaration.");
            // prematurely end parsing due to irrecoverable error
            return NFUNDNode;
        }
        // insert program id identifier into symbol table
        currentToken = p.getCurrentToken();
        SymbolTableRecord stRec = p.insertSymbolIdentifier(currentToken);
        NFUNDNode.setSymbolRecord(stRec);
        p.moveToNextToken();

        // (
        if (!p.currentTokenIs(Token.TLPAR)) {
            p.generateSyntaxError("Expected the chracter '('.");
            // prematurely end parsing due to irrecoverable error
            return NFUNDNode;
        }
        p.moveToNextToken();


        // <plist>
        TreeNode plist = null;
        if (!p.currentTokenIs(Token.TRPAR)) {
            plist = NPLIST.generateTreeNode(p, symbolTable);
            if (plist.getNodeType() == TreeNodeType.NUNDEF) {
                plist = null;
            }
        }

        // )
        if (!p.currentTokenIs(Token.TRPAR)) {
            p.generateSyntaxError("Unclosed paranthesis for function definition.");
            // prematurely end parsing due to irrecoverable error
            return NFUNDNode;
        }
        p.moveToNextToken();

        // :
        if (!p.currentTokenIs(Token.TCOLN)) {
            p.generateSyntaxError("Expected the chracter ':'.");
            // prematurely end parsing due to irrecoverable error
            return NFUNDNode;
        }
        p.moveToNextToken();

        // <rtype>
        if (!p.currentTokenIs(Token.TVOID) && !p.currentTokenIs(Token.TINTG) && !p.currentTokenIs(Token.TREAL) && !p.currentTokenIs(Token.TBOOL)) {
            p.generateSyntaxError("Expected a function reurn type: 'integer', 'real', 'boolean', 'void'.");
            return NFUNDNode;
        }
        // set return type of function
        NFUNDNode.setSymbolRecordDataType(p.getCurrentToken());

        p.moveToNextToken();


        // <funcbody>
        // <funcbody>   --> <locals> begin <stats> end

        // <locals>
        // <locals>     --> <dlist> | ε
        // <dlist>
        TreeNode dlist = null;
        if (!p.currentTokenIs(Token.TBEGN)) {
            dlist = NDLIST.generateTreeNode(p, symbolTable);

            if (dlist.getNodeType() == TreeNodeType.NUNDEF) {
                dlist = null;
                try {
                    errorRecoveryDlist(p);
                } catch (Exception e) {
                    return NFUNDNode;
                }
            }
        }

        // begin
        if (!p.currentTokenIs(Token.TBEGN)) {
            p.generateSyntaxError("Expected the keyword 'begin'.");
            return NFUNDNode;
        }
        p.moveToNextToken();

        // <stats>
        TreeNode stats = NSTATS.generateTreeNode(p, symbolTable);
        if (stats.getNodeType() == TreeNodeType.NUNDEF) {
            // stats are a necessary part of the function body
            // therefore we will just return an invalid NFUND
            return NFUNDNode;
        }

        // end
        if (!p.currentTokenIs(Token.TEND)) {
            p.generateSyntaxError("Expected the keyword 'end'.");
            return NFUNDNode;
        }
        p.moveToNextToken();

        NFUNDNode.setValue(TreeNodeType.NFUND);
        NFUNDNode.setSymbolTable(symbolTable);

        // we need to fix (by reversing) the offsets for the parameters
        fixParameters(plist);

        // we need to fix the local declarations so that any existing array declarations
        // have proper offsets
        fixLocalArrayDeclarationOffsets(dlist);

        NFUNDNode.setLeft(plist);
        NFUNDNode.setMiddle(dlist);
        NFUNDNode.setRight(stats);
        return NFUNDNode;
    }

    private static void errorRecoveryDlist (CD19Parser p) throws Exception {
        // we need to go the next 'begin' token,
        // in failing that we need to go to either the next 'function' token
        // or 'main' token

        int nextBegin = p.nextTokenOccursAt(Token.TBEGN);
        int nextFunction = p.nextTokenOccursAt(Token.TFUNC);
        int nextMain = p.nextTokenOccursAt(Token.TMAIN);

        if (nextBegin == -1) {
            if (nextFunction != -1) {
                p.tokensJumpTo(nextFunction);
                return;
            } else if (nextFunction != -1) {
                p.tokensJumpTo(nextMain);
                return;
            }
            throw new Exception("Unable to recover");
        }

        if (nextFunction != -1 && nextBegin < nextFunction) {
            p.tokensJumpTo(nextBegin);
            return;
        }

        if (nextMain != -1 && nextBegin < nextMain) {
            p.tokensJumpTo(nextBegin);
            return;
        }

        throw new Exception("Unable to recover");
    }

    public static void fixParameters (TreeNode treeNode) {
        if (treeNode == null) {
            return;
        }

        // count the amount of parameters
        int noOfParameters = countNoOfParameters(treeNode, 0);

        fixParametersRecursive(treeNode, noOfParameters, 0);
    }

    public static int countNoOfParameters (TreeNode treeNode, int noOfParametersSoFar) {
        if (treeNode == null) {
            return noOfParametersSoFar;
        }

        if (treeNode.getNodeType() != TreeNodeType.NPLIST) {
            noOfParametersSoFar++;
            return noOfParametersSoFar;
        }

        noOfParametersSoFar = countNoOfParameters(treeNode.getLeft(), noOfParametersSoFar);
        noOfParametersSoFar = countNoOfParameters(treeNode.getRight(), noOfParametersSoFar);

        return noOfParametersSoFar;
    }

    public static int fixParametersRecursive (TreeNode treeNode, int totalAmountOfParameters, int noOfParametersEncountered) {
        if (treeNode == null) {
            return noOfParametersEncountered;
        }

        if (treeNode.getNodeType() != TreeNodeType.NPLIST) {
            noOfParametersEncountered++;

            if (treeNode.getNodeType() == TreeNodeType.NSIMP) {
                int oldOffset = treeNode.getLeft().getSymbolRecord().getOffset();
                int newOffset = -8 + (totalAmountOfParameters * (-8)) + (noOfParametersEncountered * 8);
                treeNode.getLeft().getSymbolRecord().setOffset(newOffset);
            } else if (treeNode.getNodeType() == TreeNodeType.NARRP) {
                int oldOffset = treeNode.getLeft().getSymbolRecord().getOffset();
                int newOffset = -8 + (totalAmountOfParameters * (-8)) + (noOfParametersEncountered * 8);
                treeNode.getLeft().getSymbolRecord().setOffset(newOffset);
            } else if (treeNode.getNodeType() == TreeNodeType.NARRC) {
                int oldOffset = treeNode.getLeft().getSymbolRecord().getOffset();
                int newOffset = -8 + (totalAmountOfParameters * (-8)) + (noOfParametersEncountered * 8);
                treeNode.getLeft().getSymbolRecord().setOffset(newOffset);
            }

            return noOfParametersEncountered;
        }

        noOfParametersEncountered = fixParametersRecursive(treeNode.getLeft(), totalAmountOfParameters, noOfParametersEncountered);
        noOfParametersEncountered = fixParametersRecursive(treeNode.getRight(), totalAmountOfParameters, noOfParametersEncountered);

        return noOfParametersEncountered;
    }

    public static void fixLocalArrayDeclarationOffsets (TreeNode treeNode) {
        int amountOfNonArrayDeclarations = countAmountOfNonArrayDeclarations(treeNode, 0);

        fixLocalArrayDeclarationOffsetsRecursive(treeNode , amountOfNonArrayDeclarations ,0);
    }

    public static int fixLocalArrayDeclarationOffsetsRecursive(TreeNode treeNode, int totalAmountOfNonArrayDeclarations, int arrayDeclarationsEncountered) {
        if (treeNode == null) {
            return arrayDeclarationsEncountered;
        }

        if (treeNode.getNodeType() != TreeNodeType.NDLIST) {
            if (treeNode.getNodeType() == TreeNodeType.NARRD) {
                arrayDeclarationsEncountered++;
                int newOffset = 8 + (totalAmountOfNonArrayDeclarations * 8) + (arrayDeclarationsEncountered * 8);
                treeNode.getSymbolRecord().setOffset(newOffset);

                // also set the base register to 2
                treeNode.getSymbolRecord().setBaseRegister(2);
            }
            return arrayDeclarationsEncountered;
        }

        arrayDeclarationsEncountered = fixLocalArrayDeclarationOffsetsRecursive(treeNode.getLeft(), totalAmountOfNonArrayDeclarations, arrayDeclarationsEncountered);
        arrayDeclarationsEncountered = fixLocalArrayDeclarationOffsetsRecursive(treeNode.getRight(), totalAmountOfNonArrayDeclarations, arrayDeclarationsEncountered);
        return arrayDeclarationsEncountered;
    }

    public static int countAmountOfNonArrayDeclarations (TreeNode treeNode, int amountSoFar) {
        if (treeNode == null) {
            return amountSoFar;
        }

        if (treeNode.getNodeType() != TreeNodeType.NDLIST) {
            if (treeNode.getNodeType() != TreeNodeType.NARRD) {
                amountSoFar++;
            }
            return amountSoFar;
        }

        amountSoFar = countAmountOfNonArrayDeclarations(treeNode.getLeft(), amountSoFar);
        amountSoFar = countAmountOfNonArrayDeclarations(treeNode.getRight(), amountSoFar);
        return amountSoFar;
    }

}
