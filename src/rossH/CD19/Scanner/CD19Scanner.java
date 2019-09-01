/*
 *  Author: Ross Hurley
 *  Last edited: 1/09/2019
 *  Made for COMP3290.
 */

/*
 *   This program generates tokens used in the lexical analysis phase.
 *   It acts as a state machine, which each call of getToken giving the next
 *   state which corresponds to a known token for the CD19 language.
 *
 */


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

    private CD19ScannerStateMachine.CD19ScannerState nextState;
    CD19ScannerStateMachine.CD19ScannerState currentState;

    private int printCurrentLineLength;

    public CD19Scanner (String srcCode) {
        this.srcCode = srcCode;
        this.srcCodePos = 0;
        this.currentColumnNo = 1;
        this.currentLineNo = 1;
        this.lexemeBuffer = "";
        this.endOfFile = false;
        this.printCurrentLineLength = 0;

        Token.setup();
        CD19ScannerStateMachine.setup();
    }

    public boolean eof () {
        return endOfFile;
    }

    public Token getToken () {
        if (endOfFile || srcCodePos >= srcCode.length() || nextState == CD19ScannerStateMachine.CD19ScannerState.EOF) {
            endOfFile = true;
            return new Token(Token.TEOF, currentLineNo, currentColumnNo, null);
        }

        char currentChar = srcCode.charAt(srcCodePos);
        lexemeBuffer = "";
        currentState = CD19ScannerStateMachine.CD19ScannerState.Start;
        nextState = currentState;

        int asciiIndex;
        while (nextState != CD19ScannerStateMachine.CD19ScannerState.PossibleEndOfToken) {
            if (endOfFile) {
                nextState = CD19ScannerStateMachine.CD19ScannerState.EOF;
                break;
            }
            nextState = CD19ScannerStateMachine.transition(nextState, currentChar);

            // We do not want to move the head past the current char as this is a char that
            // has caused a possible recognition of a token and we will the current char for the
            // next possible token
            if (nextState == CD19ScannerStateMachine.CD19ScannerState.PossibleEndOfToken || nextState == CD19ScannerStateMachine.CD19ScannerState.IllegalString) {
                break;
            }

            // we have just encountered an illegal char when not waiting to build a token
            // and we are not already building a sequence of illegal chars
            // thus we have just encountered an illegal char while in some building state
            if ((currentState != CD19ScannerStateMachine.CD19ScannerState.IllegalCharacter &&
                    currentState != CD19ScannerStateMachine.CD19ScannerState.Start) &&
                    nextState == CD19ScannerStateMachine.CD19ScannerState.IllegalCharacter) {
                break;
            }

            // we have  just recognized a comment
            // so we should clear the lexeme buffer
            if ( currentState == CD19ScannerStateMachine.CD19ScannerState.PossibleComment &&
                    nextState == CD19ScannerStateMachine.CD19ScannerState.Comment) {
                currentState = CD19ScannerStateMachine.CD19ScannerState.Comment;
                lexemeBuffer = "";
            }

            asciiIndex = (int) currentChar;

            if (asciiIndex == 9) { // tab - 4 units
                currentColumnNo += 4;
            } else { // single - 1 unit
                currentColumnNo++;
            }

            // anything within a comment is not consumed
            if (currentState != CD19ScannerStateMachine.CD19ScannerState.Comment) {
                if (currentChar == '"') {
                    lexemeBuffer += '\"';
                } else if (asciiIndex == 10) { // newline (we're on windows, lines are ended via <CR><LF>, so we will just count <LF>
                    currentLineNo++;
                    currentColumnNo = 1;
                } else if (asciiIndex == 13) { // <CR>
                    // don't add it to the lexeme buffer
                } else if (asciiIndex == 9 || asciiIndex == 32 && (nextState == CD19ScannerStateMachine.CD19ScannerState.Start)) { // space
                    // don't add space to the lexeme buffer if we aren't currently
                    // building a potential token
                } else {
                    lexemeBuffer += currentChar;
                }
            }

            currentChar = moveHead();
            currentState = nextState;
        }

        int returnColumnNo = -1;
        // getting here implies that we are returning something that isn't EOF
        // so we will need to get th EOF token
        endOfFile = false;

        // we have just recognized a single char token
        if (isSingleCharToken(currentState) ) {
            if (Token.matchToken(lexemeBuffer) == -1) {
                System.out.println("SHOULD NEVER HAPPEN");
                System.out.println(currentState);
                System.out.println("#" + lexemeBuffer + "#");
                System.exit(1);
            }
            return new Token(Token.matchToken(lexemeBuffer), currentLineNo, currentColumnNo - 1, null);
        }

        if (isDoubleCharToken(currentState)) {
            if (Token.matchToken(lexemeBuffer) == -1) {
                System.out.println("SHOULD NEVER HAPPEN");
                System.out.println(currentState);
                System.out.println("#" + lexemeBuffer + "#");
                System.exit(1);
            }
            return new Token(Token.matchToken(lexemeBuffer), currentLineNo, currentColumnNo - 2, null);
        }

        // we have either just recognized a ident or a keyword
        if (currentState == CD19ScannerStateMachine.CD19ScannerState.Identifier) {
            returnColumnNo = currentColumnNo - lexemeBuffer.length();
            // The Token class will figure out if the ident is a actually a keyword or not
            return new Token(Token.TIDEN, currentLineNo, returnColumnNo, lexemeBuffer);
        }

        // We have successfully recognized a string literal
        if (currentState == CD19ScannerStateMachine.CD19ScannerState.StringEnd) {
            returnColumnNo = currentColumnNo - lexemeBuffer.length();
            return new Token(Token.TSTRG, currentLineNo, returnColumnNo, lexemeBuffer);
        }

        // we have just recognized an integer literal
        if (currentState == CD19ScannerStateMachine.CD19ScannerState.Integer) {
            returnColumnNo = currentColumnNo - lexemeBuffer.length();
            return new Token(Token.TILIT, currentLineNo, returnColumnNo, lexemeBuffer);
        }

        // we have just recognized a real literal
        if (currentState == CD19ScannerStateMachine.CD19ScannerState.Real) {
            returnColumnNo = currentColumnNo - lexemeBuffer.length();
            return new Token(Token.TFLIT, currentLineNo, returnColumnNo, lexemeBuffer);
        }

        // [Possible Real] ended
        if (currentState == CD19ScannerStateMachine.CD19ScannerState.PossibleReal) {
            // setup the scanner for the next getToken (which will always be a dot token)
            // by walking back the last move operation
            srcCodePos--;
            returnColumnNo = currentColumnNo - lexemeBuffer.length(); // the column number for the int
            currentColumnNo--;
            String returnLexemeBuffer = lexemeBuffer;
            returnLexemeBuffer = returnLexemeBuffer.substring(0, returnLexemeBuffer.length() - 1);
            return new Token(Token.TILIT, currentLineNo, returnColumnNo , returnLexemeBuffer);
        }

        // [Possible Comment or Divide] ended
        if (currentState == CD19ScannerStateMachine.CD19ScannerState.PossibleCommentOrDivide) {
            return new Token(Token.TDIVD, currentLineNo, currentColumnNo - 1, null);
        }

        // [Possible Comment] ended
        if (currentState == CD19ScannerStateMachine.CD19ScannerState.PossibleComment) {
            // setup the scanner for the next getToken (which will always be a recognition of either a minus or a minus equals)
            // by walking back the last move operation
            srcCodePos--;
            returnColumnNo = currentColumnNo - 2;
            currentColumnNo--;
            return new Token(Token.TDIVD, currentLineNo, returnColumnNo , null);
        }

        // [PossibleNotEquals] ended
        if (currentState == CD19ScannerStateMachine.CD19ScannerState.PossibleNotEquals) {
            return new Token(Token.TUNDF, currentLineNo, currentColumnNo - 1, "!");
        }


        if (nextState == CD19ScannerStateMachine.CD19ScannerState.EOF && currentState == CD19ScannerStateMachine.CD19ScannerState.String) {
            // check if we have hit eof with an incomplete string
            currentState = CD19ScannerStateMachine.CD19ScannerState.IllegalString;
            srcCodePos++;
        } else if (nextState == CD19ScannerStateMachine.CD19ScannerState.IllegalString) {
            currentState  = CD19ScannerStateMachine.CD19ScannerState.IllegalString;
        }

        // blank last line
        if (nextState == CD19ScannerStateMachine.CD19ScannerState.EOF && currentState == CD19ScannerStateMachine.CD19ScannerState.Start) {
            endOfFile = true;
            return new Token(Token.TEOF, currentLineNo, currentColumnNo, null);
        }

        // [Some Invalid State] ended
        if (isIllegalState(currentState)) {
            // Generate errors
            // return undefined token
            // we need walk back the column no to the start of the illegal sequence
            returnColumnNo = currentColumnNo - lexemeBuffer.length();
            return new Token(Token.TUNDF, currentLineNo, returnColumnNo, lexemeBuffer);
        }

        // SHOULD NEVER GET TO THIS POINT
        System.out.println("SHOULD NEVER GET TO THIS POINT -- end of getToken");
        System.out.println(currentState);
        System.out.println("#" + lexemeBuffer + "#");
        return new Token(Token.TUNDF, currentLineNo, currentColumnNo, lexemeBuffer);
    }

    private boolean isSingleCharToken (CD19ScannerStateMachine.CD19ScannerState state) {
        return state == CD19ScannerStateMachine.CD19ScannerState.RightBracket ||
                state == CD19ScannerStateMachine.CD19ScannerState.LeftBracket ||
                state == CD19ScannerStateMachine.CD19ScannerState.LeftSquareBracket ||
                state == CD19ScannerStateMachine.CD19ScannerState.RightSquareBracket ||
                state == CD19ScannerStateMachine.CD19ScannerState.Colon ||
                state == CD19ScannerStateMachine.CD19ScannerState.SemiColon ||
                state == CD19ScannerStateMachine.CD19ScannerState.Comma ||
                state == CD19ScannerStateMachine.CD19ScannerState.Plus ||
                state == CD19ScannerStateMachine.CD19ScannerState.Minus ||
                state == CD19ScannerStateMachine.CD19ScannerState.Multiply ||
                state == CD19ScannerStateMachine.CD19ScannerState.ToThePowerOf ||
                state == CD19ScannerStateMachine.CD19ScannerState.Modulo ||
                state == CD19ScannerStateMachine.CD19ScannerState.Equals ||
                state == CD19ScannerStateMachine.CD19ScannerState.GreaterThan ||
                state == CD19ScannerStateMachine.CD19ScannerState.LessThan ||
                state == CD19ScannerStateMachine.CD19ScannerState.Dot;
        // We intentionally leave out PossibleCommentOrDivide as that is handled else where
    }

    private boolean isDoubleCharToken (CD19ScannerStateMachine.CD19ScannerState state) {
        return state == CD19ScannerStateMachine.CD19ScannerState.Equals ||
                state == CD19ScannerStateMachine.CD19ScannerState.PlusEquals ||
                state == CD19ScannerStateMachine.CD19ScannerState.MinusEquals ||
                state == CD19ScannerStateMachine.CD19ScannerState.MultiplyEquals ||
                state == CD19ScannerStateMachine.CD19ScannerState.DivideEquals ||
                state == CD19ScannerStateMachine.CD19ScannerState.EqualsEquals ||
                state == CD19ScannerStateMachine.CD19ScannerState.NotEquals ||
                state == CD19ScannerStateMachine.CD19ScannerState.GreaterOrEqualTo ||
                state == CD19ScannerStateMachine.CD19ScannerState.LesserOrEqualTo;
    }

    private boolean isIllegalState (CD19ScannerStateMachine.CD19ScannerState state) {
        return state == CD19ScannerStateMachine.CD19ScannerState.IllegalCharacter ||
                state == CD19ScannerStateMachine.CD19ScannerState.IllegalString;
    }

    private char moveHead () {
        if (srcCodePos < (srcCode.length() - 1)) {
            srcCodePos++;
        } else {
            endOfFile = true;
        }
        return srcCode.charAt(srcCodePos);
    }

    public void printToken (Token token) {
        String tokenName = Token.TPRINT[token.value()];

        boolean needToPrintLexeme = false;
        if (tokenName.equals("TIDEN ") ||
                tokenName.equals("TILIT ") ||
                tokenName.equals("TFLIT ") ||
                tokenName.equals("TSTRG ") ||
                tokenName.equals("TUNDF ")) {
            needToPrintLexeme = true;
        }

        if (needToPrintLexeme) {
            printCurrentToken(tokenName, token.getStr() + " ");
        } else {
            printCurrentToken(tokenName, "");
        }

    }

    private void printCurrentToken (String tokenName, String lexeme) {
        // need to handle errors
        if (tokenName.equals("TUNDF ")) {
            if (printCurrentLineLength != 0) {
                System.out.println();
            }
            System.out.println("TUNDF ");
            System.out.println("lexical error " + lexeme);
            printCurrentLineLength = 0;
            return;
        }

        // assumes we are starting fresh at a new column

        // print token name first
        // jump to new line if needed
        if (printCurrentLineLength + 6 + lexeme.length() >= 66) {
            System.out.println();
            printCurrentLineLength = 5;
        } else {
            if (printCurrentLineLength == 0) {
                printCurrentLineLength = 5;
            } else {
                printCurrentLineLength += 6;
            }
        }
        System.out.print(tokenName);

        if (lexeme.length() == 0) {
            return;
        }

        for (int i = 0; i < lexeme.length(); i++) {
            printCurrentLineLength++;
            System.out.print(lexeme.charAt(i));
            if (printCurrentLineLength == 65) {
                System.out.println();
                printCurrentLineLength = 0;
            }
        }

        String padding;
        if (lexeme.length() < 6) {
            padding = generateSpace(6 - lexeme.length());
            System.out.print(padding);
            printCurrentLineLength += padding.length();
            return;
        }

        int over = (printCurrentLineLength + (6 - printCurrentLineLength % 6)) - 1;
        over = over - printCurrentLineLength;
        if (over != 0) {
            padding = generateSpace(over);
            if (printCurrentLineLength + padding.length() >= 66) {
                System.out.println();
                printCurrentLineLength = 0;
            } else {
                System.out.print(padding);
                printCurrentLineLength += padding.length();
            }
        }

    }


    private String generateSpace (int amount) {
        String spaces = "";
        for (int i = 0; i < amount; i++) {
            spaces = spaces + " ";
        }
        return spaces;
    }

}
