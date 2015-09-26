import ij.IJ;
import ij.gui.GenericDialog;
import ij.plugin.*;
import java.io.File;
import java.io.FilenameFilter;
import java.awt.Label;
import java.util.Arrays;
import java.util.Vector;

/**
 * Rainman is an image analysis program developed to count and quantify the fluorescence
 * intensity of cells in an image obtained via microscopy.
 * @author Adam Waite
 * @version 1.0
 */
public class Rainman_ implements PlugIn {
  private static File project_path;
  private static File results_path;
  private static File compiled_data_path;
  private static File path_to_counts;
  private static File path_to_stack_lists;
  private static File path_to_drawing_list;
  private static File path_to_skip_list;


  private static File[] paths;
  private static File[] drawing_paths;


  // Default parameters for brightfield manipulation.
  private String size = "1-200";
  private String circ = "0.50-1.0";
  private int    rb_radius = 50; // Radius for rolling ball algorithm.
  private int    find_max_tol = 10;
  private int    area_thresh = 4;
  private int    sd_area_thresh = 152; // Defines cutoff for SD/Area ratio.
  private int    sd_above_bg = 4;

  // Filter settings
  private int bf  = 0;
  private int yfp = 1;
  private int rfp = 2;
  private int cfp = 3;
  private int dead = 4;


  // Translations relative to brightfield.
  private static int NFILTERS = 5;
  private String bft   = "0,0";
  private String yfpt  = "1,5";
  private String rfpt  = "1,3";
  private String cfpt  = "1,4";
  private String deadt = "1,3";
  // Maximum number of pixels a selection will be translated.
  private int[] max_trans = {5,1}; 

  private static String[] trans = new String[NFILTERS];

  // Qantitative data
  private static int[] count_array;
  private static int[] ind_count_array;
  private int final_bf_counts = 0;

  
  //---------------------------------------------------//
  //-- Methods                                        //
  //-------------------------------------------------//
  /**
   * Used by ImageJ to run program.  Sets up parameters for execution.
   */
  public void run(String arg) {
    boolean AUTO = true;
    if (arg == "") {
      project_path = new File(IJ.getDirectory( "Choose a Directory" ));
      AUTO = false;
    } else {
      project_path = new File(arg);
    }
    results_path = new File(project_path, "results");
    compiled_data_path = new File(results_path,"compiled");
    safe_mkdir(results_path);
    safe_mkdir(compiled_data_path);


    if (!AUTO) showDialog();
    Vector<File> paths = generatePaths();

  }




  /** 
   * Generates and displays parameter dialog box, and collects parameters.
   */
  public void showDialog() {
    int DUNITS = 0;
    int OUNITS = 0;
    GenericDialog gd = new GenericDialog("Set Parameters");
    gd.addMessage("Brightfield Parameters");
    gd.addStringField("  Size (pixels^2):", size, 10);
    gd.addStringField("  Circularity:", circ, 10);
    gd.addNumericField("  Rolling ball radius:", rb_radius,DUNITS);
    gd.addNumericField("  Find Maxima Tolerance:", find_max_tol,DUNITS);

    gd.addMessage("Determines which selections are false positives");
    gd.addNumericField("  Area Threshold (pixels^2):",area_thresh,DUNITS);
    gd.addNumericField("  SD/Area Threshold:",sd_area_thresh,DUNITS);

    gd.addMessage("Filter settings");
    gd.addNumericField("  Brightfield: WL",bf,OUNITS);
    gd.addNumericField("  YFP: WL",yfp,OUNITS);
    gd.addNumericField("  RFP: WL",rfp,OUNITS);
    gd.addNumericField("  CFP: WL",cfp,OUNITS);
    gd.addNumericField("  DEAD: WL",dead,OUNITS);

    gd.addMessage("Filter translations relative to brightfield.\nOrigin is top left corner of image.\nEnter as x,y.");
    gd.addStringField("YFP:",yfpt);
    gd.addStringField("RFP:",rfpt);
    gd.addStringField("CFP:",cfpt);
    gd.addStringField("DEAD:",deadt);


    gd.addMessage("To determine cell counts in each channel");
    gd.addNumericField("Standard deviations above background:",sd_above_bg,DUNITS);

    gd.showDialog();

    // Brightfield
    size         = gd.getNextString();
    circ         = gd.getNextString();
    rb_radius    = (int) gd.getNextNumber();
    find_max_tol = (int) gd.getNextNumber();

    // Threshold
    area_thresh   = (int) gd.getNextNumber();
    sd_area_thresh = (int) gd.getNextNumber();

    // Filter settings
    bf   = (int) gd.getNextNumber();
    yfp  = (int) gd.getNextNumber();
    rfp  = (int) gd.getNextNumber();
    cfp  = (int) gd.getNextNumber();
    dead = (int) gd.getNextNumber();

    // Translations
    yfpt  = gd.getNextString();
    rfpt  = gd.getNextString();
    cfpt  = gd.getNextString();
    deadt = gd.getNextString();

    sd_above_bg = (int) gd.getNextNumber();

    // Set up the translation array.
    trans[bf]   = bft;
    trans[yfp]  = yfpt;
    trans[rfp]  = rfpt;
    trans[cfp]  = cfpt;
    trans[dead] = deadt;

    max_trans = getMaxTranslation(trans);
  }

  /**
   * Determines the maximum selection translation in the y direction.
   * @param tr An array of strings containing selection translations
   * relative to brightfield.  Each entry contains two digits, separated by
   * a comma. 
   * @return An integer array containing the maximum x and y translations.
   *
   */
  protected int[] getMaxTranslation(String [] tr) {

    int[] maxs = {0,0};
    for (int i=0;i<tr.length;i++) {
      String[] ns = tr[i].split(",");
      if (ns.length != 2) {
        IJ.error("Fatal Error", 
            "Wrong number of coordinates ("+ns.length+")");
      }
      for (int j=0; j<ns.length; j++) {
        int n = Integer.parseInt(ns[j]);
        if (n > maxs[j]) maxs[j] = n;
      }
    }
    return maxs;
  }

  public void safe_mkdir(File path) {
    if (!path.exists()) {
      if (!path.mkdir()) IJ.error("Path Error","Couldn't mkdir "+path.toString());
    }
  }
}
