import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/** NFPath is a class designed to handle the output of our automated
 * microscopy experiments.
 * @author Adam Waite
 * @version 0.1
 */
public class NFPath {
	private static final short TURRETS = 6;
	private static int timepoint = 0;
	private static int position = 0;

	// File name for the root directory that we will search
	private File project_path;

	// Maximum numbers of Position folders, wavelength folders
	// and timepoints, respectively
	private int nPos = 0;
	private int nWls = 0;
	private int nTps = 0;

	// Holds file paths for the "highest" containing directory
	private File[] positions;
	// Holds "full" paths for .tif files (up to the root directory project_path)
	private File[] paths;

	// Boolean flag, for testing purposes
	private final boolean DEBUG = false;

	// This file filter accepts all directories that contain a string 
	// with a captial letter, followed by number and possibly
	// a lower case letter
	FilenameFilter position_filter = new FilenameFilter() {
		public boolean accept (File dir, String str) {
			return (str.matches("[A-Z][0-9]+[a-z]?"));
			
		}
	};

	// This file filter accepts all directories that contain a string 
	// with WL followed by a number (that is; any 
	// wavelength directory)
	FilenameFilter wl_filter = new FilenameFilter() {
		public boolean accept (File dir, String str) {
			return (str.matches("WL[0-9]+") );
		}
	};

	// This file filter accepts all files that end with .tif
	FilenameFilter tp_filter = new FilenameFilter() {
		public boolean accept (File dir, String str) {
			return str.endsWith(".tif");
		}
	};

	/*
	 * Constructs an NFPath object.
	*/
	public NFPath(File path) {
		project_path = path;

		try {
			positions = project_path.listFiles(position_filter);
			
			// If no folders match the condition set by a position filter
			// In form the user and exit
			if (positions.length == 0) 
				throw new Exception("No folders match position filter");
			Arrays.sort(positions);
			if (positions.length > nPos)
				nPos = positions.length;

			// Store the writing index for the paths array
			int writeIndex = 0;
			
			// Iterate through each file that passed the position filter
			// and collect all of the wavelengths in in File[] 
			for (int p=0; p<positions.length; p++) {
				File [] wls = positions[p].listFiles(wl_filter);

				// If there are no wavelength files in the directory,
				// Inform the user 
				if (wls.length == 0) 
					throw new Exception("No folders match wavelength filter");
				Arrays.sort(wls);

				// Update nWls 
				if (wls.length > nWls) 
					nWls = wls.length;

				/* DEBUGGING CODE
				 *	This code prints out all of the directories that are subdirectories
				 * of the directory at index p of the positions File array
				 */
				if(DEBUG){
					System.out.println("Directories in directory " + positions[p] );
					for(int i = 0; i < wls.length; i ++){
						System.out.println(wls[i].getName());
					}
					System.out.println();
				}
				// END DEBUGGING CODE */				

				for (int w=0; w<wls.length; w++) {
					File[] tps = wls[w].listFiles(tp_filter);
					if (tps.length == 0) 
						throw new Exception("No folders match timepoint filter");
					Arrays.sort(tps);

					/* DEBUGGING CODE
					 *	This code prints out all of the files that are in the  
					 *  directory at index w of the wls File array
					 */
					if(DEBUG){
						System.out.println("Files in directory " + wls[w] );
						for(int i = 0; i < tps.length; i ++){
							System.out.println(tps[i].getName());
						}
						System.out.println();
					}
					// END DEBUGGING CODE */	

					// On the first loop, initialize paths array.  The number of
					// timepoints for any position/wavelength will be =< the number
					// of timepoints at the first positoin/wavelength.
					if (p==0 && w==0) 
						paths = new File[positions.length*wls.length*tps.length];
					if (tps.length > nTps) 
						nTps = tps.length;

					// Write the .tif file names to open spots in the paths array
					for (int i=0; i<tps.length; i++) {
						paths[i + writeIndex] = tps[i];
					}
					// Increment the writeIndex to the proper location
					writeIndex += tps.length;


					/* DEBUGGING CODE */
					if(DEBUG){
						System.out.println("Listing all paths " + wls[w] );
						this.listAllPaths();
						System.out.println();
					}
					// END DEBUGGING CODE */	
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}

	/** Returns the File object representing the project path.
	 * @return A file object
	 */
  public File projectPath() { return project_path; }

	/** Set the current position with a position name.
	 * @param p_string a string corresponding to the position name.
	 */
	public void setPosition(String p_string) {
		for (int i=0; i<positions.length; i++) {
			if ( positions[i].getName().matches(p_string) ){
				position = i; 
				break;
			}
		}
	}
	

	/** Set the current position to the int specified 
	 * @param p an int to set the position to
	 */	
	public void setPosition(int p) { 
		if( p >= 0 && p < nPos){
			position = p; 
		} else{
			System.err.println("ERROR: " + p + " is not between 0 and " + nPos + ". Keeping position at " + position + ".");
		}
	}

	/** Return the current position, as an int
	 * @ return the current position index
	 */
	public int currentPositionIndex() { return position; }

	/* Return the file at the currentPosition
	 * @ return the file at current Position
	 */
	public File currentPosition() { return positions[position]; }

	/** Resets the timepoint to a different value.
	 * @param t the timepoint to set.  Defaults to zero.
	 */
	public void setTimepoint(int t) { 
		if(t < nTps && t >= 0 ){
			timepoint = t; 
		}
		else{
			System.err.println("Value " + t + " is not between 0 and " + nTps + ". Timepoint remains " + timepoint + ".");
		}
	}

	/*
	 * Sets the timepoint to the default value of 0
	 */
	public void setTimepoint(){
		setTimepoint(0);
	}

	/** Returns the current timepoint.
	 */
	public int currentTimepoint() { return timepoint; }


	/** Returns all of the file in positions
	 */
	public File[] getPositions(){
		return positions;
	} 
	
	/** Returns all of the file in positions
	 */
	public File[] getPaths(){
		return paths;
	} 
	
	/** Returns all files of the current timepoint and position.
	 * @param t the timepoint to retrieve.
	 * @return an array of paths as <code>File</code> objects.
	 */
	public File[] getFiles(int p, int t) {
		return nfMatchPT(positions[p].getName(),Integer.toString(t));
	}

	/* Retuns all files of this timepoint and specified position
	 *
	 */
	public File[] getFiles(String pos) {
		return nfMatchPT(positions[positionIndex(pos)].getName(), Integer.toString(timepoint));
	}

	/* Retuns all files of the specified position and time
	 *
	 */
	public File[] getFiles(String pos, int t) {
		return nfMatchPT(positions[positionIndex(pos)].getName(), Integer.toString(t));
	}
	
	/* Returns all files of the current timepoint and position
	 *
	 */
	public File[] getFiles() {
		return getFiles(position, timepoint);
	}

	/** Returns the files associated with a timepoints and increments the
	 * current timepoint.
	 * @return an array of paths as <code>File</code> objects.
	 */
	public File[] getNextTimepoint() {
		if (timepoint >= nTps) 
			return null;
		return getFiles(position, timepoint++);
	}

	/** Returns the files associated with a wavelength
	 *
	 */
	public File[] getFilesWavelength(String wav){
		return nfMatchW(wav);
	}

	/** Returns the files associated with a position
	 *
	 */
	public File[] getFilesPosition(String pos){
		return nfMatchP(pos);
	}
	
	/** Returns the files associated with a time
	 *
	 */
	public File[] getFilesTime(int t){
		return nfMatchT(Integer.toString(t));
	}

	/** Returns the files associated with a time and wavelength
	 *
	 */
	public File[] getFilesTimeWavelength(int t, String wav){
		return nfMatchTW(Integer.toString(t), wav);
	}

	/* Returns the files associated with a Position and Wavelength
	 *
	 */
	public File[] getFilesPositionWavelength(String pos, String wav){
		return nfMatchPW(pos, wav);
	}

	/* Returns the files associated with a position, time
	 * and wavelength. This should uniquely determine a single file
	 */
	public File[] getFiles(String pos, int t, String wav){
		return nfMatch(pos, Integer.toString(t), wav);
	}

	/* Return the index at which positions[] contains string pos
	 * Returns -1 if not found
	 */
	public int positionIndex(String pos) {
		for (int i=0; i<positions.length; i++) {
			if (positions[i].getName().matches(pos)) {
				return i;
			}
		}
		return -1; 
	}

	/* Lists all position files stored in the File[] positions
	 *
	 */
	public void listAllPositions(){
		for(int i =0; i < positions.length; i++){
			System.out.println(positions[i].getName());
		}
	}

	/* Lists all paths files stored in the File[] positions
	 *
	 */
	public void listAllPaths(){
		for(int i =0; i < paths.length; i++){
			System.out.println(paths[i]);
		}
	}

	/* Lists all .tif files by the file name, not the path
	 *
	 */
	public void listAllTifs(){
		for(int i =0; i < paths.length; i++){
			System.out.println(paths[i].getName());
		}
	}

	// Prints the nPos, nWls, and nTps
	public void printns(){
		System.out.println("nPos is " + nPos + ", nWls is " + nWls + ", and nTps is " + nTps);
	}

	public int getnPos(){
		return nPos;
	}

	public int getnWls(){
		return nWls;
	}

	public int getnTps(){
		return nTps;
	}


	/* --------------- Private Methods ----------------------------- */

	/* Generates a File[]  of all files that match a, b, and c
	 * where b is the timepoint, a is the position, and c is the wavelength
	 */
	private File[] nfMatch(String a, String b, String c) {
		File[] res;
		Vector<File> tmp = new Vector<File>();
		// System.out.println(a+"\t"+b+"\t"+c);
		// b is the timepoint, a is the position, c is the wavelength
		String matchThis = ".*"+ b + "_.*" + a + "_.*" +  c + ".*" ;
		Pattern pattern = Pattern.compile(matchThis);
		//System.out.println(pattern.pattern());
		// For every file in the paths []
		for (File p : paths) {
			//System.out.println(p.getName());
			Matcher matcher = pattern.matcher(p.getName());
			boolean found = matcher.matches();
			if(found){
			// If there is a match, then print it out and add it to the vector
			//if (p.getName().matches(a) && p.getName().matches(b) && p.getName().matches(c)) {
				System.out.println("Found: " + p);
				tmp.add(p);
			}
		}
		// If no matches were found, inform the user
		if (tmp.size() == 0)
			System.err.println("No matches found for '"+a+"', '"+b+"', "+c+" in "+project_path);
		// Copy the vector into the array
		res = new File[tmp.size()];
		for (int i=0; i<tmp.size(); i++) {
			res[i] = tmp.elementAt(i);
		}
		return res;
	}

	/* Matches a timepoint 'b' and a position 'a'
	 *
	 */
	private File[] nfMatchPT(String a, String b) {
		return nfMatch(a,b,"");
	}

	/* Matches a wavelength 'c' and a position 'a'
	 *
	 */
	private File[] nfMatchPW(String a, String c) {
		return nfMatch(a,"", c);
	}

	/* Matches a wavelength 'c' and a timepoint 'b'
	 *
	 */
	private File[] nfMatchTW(String b, String c) {
		return nfMatch("", b, c);
	}

	/* Matches a position 'a'
	 *
	 */
	private File[] nfMatchP(String a) {
		return nfMatch(a,"","");
  	}

	// Matches a timepoint 'b'
	private File[] nfMatchT(String b){
		return nfMatch("", b, "");
	}

	// Matches a wavelength 'c'
	private File[] nfMatchW(String c){
		return nfMatch("", "", c);
	}

	// 
}
