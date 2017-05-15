package jack;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		String fileInput;
		//input as command argument, or runtime argument (Convienent for Eclipse)
		if (args.length != 1){
			System.out.println("Looks like you're forgetting something. Would you like to specify a file/directory now?");
			System.out.println("Target: ");
			Scanner fScan = new Scanner(System.in);
			fileInput = fScan.nextLine();
			fScan.close();
		} else
			fileInput = args[0]; //CLI

		//Sloppy Initialization
        File fileIn = new File(fileInput);
        String fileOutPath = "", tokenFileOutPath = "";
        File fileOut,tokenFileOut;
        ArrayList<File> jFiles = new ArrayList<File>();

        //To directory or not to directory, there is no filing
        if (fileIn.isFile()) {
            String path = fileIn.getAbsolutePath();
            if (!path.endsWith(".jack")) 
                throw new IllegalArgumentException("You dont know Jack! Only .Jack Files Supported");
            jFiles.add(fileIn);
        } else if (fileIn.isDirectory()) {
            jFiles = listJacks(fileIn);
            if (jFiles.size() == 0)
            	throw new IllegalArgumentException("You dont know Jack! Only .Jack Files Supported");
        }

        //Runs tokenizer for each file in queue
        for (File f: jFiles) {
            fileOutPath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + ".xml";
            tokenFileOutPath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + "T.xml";
            fileOut = new File(fileOutPath);
            tokenFileOut = new File(tokenFileOutPath);

            Crawler creepy = new Crawler(f,fileOut,tokenFileOut);
            creepy.compileClass();
            
            //Extremely useful when you suck at / vs \
            System.out.println("File created : " + fileOutPath);
            System.out.println("File created : " + tokenFileOutPath);
        }
	}

	/**
	 * Helper Method for the lazy, finds and <Array>lists all .Jack files
	 * Stolen from Compiler1
	 * @param dir
	 * @return
	 */
	private static ArrayList<File> listJacks(File dir){
        File[] files = dir.listFiles();
        ArrayList<File> jacks = new ArrayList<File>();
        for (File f : files)
            if (f.getName().endsWith(".jack"))
                jacks.add(f);
        return jacks;

    }
}
