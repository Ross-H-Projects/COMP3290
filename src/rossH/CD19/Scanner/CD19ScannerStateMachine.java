package rossH.CD19.Scanner;

import java.util.*;

public class CD19ScannerStateMachine {


    // Define the states of the machine
    public enum CD19ScannerState {
        // -- Helper States --
            // abstract / virtual
            Start, // where we are still scanning undetermined / illegal tokens
            possibleEndOfToken, // where we MAY have just recognized a token successfully

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

    private static Map< CD19ScannerState, CD19ScannerState > AlphabeticalTransition = new HashMap<CD19ScannerState, CD19ScannerState>();
    private static Map< CD19ScannerState, CD19ScannerState > NumericTransition = new HashMap<CD19ScannerState, CD19ScannerState>();
    private static Map< CD19ScannerState, CD19ScannerState > SpaceTransition = new HashMap<CD19ScannerState, CD19ScannerState>();
    private static Map< CD19ScannerState, CD19ScannerState > NewlineTransition = new HashMap<CD19ScannerState, CD19ScannerState>();
    private static Map< Key, CD19ScannerState > LegalSpecialCharacterTransition = new HashMap<Key, CD19ScannerState>();
    private static boolean isSetup = false;

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

    private static void setupAlphabeticalTransition () {
        // On hitting a [Alphabetic, Numeric, ..] character, when we are in the CURRENT STATE, transition to NEXT STATE

        // <Current State, Next State>
        // Legal
        AlphabeticalTransition.put(CD19ScannerState.Identifier, CD19ScannerState.Identifier);
        AlphabeticalTransition.put(CD19ScannerState.Start, CD19ScannerState.Identifier);
        AlphabeticalTransition.put(CD19ScannerState.String, CD19ScannerState.String);
        AlphabeticalTransition.put(CD19ScannerState.Comment, CD19ScannerState.Comment);
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
        LegalSpecialCharacterTransition.put(new Key( ')', CD19ScannerState.Start.name()), CD19ScannerState.RightBracket );
        LegalSpecialCharacterTransition.put(new Key( ')', CD19ScannerState.Start.name()), CD19ScannerState.RightBracket );
        LegalSpecialCharacterTransition.put(new Key( '[', CD19ScannerState.Start.name()), CD19ScannerState.LeftSquareBracket );

        LegalSpecialCharacterTransition.put(new Key( ']', CD19ScannerState.Start.name()), CD19ScannerState.RightSquareBracket );
        LegalSpecialCharacterTransition.put(new Key( '^', CD19ScannerState.Start.name()), CD19ScannerState.ToThePowerOf );
        LegalSpecialCharacterTransition.put(new Key( '%', CD19ScannerState.Start.name()), CD19ScannerState.Modulo);
        LegalSpecialCharacterTransition.put(new Key( ',', CD19ScannerState.Start.name()), CD19ScannerState.Comma );
        LegalSpecialCharacterTransition.put(new Key( '"', CD19ScannerState.Start.name()), CD19ScannerState.DoubleQuotes );
        LegalSpecialCharacterTransition.put(new Key( ':', CD19ScannerState.Start.name()), CD19ScannerState.Colon );
        LegalSpecialCharacterTransition.put(new Key( ';', CD19ScannerState.Start.name()), CD19ScannerState.SemiColon );
        LegalSpecialCharacterTransition.put(new Key( '*', CD19ScannerState.Start.name()), CD19ScannerState.Multiply );
        LegalSpecialCharacterTransition.put(new Key( '/', CD19ScannerState.Start.name()), CD19ScannerState.PossibleCommentOrDivide );
        LegalSpecialCharacterTransition.put(new Key( '+', CD19ScannerState.Start.name()), CD19ScannerState.Plus );
        LegalSpecialCharacterTransition.put(new Key( '-', CD19ScannerState.Start.name()), CD19ScannerState.Minus );
        LegalSpecialCharacterTransition.put(new Key( '!', CD19ScannerState.Start.name()), CD19ScannerState.PossibleNotEquals );
        LegalSpecialCharacterTransition.put(new Key( '=', CD19ScannerState.Start.name()), CD19ScannerState.Equals );
        LegalSpecialCharacterTransition.put(new Key( '<', CD19ScannerState.Start.name()), CD19ScannerState.LessThan );
        LegalSpecialCharacterTransition.put(new Key( '>', CD19ScannerState.Start.name()), CD19ScannerState.GreaterThan );
        LegalSpecialCharacterTransition.put(new Key( '.', CD19ScannerState.Start.name()), CD19ScannerState.Dot );
        


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
    }

    public static void arb () {
        /*
        CD19ScannerState a = LegalSpecialCharacterTransition.get( new Key('(', CD19ScannerState.Start.name()) );
        System.out.println("a: " + a);
        if (a == CD19ScannerState.Colon) {
            System.out.println("yyy");
        } else {
            System.out.println("xxxxxxxxx");
        }
        */

    }

    public static CD19ScannerState transition(CD19ScannerState presentState, char presentChar) {

        int asciiIndex = (int)presentChar;
        CD19ScannerState nextState;
        // Current char is alphabetical
        if ((asciiIndex >= 65 && asciiIndex <= 90) || (asciiIndex >= 97 && asciiIndex <= 122)) {
            nextState = AlphabeticalTransition.get(presentState);
            if (nextState != null) {
                return nextState;
            }
            return CD19ScannerState.possibleEndOfToken;
        }

        // Current char is Numeric
        if (asciiIndex >= 48 && asciiIndex <= 57) {
            if (asciiIndex == 48 && presentState == CD19ScannerState.Start) { // Zero
                nextState = CD19ScannerState.Zero;
            } else {
                nextState = NumericTransition.get(presentState);
            }

            if (nextState != null) {
                return nextState;
            }
            return CD19ScannerState.possibleEndOfToken;
        }

        // Current char is a space char
        // tab, space
        if (asciiIndex == 9 || asciiIndex == 32) {
            nextState = SpaceTransition.get(presentState);
            if (nextState != null) {
                return nextState;
            }
            return CD19ScannerState.possibleEndOfToken;
        }

        // Current char is a newline char
        // newline or CR
        if (asciiIndex == 10 || asciiIndex == 13) {
            nextState = NewlineTransition.get(presentState);
            if (nextState != null) {
                return nextState;
            }
            return CD19ScannerState.possibleEndOfToken;
        }

        // Current char is a special char
        if (asciiIndex == 91 || asciiIndex == 93 || asciiIndex == 94 ||
                asciiIndex == 33 || asciiIndex == 34 || asciiIndex == 37 ||
                (asciiIndex >= 40 && asciiIndex <= 47) ||
                (asciiIndex >= 58 && asciiIndex <= 62)
        ) {
            nextState = LegalSpecialCharacterTransition.get( new Key(presentChar, presentState.name()) );
            if (nextState != null) {
                return nextState;
            }
            return CD19ScannerState.possibleEndOfToken;
        }

        // Current char is an IllealCharacter
        return CD19ScannerState.IllegalCharacter;
    }



}

