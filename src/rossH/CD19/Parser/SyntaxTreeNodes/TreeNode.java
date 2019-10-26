package rossH.CD19.Parser.SyntaxTreeNodes;

//	COMP3290 CD19 Compiler
//		Syntax Tree Node Class - Builds a syntax tree node
//
//
//		Check this out as you use it, my parser is well on the way but it isn't working yet.
//

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import rossH.CD19.Parser.SymbolTable.SymbolTableRecord;

public class TreeNode {

    public enum TreeNodeDataType {
        Integer,
        Real,
        Bool,
        Undefined,
        Array, // ??

        // Special datatypes
        Program,
        Procedure,
        Node
    }

    // SYNTAX TREE NODE VALUES
    // ***********************
    public static final int NUNDEF = 0,
            NPROG = 1,		NGLOB = 2,		NILIST = 3,		NINIT = 4,		NFUNCS = 5,
            NMAIN = 6,		NSDLST = 7,		NTYPEL = 8,		NRTYPE = 9,		NATYPE = 10,
            NFLIST = 11,	NSDECL = 12,	NALIST = 13,	NARRD = 14,		NFUND = 15,
            NPLIST = 16,	NSIMP = 17,		NARRP = 18,		NARRC = 19,		NDLIST = 20,
            NSTATS = 21,	NFOR = 22,		NREPT = 23,		NASGNS = 24,	NIFTH = 25,
            NIFTE = 26,		NASGN = 27,		NPLEQ = 28,		NMNEQ = 29,		NSTEQ = 30,
            NDVEQ = 31,		NINPUT = 32,	NPRINT = 33,	NPRLN = 34,		NCALL = 35,
            NRETN = 36,		NVLIST = 37,	NSIMV = 38,		NARRV = 39,		NEXPL = 40,
            NBOOL = 41,		NNOT = 42,		NAND = 43,		NOR = 44,		NXOR = 45,
            NEQL = 46,		NNEQ = 47,		NGRT = 48,		NLSS = 49,		NLEQ = 50,
            NADD = 51,		NSUB = 52,		NMUL = 53,		NDIV = 54,		NMOD = 55,
            NPOW = 56,		NILIT = 57,		NFLIT = 58,		NTRUE = 59,		NFALS = 60,
            NFCALL = 61,	NPRLST = 62,	NSTRG = 63,		NGEQ = 64;

    private static final String PRINTNODE[] = {  	//  PRINTNODE[TreeNode Value] will produce the associated String
            //  e.g. PRINTNODE[NPROG] will be the String "NPROG".
            "NUNDEF",
            "NPROG",	"NGLOB",	"NILIST",	"NINIT",	"NFUNCS",
            "NMAIN",	"NSDLST",	"NTYPEL",	"NRTYPE",	"NATYPE",
            "NFLIST",	"NSDECL",	"NALIST",	"NARRD",	"NFUND",
            "NPLIST",	"NSIMP",	"NARRP",	"NARRC",	"NDLIST",
            "NSTATS",	"NFOR",		"NREPT",	"NASGNS",	"NIFTH",
            "NIFTE",	"NASGN",	"NPLEQ",	"NMNEQ",	"NSTEQ",
            "NDVEQ",	"NINPUT",	"NPRINT",	"NPRLN",	"NCALL",
            "NRETN",	"NVLIST",	"NSIMV",	"NARRV",	"NEXPL",
            "NBOOL",	"NNOT",		"NAND",		"NOR",		"NXOR",
            "NEQL",		"NNEQ",		"NGRT",		"NLSS",		"NLEQ",
            "NADD",		"NSUB",		"NMUL",		"NDIV",		"NMOD",
            "NPOW",		"NILIT",	"NFLIT",	"NTRUE",	"NFALS",
            "NFCALL",	"NPRLST",	"NSTRG",	"NGEQ" };

    private static boolean isSetup = false;

    private static Map<TreeNodeType, Integer> nodeTypeIntMapping = new HashMap<TreeNodeType, Integer>();

    private static int count = 0;

    private TreeNodeType nodeType;
    private int nodeValue;
    private TreeNode left,middle,right;
    private SymbolTableRecord symbolRecord;
    private TreeNodeDataType dataType;


    public TreeNode (TreeNodeType nValue) {
        nodeType = nValue;
        nodeValue = nodeTypeIntMapping.get(nodeType);
        left = null;
        middle = null;
        right = null;
        symbolRecord = null;
        dataType = null;
    }

    public TreeNode (TreeNodeType nValue, SymbolTableRecord st) {
        this(nValue);
        this.symbolRecord = st;
    }

    public TreeNode (TreeNodeType nValue,  TreeNode l, TreeNode r) {
        this(nValue);
        this.left = l;
        this.right = r;
    }

    public TreeNode (TreeNodeType nValue, TreeNode l, TreeNode m, TreeNode r) {
        this(nValue, l, r);
        this.middle = m;
    }

    public TreeNodeType getNodeType() {
        return this.nodeType;
    }

    public int getValue() {
        return this.nodeValue;
    }

    public TreeNode getLeft() {
        return this.left;
    }

    public TreeNode getMiddle() {
        return this.middle;
    }

    public TreeNode getRight() {
        return this.right;
    }

    public SymbolTableRecord getSymbolRecord() {
        return this.symbolRecord;
    }

    //public StRec getType() { return type; }

    public void setValue (TreeNodeType nValue) {
        this.nodeType = nValue;
        this.nodeValue = nodeTypeIntMapping.get(nodeType);
    }

    public void setLeft(TreeNode l) {
        this.left = l;
    }

    public void setMiddle(TreeNode m) {
        this.middle = m;
    }

    public void setRight(TreeNode r) {
        this.right = r;
    }

    public void setSymbolRecord(SymbolTableRecord stRec) {
        this.symbolRecord = stRec;
    }

    public static void setup () {
        if (isSetup) {
            return;
        }

        nodeTypeIntMapping.put(TreeNodeType.NUNDEF, NUNDEF);
        nodeTypeIntMapping.put(TreeNodeType.NPROG, NPROG);
        nodeTypeIntMapping.put(TreeNodeType.NGLOB, NGLOB);
        nodeTypeIntMapping.put(TreeNodeType.NILIST, NILIST);
        nodeTypeIntMapping.put(TreeNodeType.NINIT, NINIT);
        nodeTypeIntMapping.put(TreeNodeType.NFUNCS, NFUNCS);
        nodeTypeIntMapping.put(TreeNodeType.NMAIN, NMAIN);
        nodeTypeIntMapping.put(TreeNodeType.NSDLST, NSDLST);
        nodeTypeIntMapping.put(TreeNodeType.NTYPEL, NTYPEL);
        nodeTypeIntMapping.put(TreeNodeType.NRTYPE, NRTYPE);
        nodeTypeIntMapping.put(TreeNodeType.NATYPE, NATYPE);
        nodeTypeIntMapping.put(TreeNodeType.NFLIST, NFLIST);
        nodeTypeIntMapping.put(TreeNodeType.NSDECL, NSDECL);
        nodeTypeIntMapping.put(TreeNodeType.NALIST, NALIST);
        nodeTypeIntMapping.put(TreeNodeType.NARRD, NARRD);
        nodeTypeIntMapping.put(TreeNodeType.NFUND, NFUND);
        nodeTypeIntMapping.put(TreeNodeType.NPLIST, NPLIST);
        nodeTypeIntMapping.put(TreeNodeType.NSIMP, NSIMP);
        nodeTypeIntMapping.put(TreeNodeType.NARRP, NARRP);
        nodeTypeIntMapping.put(TreeNodeType.NARRC, NARRC);
        nodeTypeIntMapping.put(TreeNodeType.NDLIST, NDLIST);
        nodeTypeIntMapping.put(TreeNodeType.NSTATS, NSTATS);
        nodeTypeIntMapping.put(TreeNodeType.NFOR, NFOR);
        nodeTypeIntMapping.put(TreeNodeType.NREPT, NREPT);
        nodeTypeIntMapping.put(TreeNodeType.NASGNS, NASGNS);
        nodeTypeIntMapping.put(TreeNodeType.NIFTH, NIFTH);
        nodeTypeIntMapping.put(TreeNodeType.NIFTE, NIFTE);
        nodeTypeIntMapping.put(TreeNodeType.NASGN, NASGN);
        nodeTypeIntMapping.put(TreeNodeType.NPLEQ, NPLEQ);
        nodeTypeIntMapping.put(TreeNodeType.NMNEQ, NMNEQ);
        nodeTypeIntMapping.put(TreeNodeType.NSTEQ, NSTEQ);
        nodeTypeIntMapping.put(TreeNodeType.NDVEQ, NDVEQ);
        nodeTypeIntMapping.put(TreeNodeType.NINPUT, NINPUT);
        nodeTypeIntMapping.put(TreeNodeType.NPRINT, NPRINT);
        nodeTypeIntMapping.put(TreeNodeType.NPRLN, NPRLN);
        nodeTypeIntMapping.put(TreeNodeType.NCALL, NCALL);
        nodeTypeIntMapping.put(TreeNodeType.NRETN, NRETN);
        nodeTypeIntMapping.put(TreeNodeType.NVLIST, NVLIST);
        nodeTypeIntMapping.put(TreeNodeType.NSIMV, NSIMV);
        nodeTypeIntMapping.put(TreeNodeType.NARRV, NARRV);
        nodeTypeIntMapping.put(TreeNodeType.NEXPL, NEXPL);
        nodeTypeIntMapping.put(TreeNodeType.NBOOL, NBOOL);
        nodeTypeIntMapping.put(TreeNodeType.NNOT, NNOT);
        nodeTypeIntMapping.put(TreeNodeType.NAND, NAND);
        nodeTypeIntMapping.put(TreeNodeType.NOR, NOR);
        nodeTypeIntMapping.put(TreeNodeType.NXOR, NXOR);
        nodeTypeIntMapping.put(TreeNodeType.NEQL, NEQL);
        nodeTypeIntMapping.put(TreeNodeType.NNEQ, NNEQ);
        nodeTypeIntMapping.put(TreeNodeType.NGRT, NGRT);
        nodeTypeIntMapping.put(TreeNodeType.NLSS, NLSS);
        nodeTypeIntMapping.put(TreeNodeType.NLEQ, NLEQ);
        nodeTypeIntMapping.put(TreeNodeType.NADD, NADD);
        nodeTypeIntMapping.put(TreeNodeType.NSUB, NSUB);
        nodeTypeIntMapping.put(TreeNodeType.NMUL, NMUL);
        nodeTypeIntMapping.put(TreeNodeType.NDIV, NDIV);
        nodeTypeIntMapping.put(TreeNodeType.NMOD, NMOD);
        nodeTypeIntMapping.put(TreeNodeType.NPOW, NPOW);
        nodeTypeIntMapping.put(TreeNodeType.NILIT, NILIT);
        nodeTypeIntMapping.put(TreeNodeType.NFLIT, NFLIT);
        nodeTypeIntMapping.put(TreeNodeType.NTRUE, NTRUE);
        nodeTypeIntMapping.put(TreeNodeType.NFALS, NFALS);
        nodeTypeIntMapping.put(TreeNodeType.NFCALL, NFCALL);
        nodeTypeIntMapping.put(TreeNodeType.NPRLST, NPRLST);
        nodeTypeIntMapping.put(TreeNodeType.NSTRG, NSTRG);
        nodeTypeIntMapping.put(TreeNodeType.NGEQ, NGEQ);
    };

    //public void setType(StRec st) { type = st; }


    //
    // Call is: TreeNode.printTree(outfile, rootOfTree);
    //	-> prints tree pre-order as a flat 7 values per line
    //
    //   I am used to this type of print - if you cannot use
    //	it then you are free to implement your own XML or
    //	whatever you like tree output routine.
    //

    private static BufferedWriter xmlFileWriter;

    public static void setXmlFileWriter (BufferedWriter fileWriter) {
        xmlFileWriter = fileWriter;
    }

    public static void printTree(TreeNode tr, String nodePos) throws IOException {
        if (tr.nodeValue == NUNDEF) {
            return;
        }

        if (tr.nodeValue == NPROG) {
            if (xmlFileWriter != null) {
                xmlFileWriter.append("<root>\n");
            }
            count = 0;
        }
        ;
        if (xmlFileWriter != null) {
            xmlFileWriter.append("<nodeType value=\"" + PRINTNODE[tr.nodeValue] + " \"/>\n");
        }
        System.out.print(PRINTNODE[tr.nodeValue] + " ");
        count++;

        if (count % 7 == 0)  {
            System.out.println();
        }

        if (tr.getSymbolRecord() != null) {
            String toPrint = tr.getSymbolRecord().getLexeme();
            System.out.print(toPrint + " ");
            if (xmlFileWriter != null) {
                String toPrintXml = toPrint.replace("\"", "");
                xmlFileWriter.append("<nodeSymbolValue value=\"" + toPrintXml + " \"/>\n");
            }
            count++;
            if (count % 7 == 0) {
                System.out.println();
            };
        }

        /*if (tr.getNodeType()  != null) {
            out.print(  tr.type.getName() + " ");
            count++;
            if (count%7 == 0) out.println();
        }*/

        // pre-order traversal of syntax tree
        if (tr.left   != null) {
            if (xmlFileWriter != null) {
                xmlFileWriter.append("<child which=\"left\">\n");
            }

            printTree(tr.left, "left");

            if (xmlFileWriter != null) {
                xmlFileWriter.append("</child>\n");
            }
        }

        if (tr.middle != null) {
            if (xmlFileWriter != null) {
                xmlFileWriter.append("<child which=\"middle\">\n");
            }

            printTree(tr.middle, "middle");

            if (xmlFileWriter != null) {
                xmlFileWriter.append("</child>\n");
            }
        }

        if (tr.right  != null) {
            if (xmlFileWriter != null) {
                xmlFileWriter.append("<child which=\"right\">\n");
            }

            printTree(tr.right, "right");

            if (xmlFileWriter != null) {
                xmlFileWriter.append("</child>\n");
            }
        }

        if (tr.nodeValue == NPROG && count % 7 != 0) {
            System.out.println();
        };

        if (tr.nodeValue == NPROG) {
            if (xmlFileWriter != null) {
                xmlFileWriter.append("</root>");
            }
        }
    }
}