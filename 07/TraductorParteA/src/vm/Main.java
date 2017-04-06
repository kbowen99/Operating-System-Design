package vm;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	/*
	 * Source .VM File/Directory
	 */
	public static String inputDir;
	/*
	 * Destination .Hack Files
	 */
	public static String outDir;
	
	
	public static ArrayList<File> vmFiles = new ArrayList<>();

	public static void main(String[] args) {
		String[] tst = {"C:\\Users\\kurti\\Google Drive\\Programs\\NAND2\\Operating-System-Design\\07\\StackArithmetic\\SimpleAdd\\"};args = tst;
		
		/*
		 * File Input
		 */
		if (args.length < 1){
			System.out.println("No File/Dir Specifified. Please Enter One Now:");
			Scanner lazyPerson = new Scanner(System.in);
			inputDir = lazyPerson.nextLine();
			lazyPerson.close();
		} else 
			inputDir = args[0];
		
		/*
		 * File Handling
		 */
		if ((new File(inputDir).isFile()))
			if ((new File(inputDir)).getAbsolutePath().contains(".vm")){
				File tmp = new File(inputDir);
				vmFiles.add(tmp);
				outDir = tmp.getAbsolutePath().substring(0, tmp.getAbsolutePath().lastIndexOf(".")) + ".asm";
			} else
				throw new IllegalArgumentException("Someone Does Not Understand that this program translates .vm to .asm");
		else if ((new File(inputDir)).isDirectory()){
			for (File f : (new File(inputDir)).listFiles())
				if (f.getName().endsWith(".vm"))
					vmFiles.add(f);
			outDir = (new File(inputDir)).getAbsolutePath() + "\\" +  (new File(inputDir)).getName() + ".asm";
		}
		
		/*
		 * Quick Feedback
		 */
		System.out.println("Recognized Files:");
		for (File f : vmFiles)
			System.out.println(f.getName());
		System.out.println("Outputting Files to: \n" + outDir);
		
		/*
		 * That Actual Translating Part....
		 */
		File fOut = new File(outDir);
		CodeWriter write = new CodeWriter(fOut);
		
		for (File f : vmFiles){
			write.setFileName(f);
			Parser p = new Parser(f);
			
			while (p.hasMoreCommands()){
				p.advance();
				switch (p.getCommand()){
				case MATHS:
					write.writeMaths(p.getMath());break;
				case POP:
				case PUSH:
					write.writePushPop(p.getCommand(), p.getArg1(), p.getArg2());break;
				case LABEL:
					write.writeLabel(p.getArg1());break;
				case GOTO:
					write.writeGoTo(p.getArg1());break;
				case IF:
					write.writeIF(p.getArg1());break;
				case RETURN:
					write.ASMReturn();break;
				case FUNCTION:
					write.writeFunction(p.getArg1(), p.getArg2());break;
				case CALL:
					write.writeCall(p.getArg1(), p.getArg2());
				case NONE:
				default:
					break;
				}
			}
		}
		
		write.kill();
		System.out.println("Translation Complete.");
	}

}
