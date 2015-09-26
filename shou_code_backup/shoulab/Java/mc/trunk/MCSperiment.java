// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :

package org.fhcrc.honeycomb.mc;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Date;

/** MCSperiment is a class designed to handle the output of our automated
 * microscopy experiments.
 * @author Adam Waite
 * @version %I%,%G%
 */
public class MCSperiment {
	private static final short TURRETS = 6;
	private int timepoint  = 0;
	private int position   = 0;
  private int wavelength = 0;
	private String[] positions;

	private File project_path;
  private static long experiment_start_time = 0;
  private LinkedHashMap<String, LinkedHashMap<String, ArrayList<MCImage>>> 
    projectMap = 
    new LinkedHashMap<String, LinkedHashMap<String, ArrayList<MCImage>>>();

  private static final int TIMEPOINT_DIGITS = 4;
  private static final String POSITION_PATTERN = "[A-Z]\\d+[a-z]?"; 
  private static final String WAVELENGTH_PATTERN = "WL\\d"; 
  private static final String FILE_REGEX = 
    ".*_\\d{4}_[A-Z]\\d{2}[a-z]?_WL\\d\\.tif";

  public static FilenameFilter filter(final String regex) {
    return new FilenameFilter() {
      private Pattern pattern = Pattern.compile(regex);
      public boolean accept(File dir, String str) {
        return pattern.matcher(str).matches();
      }
    };
  }


	/** 
   * Constructs an MCSperiment object.
   * @param project_path a path to the folder containing the experiment.
	*/
	public MCSperiment(File project_path) {
    this.project_path = project_path;

    boolean isFirstImage = true;
		try {
      File[] pos_paths = project_path.listFiles(filter(POSITION_PATTERN));
			
			if (pos_paths == null)
        throw new NullPointerException( "In '" + project_path + "':" +
            "No folders match position filter");

      Arrays.sort(pos_paths);

			for (int p=0; p<pos_paths.length; p++) {
				File [] wls = pos_paths[p].listFiles(filter(WAVELENGTH_PATTERN));

				if (wls == null)
          throw new NullPointerException( "In '" + pos_paths[p] + "':" + 
              "No folders match wavelength filter");
				Arrays.sort(wls);

        LinkedHashMap<String, ArrayList<MCImage>> wlMap = 
          new LinkedHashMap<String, ArrayList<MCImage>>();
				for (int w=0; w<wls.length; w++) {
					File[] tps = wls[w].listFiles(filter(FILE_REGEX));
					if (tps == null) 
						throw new NullPointerException(
                "In '" + pos_paths[p] + "', '" + wls[w] + "':" +
                "No folders match timepoint filter");
          Arrays.sort(tps);


          ArrayList<MCImage> mctps = new ArrayList<MCImage>();
          for (int i=0; i<tps.length; i++) 
            mctps.add( new MCImage(tps[i]) );

          if (isFirstImage) {
            setStartTime(mctps.get(0));
            isFirstImage = false;
          }

          wlMap.put(wls[w].getName(), mctps);
				}
        projectMap.put(pos_paths[p].getName(), wlMap);
			}
		} catch (NullPointerException npe) {
      npe.printStackTrace(System.err);
    } catch (Exception e) {
      e.printStackTrace(System.err);
		}
	}

	/** Returns the project path.
	 * @return the project path as a <code>File<\code> object.
	 */
  public File getProjectPath() { return project_path; }

	/** Returns the project name.
	 * @return the project name as a <code>String<\code>.  The project name
   * is the name of the folder containing the positions.
	 */
  public String getProjectName() { return project_path.getName(); }

  /** 
   * Sets the experiment start time.  Calculations made by
   * <code>getExperimentTime</code> methods use this number.
   * @param image an <code>MCImage</code> whose acquisition time will
   * be used as the start time.
   */
  public void setStartTime(MCImage image) {
    experiment_start_time = image.getAcquisitionTime();
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
   * @param image the image whose acquisition time will be compared
   * to the start time.
   * @param unit the unit desired.  Can be "d" for days, "h" for hours,
   * or "m" for minutes.
   * @return the number of hours elapsed since the start of the
   * experiment.
   * @throws Exception if the unit is not recognized.
   */
  public double elapsedTime (MCImage image, char unit) throws Exception {
    int conversion = 1000 * 60;
    double et = image.getAcquisitionTime() - experiment_start_time;
    switch (unit) {
      case 'm': break;
      case 'h': conversion = conversion * 60; break;
      case 'd': conversion = conversion * 60 * 24; break;
      default: throw new Exception("Don't recognize unit " + unit);
    }
    return et/conversion;
  }

  /** 
   * Returns the number of positions in the experiment.
   * @return the number of positions in the experiment.
   */
  public int getNPositions() { return projectMap.size(); }

  /** 
   * Returns the number of wavelengths for a specified position.
   * @param position the position.
   * @return the number of wavelengths.
   */
  public int getNWavelengths(String position) { 
    return projectMap.get(position).size();
  }

  /** 
   * Returns the number of timepoints for a specified position and
   * wavelength.
   * @param position the position.
   * @param wavelength the wavelength.
   * @return the number of timepoints.
   */
  public int getNTimepoints(String position, String wavelength) {
    return projectMap.get(position).get(wavelength).size();
  }

  /** 
   * Returns the <code>Set</code> of positions in the experiment.
   * @return the <code>Set</code> of positions in the experiment.
   */
  public Set<String> getPositions() { return projectMap.keySet(); }

  /** 
   * Returns the <code>Set</code> of wavelengths for a given position.
   * @param position the desired position.
   * @return the <code>Set</code> of wavelengths for a given position.
   */
  public Set<String> getWavelengths(String position) { 
    return projectMap.get(position).keySet();
  }

  /** 
   * Returns a <code>List</code> of <code>MCImage</code>s for a given 
   * position and wavelength.
   * @param position the desired position.
   * @param wavelength the desired wavelength.
   * @return a <code>List</code> of <code>MCImage</code>s.
   */
  public List<MCImage> getTimepoints(String position, String wavelength) {
    return projectMap.get(position).get(wavelength);
  }
}
