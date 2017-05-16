package jack;

import java.util.HashMap;

/**
 * Pretty much just a fancy wrapper for hashmaps
 * I stole this idea from stackoverflow, it seemed like a fun implementation.
 * should this be static?
 * @author kurti
 *
 */
public class SymbolTable {
	
	/**
	 * STATIC, FIELD
	 */
	private HashMap<String,Symbol> stat_Field;
	/**
	 * var
	 */
	private HashMap<String,Symbol> symbols;
	/**
	 * index mapping
	 */
	private HashMap<Symbol.type,Integer> indexes;
	    
	/**
	 * Constructs a new symbol table
	 */
	public SymbolTable() {
		stat_Field = new HashMap<String, Symbol>();
	    symbols = new HashMap<String, Symbol>();
	    indexes = new HashMap<Symbol.type, Integer>();
	    indexes.put(Symbol.type.ARG,0);
	    indexes.put(Symbol.type.FIELD,0);
	    indexes.put(Symbol.type.STATIC,0);
	    indexes.put(Symbol.type.VAR,0);
	
	}
	
	/**
	 * Starts SYmboling sUB
	 */
	public void startSubroutine(){
	    symbols.clear();
	    indexes.put(Symbol.type.VAR,0);
	    indexes.put(Symbol.type.ARG,0);
	}
	
	/**
	 * creates Symbol for
	 * @param name
	 * @param type
	 * @param kind
	 */
	public void define(String name, String type, Symbol.type kind){
	    if (kind == Symbol.type.ARG || kind == Symbol.type.VAR){
	        int tmp = indexes.get(kind);
	        Symbol symbol = new Symbol(type,kind,tmp);
	        indexes.put(kind,tmp+1);
	        symbols.put(name,symbol);
	    }else if(kind == Symbol.type.STATIC || kind == Symbol.type.FIELD){
	        int index = indexes.get(kind);
	        Symbol symbol = new Symbol(type,kind,index);
	        indexes.put(kind,index+1);
	        stat_Field.put(name,symbol);
	    }
	}
	
	/**
	 * @param kind
	 * @return Count of That Kind
	 */
	public int varCount(Symbol.type kind){
	    return indexes.get(kind);
	}

	/*
     * @return what kind
     */
    public Symbol.type kindOf(String name){
        if (lookUp(name) != null)
        	return lookUp(name).getT();
        return Symbol.type.NONE;
    }
	
	/**
	 * @return what type
	 */
	public String typeOf(String name){
	    if (lookUp(name) != null) 
	    	return lookUp(name).getVariety();
	    return "";
	}

    /**
     * @return index of
     */
    public int indexOf(String name){
        if (lookUp(name) != null)
        	return lookUp(name).getIndex();
        return -1;
    }

    /**
     * @return that symbol
     */
    private Symbol lookUp(String name){
        if (stat_Field.get(name) != null)
            return stat_Field.get(name);
        else if (symbols.get(name) != null)
            return symbols.get(name);
        else 
            return null;
        

    }
}
