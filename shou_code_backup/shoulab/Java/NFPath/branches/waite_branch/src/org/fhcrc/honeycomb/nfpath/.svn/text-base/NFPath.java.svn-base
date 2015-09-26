// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :

package org.fhcrc.honeycomb.nfpath;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Date;

/** NFPath is a class designed to handle the output of our automated
 * microscopy experiments.
 * @author Adam Waite
 * @version %I%,%G%
 */
public class NFPath {
	private static final short TURRETS = 6;
	private static int timepoint = 0;
	private static int position = 0;
	private static String[] positions;

	// File name for the root directory that we will search
	private File project_path;

  // This defaults to the aquisition time of the first object made.
  private static long experiment_start_time = 0;

	// Maximum numbers of position folders, wavelength folders
	// and timepoints, respectively.
	private int nPos = 0;
	private int nWls = 0;
	private int nTps = 0;


  // The MCImages
  private NFObject[] objs;

	// Boolean flag, for testing purposes
	private final boolean DEBUG = false;

  private static final int TIMEPOINT_DIGITS = 4;
  private static final String POSITION_PATTERN = "[A-Z]\\d+[a-z]?"; 
  private static final String WAVELENGTH_PATTERN = "WL\\d"; 
  private static final String FILE_REGEX = 
    ".*_\\d{4}_[A-Z]\\d{2}[a-z]?_WL\\d\\.tif";

  // A filter for directories.
  public static FilenameFilter filter(final String regex) {
    // Anonymous inner class.
    return new FilenameFilter() {
      private Pattern pattern = Pattern.compile(regex);
      public boolean accept(File dir, String str) {
        return pattern.matcher(str).matches();
      }
    };
  }


	/**
	 * Constructs an NFPath object.
   * @param path a path to the folder containing the experiment.
	*/
	public NFPath(File path) {
		project_path = path;

		try {
			File[] pos_paths = project_path.listFiles(filter(POSITION_PATTERN));
      positions = new String[pos_paths.length];
      Vector<NFObject> temp_objs = new Vector<NFObject>();
			
			// If no folders match the condition set by a position filter
			// Inform the user and exit.
			if (pos_paths.length == 0) throw new Exception(
            "In '" + project_path + "':" +
            "No folders match position filter");
			Arrays.sort(pos_paths);
			if (pos_paths.length > nPos)
				nPos = pos_paths.length;

			// Store the writing index for the paths array
      int idx = 0;
			
			// Iterate through each file that passed the position filter
			// and collect all of the wavelengths in in File[] 
			for (int p=0; p<pos_paths.length; p++) {
        positions[p] = pos_paths[p].getName();
				File [] wls = pos_paths[p].listFiles(filter(WAVELENGTH_PATTERN));

				// If there are no wavelength files in the directory,
				// Inform the user 
				if (wls.length == 0) 
					throw new Exception(
              "In '" + pos_paths[p] + "':" +
              "No folders match wavelength filter");
				Arrays.sort(wls);

				// Update nWls 
				if (wls.length > nWls) 
					nWls = wls.length;

				/* DEBUGGING CODE
				 *	This code prints out all of the directories that are
         *	subdirectories of the directory at index p of the
         *	positions File array
				 */
				if(DEBUG){
					System.out.println("Directories in directory " + pos_paths[p] );
					for(int i = 0; i < wls.length; i ++){
						System.out.println(wls[i].getName());
					}
					System.out.println();
				}
				// END DEBUGGING CODE */				

				for (int w=0; w<wls.length; w++) {
					File[] tps = wls[w].listFiles(filter(FILE_REGEX));
					if (tps.length == 0) 
						throw new Exception(
                "In '" + pos_paths[p] + "', '" + wls[w] + "':" +
                "No folders match timepoint filter");
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
					// of timepoints at the first position/wavelength.
					if (p==0 && w==0) {
            temp_objs.ensureCapacity(pos_paths.length*wls.length*tps.length);
          }
					if (tps.length > nTps) 
						nTps = tps.length;

					// Write the .tif file names to open spots in the paths array
					for (int i=0; i<tps.length; i++) {
            temp_objs.add(new NFObject(tps[i]));
					}
				}
			}
      // Copy vector to array.
      objs = new NFObject[temp_objs.size()];
      objs = (NFObject[]) temp_objs.toArray(objs);

      // Set the start time to the first image.
      experiment_start_time = objs[0].aquisitionTime();
		} catch (Exception e) {
      e.printStackTrace(System.err);
		}
	}

	/** Returns the project path.
	 * @return the project path.
	 */
  public File projectPath() { return project_path; }

	/** Set the current position by name.
	 * @param p_string a string corresponding to the position name.
	 */
	public void setPosition(String p_string) {
		for (int i=0; i<positions.length; i++) {
			if ( positions[i].matches(p_string) ){
				position = i; 
				break;
			}
		}
    System.err.println("Didn't find a position called '" + p_string +"'.");
	}
	
	/** Set the current position by number.
	 * @param p an <code>int</code> to set the position to.
	 */	
	public void setPosition(int p) { 
		if( p >= 0 && p < nPos){
			position = p; 
		} else {
      System.err.println(
          "ERROR: " + p + " is not between 0 and " + nPos +
          ". Keeping position at " + position + ".");
		}
	}
	/** Return all positions.
	 * @return all positions.
	 */
	public String[] allPositions() { return positions; }

	/** Return the current position, as an int.
	 * @return the current position index.
	 */
	public int currentPositionIndex() { return position; }

	/** Return the current position.
	 * @return the current position.
	 */
	public String currentPosition() { return positions[position];
  }

	/** Sets the timepoint to the specified value.
	 * @param t the timepoint of interest. Defaults to zero.
	 */
	public void setTimepoint(int t) { 
		if(t < nTps && t >= 0 ){
			timepoint = t; 
		}
		else{
      System.err.println(
          "Value " + t + " is not between 0 and " + nTps + "." +
          " Timepoint remains " + timepoint + "."
      );
		}
	}

	/** Sets the timepoint to the default value of 0.
	 */
	public void setTimepoint(){
		setTimepoint(0);
	}

	/** Returns the current timepoint.
   * @return the current timepoint.
	 */
	public int currentTimepoint() { return timepoint; }


	/** Returns all of the experiment paths up to their positions.
   * @return an array of paths up to the position folder.
	 */
	public String[] getAllPositions() {
		return positions;
	} 

  public NFObject[] getAll() {
    return objs;
  }
	
	/** Return all of the image paths.
	 */
	public File[] getAllImagePaths(){
    File[] paths = new File[objs.length];
    for (int i=0; i<objs.length; i++) {
      paths[i] = objs[i].imagePath();
    }
		return paths;
	} 

	/** Return all of the info paths.
	 */
	public File[] getAllInfoPaths(){
    File[] paths = new File[objs.length];
    for (int i=0; i<objs.length; i++) {
      paths[i] = objs[i].infoPath();
    }
		return paths;
	} 


  /*----------------------------------------------------
   * 
   * Public methods that return indeces. 
   *
   * ----*/

	/** Return the index representing the specified position.
   * @param pos the position to find.
   * @return the index of the position.  Returns -1 if not found.
	 */
	public int positionIndex(String pos) {
		for (int i=0; i<positions.length; i++) {
			if (positions[i].matches(pos)) {
				return i;
			}
		}
		return -1; 
	}

  /** Returns the number of positions for this object.
   * @return the number of positions for this object.
   */
	public int getnPos(){
		return nPos;
	}

  /** Returns the number of wavelengths for this object.
   * @return the number of wavelengths for this object.
   */
	public int getnWls(){
		return nWls;
	}

  /** Returns the number of timepoints for this object.
   * @return the number of timepoints for this object.
   */
	public int getnTps(){
		return nTps;
	}


  /*----------------------------------------------------
   * 
   * Public methods that print messages
   *
   * ----*/

	/** Lists all position files stored in the File[] positions
	 */
	public void listAllPositions(){
		for(int i =0; i < positions.length; i++){
			System.out.println(positions[i]);
		}
    System.out.println("\n");
	}

	/** Lists all paths to image files stored in the
   * <code>NFObject</code> array.
	 */
	public void listAllPaths(){
    for (int i=0; i<objs.length; i++) {
      System.out.println(objs[i].imagePath());
		}
    System.out.println("\n");
	}

	/** Lists all paths to info files stored in the
   * <code>NFObject</code> array.
	 */
	public void listAllInfoPaths(){
    int i = 0;
    while (objs[i] != null) {
			System.out.println(objs[i++].infoPath());
		}
    System.out.println("\n");
	}

	/** Lists all .tif files by the file name, not the path.
	 */
	public void listAllTifs(){
		for(int i =0; i < objs.length; i++){
			System.out.println(objs[i].imagePath().getName());
		}
	}

	/** Prints the number of positions, wavelengths, and timepoints for
   * this object.
   */
	public void printNumbers(){
		System.out.println(
        "nPos is " + nPos + ", nWls is " + nWls + ", and nTps is " + nTps);
	}

  /*----------------------------------------------------
   * 
   * Public methods that find and return NFObjects 
   *
   * ----*/
	/** Returns the next position available and sets the current position
   * to the next position.
	 * @return the next position.  Returns <code>null</code> and
   * remains at current position if no more positions are available.
	 */
	public String nextPosition() {
    // Return 'null' if this is the final position.
		if (position >= nPos-1) return null;
    // Otherwise, set the position to the next position
    // and return the next position.
    return positions[++position];
	}

	/** Returns the <code>NFObject</cod>s associated with the specified
   * position and the current timepoint, then increments the timepoint.
   * Since the number of timepoints for a given position/wavelength
   * combination is not guaranteed to be constant, a <code>null</code> return
   * from <code>nfMatch</code> is used to indicate that no more timepoints
   * remain.
	 * @return an array of <code>NFObject</code>s.  Returns <code>null</code>
   * if no more timepoints are available.
	 */
	public NFObject[] nextTimepoint(String pos) {
    return nfMatchPosTp(pos, timepoint++);
	}

	/** Returns the <code>NFObject</cod>s associated with the
   * current timepoint and position, then increments the timepoint.
   * Since the number of timepoints for a given position/wavelength
   * combination is not guaranteed to be constant, a <code>null</code> return
   * from <code>nfMatch</code> is used to indicate that no more timepoints
   * remain.
	 * @return an array of <code>NFObject</code>s. Returns <code>null</code>
   * if no more timepoints are available.
	 */
	public NFObject[] nextTimepoint() {
		return nfMatchPosTp(positions[position], timepoint++);
	}

	/** Find all <code>NFObject</cod>s at the given position.
   * @param pos the position to find.
	 * @return an array of <code>NFObject</code>s matching the given position.
   * Returns <code>null</code> if no matches are found.
	 */
	public NFObject[] nfMatchPos(String pos) {
    return nfMatch(pos, ".+?", ".+?");
  }

	/** Find all <code>NFObject</cod>s at the given wavelength.
   * @param wl the wavelength to find.
	 * @return an array of <code>NFObject</code>s matching
   * the given wavelength.
   * Returns <code>null</code> if no matches are found.
	 */
	public NFObject[] nfMatchWl(int wl) {
    return nfMatch(".+?", Integer.toString(wl), ".+?");
  }

	/** Find all <code>NFObject</cod>s at the given timepoint.
   * @param tp the timepoint to find.
	 * @return an array of <code>NFObject</code>s matching
   * the given timepoint.
   * Returns <code>null</code> if no matches are found.
	 */
	public NFObject[] nfMatchTp(int tp) {
    return nfMatch(".+?", ".+?", Integer.toString(tp));
  }

	/** Find all <code>NFObject</cod>s at the given position and timepoint.
   * @param pos the position to find.
   * @param tp  the timepoint to find.
	 * @return an array of <code>NFObject</code>s matching
   * the given position and timepoint.
   * Returns <code>null</code> if no matches are found.
	 */
	public NFObject[] nfMatchPosTp(String pos, int tp) {
    return nfMatch(pos, ".+?", Integer.toString(tp));
  }

	/** Find all <code>NFObject</cod>s at the given position and wavelength.
   * @param pos the position to find.
   * @param wl  the wavelength to find.
	 * @return an array of <code>NFObject</code>s matching
   * the given position and wavelength.
   * Returns <code>null</code> if no matches are found.
	 */
	public NFObject[] nfMatchPosWl(String pos, int wl) {
    return nfMatch(pos, Integer.toString(wl),".+?");
  }

	/** Find all <code>NFObject</cod>s at the given wavelength and timepoint.
   * @param wl  the wavelength to find.
   * @param tp the timepoint to find.
	 * @return an array of <code>NFObject</code>s matching
   * the given wavelength and timepoint.
   * Returns <code>null</code> if no matches are found.
	 */
	public NFObject[] nfMatchWlTp(int wl, int tp) {
    return nfMatch(".+?", Integer.toString(wl), Integer.toString(tp));
  }

  /** Find <code>NFObject</code>s meeting the indicated criteria.  This
   * is the main searching method. Returns <code>null</code> if no object
   * matching the criteria is found.
   * @param pos a <code>String</code> representing a position.
   * @param wl  a <code>String</code> representing a wavelength.
   * @param tp  a <code>String</code> representing a timepoint.
   * @return an array of <code>NFObject</code>s matching the given criteria,
   * or 'null' if nothing is found.
   */
	public NFObject[] nfMatch(String pos, String wl, String tp) {
      NFObject[] res = null;
    try {
      Vector<NFObject> tmp = new Vector<NFObject>();
      // Pad the timepoint with the appropriate number of zeros.
      if (tp.matches("\\d+")) {
        while (tp.length() < TIMEPOINT_DIGITS) tp = "0" + tp;
      }

      String full_match = ".+?_"+ tp + "_" + pos + "_WL" + wl;
      Pattern pattern = Pattern.compile(full_match);
      for (NFObject o : objs) {
        Matcher matcher = pattern.matcher(o.getName());
        if(matcher.matches()){
          // If there is a match, print it out and add it to the vector.
          if (DEBUG == true) System.out.println("Found: " + o);
          tmp.add(o);
        }
      }
      // If no matches were found, inform the user
      if (tmp.size() == 0) {
        if (DEBUG == true) {
          System.out.println(
            "No matches found for\n" +
            "\tposition:   '" + pos + "'\n" +
            "\twavelength: '" + wl + "'\n" +
            "\ttimepoint:  '" + tp + "'\n" +
            "('.+?' indicates this criteria was not used)");
            return null;
        } else {
          return null;
        }
      }

      // Copy the vector into the array
      res = new NFObject[tmp.size()];
      res = (NFObject[]) tmp.toArray(res);
    } catch(Exception e) {
      e.printStackTrace(System.err);
    }
    return res;
	}

  /** 
   * Sets the experiment start time.  Calculations made by
   * <code>getExperimentTime</code> methods use this number.
   * @param image an <code>NFObject</code> whose aquisition time will
   * be used as the start time.
   * @see #elapsedTimeHr(NFObject)
   */
  public void setStartTime(NFObject image) {
    experiment_start_time = image.aquisitionTime();
  }

  /** 
   * Returns the experiment start date.  
   * @return the experiment start date.
   */
  public Date getStartDate() { return new Date(experiment_start_time); }

  /** 
   * Returns the experiment start time.  
   * @return the experiment start time.
   */
  public long getStartTime() { return experiment_start_time; }

  /**
   * Get the amount of time that has elapsed in the experiment since
   * <code>experiment_start_time</code>
   * @param image the image whose aquisition time will be compared
   * to the start time.
   * @return the number of hours elapsed since the start of the
   * experiment.
   */
  public double elapsedTimeHr(NFObject image) {
    double MSEC_PER_HR = 3600000;
    return
      (image.aquisitionTime() - experiment_start_time) / MSEC_PER_HR;
  }
}
