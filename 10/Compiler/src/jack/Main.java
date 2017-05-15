package jack;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Tries to tokenize Jack Files. Doesnt do JACK
 * @author Kbowen99 
 *
 */
public class Main {

	/**
	 * Does Jack
	 * @param args
	 */
	public static void main(String[] args) {
		String fileInput;
		if (args.length != 1){
			System.out.println("Looks like you're forgetting something. Would you like to specify a file/directory now?");
			System.out.println("Target: ");
			Scanner fScan = new Scanner(System.in);
			fileInput = fScan.nextLine();
			fScan.close();
		} else
			fileInput = args[0];

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

        for (File f: jFiles) {

            fileOutPath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + ".xml";
            tokenFileOutPath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + "T.xml";
            fileOut = new File(fileOutPath);
            tokenFileOut = new File(tokenFileOutPath);

            Crawler compilationEngine = new Crawler(f,fileOut,tokenFileOut);
            compilationEngine.compileClass();

            System.out.println("File created : " + fileOutPath);
            System.out.println("File created : " + tokenFileOutPath);
        }

	}
	
	/**
	 * Helper Method for the lazy, finds and <Array>lists all .Jack files
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
