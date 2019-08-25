package rossH.CD19.Scanner;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class CD19ScannerChasis {

    public static void main(String[] args) {
        // grab input source file text
        String sourceFileName = null;
        String sourceText = null;
        BufferedInputStream sourceFileStream = null;
        try {
            sourceFileName = args[0];
            sourceText = new String( (new FileInputStream(sourceFileName)).readAllBytes() );
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

        // get tokens
        CD19Scanner scanner = new CD19Scanner(sourceText);
        Token token;
        while (!scanner.eof()) {
            token = scanner.gettoken();
            scanner.printtoken(token);
        }

        scanner.arb();

        // close input source file
        if (sourceFileStream != null) {
            try {
                sourceFileStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
