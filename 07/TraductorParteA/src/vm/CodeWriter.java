package vm;

import java.io.File;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class CodeWriter {
	private int arthJumpFlag;
    private PrintWriter OUT;
    private static int labelCnt = 0;
    private static String dir = "";
    
    public CodeWriter(File f){
    	try {
			dir = f.getName();
		    OUT = new PrintWriter(f);
		    arthJumpFlag = 0;
		    doBootStrap();
    	} catch (Exception e) {}
    }
    
    /**
     * Change file name, for use with directories (mult files)
     * @param f File w/Directory
     */
    public void setFileName(File f){
    	dir = f.getName();
    }
    
    /**
     * Write Assembly from math command
     * @param m Math to Translate
     */
    public void writeMaths(Parser.MATH m){
    	switch (m){
		case ADD:
		case SUB:
		case AND:
		case OR:
			OUT.print("@SP\n" +
	                "AM=M-1\n" +
	                "D=M\n" +
	                "A=A-1\n" + 
	                (m==Parser.MATH.ADD ? "M=M+D\n" : 
	                (m==Parser.MATH.SUB ? "M=M-D\n" : 
	                (m==Parser.MATH.AND ? "M=M&D\n" : "M=M|D\n"))));
			break;
		case GT:
		case LT:
		case EQ:
			OUT.print("@SP\n" +
	                "AM=M-1\n" +
	                "D=M\n" +
	                "A=A-1\n" +
	                "D=M-D\n" +
	                "@FALSE" + arthJumpFlag + "\n" +
	                "D;" + (m==Parser.MATH.GT ? "JLE" : 
	                (m==Parser.MATH.LT ? "JGE" : "JNE")) + "\n" +
	                "@SP\n" +
	                "A=M-1\n" +
	                "M=-1\n" +
	                "@CONTINUE" + arthJumpFlag + "\n" +
	                "0;JMP\n" +
	                "(FALSE" + arthJumpFlag + ")\n" +
	                "@SP\n" +
	                "A=M-1\n" +
	                "M=0\n" +
	                "(CONTINUE" + arthJumpFlag + ")\n");
			break;
		case NOT:
			OUT.print("@SP\n"
					+ "A=M-1\n"
					+ "M=!M\n");
			break;
		case NEG:
			OUT.print("D=0\n"
					+ "@SP\n"
					+ "A=M-1\n"
					+ "M=D-M\n");
				break;
		case NONE:
			break;
		default:
			break;
    	
    	}
    }
    
    /**
     * Writes Out Push/Pop
     * @param c Command
     * @param s Segment
     * @param index
     */
    public void writePushPop(Parser.CMD c, String s, int index){
		switch (s){
		case "constant":
			OUT.print("@" + index + "\n" 
						+ "D=A\n"
						+ "@SP\n"
						+ "A=M\n"
						+ "M=D\n"
						+ "@SP\n"
						+ "M=M+1\n");
			break;
		case "local":
		case "argument":
		case "this":
		case "that":
			String tmp = (s.equals("local") ? "LCL" : (s.equals("argument") ? "ARG" : (s.equals("this") ? "THIS" : "THAT")));
			OUT.print((c==Parser.CMD.PUSH ? ASMPush(tmp,index,false) : ASMPop(tmp,index,false)));
			break;
		case "temp":
			OUT.print((c==Parser.CMD.PUSH ? ASMPush("R5", index + 5,false) : ASMPop("R5", index + 5,false)));
			break;
		case "pointer":
			OUT.print((c==Parser.CMD.PUSH ? ASMPush((index==0 ? "THIS" : "THAT"),index,true) : ASMPop((index==0 ? "THIS" : "THAT"),index,true)));
			break;
		case "static":
			if (c == Parser.CMD.POP)
				OUT.print("@" + dir + index + "\n" + "D=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
			else
				OUT.print("@" + dir + index + "\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
		default:
			break;
		}
    }
    
    /**
     * Helper Method
     * Filters Label Output for Label/Goto/etc
     * @param prefix
     * @param label
     * @param Suffix
     */
    private void filterOut(String prefix, String label, String suffix){
    	if (Pattern.compile("^[^0-9][0-9A-Za-z\\_\\:\\.\\$]+").matcher(label).find())
            OUT.print(prefix + label + suffix);
    }
  
    /**
     * Writes Label out to ASM
     * @param label Label to Write (& Filter)
     */
    public void writeLabel(String label){
    	filterOut("(",label,"\n)");
    }
    
    /**
     * Writes GOTO to ASM
     * @param label Symbol to filter
     */
    public void writeGoTo(String label){
    	filterOut("@",label,"\n0;JMP\n");
    }
    
    /**
     * Writes IF to ASM
     * @param label Symbol to IF
     */
    public void writeIF(String label){
    	if (Pattern.compile("^[^0-9][0-9A-Za-z\\_\\:\\.\\$]+").matcher(label).find())
    		OUT.print("@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" + 
                    "@" + label +
                    "\nD;JNE\n");
    }
    
    /**
     * Stops Writing the file out
     */
    public void kill(){
    	OUT.close();
    }
    
    /**
     * Initializes the File.
     * CALL ONLY ONCE
     */
    public void doBootStrap(){
    	OUT.print("@256\n" +
                "D=A\n" +
                "@SP\n" +
                "M=D\n");
    	writeCall("Sys.init",0);
    }
    
    /**
     * Writes Function Call func to OUT n times
     * @param func
     * @param n
     */
    public void writeFunction(String func, int n){
    	OUT.print("(" + func +")\n");
        for (int i = 0; i < n; i++)
        	writePushPop(Parser.CMD.PUSH,"constant",0);
    }
    
    
    /*
     * ----------------------------------------------------
     * ------------------ASM Translates--------------------
     * ----------------------------------------------------
     * 
     * Methods are used for standardization, Some Templates allow modification
     */
    
    /**
     * ASM Code for pushing elements
     * @param segment
     * @param index
     * @param doPointer
     * @return
     */
    private String ASMPush(String segment, int index, boolean doPointer){
        return "@" + segment + "\n" +
                "D=M\n"+
                (doPointer ? "" : "@" + index + "\n" + "A=D+A\nD=M\n") +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";
    }
    
    /**
     * ASM Code for Popping elements
     * @param segment
     * @param index
     * @param doPointer
     * @return
     */
    private String ASMPop(String segment, int index, boolean doPointer){
        return "@" + segment + "\n" +
        		(doPointer ? "D=A\n" : "D=M\n@" + index + "\nD=D+A\n") +
                "@R13\n" +
                "M=D\n" +
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@R13\n" +
                "A=M\n" +
                "M=D\n";
    }
    
    /**
     * ASM Implementation of CALL Command
     * @param functionName
     * @param numArgs
     */
    public void writeCall(String functionName, int numArgs){
        String newLabel = "RETURN_LABEL" + (labelCnt++);
        OUT.print("@" + newLabel + "\n" +
        			"D=A\n"+
        			"@SP\n"+
        			"A=M\n"+
        			"M=D\n"+
        			"@SP\n"+
        			"M=M+1\n"+
        			ASMPush("LCL",0,true)+
        			ASMPush("ARG",0,true)+
        			ASMPush("THIS",0,true)+
        			ASMPush("THAT",0,true)+
        			"@SP\n" +
                    "D=M\n" +
                    "@5\n" +
                    "D=D-A\n" +
                    "@" + numArgs + "\n" +
                    "D=D-A\n" +
                    "@ARG\n" +
                    "M=D\n" +
                    "@SP\n" +
                    "D=M\n" +
                    "@LCL\n" +
                    "M=D\n" +
                    "@" + functionName + "\n" +
                    "0;JMP\n" +
                    "(" + newLabel + ")\n"
                    );

    }
    
    /**
     * @param position
     * @return ASM to Savs to pos
     */
    private String ASMsaveFrame(String position){
        return "@R11\n" +
                "D=M-1\n" +
                "AM=D\n" +
                "D=M\n" +
                "@" + position + "\n" +
                "M=D\n";

    }
    
    /**
     * @return ASM Implementation of return function
     */
    public String ASMReturn(){
    	return "@LCL\n" +
                "D=M\n" +
                "@R11\n" +
                "M=D\n" +
                "@5\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@R12\n" +
                "M=D\n" +
                ASMPop("ARG",0,false) +
                "@ARG\n" +
                "D=M\n" +
                "@SP\n" +
                "M=D+1\n" +
                ASMsaveFrame("THAT") +
                ASMsaveFrame("THIS") +
                ASMsaveFrame("ARG") +
                ASMsaveFrame("LCL") +
                "@R12\n" +
                "A=M\n" +
                "0;JMP\n";
    }
}
