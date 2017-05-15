package jack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;



public class Crawler {
	private PrintWriter writer;
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
            writer = new PrintWriter(output);
            tokenWriter = new PrintWriter(outToken);
        } catch (FileNotFoundException e){ }
	}
	
	
	private void compileType(){
        tz.advance();
        boolean isType = false;
        
        if(tz.getTokenType() == JTokenz.tokenType.KEYWORD && (tz.findKeyword() == JTokenz.keywords.INT || tz.findKeyword() == JTokenz.keywords.CHAR || tz.findKeyword() == JTokenz.keywords.BOOLEAN)){
            writer.print("<keyword>" + tz.getCurrTokenString() + "</keyword>\n");
        	tokenWriter.print("<keyword>" + tz.getCurrTokenString() + "</keyword>\n");
            isType = true;
        }

        if (tz.getTokenType() == JTokenz.tokenType.IDENTIFIER){
        	writer.print("<identifier>" + tz.identifier() + "</identifier>\n");
        	tokenWriter.print("<identifier>" + tz.identifier() + "</identifier>\n");
            isType = true;
        }
    }
	
	/**
	 * Attempt to compile a class
	 */
	public void compileClass(){
        tz.advance();
        writer.print("<class>\n");
        tokenWriter.print("<tokens>\n");

        writer.print("<keyword>class</keyword>\n");
        tokenWriter.print("<keyword>class</keyword>\n");

        //className
        tz.advance();

//        if (tz.tokenType() != JackTokenizer.IDENTIFIER){
//            error("className");
//        }

        writer.print("<identifier>" + tz.identifier() + "</identifier>\n");
        tokenWriter.print("<identifier>" + tz.identifier() + "</identifier>\n");

        //'{'
        doSymbol('{');

        //classVarDec* subroutineDec*
        compileClassVarDec();
        compileSubroutine();

        //'}'
        doSymbol('}');

//        if (tz.hasMoreTokens()){
//            throw new IllegalStateException("Unexpected tokens");
//        }

        tokenWriter.print("</tokens>\n");
        writer.print("</class>\n");

        //save file
        writer.close();
        tokenWriter.close();
    }
	
	private void compileClassVarDec(){

        //first determine whether there is a classVarDec, nextToken is } or start subroutineDec
        tz.advance();

        //next is a '}'
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '}'){
            tz.undo();
            return;
        }

        //next is subroutineDec
        if (tz.findKeyword() == JTokenz.keywords.CONSTRUCTOR || tz.findKeyword() == JTokenz.keywords.FUNCTION || tz.findKeyword() == JTokenz.keywords.METHOD){
            tz.undo();
            return;
        }

        writer.print("<classVarDec>\n");

//        //classVarDec exists
//        if (tz.keyWord() != JackTokenizer.STATIC && tokenizer.keyWord() != JackTokenizer.FIELD){
//            error("static or field");
//        }

        writer.print("<keyword>" + tz.getCurrTokenString() + "</keyword>\n");
        tokenWriter.print("<keyword>" + tz.getCurrTokenString() + "</keyword>\n");

        //type
        compileType();

        //at least one varName
        boolean varNamesDone = false;

        do {

            //varName
            tz.advance();
//            if (tokenizer.tokenType() != JackTokenizer.IDENTIFIER){
//                error("identifier");
//            }

            writer.print("<identifier>" + tz.identifier() + "</identifier>\n");
            tokenWriter.print("<identifier>" + tz.identifier() + "</identifier>\n");

            //',' or ';'
            tz.advance();

//            if (tz.tokenType() != JackTokenizer.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ';')){
//                error("',' or ';'");
//            }

            if (tz.sym() == ','){

                writer.print("<symbol>,</symbol>\n");
                tokenWriter.print("<symbol>,</symbol>\n");

            }else {

                writer.print("<symbol>;</symbol>\n");
                tokenWriter.print("<symbol>;</symbol>\n");
                break;
            }


        }while(true);

        writer.print("</classVarDec>\n");

        compileClassVarDec();
    }
	
    private void compileSubroutineBody(){
        writer.print("<subroutineBody>\n");
        //'{'
        doSymbol('{');
        //varDec*
        compileVarDec();
        //statements
        writer.print("<statements>\n");
        compileStatement();
        writer.print("</statements>\n");
        //'}'
        doSymbol('}');
        writer.print("</subroutineBody>\n");
    }
    
    private void compileStatement(){

        //determine whether there is a statementnext can be a '}'
        tz.advance();

        //next is a '}'
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '}'){
            tz.undo();
            return;
        }

        //next is 'let'|'if'|'while'|'do'|'return'
        if (tz.getTokenType() != JTokenz.tokenType.KEYWORD){
            //error("keyword");
        }else {
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

        //check if there is parameterList, if next token is ')' than go back
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ')'){
            tz.undo();
            return;
        }

        //there is parameter, at least one varName
        tz.undo();
        do {
            //type
            compileType();

            //varName
            tz.advance();
            if (tz.getTokenType() != JTokenz.tokenType.IDENTIFIER){
                //error("identifier");
            }
            writer.print("<identifier>" + tz.identifier() + "</identifier>\n");
            tokenWriter.print("<identifier>" + tz.identifier() + "</identifier>\n");

            //',' or ')'
            tz.advance();
            if (tz.getTokenType() != JTokenz.tokenType.SYMBOL || (tz.sym() != ',' && tz.sym() != ')')){
                //error("',' or ')'");
            }

            if (tz.sym() == ','){
                writer.print("<symbol>,</symbol>\n");
                tokenWriter.print("<symbol>,</symbol>\n");
            }else {
                tz.undo();
                break;
            }

        }while(true);

    }
    
    private void compileVarDec(){

        //determine if there is a varDec

        tz.advance();
        //no 'var' go back
        if (tz.getTokenType() != JTokenz.tokenType.KEYWORD || tz.findKeyword() != JTokenz.keywords.VAR){
            tz.undo();
            return;
        }

        writer.print("<varDec>\n");

        writer.print("<keyword>var</keyword>\n");
        tokenWriter.print("<keyword>var</keyword>\n");

        //type
        compileType();

        //at least one varName
        boolean varNamesDone = false;

        do {

            //varName
            tz.advance();

            if (tz.getTokenType() != JTokenz.tokenType.IDENTIFIER){
                //error("identifier");
            }

            writer.print("<identifier>" + tz.identifier() + "</identifier>\n");
            tokenWriter.print("<identifier>" + tz.identifier() + "</identifier>\n");

            //',' or ';'
            tz.advance();

//            if (tz.tokenType() != JackTokenizer.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ';')){
//                error("',' or ';'");
//            }

            if (tz.sym() == ','){

                writer.print("<symbol>,</symbol>\n");
                tokenWriter.print("<symbol>,</symbol>\n");

            }else {

                writer.print("<symbol>;</symbol>\n");
                tokenWriter.print("<symbol>;</symbol>\n");
                break;
            }


        }while(true);

        writer.print("</varDec>\n");

        compileVarDec();

    }
	
    private void compileDo(){
        writer.print("<doStatement>\n");

        writer.print("<keyword>do</keyword>\n");
        tokenWriter.print("<keyword>do</keyword>\n");
        //subroutineCall
        compileSubroutineCall();
        //';'
        doSymbol(';');

        writer.print("</doStatement>\n");
    }
    
    private void compileLet(){

        writer.print("<letStatement>\n");

        writer.print("<keyword>let</keyword>\n");
        tokenWriter.print("<keyword>let</keyword>\n");

        //varName
        tz.advance();
//        if (tokenizer.tokenType() != JackTokenizer.IDENTIFIER){
//            error("varName");
//        }

        writer.print("<identifier>" + tz.identifier() + "</identifier>\n");
        tokenWriter.print("<identifier>" + tz.identifier() + "</identifier>\n");

        //'[' or '='
        tz.advance();
//        if (tz.getTokenType() != JackTokenizer.SYMBOL || (tokenizer.symbol() != '[' && tokenizer.symbol() != '=')){
//            error("'['|'='");
//        }

        boolean expExist = false;

        //'[' expression ']'
        if (tz.sym() == '['){

            expExist = true;

            writer.print("<symbol>[</symbol>\n");
            tokenWriter.print("<symbol>[</symbol>\n");

            compileExpression();

            //']'
            tz.advance();
            if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ']'){
                writer.print("<symbol>]</symbol>\n");
                tokenWriter.print("<symbol>]</symbol>\n");
            }else {
                //error("']'");
            }
        }

        if (expExist) tz.advance();

        //'='
        writer.print("<symbol>=</symbol>\n");
        tokenWriter.print("<symbol>=</symbol>\n");

        //expression
        compileExpression();

        //';'
        doSymbol(';');

        writer.print("</letStatement>\n");
    }
    
    private void compilesWhile(){
        writer.print("<whileStatement>\n");

        writer.print("<keyword>while</keyword>\n");
        tokenWriter.print("<keyword>while</keyword>\n");
        //'('
        doSymbol('(');
        //expression
        compileExpression();
        //')'
        doSymbol(')');
        //'{'
        doSymbol('{');
        //statements
        writer.print("<statements>\n");
        compileStatement();
        writer.print("</statements>\n");
        //'}'
        doSymbol('}');

        writer.print("</whileStatement>\n");
    }
    
    private void compileReturn(){
        writer.print("<returnStatement>\n");

        writer.print("<keyword>return</keyword>\n");
        tokenWriter.print("<keyword>return</keyword>\n");

        //check if there is any expression
        tz.advance();
        //no expression
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ';'){
            writer.print("<symbol>;</symbol>\n");
            tokenWriter.print("<symbol>;</symbol>\n");
            writer.print("</returnStatement>\n");
            return;
        }

        tz.undo();
        //expression
        compileExpression();
        //';'
        doSymbol(';');

        writer.print("</returnStatement>\n");
    }
    
    private void compileIf(){
        writer.print("<ifStatement>\n");

        writer.print("<keyword>if</keyword>\n");
        tokenWriter.print("<keyword>if</keyword>\n");
        //'('
        doSymbol('(');
        //expression
        compileExpression();
        //')'
        doSymbol(')');
        //'{'
        doSymbol('{');
        //statements
        writer.print("<statements>\n");
        compileStatement();
        writer.print("</statements>\n");
        //'}'
        doSymbol('}');

        //check if there is 'else'
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.KEYWORD && tz.findKeyword() == JTokenz.keywords.ELSE){
            writer.print("<keyword>else</keyword>\n");
            tokenWriter.print("<keyword>else</keyword>\n");
            //'{'
            doSymbol('{');
            //statements
            writer.print("<statements>\n");
            compileStatement();
            writer.print("</statements>\n");
            //'}'
            doSymbol('}');
        }else {
            tz.undo();
        }

        writer.print("</ifStatement>\n");

    }
    
    private void compileTerm(){

        writer.print("<term>\n");

        tz.advance();
        //check if it is an identifier
        if (tz.getTokenType() == JTokenz.tokenType.IDENTIFIER){
            //varName|varName '[' expression ']'|subroutineCall
            String tempId = tz.identifier();

            tz.advance();
            if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '['){
                writer.print("<identifier>" + tempId + "</identifier>\n");
                tokenWriter.print("<identifier>" + tempId + "</identifier>\n");
                //this is an array entry
                writer.print("<symbol>[</symbol>\n");
                tokenWriter.print("<symbol>[</symbol>\n");
                //expression
                compileExpression();
                //']'
                doSymbol(']');
            }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && (tz.sym() == '(' || tz.sym() == '.')){
                //this is a subroutineCall
                tz.undo();tz.undo();
                compileSubroutineCall();
            }else {
                writer.print("<identifier>" + tempId + "</identifier>\n");
                tokenWriter.print("<identifier>" + tempId + "</identifier>\n");
                //this is varName
                tz.undo();
            }

        }else{
            //integerConstant|stringConstant|keywordConstant|'(' expression ')'|unaryOp term
            if (tz.getTokenType() == JTokenz.tokenType.INT_CONSTANT){
                writer.print("<integerConstant>" + tz.intValue() + "</integerConstant>\n");
                tokenWriter.print("<integerConstant>" + tz.intValue() + "</integerConstant>\n");
            }else if (tz.getTokenType() == JTokenz.tokenType.STRING_CONSTANT){
                writer.print("<stringConstant>" + tz.stringVal() + "</stringConstant>\n");
                tokenWriter.print("<stringConstant>" + tz.stringVal() + "</stringConstant>\n");
            }else if(tz.getTokenType() == JTokenz.tokenType.KEYWORD &&
                            (tz.findKeyword() == JTokenz.keywords.TRUE ||
                            tz.findKeyword() == JTokenz.keywords.FALSE ||
                            tz.findKeyword() == JTokenz.keywords.NULL ||
                            tz.findKeyword() == JTokenz.keywords.THIS)){
                    writer.print("<keyword>" + tz.getCurrTokenString() + "</keyword>\n");
                    tokenWriter.print("<keyword>" + tz.getCurrTokenString() + "</keyword>\n");
            }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '('){
                writer.print("<symbol>(</symbol>\n");
                tokenWriter.print("<symbol>(</symbol>\n");
                //expression
                compileExpression();
                //')'
                doSymbol(')');
            }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && (tz.sym() == '-' || tz.sym() == '~')){
                writer.print("<symbol>" + tz.sym() + "</symbol>\n");
                tokenWriter.print("<symbol>" + tz.sym() + "</symbol>\n");
                //term
                compileTerm();
            }else {
                //error("integerConstant|stringConstant|keywordConstant|'(' expression ')'|unaryOp term");
            }
        }

        writer.print("</term>\n");
    }
    
    private void compileSubroutineCall(){

        tz.advance();
        if (tz.getTokenType() != JTokenz.tokenType.IDENTIFIER){
            //error("identifier");
        }

        writer.print("<identifier>" + tz.identifier() + "</identifier>\n");
        tokenWriter.print("<identifier>" + tz.identifier() + "</identifier>\n");

        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '('){
            //'(' expressionList ')'
            writer.print("<symbol>(</symbol>\n");
            tokenWriter.print("<symbol>(</symbol>\n");
            //expressionList
            writer.print("<expressionList>\n");
            compileExpressionList();
            writer.print("</expressionList>\n");
            //')'
            doSymbol(')');
        }else if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '.'){
            //(className|varName) '.' subroutineName '(' expressionList ')'
            writer.print("<symbol>.</symbol>\n");
            tokenWriter.print("<symbol>.</symbol>\n");
            //subroutineName
            tz.advance();
            if (tz.getTokenType() != JTokenz.tokenType.IDENTIFIER){
                //error("identifier");
            }
            writer.print("<identifier>" + tz.identifier() + "</identifier>\n");
            tokenWriter.print("<identifier>" + tz.identifier() + "</identifier>\n");
            //'('
            doSymbol('(');
            //expressionList
            writer.print("<expressionList>\n");
            compileExpressionList();
            writer.print("</expressionList>\n");
            //')'
            doSymbol(')');
        }else {
            //error("'('|'.'");
        }
    }
    
    private void compileExpression(){
        writer.print("<expression>\n");

        //term
        compileTerm();
        //(op term)*
        do {
            tz.advance();
            //op
            if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.isOperation()){
                if (tz.sym() == '>'){
                    writer.print("<symbol>&gt;</symbol>\n");
                    tokenWriter.print("<symbol>&gt;</symbol>\n");
                }else if (tz.sym() == '<'){
                    writer.print("<symbol>&lt;</symbol>\n");
                    tokenWriter.print("<symbol>&lt;</symbol>\n");
                }else if (tz.sym() == '&') {
                    writer.print("<symbol>&amp;</symbol>\n");
                    tokenWriter.print("<symbol>&amp;</symbol>\n");
                }else {
                    writer.print("<symbol>" + tz.sym() + "</symbol>\n");
                    tokenWriter.print("<symbol>" + tz.sym() + "</symbol>\n");
                }
                //term
                compileTerm();
            }else {
                tz.undo();
                break;
            }

        }while (true);

        writer.print("</expression>\n");
    }
    
    private void compileExpressionList(){
        tz.advance();
        //determine if there is any expression, if next is ')' then no
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ')'){
            tz.undo();
        }else {

            tz.undo();
            //expression
            compileExpression();
            //(','expression)*
            do {
                tz.advance();
                if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == ','){
                    writer.print("<symbol>,</symbol>\n");
                    tokenWriter.print("<symbol>,</symbol>\n");
                    //expression
                    compileExpression();
                }else {
                    tz.undo();
                    break;
                }

            }while (true);

        }
    }
    
    private void doSymbol(char symbol){
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == symbol){
            writer.print("<symbol>" + symbol + "</symbol>\n");
            tokenWriter.print("<symbol>" + symbol + "</symbol>\n");
        }
    }
    
    private void compileSubroutine(){

        //determine whether there is a subroutine, next can be a '}'
        tz.advance();

        //next is a '}'
        if (tz.getTokenType() == JTokenz.tokenType.SYMBOL && tz.sym() == '}'){
            tz.undo();
            return;
        }

        //start of a subroutine
        if (tz.getTokenType() != JTokenz.tokenType.KEYWORD || (tz.findKeyword() != JTokenz.keywords.CONSTRUCTOR && tz.findKeyword() != JTokenz.keywords.FUNCTION && tz.findKeyword() != JTokenz.keywords.METHOD)){
            //error("constructor|function|method");
        }

        writer.print("<subroutineDec>\n");

        writer.print("<keyword>" + tz.getCurrTokenString() + "</keyword>\n");
        tokenWriter.print("<keyword>" + tz.getCurrTokenString() + "</keyword>\n");

        //'void' or type
        tz.advance();
        if (tz.getTokenType() == JTokenz.tokenType.KEYWORD && tz.findKeyword() == JTokenz.keywords.VOID){
            writer.print("<keyword>void</keyword>\n");
            tokenWriter.print("<keyword>void</keyword>\n");
        }else {
            tz.undo();
            compileType();
        }

        //subroutineName which is a identifier
        tz.advance();
        if (tz.getTokenType() != JTokenz.tokenType.IDENTIFIER){
            //error("subroutineName");
        }

        writer.print("<identifier>" + tz.identifier() + "</identifier>\n");
        tokenWriter.print("<identifier>" + tz.identifier() + "</identifier>\n");

        //'('
        doSymbol('(');

        //parameterList
        writer.print("<parameterList>\n");
        compileParameterList();
        writer.print("</parameterList>\n");

        //')'
        doSymbol(')');

        //subroutineBody
        compileSubroutineBody();

        writer.print("</subroutineDec>\n");

        compileSubroutine();

    }
}
