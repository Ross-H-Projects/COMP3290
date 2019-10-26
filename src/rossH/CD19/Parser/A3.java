

import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Scanner.CD19Scanner;
import rossH.CD19.Scanner.Token;
import rossH.CD19.Parser.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class A3 {
    public static void main(String[] args) {
        boolean debug = true;

        // get source program content
        String sourceFileName = null;
        String sourceText = null;
        try {
            sourceFileName = args[0];
            sourceText = new String(Files.readAllBytes(Paths.get(sourceFileName)));
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

        // transform source program into tokens
        CD19Scanner scanner = new CD19Scanner(sourceText);
        List<Token> tokens = new LinkedList<>();
        Token token;
        boolean isLexicalErrorPresent = false;
        while (!scanner.eof()) {
            token = scanner.getToken();
            if (token.value() == Token.TUNDF) {
                isLexicalErrorPresent = true;
                break;
            }
            tokens.add(token);
        }

        // don't do syntax analysis if lexical analysis failed
        if (isLexicalErrorPresent) {
            System.out.println("Error(s) occured while performing lexical analysis. Not going on to perform Syntax analysis.");
            System.out.println("Ending program...");
            return;
        }

        // transform tokens into a synatax tree
        CD19Parser parser = new CD19Parser(tokens);
        TreeNode NPROG = parser.parse();


        try {
            BufferedWriter lstFileWriter = new BufferedWriter(new FileWriter("output.lst", false));
            TreeNode.setLstFileWriter(lstFileWriter);
            lstFileWriter.write("");

            if (debug) {
                // write parsed grammar to screen and
                // output.lst file and treeOutput.xml
                BufferedWriter xmlFileWriter = new BufferedWriter(new FileWriter("treeOutput.xml", false));
                TreeNode.setXmlFileWriter(xmlFileWriter);
                xmlFileWriter.write("");
                TreeNode.printTree(NPROG, "");
                xmlFileWriter.close();
            } else {
                // write parsed grammar to screen and
                // output.lst file
                TreeNode.printTree(NPROG, "");
            }


            // print syntax tree parsing errors to screen
            // and output.lst file
            List<String> syntaxErrors = parser.getSyntaxErrors();
            if (syntaxErrors.size() > 0) {
                System.out.print("\n\nErrors:\n\n");
                lstFileWriter.append("\n\nErrors:\n\n");
            }

            for (int i = 0; i < syntaxErrors.size(); i++) {
                System.out.println(syntaxErrors.get(i));
                lstFileWriter.append(syntaxErrors.get(i));
            }
            lstFileWriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}