package jack;

public class Symbol {
	
	public static enum type {
		STATIC, FIELD, ARG, VAR, NONE
	};
	
	String variety;
	type t;
	int index;
	
	/**
	 * Construct a Symbol Object
	 * @param variety
	 * @param t
	 * @param index
	 */
	public Symbol(String variety, type t, int index){
		this.variety = variety;
		this.t = t;
		this.index = index;
	}
	
	public String toString(){
		return "{" + "type='" + variety + '\'' +
                ", kind=" + t +", index=" + index + '}';
	}

	/**
	 * @return the variety
	 */
	public String getVariety() {
		return variety;
	}

	/**
	 * @param variety the variety to set
	 */
	public void setVariety(String variety) {
		this.variety = variety;
	}

	/**
	 * @return the t
	 */
	public type getT() {
		return t;
	}

	/**
	 * @param t the t to set
	 */
	public void setT(type t) {
		this.t = t;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}
