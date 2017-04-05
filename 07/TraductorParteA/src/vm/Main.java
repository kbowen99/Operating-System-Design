package vm;

import java.io.File;
import java.io.FilenameFilter;
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
		//String[] tst = {"C:\\Users\\kurti\\Google Drive\\Programs\\NAND2\\Operating-System-Design\\07\\StackArithmetic\\SimpleAdd\\"};args = tst;
		
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
	}

}
