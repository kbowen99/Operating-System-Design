package jack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * This Code is not fun.
 * I would highly reccomend staying away...
 * @author Kbowen99
 *
 */
public class Crawler {
	private VMW writer;
	private PrintWriter tokenWriter;
	private JTokenz tz;
	
	/**
	 * Creates a new Crawler
	 * @param input File to Crawl
	 * @param output Output File
	 * @param outToken Tokenized Output
	 */
	public Crawler(File input, File output, File outToken){
        try {
            tz = new JTokenz(input);
            writer = new VMW(output);
            tokenWriter = new PrintWriter(outToken);
        } catch (FileNotFoundException e){ }
	}
	
	/**
	 * Compiles type Variables (Int, CHar, Bool,) or ID
	 */
	private void compileType(){
        tz.advance();
        
        if(tz.getTokenType() == JTokenz.tokenType.KEYWORD && (tz.findKeyword() == JTokenz.keywords.INT || tz.findKeyword() == JTokenz.keywords.CHAR || tz.findKeyword() == JTokenz.keywords.BOOLEAN)){
            writer.print("<keyword> " + tz.getCurrTokenString() + " </keyword>\n");
        	tokenWriter.print("<keyword> " + tz.getCurrTokenString() + " </keyword>\n");
        }

        if (tz.getTokenType() == JTokenz.tokenType.IDENTIFIER){
        	writer.print("<identifier> " + tz.identifier() + " </identifier>\n");
        	tokenWriter.print("<identifier> " + tz.identifier() + " </identifier>\n");
        }
    }
	
	/**
	 * Attempts to compile a class
	 */
	public void compileClass(){
        tz.advance();
        writer.print("<class>\n");
        tokenWriter.print("<tokens>\n");

        writer.print("<keyword> class </keyword>\n");
        tokenWriter.print("<keyword> class </keyword>\n");
        
        //className
        tz.advance();
        writer.print("<identifier> " + tz.identifier() + " </identifier>\n");
        tokenWriter.print("<identifier> " + tz.identifier() + " </identifier>\n");
        doSymbol('{');
        
        //classVarDec* subroutineDec*
        compileClassVarDec();
        compileSubroutine();
        doSymbol('}');
        
        tokenWriter.print("</tokens>\n");
        writer.print("</class>\n");
        
        writer.close();
        tokenWriter.close();
    }
	
	/**
	 * Compiles ClassVarDescription (I KNow these names are cryptic)
	 * Recursive Calls are probably the most ghetto way to do this, but work nonetheless
	 */
	private void compileClassVarDec(){
		//ID
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
        writer.print("<classVarDec>\n");
        writer.print("<keyword> " + tz.getCurrTokenString() + " </keyword>\n");
        tokenWriter.print("<keyword> " + tz.getCurrTokenString() + " </keyword>\n");
        compileType();

        while(true){
            tz.advance();

            writer.print("<identifier> " + tz.identifier() + " </identifier>\n");
            tokenWriter.print("<identifier> " + tz.identifier() + " </identifier>\n");
            
            tz.advance();
            
            if (tz.sym() == ','){
                writer.print("<symbol> , </symbol>\n");
                tokenWriter.print("<symbol> , </symbol>\n");
            }else {
                writer.print("<symbol> ; </symbol>\n");
                tokenWriter.print("<symbol> ; </symbol>\n");
                break;
            }
        }
        writer.print("</classVarDec>\n");
        compileClassVarDec();
    }
	
    private void compileSubroutineBody(){
		writer.print("<subroutineBody>\n");
		doSymbol('{');
		compileVarDec();
		writer.print("<statements>\n");
		compileStatement();
		writer.print("</statements>\n");
		doSymbol('}');
		writer.print("</subroutineBody>\n");
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
                	compilesWhile();break;
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
        
        while(true){
            compileType();
            tz.advance();
            
            writer.print("<identifier> " + tz.identifier() + " </identifier>\n");
            tokenWriter.print("<identifier> " + tz.identifier() + " </identifier>\n");

            tz.advance();

            if (tz.sym() == ','){
                writer.print("<symbol> , </symbol>\n");
                tokenWriter.print("<symbol> , </symbol>\n");
            }else {
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

        writer.print("<varDec>\n");

        writer.print("<keyword> var </keyword>\n");
        tokenWriter.print("<keyword> var </keyword>\n");

        compileType();
        
        while(true) {
            tz.advance();

            writer.print("<identifier> " + tz.identifier() + " </identifier>\n");
            tokenWriter.print("<identifier> " + tz.identifier() + " </identifier>\n");

            tz.advance();

            if (tz.sym() == ','){
                writer.print("<symbol> , </symbol>\n");
                tokenWriter.print("<symbol> , </symbol>\n");
            }else {
                writer.print("<symbol> ; </symbol>\n");
                tokenWriter.print("<symbol> ; </symbol>\n");
                break;
            }
        }
        writer.print("</varDec>\n");
        compileVarDec();
    }
	
    private void compileDo(){
        writer.print("<doStatement>\n");
        writer.print("<keyword> do </keyword>\n");
        tokenWriter.print("<keyword> do </keyword>\n");
        compileSubroutineCall();
        doSymbol(';');
        writer.print("</doStatement>\n");
    }
    
    private void compileLet(){
        writer.print("<letStatement>\n");
        writer.print("<keyword> let </keyword>\n");
        tokenWriter.print("<keyword> let </keyword>\n");

        tz.advance();

        writer.print("<identifier> " + tz.identifier() + " </identifier>\n");
        tokenWriter.print("<identifier> " + tz.identifier() + " </identifier>\n");

        tz.advance();

        boolean expExist = false;

        if (tz.sym() == '['){
            expExist = true;
            writer.print("<symbol> [ </symbol>\n");
            tokenWriter.print("<symbol> [ </symbol>\n");
            compileExpression();
            
            tz.advance();
            if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ']'){
                writer.print("<symbol> ] </symbol>\n");
                tokenWriter.print("<symbol> ] </symbol>\n");
            }
        }

        if (expExist) 
        	tz.advance();
        writer.print("<symbol> = </symbol>\n");
        tokenWriter.print("<symbol> = </symbol>\n");

        compileExpression();
        doSymbol(';');
        writer.print("</letStatement>\n");
    }
    
    private void compilesWhile(){
        writer.print("<whileStatement>\n");

        writer.print("<keyword> while </keyword>\n");
        tokenWriter.print("<keyword> while </keyword>\n");
        doSymbol('(');
        compileExpression();
        doSymbol(')');
        doSymbol('{');
        writer.print("<statements>\n");
        compileStatement();
        writer.print("</statements>\n");
        doSymbol('}');
        writer.print("</whileStatement>\n");
    }
    
    private void compileReturn(){
        writer.print("<returnStatement>\n");

        writer.print("<keyword> return </keyword>\n");
        tokenWriter.print("<keyword> return </keyword>\n");
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ';'){
            writer.print("<symbol> ; </symbol>\n");
            tokenWriter.print("<symbol> ; </symbol>\n");
            writer.print("</returnStatement>\n");
            return;
        }
        tz.undo();
        compileExpression();
        doSymbol(';');
        writer.print("</returnStatement>\n");
    }
    
    private void compileIf(){
        writer.print("<ifStatement>\n");
        writer.print("<keyword> if </keyword>\n");
        tokenWriter.print("<keyword> if </keyword>\n");
        doSymbol('(');
        compileExpression();
        doSymbol(')');
        doSymbol('{');
        writer.print("<statements>\n");
        compileStatement();
        writer.print("</statements>\n");
        doSymbol('}');
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.KEYWORD && tz.findKeyword() == JTokenz.keywords.ELSE){
            writer.print("<keyword> else </keyword>\n");
            tokenWriter.print("<keyword> else </keyword>\n");
            doSymbol('{');
            writer.print("<statements>\n");
            compileStatement();
            writer.print("</statements>\n");
            doSymbol('}');
        }else
            tz.undo();
        writer.print("</ifStatement>\n");
    }
    
    private void compileTerm(){
        writer.print("<term>\n");
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.IDENTIFIER){
            String tmp = tz.identifier();
            tz.advance();
            if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '['){
                writer.print("<identifier> " + tmp + " </identifier>\n");
                tokenWriter.print("<identifier> " + tmp + " </identifier>\n");
                writer.print("<symbol> [ </symbol>\n");
                tokenWriter.print("<symbol> [ </symbol>\n");
                compileExpression();
                doSymbol(']');
            }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && (tz.sym() == '(' || tz.sym() == '.')){
                tz.undo();tz.undo();
                compileSubroutineCall();
            }else {
                writer.print("<identifier> " + tmp + " </identifier>\n");
                tokenWriter.print("<identifier> " + tmp + " </identifier>\n");
                tz.undo();
            }

        }else{
            if (tz.getTokenType() == JTokenz.tokenType.INT_CONSTANT){
                writer.print("<integerConstant> " + tz.intValue() + " </integerConstant>\n");
                tokenWriter.print("<integerConstant> " + tz.intValue() + " </integerConstant>\n");
            }else if (tz.getTokenType() == JTokenz.tokenType.STRING_CONSTANT){
                writer.print("<stringConstant> " + tz.stringVal() + " </stringConstant>\n");
                tokenWriter.print("<stringConstant> " + tz.stringVal() + " </stringConstant>\n");
            }else if(tz.getTokenType() == JTokenz.tokenType.KEYWORD &&
                            (tz.findKeyword() == JTokenz.keywords.TRUE ||
                            tz.findKeyword() == JTokenz.keywords.FALSE ||
                            tz.findKeyword() == JTokenz.keywords.NULL ||
                            tz.findKeyword() == JTokenz.keywords.THIS)){
                    writer.print("<keyword> " + tz.getCurrTokenString() + " </keyword>\n");
                    tokenWriter.print("<keyword> " + tz.getCurrTokenString() + " </keyword>\n");
            }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '('){
                writer.print("<symbol> ( </symbol>\n");
                tokenWriter.print("<symbol> ( </symbol>\n");
                compileExpression();
                doSymbol(')');
            }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && (tz.sym() == '-' || tz.sym() == '~')){
                writer.print("<symbol> " + tz.sym() + " </symbol>\n");
                tokenWriter.print("<symbol> " + tz.sym() + " </symbol>\n");
                compileTerm();
            }
        }
        writer.print("</term>\n");
    }
    
    private void compileSubroutineCall(){
        tz.advance();
        writer.print("<identifier> " + tz.identifier() + " </identifier>\n");
        tokenWriter.print("<identifier> " + tz.identifier() + " </identifier>\n");

        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '('){
            writer.print("<symbol> ( </symbol>\n");
            tokenWriter.print("<symbol> ( </symbol>\n");
            writer.print("<expressionList>\n");
            compileExpressionList();
            writer.print("</expressionList>\n");
            doSymbol(')');
        }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '.'){
            writer.print("<symbol> . </symbol>\n");
            tokenWriter.print("<symbol> . </symbol>\n");
            tz.advance();
            writer.print("<identifier> " + tz.identifier() + " </identifier>\n");
            tokenWriter.print("<identifier> " + tz.identifier() + " </identifier>\n");
            doSymbol('(');
            writer.print("<expressionList>\n");
            compileExpressionList();
            writer.print("</expressionList>\n");
            doSymbol(')');
        }
    }
    
    private void compileExpression(){
        writer.print("<expression>\n");

        compileTerm();

        while (true) {
            tz.advance();
            if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.isOperation()){
                if (tz.sym() == '>'){
                    writer.print("<symbol> &gt; </symbol>\n");
                    tokenWriter.print("<symbol> &gt; </symbol>\n");
                }else if (tz.sym() == '<'){
                    writer.print("<symbol> &lt; </symbol>\n");
                    tokenWriter.print("<symbol> &lt; </symbol>\n");
                }else if (tz.sym() == '&') {
                    writer.print("<symbol> &amp; </symbol>\n");
                    tokenWriter.print("<symbol> &amp; </symbol>\n");
                }else {
                    writer.print("<symbol> " + tz.sym() + " </symbol>\n");
                    tokenWriter.print("<symbol> " + tz.sym() + " </symbol>\n");
                }
                compileTerm();
            }else {
                tz.undo();
                break;
            }
        }
        writer.print("</expression>\n");
    }
    
    private void compileExpressionList(){
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ')')
            tz.undo();
        else {
            tz.undo();
            compileExpression();
            while (true) {
                tz.advance();
                if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ','){
                    writer.print("<symbol> , </symbol>\n");
                    tokenWriter.print("<symbol> , </symbol>\n");
                    compileExpression();
                }else {
                    tz.undo();
                    break;
                }
            }
        }
    }
    
    private void doSymbol(char symbol){
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == symbol){
            writer.print("<symbol> " + symbol + " </symbol>\n");
            tokenWriter.print("<symbol> " + symbol + " </symbol>\n");
        }
    }
    
    private void compileSubroutine(){
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '}'){
            tz.undo();
            return;
        }
        
        writer.print("<subroutineDec>\n");
        writer.print("<keyword> " + tz.getCurrTokenString() + " </keyword>\n");
        tokenWriter.print("<keyword> " + tz.getCurrTokenString() + " </keyword>\n");

        tz.advance();
        
        if (tz.getTokenType() == JTokenz.tokenType.KEYWORD && tz.findKeyword() == JTokenz.keywords.VOID){
            writer.print("<keyword> void </keyword>\n");
            tokenWriter.print("<keyword> void </keyword>\n");
        } else {
            tz.undo();
            compileType();
        }
        
        tz.advance();

        writer.print("<identifier> " + tz.identifier() + " </identifier>\n");
        tokenWriter.print("<identifier> " + tz.identifier() + " </identifier>\n");
        doSymbol('(');
        writer.print("<parameterList>\n");
        compileParameterList();
        writer.print("</parameterList>\n");
        doSymbol(')');
        compileSubroutineBody();
        writer.print("</subroutineDec>\n");
        compileSubroutine();
    }
}