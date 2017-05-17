package jack;

import java.io.File;
import jack.JTokenz.keywords;
import jack.Symbol.type;
import jack.VMW.Seg;

/**
 * This Code is not fun.
 * I would highly recommend staying away...
 * @author Kbowen99
 *
 */
public class Crawler {
	private VMW writer;
	private JTokenz tz;
	private SymbolTable sTable;
	private String currentClass = "";
    private String currentSubroutine = "";
    private int lblIndex = 0;
	
	/**
	 * Creates a new Crawler
	 * @param input File to Crawl
	 * @param output Output File
	 * @param outToken Tokenized Output
	 */
	public Crawler(File input, File output){
        try {
            tz = new JTokenz(input);
            writer = new VMW(output);
            sTable = new SymbolTable();
        } catch (Exception e){ }
	}
	
	/**
	 * Compiles type Variables (Int, CHar, Bool,) or ID
	 */
	private String compileType(){
        tz.advance();
        
        if(tz.getTokenType() == JTokenz.tokenType.KEYWORD && (tz.findKeyword() == JTokenz.keywords.INT || tz.findKeyword() == JTokenz.keywords.CHAR || tz.findKeyword() == JTokenz.keywords.BOOLEAN)){
            return tz.getCurrTokenString();
        }

        if (tz.getTokenType() == JTokenz.tokenType.IDENTIFIER){
        	return tz.identifier();
        }
        
        return "";
    }
	
	/**
	 * Attempts to compile a class
	 */
	public void compileClass(){
        tz.advance();
        if (tz.getTokenType() != JTokenz.tokenType.KEYWORD || tz.findKeyword() != JTokenz.keywords.CLASS)
            System.out.println(tz.getCurrTokenString());
        tz.advance();
        currentClass = tz.identifier();
        doSymbol('{');
        compileClassVarDec();
        compileSubroutine();
        doSymbol('}');
        writer.kill();

    }
	
	/**
	 * Compiles ClassVarDescription (I KNow these names are cryptic)
	 * Recursive Calls are probably the most ghetto way to do this, but work nonetheless
	 */
	private void compileClassVarDec(){
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '}'){
            tz.undo();
            return;
        }
        //Something has to happen
        if (tz.findKeyword() == JTokenz.keywords.CONSTRUCTOR || tz.findKeyword() == JTokenz.keywords.FUNCTION || tz.findKeyword() == JTokenz.keywords.METHOD){
            tz.undo();
            return;
        }
        
        //Switch Tokenizer keyword to Symbol Keyword, its a little ghetto
        Symbol.type key = (tz.findKeyword() == JTokenz.keywords.STATIC? Symbol.type.STATIC : (tz.findKeyword() == JTokenz.keywords.FIELD ? Symbol.type.FIELD : null));
        String type = compileType();
        String name = "";

        while(true){
            tz.advance();
            name = tz.identifier();
            sTable.define(name, type, key);
            tz.advance();
            if (tz.sym() == ';')
            	break;
        }
        compileClassVarDec();
    }
	
    private void compileSubroutineBody(JTokenz.keywords keyword){
		doSymbol('{');
		compileVarDec();
		writeFunctionDec(keyword);
		compileStatement();
		doSymbol('}');
    }
    
    private void writeFunctionDec(keywords keyword) {
		writer.writeFunction(currentFunction(), sTable.varCount(Symbol.type.VAR));
		if (keyword == JTokenz.keywords.METHOD){
            writer.writePush(VMW.Seg.ARG, 0);
            writer.writePop(VMW.Seg.POINTER,0);
        }else if (keyword == JTokenz.keywords.CONSTRUCTOR){
            writer.writePush(VMW.Seg.CONST,sTable.varCount(Symbol.type.FIELD));
            writer.writeCall("Memory.alloc", 1);
            writer.writePop(VMW.Seg.POINTER,0);
        }
	}

	/**
     * Recursively compile stuff!
     */
    private void compileStatement(){
        tz.advance();

        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '}'){
            tz.undo();
            return;
        }
        
        if (!(tz.getTokenType() != JTokenz.tokenType.KEYWORD)){
            switch (tz.findKeyword()){
                case LET:
                	compileLet();break;
                case IF:
                	compileIf();break;
                case WHILE:
                	compileWhile();break;
                case DO:
                	compileDo();break;
                case RETURN:
                	compileReturn();break;
                default:;
            }
        }

        compileStatement();
    }
    
    private void compileParameterList(){
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ')'){
            tz.undo();
            return;
        }
        tz.undo();
        
        String type = "";
        
        while(true){
            type = compileType();
            tz.advance();
            sTable.define(tz.identifier(), type, Symbol.type.ARG);
            tz.advance();

            if (tz.sym() == ')'){
                tz.undo();
                break;
            }
        }
    }
    
    private void compileVarDec(){
        tz.advance();
        if (tz.getTokenType() != JTokenz.tokenType.KEYWORD || tz.findKeyword() != JTokenz.keywords.VAR){
            tz.undo();
            return;
        }
        
        String type = compileType();
        
        while(true) {
            tz.advance();
            sTable.define(tz.identifier(), type, Symbol.type.VAR);
            tz.advance();
            if (tz.sym() == ';')
                break;
        }
        compileVarDec();
    }
	
    private void compileDo(){
        compileSubroutineCall();
        doSymbol(';');
        writer.writePop(VMW.Seg.TEMP, 0);
    }
    
    private void compileLet(){
        tz.advance();
        String tmp = tz.identifier();
        tz.advance();
        boolean expExist = false;

        if (tz.sym() == '['){
            expExist = true;
            writer.writePush(getSeg(sTable.kindOf(tmp)),sTable.indexOf(tmp));
            compileExpression();
            doSymbol(']');
            writer.writeArithmetic(VMW.Op.ADD);
        }
        if (expExist) 
        	tz.advance();
        compileExpression();
        doSymbol(';');
        if (expExist){
            writer.writePop(Seg.TEMP,0);
            writer.writePop(Seg.POINTER,1);
            writer.writePush(Seg.TEMP,0);
            writer.writePop(Seg.THAT,0);
        } else 
            writer.writePop(getSeg(sTable.kindOf(tmp)), sTable.indexOf(tmp));
    }
    
    /**
     * Symbol Type --> VMW Seg
     * @param kindOf
     * @return
     */
	private Seg getSeg(type kindOf) {
        switch (kindOf){
            case FIELD:
            	return Seg.THIS;
            case STATIC:
            	return Seg.STATIC;
            case VAR:
            	return Seg.LOCAL;
            case ARG:
            	return Seg.ARG;
            default:
            	return Seg.NONE;
        }
	}

	private void compileWhile(){
		String lbl1 = newLbl();
		String lbl2 = newLbl();
		writer.writeLabel(lbl1);
        doSymbol('(');
        compileExpression();
        doSymbol(')');
        writer.writeArithmetic(VMW.Op.NOT);
        writer.writeIf(lbl2);
        doSymbol('{');
        compileStatement();
        doSymbol('}');
        writer.writeGoto(lbl1);
        writer.writeLabel(lbl2);
    }
    
	/**
	 * @return Increments to next label index, generates string
	 */
    private String newLbl() {
    	return "LABEL_" + lblIndex++;
	}

	private void compileReturn(){
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ';'){
            writer.writePush(Seg.CONST, 0);
        } else {
        	tz.undo();
            compileExpression();
            doSymbol(';');
        }
        writer.writeReturn();
    }
    
    private void compileIf(){
    	String elseLbl = newLbl();
        String outLbl = newLbl();
        doSymbol('(');
        compileExpression();
        doSymbol(')');
        writer.writeArithmetic(VMW.Op.NOT);
        writer.writeIf(elseLbl);
        doSymbol('{');
        compileStatement();
        doSymbol('}');
        writer.writeGoto(outLbl);
        writer.writeLabel(elseLbl);
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.KEYWORD && tz.findKeyword() == JTokenz.keywords.ELSE){
            doSymbol('{');
            compileStatement();
            doSymbol('}');
        } else
            tz.undo();
        writer.writeLabel(outLbl);
    }
    
    private void compileTerm(){
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.IDENTIFIER){
            String tmp = tz.identifier();
            tz.advance();
            if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '['){
            	writer.writePush(getSeg(sTable.kindOf(tmp)),sTable.indexOf(tmp));
                compileExpression();
                doSymbol(']');
                writer.writeArithmetic(VMW.Op.ADD);
                writer.writePop(Seg.POINTER,1);
                writer.writePush(Seg.THAT,0);
            }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && (tz.sym() == '(' || tz.sym() == '.')){
                tz.undo();tz.undo();
                compileSubroutineCall();
            }else {
                tz.undo();
                writer.writePush(getSeg(sTable.kindOf(tmp)), sTable.indexOf(tmp));
            }

        }else{
            if (tz.getTokenType() == JTokenz.tokenType.INT_CONSTANT){
            	writer.writePush(Seg.CONST,tz.intValue());
            }else if (tz.getTokenType() == JTokenz.tokenType.STRING_CONSTANT){
            	String str = tz.stringVal();
                writer.writePush(Seg.CONST,str.length());
                writer.writeCall("String.new",1);

                for (int i = 0; i < str.length(); i++){
                    writer.writePush(Seg.CONST,(int)str.charAt(i));
                    writer.writeCall("String.appendChar",2);
                }
            }else if(tz.getTokenType() == JTokenz.tokenType.KEYWORD){
            	if (tz.findKeyword() == JTokenz.keywords.TRUE){
            		writer.writePush(Seg.CONST,0);
                    writer.writeArithmetic(VMW.Op.NOT);
            	} else if (tz.findKeyword() == JTokenz.keywords.FALSE || tz.findKeyword() == JTokenz.keywords.NULL)
            		writer.writePush(Seg.CONST,0);
            	else if (tz.findKeyword() == JTokenz.keywords.THIS)
            		writer.writePush(Seg.POINTER,0);                  
            }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '('){
                compileExpression();
                doSymbol(')');
            }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && (tz.sym() == '-' || tz.sym() == '~')){
            	char s = tz.sym();
                compileTerm();
                if (s == '-')
                    writer.writeArithmetic(VMW.Op.NEG);
                else
                    writer.writeArithmetic(VMW.Op.NOT);
            }
        }
        
    }
    
    private void compileSubroutineCall(){
        tz.advance();
        String name = tz.identifier();
        int nArgs = 0;
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '('){
        	writer.writePush(Seg.POINTER,0);
        	nArgs = compileExpressionList() + 1;
            doSymbol(')');
            writer.writeCall(currentClass + '.' + name, nArgs);
        }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '.'){
        	String objName = name;
            tz.advance();
            name = tz.identifier();
            String type = sTable.typeOf(objName);
            if (type.equals(""))
                name = objName + "." + name;
            else {
                nArgs = 1;
                writer.writePush(getSeg(sTable.kindOf(objName)), sTable.indexOf(objName));
                name = sTable.typeOf(objName) + "." + name;
            }
            doSymbol('(');
            nArgs += compileExpressionList();
            doSymbol(')');
            writer.writeCall(name,nArgs);
        }
    }
    
    private void compileExpression(){
        compileTerm();
        while (true) {
            tz.advance();
            if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.isOperation()){
            	String maths = "";
                switch (tz.sym()){
                    case '+':
                    	maths = "add";break;
                    case '-':
                    	maths = "sub";break;
                    case '*':
                    	maths = "call Math.multiply 2";break;
                    case '/':
                    	maths = "call Math.divide 2";break;
                    case '<':
                    	maths = "lt";break;
                    case '>':
                    	maths = "gt";break;
                    case '=':
                    	maths = "eq";break;
                    case '&':
                    	maths = "and";break;
                    case '|':
                    	maths = "or";break;
                    default:;
                }
                compileTerm();
                writer.writeCommand(maths,"","");
            }else {
                tz.undo();
                break;
            }
        }
    }
    
    private int compileExpressionList(){
    	int n = 0;
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ')')
            tz.undo();
        else {
        	n = 1;
            tz.undo();
            compileExpression();
            while (true) {
                tz.advance();
                if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ','){
                    compileExpression();
                    n++;
                }else {
                    tz.undo();
                    break;
                }
            }
        }
        return n;
    }
    
    /**
     * Raise hell if symbol not found (Consumes next symbol)
     * @param symbol
     */
    private void doSymbol(char symbol){
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() != symbol)
        	throw new IllegalStateException("Missing Symbol! Expected: " + symbol);
    }
    
    private void compileSubroutine(){
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '}'){
            tz.undo();
            return;
        }
        
        JTokenz.keywords keyword = tz.findKeyword();
        sTable.startSubroutine();

        //for method this is the first argument
        if (tz.findKeyword() == JTokenz.keywords.METHOD)
            sTable.define("this",currentClass, Symbol.type.ARG);
        //String type = "";

        tz.advance();
        
//        if (tz.getTokenType() == JTokenz.tokenType.KEYWORD && tz.findKeyword() == JTokenz.keywords.VOID)
//            type = "void";
//        else {
//            tz.undo();
//            type = compileType();
//        }
        
        tz.advance();
        currentSubroutine = tz.identifier();
        doSymbol('(');
        compileParameterList();
        doSymbol(')');
        compileSubroutineBody(keyword);
        compileSubroutine();
    }
    
    /**
     * @return current function
     */
    private String currentFunction(){
        if (currentClass.length() != 0 && currentSubroutine.length() !=0)
            return currentClass + "." + currentSubroutine;
        return "";
    }
}