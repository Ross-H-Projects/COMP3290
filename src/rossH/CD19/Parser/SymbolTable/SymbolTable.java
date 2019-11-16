package rossH.CD19.Parser.SymbolTable;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<Integer, SymbolTableRecord> symbols;

    private int currentBaseRegister1OffsetPositive;
    private int currentBaseRegister1OffsetNegative;

    public SymbolTable () {
        symbols = new HashMap<Integer, SymbolTableRecord>();

        currentBaseRegister1OffsetNegative = -8;
        currentBaseRegister1OffsetPositive = 16;
    }

    public void setSymbolTableRecord (SymbolTableRecord STRec) {
        try {
            symbols.put(STRec.hashCode(), STRec);
        } catch (Exception e) {
            int a = 1;
        }
    }

    public SymbolTableRecord getSymbolTableRecord (int key) {
        return symbols.get(key);
    }

    public SymbolTableRecord getSymbolTableRecord (SymbolTableRecord stRec) {
        return symbols.get(stRec.hashCode());
    }

    public boolean contains (int key) {
        return symbols.containsKey(key);
    }

    public boolean contains (SymbolTableRecord stRec) {
        return symbols.containsKey(stRec.hashCode());
    }

    public int getBaseReigtser1OffsetPositive() {

        int oldBaseRegisterOffsetPositive = currentBaseRegister1OffsetPositive;
        currentBaseRegister1OffsetPositive += 8;

        return oldBaseRegisterOffsetPositive;
    }

    public int getBaseReigtser1OffsetNegative() {

        int oldBaseRegisterOffsetNegative = currentBaseRegister1OffsetNegative;
        currentBaseRegister1OffsetNegative -= 8;

        return oldBaseRegisterOffsetNegative;
    }

    public void printSymbolTableRecords () {
        for (SymbolTableRecord  str : symbols.values()) {
            System.out.println(str.getLexeme() + ", Base Reg: " + str.getBaseRegister() + ", Offset: " + str.getOffset());
        }
    }
}