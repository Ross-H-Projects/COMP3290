/*
 *  Author: Ross Hurley
 *  Last edited: 1/09/2019
 *  Made for COMP3290.
 */

/*
 *   This program defines the main entry for e scanner component and as the chasis
 *   for CD19Scanner.java
 *   made for COMP3290
 */


package rossH.CD19.Scanner;


import java.nio.file.Files;
import java.nio.file.Paths;

public class A1 {

    public static void main(String[] args) {
        // grab input source file text
        String sourceFileName = null;
        String sourceText = null;
        try {
            sourceFileName = args[0];
            sourceText = new String(Files.readAllBytes(Paths.get(sourceFileName)));
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

        // get tokens
        CD19Scanner scanner = new CD19Scanner(sourceText);
        Token token;
        while (!scanner.eof()) {
            token = scanner.getToken();
            scanner.printToken(token);
        }
    }
}
