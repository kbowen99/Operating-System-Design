package vm;

import java.io.File;
import java.util.Scanner;

/**
 * Follows the specification given in Nand2Tetris. I'm not a huge fan of the setup, but it works
 * @author Kurtis Bowen (@Kbowen99)
 *
 */
public class Parser {
	
	public enum CMD {
		MATHS,PUSH,POP,LABEL,GOTO,IF,FUNCTION,RETURN,CALL,NONE,
	};
	
	public enum MATH{
		ADD,SUB,NEG,EQ,GT,LT,AND,OR,NOT,NONE,
	}
	
	private Scanner commandScan;//Reminds me of robotics...
	private String currCommand;
	private CMD commandType = CMD.NONE;
	private MATH commandMath = MATH.NONE;
	private String arg1 = "";
	private int arg2 = -1;
	
	/**
	 * Constructs a new Parser Object
	 */
	public Parser(File input){
		try {
			commandScan = new Scanner(input);
			
			/*
			 * Uncomments the file
			 */
			String newSource = "";
			for  (String line = ""; commandScan.hasNextLine();line = commandScan.nextLine()){
				if (line.contains("//"))
					line = line.substring(0, line.indexOf("//"));
				if (line.length() > 0)
					newSource += line + "\n";
			}
			
			commandScan = new Scanner(newSource.trim());
		} catch (Exception e) {
			System.out.println("File Failed: " + input.getAbsolutePath());
		}
	}
	
	/**
	 * Advances to next command in file. Who Would've Guessed?
	 */
	public void advance(){
		commandType = CMD.NONE;
		commandMath = MATH.NONE;
		arg1 = "";
		arg2 = -1;
		currCommand = commandScan.nextLine();
		String[] c = currCommand.split(" "); //The Command, in its parts
		
		if (c[0].equals("add")){
			commandType = CMD.MATHS;
			commandMath = MATH.ADD;
		} else if (c[0].equals("sub")){
			commandType = CMD.MATHS;
			commandMath = MATH.SUB;
		} else if (c[0].equals("neg")){
			commandType = CMD.MATHS;
			commandMath = MATH.NEG;
		} else if (c[0].equals("eq")){
			commandType = CMD.MATHS;
			commandMath = MATH.EQ;
		} else if (c[0].equals("gt")){
			commandType = CMD.MATHS;
			commandMath = MATH.GT;
		} else if (c[0].equals("and")){
			commandType = CMD.MATHS;
			commandMath = MATH.AND;
		} else if (c[0].equals("or")){
			commandType = CMD.MATHS;
			commandMath = MATH.OR;
		} else if (c[0].equals("not")){
			commandType = CMD.MATHS;
			commandMath = MATH.NOT;
		} else if (c[0].equals("return")){
			commandType = CMD.RETURN;
			arg1 = c[0];
		} else {
			arg1 = c[1];
			
			if (c[0].equals("push"))
				commandType = CMD.PUSH;
			else if (c[0].equals("pop"))
				commandType = CMD.POP;
			else if (c[0].equals("label"))
				commandType = CMD.LABEL;
			else if (c[0].equals("if-goto"))
				commandType = CMD.IF;
			else if (c[0].equals("goto"))
				commandType = CMD.GOTO;
			else if (c[0].equals("function"))
				commandType = CMD.FUNCTION;
			else if (c[0].equals("call"))
				commandType = CMD.CALL;
			else
				throw new IllegalArgumentException("Something Broke. That wasnt a valid command");
			
			if ("push pop function call".contains(c[0]))
				arg2 = Integer.parseInt(c[2]);
		}
	}
	
	/**
	 * Helper Method. Finds String in array
	 * @param arr - Array to search
	 * @param toFind - String to find
	 * @return - Index of String
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private int sArrayIndex(String[] arr, String toFind){
		for (int i = 0; i < arr.length; i++)
			if (arr[i].equals(toFind))
				return i;
		return -1;
	}
	
	/**
	 * Returns whether or not there are more commands
	 * Just following the Spec Sheet.
	 */
	public boolean hasMoreCommands(){
		return commandScan.hasNext();
	}
	
	/**
	 * @return Current CommandType (From Enumeration CMD)
	 */
	public CMD getCommand(){
		return this.commandType;
	}
	
	/**
	 * @return First Argument of current command
	 */
	public String getArg1(){
		if (this.commandType != CMD.RETURN)
			return this.arg1;
		throw new IllegalStateException("No");
	}
	
	/**
	 * @return Second Argument of Current Command
	 */
	public int getArg2(){
		if (this.commandType == CMD.POP ||this.commandType == CMD.PUSH ||this.commandType == CMD.FUNCTION ||this.commandType == CMD.CALL)
			return this.arg2;
		throw new IllegalStateException("No");
	}
	
	/**
	 * @return Current Commands Math Type
	 */
	public MATH getMath(){
		return this.commandMath;
	}
}
