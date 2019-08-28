package rossH.CD19.Scanner;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.Vector;

public class CD19Scanner {
    private String srcCode;
    private String lexemeBuffer;
    private int srcCodePos;
    private int currentLineNo;
    private int currentColumnNo;
    private boolean endOfFile;

    public CD19Scanner (String srcCode) {
        this.srcCode = srcCode;
        this.srcCodePos = 0;
        this.currentColumnNo = 1;
        this.currentLineNo = 1;
        this.lexemeBuffer = "";
        this.endOfFile = false;

        CD19ScannerStateMachine.setup();
    }

    public boolean eof () {
        return this.endOfFile;
    }

    public void printtoken (Token token) {
        String printString = "<" + token.value() + ", ";
        printString += token.getStr() + ", ";
        printString += token.getLn() + ", ";
        printString += token.getPos() + ">";
        System.out.println(printString);

        CD19ScannerStateMachine.setup();
        currentState = CD19ScannerStateMachine.CD19ScannerState.Start;
    }

    public Token gettoken () {
        Token nextToken = null;

        char currentChar = srcCode.charAt(srcCodePos);
        CD19ScannerStateMachine.CD19ScannerState currentState = CD19ScannerStateMachine.CD19ScannerState.Start;
        CD19ScannerStateMachine.CD19ScannerState nextState = currentState;

        while (nextState != CD19ScannerStateMachine.CD19ScannerState.PossibleEndOfToken) {
            nextState = CD19ScannerStateMachine.transition(nextState, currentChar);

            // We do want to move the head past the current char as this is a char that
            // has caused a possible recognition of a token and we will the curent char for the
            // next possible token
            if (nextState == CD19ScannerStateMachine.CD19ScannerState.PossibleEndOfToken) {
                break;
            }

            // we have  just recognized a comment
            // so we should clear the lexeme buffer
            if ( currentState == CD19ScannerStateMachine.CD19ScannerState.PossibleComment &&
                    nextState == CD19ScannerStateMachine.CD19ScannerState.Comment) {
                currentState = CD19ScannerStateMachine.CD19ScannerState.Comment;
                lexemeBuffer = "";
            }

            if (currentState != CD19ScannerStateMachine.CD19ScannerState.Comment) {
                lexemeBuffer += currentChar;
            }

            currentChar = moveHead();
            currentState = nextState;
        }

        // [PossibleNotEquals] ended prematurely
        if (currentState == CD19ScannerStateMachine.CD19ScannerState.PossibleNotEquals) {
            lexemeBuffer = "";
            return new Token(Token.TUNDF, currentLineNo, currentColumnNo, "!");
        }

        // [Possible Comment or Divide] ended
        if (currentState == CD19ScannerStateMachine.CD19ScannerState.PossibleCommentOrDivide) {
            lexemeBuffer = "";
            return new Token(Token.TDIVD, currentLineNo, currentColumnNo, "/");
        }

        // [Possible Comment] ended prematurely
        if (currentState == CD19ScannerStateMachine.CD19ScannerState.PossibleComment) {
            // setup the scanner for the next gettoken (which will always be a recognition of either a minus or a minus equals)
            // by walking back the last move operation
            srcCodePos--;
            lexemeBuffer = "";
            int returnColumnNo = currentColumnNo - 1; // the column number for the divide will also be 1 behind the current column no (pointing at the minus)
            return new Token(Token.TDIVD, currentLineNo, returnColumnNo , "/");
        }

        // [Some Illegal or Invalid State] ended
        if (isIllegalState(currentState)) {
            // Generate errors
            // return undefined token
            String returnLexemeBuffer = lexemeBuffer;
            lexemeBuffer = "";

        }

        this.endOfFile = true;
        return new Token(Token.TIDEN, 1, 1, "cd19");
    }

    private boolean isIllegalState (CD19ScannerStateMachine.CD19ScannerState state) {
        return state == CD19ScannerStateMachine.CD19ScannerState.IllegalCharacter ||
                state == CD19ScannerStateMachine.CD19ScannerState.IllegalString ||
                state == CD19ScannerStateMachine.CD19ScannerState.IllegalReal;
    }

    private char moveHead () {
        srcCodePos++;
        return srcCode.charAt(srcCodePos);
    }



}
