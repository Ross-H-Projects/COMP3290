package rossH.CD19.Scanner;

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
            PossibleComment, // we have just recognized a '-' char, upon  the next char being another '-' char, we will be within a comment
            PossibleNotEquals, // we have just recognized up to '!', upon the next char being  a '+' char, we will have
            PossibleRealLiteral, // <integer>.(0)*<integer>
                                 //          ^ we have  just recognized up to here so far, if we come across
                                 //            any numbers after tis point then we will be within a Real Literal



            // Illegal chars, operators, identifiers, Strings, Literals etc
            IllegalCharacter,
            IllegalOperator,
            IllegalIdentifier,
            IllegalInteger,
            IllegalReal


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

        // -- MISC --
        Keyword, // one of the reserved key words of CD19
        Comment,  // single line comment


    };
}

