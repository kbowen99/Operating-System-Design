package vm;

import java.io.File;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class CodeWriter {
	private int arthJumpFlag;
    private PrintWriter OUT;
    private static final Pattern labelReg = Pattern.compile("^[^0-9][0-9A-Za-z\\_\\:\\.\\$]+");
    private static int labelCnt = 0;
    private static String dir = "";
    
    public CodeWriter(File f){
    	try {
			dir = f.getName();
		    OUT = new PrintWriter(f);
		    arthJumpFlag = 0;
    	} catch (Exception e) {}
    }
    
    /**
     * Change file name, for use with directories
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
    
    /*
     * ----------------------------------------------------
     * ------------------ASM Translates--------------------
     * ----------------------------------------------------
     * 
     * Methods are used for standardization, Some Templates allow modification
     */


}
