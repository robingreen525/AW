import java.io.File;
import org.fhcrc.honeycomb.nfpath.NFObject;
import org.fhcrc.honeycomb.nfpath.NFPath;

public class test_NFPath{
	public static void main (String[] arguments){
		// If there is an error, inform the user and exit gracefully
		if(arguments.length != 1){
			System.out.println("Usage: java test_NFPath [top directory to search].");
			System.exit(1);
		}

		// Make a file object for the root of the directory that we want to search
		File path = new File(arguments[0]);
		
		NFPath testPath = new NFPath(path);

		testPath.printns();

		// Print out the project path 
		System.out.println("Printing project path:");
		System.out.println(testPath.projectPath().toString() + '\n');

		// Print out all position directories and full paths
		System.out.println("Listing all position directories:");
		testPath.listAllPositions();
		System.out.println();
		System.out.println("Listing all paths of \".tif\" files:");
		testPath.listAllPaths();
		System.out.println("Listing all \".tif\" files:");
		testPath.listAllTifs();

		printWhere(testPath);
		System.out.println("Setting timepoint to 2");
		testPath.setTimepoint(2);
		printWhere(testPath);
		System.out.println("Setting timepoint to 12. Shouldn't allow this");
		testPath.setTimepoint(12);
		printWhere(testPath);

		System.out.println("The folder at position " + testPath.currentPositionIndex() + " is " + testPath.currentPosition().getName());
		System.out.println("Folder with A01 found at index " + testPath.positionIndex("A01"));
		System.out.println("Manually setting position to 1");
		testPath.setPosition(1);
		// Position should now be 2
		printWhere(testPath);
		System.out.println("Setting position to A01 (position 0)");
		testPath.setPosition("A01");
		// Position should now be 0
		printWhere(testPath);

		testPath.setPosition(0);
		testPath.setTimepoint(0);
		for(int i = 0; i < testPath.getnPos(); i++){
			for(int j = 0; j < testPath.getnTps(); j++){
				testPath.setPosition(i);
				testPath.setTimepoint(j);
				System.out.println();
				printWhere(testPath);
				File[] files = testPath.getFiles();
			}
		}
		System.out.println();
		
		System.out.println("Getting all files with A01 at timepoint 5");
		File[] files = testPath.getFiles("A01");

		System.out.println();
		testPath.setPosition(0);
		testPath.setTimepoint(0);
		System.out.println("RESETTING  TIME AND POSITION TO 0");
		printWhere(testPath);
		files = testPath.getNextTimepoint();
		System.out.println();
		while(files != null){
			printWhere(testPath);
			files = testPath.getNextTimepoint();
			System.out.println();
		}
		System.out.println("Reached null. Output sucessfully written.");
		System.out.println();

		System.out.println("Testing wavelength matcher for wavelength WL0");
		files = testPath.getFilesWavelength("WL0");

		System.out.println();
		System.out.println("Testing position, time. and wavelength matcher for \"A01\", \"1\", and \"WL0\"");
		files = testPath.getFiles("A01", 1, "WL0");

		System.out.println();
		System.out.println("Testing position matcher for \"A01\"");
		files = testPath.getFilesPosition("A01");

		System.out.println();
		System.out.println("Testing position matcher for timepoint 1");
		files = testPath.getFilesTime(1);

		System.out.println();
		System.out.println("Testing position matcher for timepoint 2 and wavelength \"WL1\"");
		files = testPath.getFilesTimeWavelength(2, "WL1");

		System.out.println();
		System.out.println("Testing position matcher for position \"A02\" and wavelength \"WL1\"");
		files = testPath.getFilesPositionWavelength("A02", "WL1");
		
	}

	public static void printWhere(NFPath testPath){
		System.out.println("Currently, we are at position "+ testPath.currentPositionIndex() + " and timepoint " + testPath.currentTimepoint());
	}
}
