package rossH.CD19.codegen;

import com.sun.source.tree.Tree;
import rossH.CD19.Parser.CD19Parser;
import rossH.CD19.Parser.SymbolTable.SymbolDataType;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNode;
import rossH.CD19.Parser.SyntaxTreeNodes.TreeNodeType;

import java.beans.Expression;
import java.util.List;

public class FunctionsGenerator {
    public static void generateCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {


    }

    public static void generateFunctionCode (TreeNode treeNode, CD19CodeGenerator codeGenerator) {
        // take note of the amount of op codes for where this function body
        // is starting to be generated
        // add a mapping in CD19CodGenerator of function names to their op code pos

        // todo
        //  CHECK IF we don't need to generate any op codes for the parameters because this is done
        //  when the function is called

        // allocate space on top of the call frame stack for the local variables
    }
}