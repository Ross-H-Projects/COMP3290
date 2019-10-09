package rossH.CD19.Parser.SymbolTable;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<Integer, SymbolTableRecord> symbols;

    public SymbolTable () {
        symbols = new HashMap<Integer, SymbolTableRecord>();
    }

    public void setSymbolTableRecord (SymbolTableRecord STRec) {
        symbols.put(STRec.hashCode(), STRec);
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
        // stub
        return true;
    }
}