package rossH.CD19.Scanner;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CD19ScannerChasis {

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
            token = scanner.gettoken();
            scanner.printtoken(token);
        }
    }
}
