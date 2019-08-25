package rossH.CD19.Scanner;

import java.util.HashMap;
import java.util.Map;

public class CD19ScannerStateMachine {

    public static CD19ScannerState arb () {
        return CD19ScannerState.Comment;
    }

    // Define the states of the machine
    public enum CD19ScannerState {
        // -- Helper States --
            // abstract / virtual
            Start, // where we are still scanning undetermined / illegal tokens
            endOfToken, // where we have successfully recognized a token,

            // Partially recognized tokens
            PossibleComment, // we have just recognized the chars '/-', upon  the next char being another '-' char, we will be within a comment
            PossibleNotEquals, // we have just recognized up to '!', upon the next char being  a '+' char, we will have
            PossibleReal, // <integer>.(0)*<integer>
                                 //          ^ we have  just recognized up to here so far, if we come across
                                 //            any numbers after tis point then we will be within a Real Literal
            PossibleCommentOrDivide, // we have just recognized the char '/', upon an identifier / real literal / integer literal / SPACE (??)
                                    // we will know we have just recognized a Divide token, if however we recognize a '-' char, we will transition to
                                    // a PossibleComment state


            // Illegal chars, operators, identifiers, Strings, Literals etc
            IllegalCharacter,
            IllegalOperator,
            IllegalIdentifier,
            IllegalInteger,
            IllegalReal,
            IllegalNumber,


        // -- SPECIAL CHARACTERS --
        LeftBracket, // (
        RightBracket, // )
        LeftSquareBracket, // [
        RightSquareBracket, // ]
        Colon, // :
        SemiColon, // ;

        // -- OPERATORS --
            // arithmetic operators
            Plus, // +
            Minus, // -
            Multiply, // *
            Divide, // /
            ToThePowerOf, // ^
            Modulo, // %

            // assign operators
            Equals, // =
            PlusEquals, // +=
            MinusEquals, // -=
            MultiplyEquals, // *=
            DivideEquals, // /=

            // comparison operators
            EqualsEquals, // ==
            NotEquals, // !=
            GreaterThan, // >
            LessThan, // <
            GreaterOrEqualTo, // >=
            LesserOrEqualTo, // <=

            // reference operators
            Dot,

        // -- Literals --
        Integer,
        Real,
        Zero,
        String,

        // -- MISC --
        Identifier,
        Keyword, // one of the reserved key words of CD19
        Comment,  // single line comment
    };

    public enum CD19ScannerCurrentCharType {
        Alphabetical, // [a-zA-Z]
        Number, // [0-9]
        LegalSpecialCharacter, // * + - . ( ) [ ] ^  ! %  = > < , " : ; / etc
        IllegalSpecialCharacter, // { } # $ @ & _ ` ~ ?  | | \ etc
        ControlAndNonPrintableASCIICharacter,
        Space, // We NEED to distinguish between a newline and a space as a string literal cannot span multiple lines
        Newspace // \n \r OR \n\r ??
    }

    // On hitting a [Alphabetic, Numeric, ..] character, when we are in the CURRENT STATE, transition to NEXT STATE
    // <Current State, Next State>

    public static Map< Enum<?>, Enum<?> > AlphabeticalTransition = new HashMap<>();
    {{
        // Legal
        AlphabeticalTransition.put(CD19ScannerState.Identifier, CD19ScannerState.Identifier);
        AlphabeticalTransition.put(CD19ScannerState.Start, CD19ScannerState.Identifier);
        AlphabeticalTransition.put(CD19ScannerState.String, CD19ScannerState.String);
        // Illegal
        AlphabeticalTransition.put(CD19ScannerState.Integer, CD19ScannerState.IllegalInteger);
        AlphabeticalTransition.put(CD19ScannerState.Real, CD19ScannerState.IllegalReal);
    }};

    public static Map< Enum<?>, Enum<?> > NumericTransition = new HashMap<>();
    {{

        // Legal
        NumericTransition.put(CD19ScannerState.Integer, CD19ScannerState.Integer);
        NumericTransition.put(CD19ScannerState.Real, CD19ScannerState.Real);
        NumericTransition.put(CD19ScannerState.PossibleReal, CD19ScannerState.Real);
        NumericTransition.put(CD19ScannerState.Identifier, CD19ScannerState.Identifier);
        NumericTransition.put(CD19ScannerState.PossibleReal, CD19ScannerState.Real);
        // Illegal
        NumericTransition.put(CD19ScannerState.Zero, CD19ScannerState.IllegalNumber); // reals or integers do not start with 0, 0 can only be within (or ended with) the integer or real
    }};


    public static CD19ScannerState transition(CD19ScannerState presentState, char presentChar) {

        return presentState;
    }
}

