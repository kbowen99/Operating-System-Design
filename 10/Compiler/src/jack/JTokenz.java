package jack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JTokenz {
	
	/**
	 * Enumerations for type of token being processed
	 */
	public enum tokenType {
		KEYWORD,SYMBOL,IDENTIFIER,INT_CONSTANT,STRING_CONSTANT,NONE
	};
	
	/**
	 * All currently supported keywords
	 */
	public enum keywords {
		CLASS,METHOD,FUNCTION,CONSTRUCTOR,INT,BOOLEAN,CHAR,VOID,VAR,STATIC,FIELD,LET,DO,IF,ELSE,WHILE,RETURN,TRUE,FALSE,NULL,THIS,NONE
	};
	
	private Scanner scanner;
	private String currentString;
    private keywords currentToken;
    private tokenType currentTokenType;
    private int tokenIndex;
    private ArrayList<String> tokens;
    
    //Regex Sucks
    private static String symbolR = "[\\&\\*\\+\\(\\)\\.\\/\\,\\-\\]\\;\\~\\}\\|\\{\\>\\=\\[\\<]";
    private static String intR = "[0-9]+";
    private static String strR = "\"[^\"\n]*\"";
    private static String idR = "[\\w_]+";
    private static String keywordR = "class" + "|" + "constructor" + "|" + "function" + "|" + "method" + 
    		"|" + "field" + "|" + "static" + "|" + "var" + "|" + "int" + "|" + "char" + "|" + "boolean" + "|" + 
    		"void" + "|" + "true" + "|" + "false" + "|" + "null" + "|" + "this" + "|" + "let" + "|" + "do" +
    		"|" + "if" + "|" + "else" + "|" + "while" + "|" + "return";
    
    private Pattern tokenRegex(){
    	return Pattern.compile(keywordR + "|" + symbolR + "|" + intR + "|" + strR + "|" + idR);
    }
	
    /**
     * Starts Tokenizing the next file
     * @param input input file
     */
	public JTokenz(File input){
		try {//Ghetto Prevention
            scanner = new Scanner(input);
            String preprocessed = "";
            String line = "";

            //Clean every line of the file
            while(scanner.hasNext())
                if ((line = nuttinButStringz(scanner.nextLine()).trim()).length() > 0) 
                    preprocessed += line + "\n";

            preprocessed = unblockComment(preprocessed).trim();

            Matcher m = tokenRegex().matcher(preprocessed);
            tokens = new ArrayList<String>();
            tokenIndex = 0;

            while (m.find())
                tokens.add(m.group());

        } catch (FileNotFoundException e) {}//This was totally meant to be used in this way

        currentToken = keywords.NONE;
        currentTokenType = tokenType.NONE;
	}
	
	/**
	 * Sanitizes String input (Removes Comments)
	 * https://youtu.be/sZ78f1gUa5Q
	 * @param in
	 * @return Clean String
	 */
    public static String nuttinButStringz(String in){
    	if (in.contains("//"))
    		in = in.substring(0, in.indexOf("//"));
        return in;
    }
    
    /**
     * Block Comments are a thing, an annoying thing...
     * @param strIn
     * @return
     */
    public static String unblockComment(String in){
    	while (in.contains("/*")){
    		int sPos = in.indexOf("/*");
    		int ePos = in.indexOf("*/");
    		in = in.substring(0, sPos) + in.substring(ePos + 2);
    	}
        return in;
    }
    
    /**
     * @return Whether or not there are more tokens
     */
    public boolean hasMoreTokens() {
        return tokenIndex < tokens.size();
    }
    
    public void advance(){
        if (hasMoreTokens()) {
            currentString = tokens.get(tokenIndex);
            tokenIndex++;
        } else 
            throw new IllegalStateException("No more tokens");
        
        //Quick sort
        if (currentString.matches(keywordR))
            currentTokenType = tokenType.KEYWORD;
        else if (currentString.matches(symbolR))
            currentTokenType = tokenType.SYMBOL;
        else if (currentString.matches(intR))
            currentTokenType = tokenType.INT_CONSTANT;
        else if (currentString.matches(strR))
            currentTokenType = tokenType.STRING_CONSTANT;
        else if (currentString.matches(idR))
            currentTokenType = tokenType.IDENTIFIER;
        else
            throw new IllegalArgumentException("Unknown token:" + currentToken);
    }
    
    /**
     * @return current string
     */
    public String getCurrTokenString(){
    	return this.currentString;
    }
    
    /**
     * @return current token type
     */
    public tokenType getTokenType(){
    	return this.currentTokenType;
    }
    
    /**
     * finds the keyword. Yeah, its bad
     * @return current Keyword
     */
    public keywords findKeyword(){
    	String cs = currentString;
		if (cs.contains("class"))
			currentToken = keywords.CLASS;
		else if (cs.contains("method"))
			currentToken = keywords.METHOD;
		else if (cs.contains("function"))
			currentToken = keywords.FUNCTION;
		else if (cs.contains("constructor"))
			currentToken = keywords.CONSTRUCTOR;
		else if (cs.contains("int"))
			currentToken = keywords.INT;
		else if (cs.contains("boolean"))
			currentToken = keywords.BOOLEAN;
		else if (cs.contains("char"))
			currentToken = keywords.CHAR;
		else if (cs.contains("void"))
			currentToken = keywords.VOID;
		else if (cs.contains("var"))
			currentToken = keywords.VAR;
		else if (cs.contains("static"))
			currentToken = keywords.STATIC;
		else if (cs.contains("field"))
			currentToken = keywords.FIELD;
		else if (cs.contains("let"))
			currentToken = keywords.LET;
		else if (cs.contains("do"))
			currentToken = keywords.DO;
		else if (cs.contains("if"))
			currentToken = keywords.IF;
		else if (cs.contains("else"))
			currentToken = keywords.ELSE;
		else if (cs.contains("while"))
			currentToken = keywords.WHILE;
		else if (cs.contains("return"))
			currentToken = keywords.RETURN;
		else if (cs.contains("true"))
			currentToken = keywords.TRUE;
		else if (cs.contains("false"))
			currentToken = keywords.FALSE;
		else if (cs.contains("null"))
			currentToken = keywords.NULL;
		else if (cs.contains("this"))
			currentToken = keywords.THIS;
    	return currentToken;
    }
    
    /**
     * I honestly dont remember what I was going to use this for...
     * Deprecated?...
     * @return
     */
    public char sym(){
    	if (currentTokenType == tokenType.SYMBOL)
    		return this.currentString.toCharArray()[0];
    	throw new IllegalStateException("You Cant do That");
    }
    
    /**
     * @return current ID
     */
    public String identifier(){
    	if (currentTokenType == tokenType.IDENTIFIER)
    		return currentString;
    	throw new IllegalStateException("You Cant do That");
    }
    
    /**
     * @return int value of token
     */
    public int intValue(){
    	if (currentTokenType == tokenType.INT_CONSTANT)
    		return Integer.parseInt(currentString);
    	throw new IllegalStateException("You Cant do That");
    }
    
    /**
     * @return String of String Constant
     */
    public String stringVal(){
    	if (currentTokenType == tokenType.STRING_CONSTANT)
    		return currentString.substring(1, currentString.length() - 1);
    	throw new IllegalStateException("You Cant do That");
    }
    
    /**
     * Jumps back to previous token
     */
    public void undo(){
    	tokenIndex--;
    }
    
    /**
     * @return Whether or not it is an operation
     */
    public boolean isOperation(){
        return "+ - * / & | < > = ".contains(sym() + "");
    }
}
