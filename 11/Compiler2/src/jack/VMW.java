package jack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * VM Writer
 * 
 * This is yet another Wrapper Class (Wraps Printwriter, Has fancylike methods)
 * 
 * Did you know if you omit the "param" portion of a method comment, it will still show parameters in the java doc?
 * Thats convenient.
 * 
 * Now if only I knew how to force a \n /n <br> newline
 * 
 */
public class VMW {
	/**
	 * Segments VMW can nicely write
	 */
    public static enum Seg {
    	CONST,ARG,LOCAL,STATIC,THIS,THAT,POINTER,TEMP,NONE,
    };
    
    /**
     * VMW Can do maths too
     */
    public static enum Op {
    	ADD,SUB,NEG,EQ,GT,LT,AND,OR,NOT,NONE,
    };

    static HashMap<Seg,String> segMap = new HashMap<Seg, String>();
    static HashMap<Op,String> opMap = new HashMap<Op, String>();
    PrintWriter VMwriter;
    
    /**
     * makes new VMWriter
     * @param f File to write to
     */
    public VMW(File f) {
        try {
            VMwriter = new PrintWriter(f);
    	}catch (FileNotFoundException e) {}
        
        
        //Mother of initiation
        segMap.put(Seg.CONST,"constant");
        segMap.put(Seg.ARG,"argument");
        segMap.put(Seg.LOCAL,"local");
        segMap.put(Seg.STATIC,"static");
        segMap.put(Seg.THIS,"this");
        segMap.put(Seg.THAT,"that");
        segMap.put(Seg.POINTER,"pointer");
        segMap.put(Seg.TEMP,"temp");

        opMap.put(Op.ADD,"add");
        opMap.put(Op.SUB,"sub");
        opMap.put(Op.NEG,"neg");
        opMap.put(Op.EQ,"eq");
        opMap.put(Op.GT,"gt");
        opMap.put(Op.LT,"lt");
        opMap.put(Op.AND,"and");
        opMap.put(Op.OR,"or");
        opMap.put(Op.NOT,"not");
    }
    
    /**
     * Push Seg Enum at index
     */
    public void writePush(Seg segment, int index){
        writeCommand("push",segMap.get(segment),index + "");
    }

    /**
     * Pop Seg Enum at index
     */
    public void writePop(Seg segment, int index){
        writeCommand("pop",segMap.get(segment),index + "");
    }

    /**
     * Sound Familiar?
     * Does math
     * @param command Operation to write
     */
    public void writeArithmetic(Op command){
        writeCommand(opMap.get(command),"","");
    }

    /**
     * Creates label
     */
    public void writeLabel(String lbl){
        writeCommand("label",lbl,"");
    }

    /**
     *goes to label
     */
    public void writeGoto(String lbl){
        writeCommand("goto",lbl,"");
    }
    /**
     * writes if-goto
     */
    public void writeIf(String lbl){
        writeCommand("if-goto",lbl,"");
    }

    /**
     * writes VM call command
     */
    public void writeCall(String name, int n){
        writeCommand("call",name,n + "");
    }

    /**
     * writes VM function 
     */
    public void writeFunction(String name, int n){
        writeCommand("function",name,n + "");
    }

    /**
     * writes return
     */
    public void writeReturn(){
        writeCommand("return","","");
    }
    
    /**
     * Writes command to PrintWriter
     * Helper methods ftw
     */
    public void writeCommand(String cmd, String arg1, String arg2){
        VMwriter.print(cmd + " " + arg1 + " " + arg2 + "\n");
    }
    
    /**
     * What do you think it does?
     */
    public void kill(){
        VMwriter.close();
    }
    
    /**
     * These Dummy Methods were added to quickly mark where in Crawler needs to be fixed (Deprecation == bad)
     */
    @Deprecated
    public void print(Object o){}
    /**
     * These Dummy Methods were added to quickly mark where in Crawler needs to be fixed (Deprecation == bad)
     */
    @Deprecated
    public void close(){};

}
