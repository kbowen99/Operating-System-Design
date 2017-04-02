package assemble;

public class Kword {
	String symbol;
	int memoryLocation;
	
	/**
	 * Construct a new KWord
	 * @param sym
	 * @param mem
	 */
	public Kword(String sym, int mem){
		this.symbol = sym;
		this.memoryLocation = mem;
	}
	
	/**
	 * @return KWord Memory Location
	 */
	public int getMemLoc(){
		return this.memoryLocation;
	}
	
	/**
	 * @return KWord Symbol
	 */
	public String getSymbol(){
		return this.symbol;
	}
	
	/**
	 * Returns whether or not the KWord is the specified symbol
	 * @param sym Symbol Comparison
	 * @return Symbol is equal
	 */
	public boolean matches(String sym){
		return this.getSymbol().contains(sym);
	}
	
	/**
	 * @return Binary form of memory value
	 */
	public String binaryValue(){
		return Integer.toBinaryString(this.getMemLoc());
	}
	
	/**
	 * @return Binary form with dummy bits
	 */
	public String binaryVariablevalue(){
		return "000000000000000".substring(0, (16 - this.binaryValue().length())) + this.binaryValue();
	}
}
