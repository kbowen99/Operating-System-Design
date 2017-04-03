package assemble;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	static ArrayList<String> instructionList = new ArrayList<>();
	static ArrayList<Kword> symbolList = new ArrayList<>();
	static Scanner sc = new Scanner(System.in);
	
	static int currVariable = 16;
	static int currLoop = 1;
	
	public static void main(String[] args) {
		String tmp;
		while ((tmp = sc.nextLine()).length() > 0)
			instructionList.add(tmp);
		
		for (String instruction : instructionList){
			if (instruction.length() > 2 && instruction.trim().substring(0, 3).contains("//"))//if commented out
				System.out.print("");
			else if (instruction.trim().contains("@"))//Variables
				if (instruction.contains("KBD"))
					System.out.println((new Kword("KBD",24576)).binaryVariablevalue());
				else if (instruction.contains("SCREEN"))
					System.out.println((new Kword("SCREEN",16384)).binaryVariablevalue());
				else if (instruction.contains("SP"))
					System.out.println(Kword.toBinaryVariablevalue(0));
				else if (instruction.contains("LCL"))
					System.out.println(Kword.toBinaryVariablevalue(1));
				else if (instruction.contains("ARG"))
					System.out.println(Kword.toBinaryVariablevalue(2));
				else if (instruction.contains("THIS"))
					System.out.println(Kword.toBinaryVariablevalue(3));
				else if (instruction.contains("THAT"))
					System.out.println(Kword.toBinaryVariablevalue(4));
				else if (instruction.contains("@R"))
					System.out.println((new Kword("",(Integer.parseInt(instruction.trim().substring(2))))).binaryVariablevalue());
				else if (instruction.trim().toCharArray()[1] < 58)//if num
					System.out.println((Kword.toBinaryVariablevalue(Integer.parseInt(instruction.trim().substring(1)))));
				else
					System.out.println(getOrSetKword((instruction.trim().substring(1)),false).binaryVariablevalue());
			else if (instruction.trim().contains("("))
				getOrSetKword((instruction.trim().substring(1,instruction.trim().length() - 1)),true).binaryVariablevalue();
		}
	}
	
	/**
	 * if Kword already exists, return it, else make it
	 * @param SYM - Symbol for dictionary
	 * @return Kword
	 */
	private static Kword getOrSetKword(String SYM, boolean isLoop){
		if (findKwordIndex(SYM) >= 0)
			return symbolList.get(findKwordIndex(SYM));
		else{
			symbolList.add(new Kword(SYM, (isLoop ? currLoop++ : currVariable++)));
			return symbolList.get(symbolList.size() - 1);
		}
	}
	
	/**
	 * Helper Method. Returns index of Kword matching symbol
	 * @param sym - Symbol to match
	 * @return Index of Kword in symbolList (or -1)
	 */
	private static int findKwordIndex(String sym){
		//System.out.println(sym);
		for (int i = 0; i < symbolList.size(); i++)
			if (symbolList.get(i).matches(sym))
				return i;
		return -1;
	}
}
