package rossH.CD19.Scanner;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CD19ScannerStateMachine {


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
            IllegalString,


        // -- SPECIAL CHARACTERS --
        LeftBracket, // (
        RightBracket, // )
        LeftSquareBracket, // [
        RightSquareBracket, // ]
        Colon, // :
        SemiColon, // ;
        Comma, // ,
        DoubleQuotes, // "

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
        StringEnd,

        // -- MISC --
        Identifier,
        Keyword, // one of the reserved key words of CD19
        Comment,  // single line comment
    };

    public enum CD19ScannerCurrentCharType {
        Alphabetical, // [a-zA-Z]
        Numeric, // [0-9]
        LegalSpecialCharacter, // * + - . ( ) [ ] ^  ! %  = > < , " : ; / etc
        IllegalSpecialCharacter, // { } # $ @ & _ ` ~ ?  | | \ etc
        ControlAndNonPrintableASCIICharacter,
        Space, // We NEED to distinguish between a newline and a space as a string literal cannot span multiple lines
        Newline // \n \r OR \n\r ??
    }


    public static Map< CD19ScannerState, CD19ScannerState > AlphabeticalTransition = new HashMap<CD19ScannerState, CD19ScannerState>();
    public static Map< CD19ScannerState, CD19ScannerState > NumericTransition = new HashMap<CD19ScannerState, CD19ScannerState>();
    public static Map< CD19ScannerState, CD19ScannerState > SpaceTransition = new HashMap<CD19ScannerState, CD19ScannerState>();
    public static Map< CD19ScannerState, CD19ScannerState > NewlineTransition = new HashMap<CD19ScannerState, CD19ScannerState>();
    public static Map< Key, CD19ScannerState > LegalSpecialCharacterTransition = new HashMap<Key, CD19ScannerState>();

    public static boolean isSetup = false;
    public static void setup () {
        if (isSetup) {
            return;
        }

        setupAlphabeticalTransition();
        setupNumericTransition();
        setupSpaceTransition();
        setupNewlineTransition();
        setupLegalSpecialCharacterTransition();

        isSetup = true;
    }



    public static void arb () {

        CD19ScannerState a = LegalSpecialCharacterTransition.get( new Key('(', CD19ScannerState.Start.name()) );
        System.out.println("a: " + a);
        if (a == CD19ScannerState.Colon) {
            System.out.println("yyy");
        } else {
            System.out.println("xxxxxxxxx");
        }
    }

    public static CD19ScannerState transition(CD19ScannerState presentState, char presentChar) {

        return presentState;
    }

    private static void setupAlphabeticalTransition () {
        // On hitting a [Alphabetic, Numeric, ..] character, when we are in the CURRENT STATE, transition to NEXT STATE

        // <Current State, Next State>
        // Legal
        AlphabeticalTransition.put(CD19ScannerState.Identifier, CD19ScannerState.Identifier);
        AlphabeticalTransition.put(CD19ScannerState.Start, CD19ScannerState.Identifier);
        AlphabeticalTransition.put(CD19ScannerState.String, CD19ScannerState.String);
        // Illegal
        AlphabeticalTransition.put(CD19ScannerState.Integer, CD19ScannerState.IllegalInteger);
        AlphabeticalTransition.put(CD19ScannerState.Real, CD19ScannerState.IllegalReal);
    }

    private static void setupNumericTransition () {
        // <Current State, Next State>
        // Legal
        NumericTransition.put(CD19ScannerState.Integer, CD19ScannerState.Integer);
        NumericTransition.put(CD19ScannerState.Real, CD19ScannerState.Real);
        NumericTransition.put(CD19ScannerState.PossibleReal, CD19ScannerState.Real);
        NumericTransition.put(CD19ScannerState.Identifier, CD19ScannerState.Identifier);
        NumericTransition.put(CD19ScannerState.String, CD19ScannerState.String);
        NumericTransition.put(CD19ScannerState.Comment, CD19ScannerState.Comment);
        // Illegal
        NumericTransition.put(CD19ScannerState.Zero, CD19ScannerState.IllegalNumber); // reals or integers do not start with 0, 0 can only be within (or ended with) the integer or real
    }

    private static void setupSpaceTransition () {
        // <Current State, Next State>
        // Legal
        SpaceTransition.put(CD19ScannerState.String, CD19ScannerState.String);
        SpaceTransition.put(CD19ScannerState.Start, CD19ScannerState.Start);
        SpaceTransition.put(CD19ScannerState.Comment, CD19ScannerState.Comment);
        // Illegal
    }

    private static void setupNewlineTransition () {
        // <Current State, Next State>
        // Legal
        NewlineTransition.put(CD19ScannerState.Comment, CD19ScannerState.Start); // newline terminates comment
        NewlineTransition.put(CD19ScannerState.Start, CD19ScannerState.Start);
        // Illegal
        NewlineTransition.put(CD19ScannerState.String, CD19ScannerState.IllegalString);
    }

    private static void setupLegalSpecialCharacterTransition () {
        // <Current Char ,<Current State, Next State> >
        // Legal
        // Simple single character tokens
        LegalSpecialCharacterTransition.put(new Key( '(', CD19ScannerState.Start.name()), CD19ScannerState.LeftBracket );
        /*LegalSpecialCharacterTransition.put(")", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.RightBracket) );
        LegalSpecialCharacterTransition.add("[", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.LeftSquareBracket) );
        LegalSpecialCharacterTransition.add("]", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.RightSquareBracket) );
        LegalSpecialCharacterTransition.add("^", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.ToThePowerOf) );
        LegalSpecialCharacterTransition.add("%", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.Modulo) );
        LegalSpecialCharacterTransition.add(",", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.Comma) );
        LegalSpecialCharacterTransition.add("\"", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.DoubleQuotes) );
        LegalSpecialCharacterTransition.add(":", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.Colon) );
        LegalSpecialCharacterTransition.add(";", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.SemiColon) );
        LegalSpecialCharacterTransition.add("*", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.Multiply) );
        LegalSpecialCharacterTransition.add("/", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.PossibleCommentOrDivide) );
        LegalSpecialCharacterTransition.add("+", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.Plus) );
        LegalSpecialCharacterTransition.add("-", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.Minus) );
        LegalSpecialCharacterTransition.add("!", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.PossibleNotEquals) );
        LegalSpecialCharacterTransition.add("=", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.Equals) );
        LegalSpecialCharacterTransition.add("<", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.LessThan) );
        LegalSpecialCharacterTransition.add(">", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.GreaterThan) );
        LegalSpecialCharacterTransition.add(".", new AbstractMap.SimpleEntry(CD19ScannerState.Start, CD19ScannerState.Dot) );


        LegalSpecialCharacterTransition.add("-", new AbstractMap.SimpleEntry(CD19ScannerState.PossibleCommentOrDivide, CD19ScannerState.PossibleComment) );
        LegalSpecialCharacterTransition.add("-", new AbstractMap.SimpleEntry(CD19ScannerState.PossibleComment, CD19ScannerState.Comment) );
        LegalSpecialCharacterTransition.add("=", new AbstractMap.SimpleEntry(CD19ScannerState.Multiply, CD19ScannerState.MultiplyEquals) );
        LegalSpecialCharacterTransition.add("=", new AbstractMap.SimpleEntry(CD19ScannerState.PossibleCommentOrDivide, CD19ScannerState.DivideEquals) );
        LegalSpecialCharacterTransition.add("=", new AbstractMap.SimpleEntry(CD19ScannerState.Plus, CD19ScannerState.PlusEquals) );
        LegalSpecialCharacterTransition.add("=", new AbstractMap.SimpleEntry(CD19ScannerState.Minus, CD19ScannerState.MinusEquals) );
        LegalSpecialCharacterTransition.add("=", new AbstractMap.SimpleEntry(CD19ScannerState.PossibleNotEquals, CD19ScannerState.NotEquals) );
        LegalSpecialCharacterTransition.add("=", new AbstractMap.SimpleEntry(CD19ScannerState.Equals, CD19ScannerState.EqualsEquals) );
        LegalSpecialCharacterTransition.add("=", new AbstractMap.SimpleEntry(CD19ScannerState.LessThan, CD19ScannerState.LesserOrEqualTo) );
        LegalSpecialCharacterTransition.add("=", new AbstractMap.SimpleEntry(CD19ScannerState.GreaterThan, CD19ScannerState.GreaterOrEqualTo) );
        LegalSpecialCharacterTransition.add(".", new AbstractMap.SimpleEntry(CD19ScannerState.Integer, CD19ScannerState.PossibleReal) );*/

        // Illegal
        // /
        // Operators: += -=
    }
}

