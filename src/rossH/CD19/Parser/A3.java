
package rossH.CD19.Parser;

import com.sun.source.tree.Tree;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Scanner.CD19Scanner;
import rossH.CD19.Scanner.Token;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class A3 {
    public static void main(String[] args) {

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
        while (!scanner.eof()) {
            token = scanner.getToken();
            scanner.printToken(token);
            tokens.add(token);
        }

        System.out.println();
        System.out.println();

        // transform tokens into a synatax tree
        CD19Parser parser = new CD19Parser(tokens);
        TreeNode NPROG = parser.parse();
        System.out.println();
        System.out.println();

        try {
            BufferedWriter xmlFileWriter = new BufferedWriter(new FileWriter("treeOutput.xml", false));
            TreeNode.setXmlFileWriter(xmlFileWriter);
            xmlFileWriter.write("");
            TreeNode.printTree(NPROG, "");
            xmlFileWriter.close();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
        // print out a <CERTAIN> traversal of the syntax tree

    }

}