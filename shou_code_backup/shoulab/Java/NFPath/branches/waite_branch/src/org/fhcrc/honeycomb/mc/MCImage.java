// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :

package org.fhcrc.honeycomb.mc;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
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
  private static final boolean DEBUG = false;

  // The regex corresponding to our image/info files.
  private static final String FILE_REGEX = 
    ".+/((.*)_(\\d+)_([A-Z]\\d+[a-z]?)_WL(\\d))\\.\\w+";
  
  // This information comes from the path to the file
  // and the file name itself.
  private File   image_path;
  private String file_name;
  private String experiment_name;
  private String position;
  private int    timepoint;
  private int    wavelength;


  // These constants relate to the current format of our
  // picture information files.
  private static final String INFO_FOLDER = "picture_information";
  private static final String INFO_DATE_FORMAT = "M/dd/yyyy H:mm a";
  private static final int INFO_LINES = 11;

  // This information comes from the file found in the
  // 'picture_information' folder.
  private File info_path;
  private Date aquisition_time;
  private String filter_cube;
  private short binning;
  private short analog_gain;
  private double exposure;
  private double x_pos;
  private double y_pos;
  private double z_pos;
  private int x_dim;
  private int y_dim;

  /**
   * Return the bare name of the file, without extension or path.  This
   * is useful for finding particular objects.
   * @return the name of the <code>MCImage</code>.
   */
  public String getName() {
    return file_name;
  }

  /**
   * Returns the path to the image file.
   * @return the path to the image file.
   */
  public File imagePath() {
    return image_path;
  }


  /**
   * Returns the path to the information file.
   * @return the path to the information file.
   */
  public File infoPath() {
    return info_path;
  }

  /**
   * Returns the aquisition date.
   * @return the aquision date as a <code>Date</code> object.
   */
  public Date aquisitionDate() { return aquisition_time; }

  /**
   * Returns the aquisition time.
   * @return the aquision time in miliseconds past the epoch.
   */
  public long aquisitionTime() { return aquisition_time.getTime(); }

  /**
   * Returns the wavelength.
   * @return the wavelength number of the current image.
   */
  public int wavelength() { return wavelength; }

  /** 
   * Creates a new MCImage.
   * @param path a path as <code>File</code> object to be converted
   * into an MCImage.
   */
  public MCImage(File path) {
    Matcher m = Pattern.compile(FILE_REGEX).matcher(path.toString());

    if (m.matches()) {
      image_path = path;
      file_name = m.group(1);

      experiment_name = m.group(2);
      position = m.group(4);
      timepoint = Integer.parseInt(m.group(3));
      wavelength = Integer.parseInt(m.group(5));

      if (DEBUG == true) {
        System.out.println(
          "\n\nIn constructor:" +
          "\n\tPath: " + image_path +
          "\n\tExperiment name: " + experiment_name +
          "\n\tPostion: " + position +
          "\n\tTimepoint: " + timepoint +
          "\n\tWavelength: " + wavelength +
          "\n\n");
      }
    } else {
      System.err.println(
          "\n\tPath '"+image_path.toString() +
          "' doesn't appear to be of the correct type.\n");
    }

    // The path passed to MCImage has the following structure:
    //    path_to_project/position/wavelength.
    // The picture information is located in the 'path_to_project' folder.
    String info_file_name = file_name + ".txt";
    info_path = 
      new File(path.getParentFile().getParentFile().getParentFile(),
               INFO_FOLDER + File.separator + info_file_name);
    if (DEBUG == true) {
      System.out.println(
          "Location of info for " + file_name + " is:\n\t" +
          info_path.toString() + "\n");
    }

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
    String[] info = new String[INFO_LINES];
    try {
      BufferedReader info_file = new BufferedReader(
        new FileReader(info_path));
      int info_line_count = 0;
      String line;
      while ((line = info_file.readLine()) != null) {
        info[info_line_count++] = (line.split("\\s+",2))[1];
      }
      if (info_line_count != INFO_LINES)
        throw new Exception (
            "\n\tInfo file contains " + info_line_count +
            " lines when it should contain " + INFO_LINES + "\n\n"
        );

      // If all went well, we can set the rest of the object's attributes.
      aquisition_time =
        new SimpleDateFormat().parse(info[0] + " " + info[1]);
      exposure      = Double.parseDouble(info[2]);
      binning       = Short.parseShort(info[3].substring(0,1));
      filter_cube   = info[4];
      analog_gain   = Short.parseShort(info[5].substring(0,1));
      x_pos         = Double.parseDouble(info[6]);
      y_pos         = Double.parseDouble(info[7]);
      z_pos         = Double.parseDouble(info[8]);
      x_dim         = Integer.parseInt(info[9]);
      y_dim         = Integer.parseInt(info[10]);

    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
  }

  /**
   * Prints all information about this object.
   * @return a string describing this object.
   */
  public String toString() {
    return(
        "\n\tImage Path: " + image_path +
        "\n\tExperiment name: " + experiment_name  +
        "\n\tPosition: " + position +
        "\n\tTimepoint: " + timepoint +
        "\n\tWavelength: " + wavelength +
        "\n\tAquisition date: " + aquisition_time + 
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
}
