// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :

package org.fhcrc.honeycomb.mc;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Date;
import java.text.SimpleDateFormat;

/** MCImage is intended to represent the file information generated
 * by a notfacs experiment.
 * @author Adam Waite
 * @version %I%,%G%
 */
public class MCImage {

  // The regex corresponding to our image/info files.
  private static final String FILE_REGEX = 
    ".+/((.*)_(\\d+)_(([A-Z]\\d+)([a-z]?))_WL(\\d))\\.\\w+";
  
  // This information comes from the path to the file
  // and the file name itself.
  private File   image_path;
  private String file_name;
  private String experiment_name;
  private String position; // 'B06a'
  private String well;     // 'B06'
  private String location; // 'a'
  private int    timepoint;
  private int    wavelength;


  // These constants relate to the current format of our
  // picture information files.
  private static final String INFO_FOLDER = "picture_information";
  private static final String INFO_DATE_FORMAT = "M/dd/yyyy H:mm a";

  // This information comes from the file found in the
  // 'picture_information' folder.
  private File info_path;
  private Date acquisition_time;
  private String filter_cube;
  private short binning;
  private short analog_gain;
  private double exposure;
  private double x_pos;
  private double y_pos;
  private double z_pos;
  private int x_dim;
  private int y_dim;
  private int shutter;
  private int intensity;

  /** 
   * Creates a new MCImage.
   * @param path a path as <code>File</code> object to be converted
   * into an MCImage.
   */
  public MCImage(File path) throws Exception, FileNotFoundException {
    Matcher m = Pattern.compile(FILE_REGEX).matcher(path.toString());

    if (m.matches()) {
      image_path = path;
      file_name = m.group(1);

      experiment_name = m.group(2);
      timepoint       = Integer.parseInt(m.group(3));
      position        = m.group(4);
      well            = m.group(5);
      location        = m.group(6);
      wavelength      = Integer.parseInt(m.group(7));
    } else {
      throw new Exception( "\n\tPath '"+path.toString() + 
                           "' doesn't appear to be of the correct type.\n");
    }

    // The path passed to MCImage has the following structure:
    //    path_to_project/position/wavelength.
    // The picture information is located in the 'path_to_project' folder.
    String info_file_name = file_name + ".txt";
    info_path = 
      new File(path.getParentFile().getParentFile().getParentFile(),
               INFO_FOLDER + File.separator + info_file_name);

    // Open the info file whose name matches the path passed to MCImage.
    // Info file is tab-delimited and as of 31 Jan, 2010 contains:
    //
    // Date           M/dd/yyyy |
    // Time           H:mm a    |  Using SimpleDateFormat
    // Exposure       \d\.\d{6}
    // Binning        \dX\d
    // Filter_Cube    \w+
    // Analog_Gain    \dX
    // X_position     -?\d+\.\d{6}
    // Y_position     -?\d+\.\d{6}
    // Z_position     -?\d+\.\d{6}
    // x_dimension    \d+
    // y_dimension    \d+
    // These next two were added recently, and are not in experiments
    // before May 2010.
    // shutter        \d+
    // intensity      \d+
    ArrayList<String> info = new ArrayList<String>();
    BufferedReader info_file = null;
    try {
      info_file = new BufferedReader(new FileReader(info_path));
      String line;
      while ((line = info_file.readLine()) != null) {
        info.add( (line.split("\\s+",2))[1] );
      }

      // If all went well, we can set the rest of the object's attributes.
      acquisition_time =
        new SimpleDateFormat().parse(info.get(0) + " " + info.get(1));
      exposure      = Double.parseDouble(info.get(2));
      binning       = Short.parseShort(info.get(3).substring(0,1));
      filter_cube   = info.get(4);
      analog_gain   = Short.parseShort(info.get(5).substring(0,1));
      x_pos         = Double.parseDouble(info.get(6));
      y_pos         = Double.parseDouble(info.get(7));
      z_pos         = Double.parseDouble(info.get(8));
      x_dim         = Integer.parseInt(info.get(9));
      y_dim         = Integer.parseInt(info.get(10));

      if (info.size() == 13) {
        shutter   = Integer.parseInt(info.get(11));
        intensity = Integer.parseInt(info.get(12));
      }
      info_file.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(0);
    } catch (Exception e) {
      try {
        info_file.close();
      } catch (IOException e2) {
        System.err.println("Can't close file!");
        e2.printStackTrace();
        throw e2;
      }
    }
  }

  /**
   * Return the bare name of the file, without extension or path.  This
   * is useful for finding particular objects.
   * @return the name of the <code>MCImage</code>.
   */
  public String getName() {
    return file_name;
  }

  /**
   * Return the timepoint of the image.
   * @return a string representing the timepoint.
   */
  public int getTimepoint() {
    return timepoint;
  }

  /**
   * Return the location of the image. For example, 'a'.
   * @return a string representing the location.
   */
  public String getLocation() { return location; }

  /**
   * Return the well that the image came from.  For example, 'B06'.
   * @return a string representing the well.
   */
  public String getWell() { return well; }

  /**
   * Return the name of the filter cube used to acquire this image.
   * @return the name of the filter cube.
   */
  public String getFilterCube() {
    return filter_cube;
  }

  /**
   * Return the exposure time in seconds.
   * @return the exposure time in seconds.
   */
  public double getExposureTime() { return exposure; }


  /**
   * Returns the x-dimension of the image, in pixels.
   * @return the number of pixels in the x-dimension of the image.
   */
  public int getXDim() { return x_dim; }

  /**
   * Returns the y-dimension of the image, in pixels.
   * @return the number of pixels in the y-dimension of the image.
   */
  public int getYDim() { return y_dim; }

  /**
   * Returns the path to the image file.
   * @return the path to the image file.
   */
  public File getImagePath() {
    return image_path;
  }


  /**
   * Returns the path to the information file.
   * @return the path to the information file.
   */
  public File getInfoPath() {
    return info_path;
  }

  /**
   * Returns the acquisition date.
   * @return the acquisition date as a <code>Date</code> object.
   */
  public Date getAcquisitionDate() { return acquisition_time; }

  /**
   * Returns the acquisition time.
   * @return the acquisition time in milliseconds past the epoch.
   */
  public long getAcquisitionTime() { return acquisition_time.getTime(); }

  /**
   * Returns the wavelength.
   * @return the wavelength number of the current image.
   */
  public int getWavelength() { return wavelength; }


  /**
   * Prints all information about this object.
   * @return a string describing this object.
   */
  public String toString() {
    return(
        "\n\tImage Path: " + image_path +
        "\n\tExperiment name: " + experiment_name  +
        "\n\tPosition: " + position +
        "\n\tWell: " + well +
        "\n\tLocation: " + location +
        "\n\tTimepoint: " + timepoint +
        "\n\tWavelength: " + wavelength +
        "\n\tAcquisition date: " + acquisition_time + 
        "\n\tBinning: " + binning +
        "\n\tFilter Cube: " + filter_cube + 
        "\n\tAnalog Gain: " + analog_gain +
        "\n\tExposure time (sec): " + exposure +
        "\n\tX Position: " + x_pos +
        "\n\tY Position: " + y_pos +
        "\n\tZ Position: " + z_pos +
        "\n\tX Picture Dimension: " + x_dim +
        "\n\tY Picture Dimension: " + y_dim + "\n\n");
  }

  /* -----------------------/
   *      Static methods
   * ---------------------*/

  /**
   * Determine whether an array of <code>MCImages</code> contains an image
   * that is not brightfield.
   * @param mcimages an array of <cod>MCImage</code>.
   * @return a boolean indicating whether a non-"BF" filter cube has been
   * found. 
   */
  public static boolean hasFluorescentImages(MCImage[] mcimages) {
    for (int i=0; i<mcimages.length; i++) {
      if (!mcimages[i].getFilterCube().equals("BF")) return true;
    }
    return false;
  }

  /**
   * Finds and returns an image based on the filter used to take it.
   * @param mcimages An array of <code>MCImage</code>
   * @param filter A string to match.
   * @return an <code>MCImage</code>
   */
  public static MCImage findByFilterName(MCImage[] mcimages, String filter)
  {
    for (int i=0;i<mcimages.length;i++)
      if (mcimages[i].getFilterCube().equals(filter))
        return mcimages[i];
    return null;
  }
}
