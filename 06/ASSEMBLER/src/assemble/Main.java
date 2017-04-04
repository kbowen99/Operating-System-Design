package assemble;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	static String[] asmDict = {
			"D=A","D=A+1","D=A-1","D=M",
			"D=M+1","D=M-1","D=1","D=0",
			"D=D+1","D=D-1","M=D","M=D+1",
			"M=D-1","M=A","M=A+1","M=A-1",
			"M=1","M=0","M=M+1","M=M-1",
			"A=D","A=D+1","A=D-1","A=M",
			"A=M+1","A=M-1","A=1","A=0",
			"A=A+1","A=A-1","D=D+A","D=A+D",
			"A=D+A","A=A+D","D=D-M","D=M-D",
			"D=A-D","D=D-A","AM=M-1","AM=M+1",
			"M=-1","A=M-D","AM=D-1","D=D+M",
			"MD=M+1","MD=M-1","M=M-D","M=M+D",
			"M=!M","D=!D","A=!A","M=D+M",
			"M=D&M","M=D|M","AD=A+1","D=!M",
	};
	static String[] binDict = {
			"1110110000010000","1110110111010000","1110110010010000","1111110000010000",
			"1111110111010000","1111110010010000","1110111111010000","1110101010010000",
			"1110011111010000","1110001110010000","1110001100001000","1110011111001000",
			"1110001110001000","1110110000001000","1110110111001000","1110110010001000",
			"1110111111001000","1110101010001000","1111110111001000","1111110010001000",
			"1110001100100000","1110011111100000","1110001110100000","1111110000100000",
			"1111110111100000","1111110010100000","1110111111100000","1110101010100000",
			"1110110111100000","1110110010100000","1110000010010000","1110000010010000",
			"1110000010100000","1110000010100000","1111010011010000","1111000111010000",
			"1110000111010000","1110010011010000","1111110010101000","1111110111101000",
			"1110111010001000","1111000111100000","1110001110101000","1111000010010000",
			"1111110111011000","1111110010011000","1111000111001000","1111000010001000",
			"1111110001001000","1110001101010000","1110110001100000","1111000010001000",
			"1111000000001000","1111010101001000","1110110111110000","1111110001010000"
	};
	
	static ArrayList<String> instructionList = new ArrayList<>();
	static ArrayList<Kword> symbolList = new ArrayList<>();
	static Scanner sc = new Scanner(System.in);
	
	static int currVariable = 16;
	static int currLoop = 1;
	
	public static void main(String[] args) {
		try {
				System.setOut(new PrintStream(new FileOutputStream("out.hack")));
		} catch (Exception e) {}
		
		{
			String instruction;
			int loopLine = 0;
			while ((instruction = sc.nextLine()).length() > 0){
				if (instruction.contains("//"))
					instruction = instruction.substring(0, instruction.indexOf("//"));
				
				//Dumb User Check: People who don't use tabs...
				instruction = instruction.trim().replace(" ", "");
				
				//Loop Check
				if (instruction.contains("(")){
					//getOrSetKword((instruction.trim().substring(1,instruction.trim().length() - 1)),true).binaryVariablevalue();
					symbolList.add(new Kword((instruction.trim().substring(1,instruction.trim().length() - 1)), loopLine));
				} else {
					instructionList.add(instruction);
					if (instruction.length() > 0)
						loopLine++;
				}
			}
		}
		
		for (String instruction : instructionList){
			//System.out.println(instruction);//Dumb User Check: People Who Comment at the end of the line..
			//if (instruction.contains("//"))
			//	instruction = instruction.substring(0, instruction.indexOf("//"));
			
			//Dumb User Check: People who don't use tabs...
			//instruction = instruction.trim().replace(" ", "");
			
			
			
			if (instruction.trim().length() > 2 && instruction.trim().substring(0, 3).contains("//"))//if commented out
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
				else if (instruction.contains("@R") && instruction.trim().toCharArray()[2] < 58)
					System.out.println((new Kword("",(Integer.parseInt(instruction.trim().substring(2))))).binaryVariablevalue());
				else if (instruction.trim().toCharArray()[1] < 58)//if num
					System.out.println((Kword.toBinaryVariablevalue(Integer.parseInt(instruction.trim().substring(1)))));
				else
					System.out.println(getOrSetKword((instruction.trim().substring(1)),false).binaryVariablevalue());
			else if (instruction.trim().contains(";J")){
				char S = instruction.trim().charAt(0);
				System.out.print("111" + (S=='M' ? "1" : "0") + (S=='D' ? "0" : "1") + (S=='D'||S=='0' ? "0" : "1") + (S=='A'||S=='M' ? "0" : "1"));
				System.out.print((S=='D'||S=='1' ? "1" : "0") + (S=='0'||S=='1' ? "1" : "0") + (S=='1' ? "1" : "0"));
				System.out.println((instruction.trim().contains("JGT") ? "000001" : 
					(instruction.trim().contains("JMP") ? "000111" : 
					(instruction.trim().contains("JNE") ? "000101" : 
					(instruction.trim().contains("JLE") ? "000110" : 
					(instruction.trim().contains("JGE") ? "000011" : "-"))))));
			}
//			else if (instruction.trim().contains("("))//Loops
//				getOrSetKword((instruction.trim().substring(1,instruction.trim().length() - 1)),true).binaryVariablevalue();
			else
				for (int i = 0; i < asmDict.length; i++)
					if (asmDict[i].equals(instruction))
						System.out.println(binDict[i]);
					
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
