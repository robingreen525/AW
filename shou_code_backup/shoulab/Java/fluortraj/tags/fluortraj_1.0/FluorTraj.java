import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.io.FileSaver;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import ij.process.AutoThresholder;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.Thresholder;
import ij.plugin.filter.ThresholdToSelection;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.GaussianBlur;
import ij.measure.Calibration;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.fhcrc.honeycomb.mc.MCSperiment;
import org.fhcrc.honeycomb.mc.MCImage;


/** Analyze fluorescence trajectories over the course of an experiment.  
 * Generates intensity versus time information for each wavelength 
 * of a given <code>MCSperiment</code>
 */
public class FluorTraj {
  private static double radius = 20;
  private static boolean SAVE_STEPS = false;
  private static boolean NORMALIZE  = false;
  private static boolean SHOW_STEPS  = false;
  private static int WAIT = 2000;
  private static File project_path;
  private static File results_path;
  private static File steps_path;

  // ImageJ objects used by methods
  private static Calibration  calib = new Calibration();
  private static GaussianBlur gb    = new GaussianBlur();
  
  public static String usage() {
    return "\n\nUsage: FluorTraj [normalize save] [path_to_project]\n" +
           "\tnormaize \tnormalize before analysis.\n" +
           "\tsave \tsave intermediate steps.\n" +
           "\tshow\tshow intermediate steps.\n";
  }

  public static void main(String[] args) throws IOException {
    PrintWriter out = null;

    try {
      if (args.length > 4  || args.length == 0)
        throw new Exception( usage() ); 

      for (int i=0; i<args.length-1; i++) {
        if (args[i].equals("normalize")) NORMALIZE  = true;
        else if (args[i].equals("save")) SAVE_STEPS = true;
        else if (args[i].equals("show")) SHOW_STEPS = true;
        else {
          System.err.println(usage());
          System.exit(1);
        }
      }

      project_path = new File(args[args.length-1]);
      System.err.println("In " + project_path);
      results_path = new File(project_path,"fluortraj_results");
      steps_path   = new File(project_path,"intermediate_steps");
      String results_name = "fluortraj_results.txt";

      MCSperiment exp = new MCSperiment(project_path);

      results_path.mkdir();
      if (SAVE_STEPS) steps_path.mkdir();

      try {
        out = new PrintWriter(new File(results_path,results_name));
        out.println(
            "well\t location\tfilter\texp\ttimepoint\ttime\tfluor_per_cell\tbmean\tfmean\tbarea\tfarea\tintden");
      } catch (IOException e) {
        System.err.println("Can't write to " + out);
      }

      // ImageJ objects used in the code.
      Opener opener           = new Opener();
      BackgroundSubtracter bs = new BackgroundSubtracter();

      calib.setUnit("pixel");

      String cp = exp.currentPosition();
      while(cp != null) {
        System.out.println("Processing position '" + cp + "'...");
        MCImage[] tps;
        while((tps = exp.nextWavelengthSet()) != null) {
          String fc = tps[0].filterCube();
          // Not working with brightfield images.
          if (fc.equals("BF")) continue;
          //if (!fc.equals("CFP")) continue;

          double fluor_per_cell = 0;
          for (int i=0; i<tps.length; i++) {
            //if (i<25 || i>50) continue;
            System.out.println("Processing image: " + tps[i].getName());
            ImagePlus orig = 
              opener.openImage(tps[i].imagePath().toString());

            if (SHOW_STEPS) shower(orig,WAIT);

            // booleans are createBackground, lightBackground,
            // useParaboloid, doPresmooth, correctCorners
            boolean createBackground = false;
            boolean lightBackground  = false;
            boolean useParaboloid    = true;
            boolean doPresmooth      = true;
            boolean correctCorners   = true;

            bs.rollingBallBackground(orig.getProcessor(),
                                     radius,
                                     createBackground,
                                     lightBackground,
                                     useParaboloid,
                                     doPresmooth,
                                     correctCorners);

            ImagePlus norm =
              new ImagePlus("normalized",orig.getProcessor().duplicate());

            // Normalize.  This makes the masks a lot cleaner.
            IJ.run(norm, "Enhance Contrast","saturated=0 normalize");

            if (SHOW_STEPS) shower(norm,WAIT);

            // Make background mask.
            ImagePlus mask = makeMask(norm.getProcessor());
            if (SHOW_STEPS) shower(mask,WAIT);

            // Calculate the remaining constant background.
            // We can either operate on the normalized image or
            // the original.  Does not appear to significantly 
            // alter the results.
            ImagePlus which = NORMALIZE ? norm : orig;
            Background bg = new Background(which, mask);

            // Measure total intensity of the image. 
            double intden = (double) getIntDen(which);

            // For the first image, assume that the foreground area
            // is proportional to the number of cells in the image.
            // We can use this to get the fluorescence intensity per cell,
            // and then normalize for the intrinsic intensity of the
            // fluorophore.
            if (i == 0) fluor_per_cell = intden / bg.fArea();

            // Print results.
            out.println(tps[i].getWell()      + "\t" +
                        tps[i].getLocation()  + "\t" +
                        fc                    + "\t" +
                        tps[i].exposureTime() + "\t" +
                        i                     + "\t" + 
                        exp.elapsedTime(tps[i],'h') + "\t" +
                        fluor_per_cell        + "\t" +
                        bg.bMean()            + "\t" +
                        bg.fMean()            + "\t" + 
                        bg.bArea()            + "\t" + 
                        bg.fArea()            + "\t" + 
                        intden);

            if (SAVE_STEPS) {
              save(steps_path,(tps[i].getName()+"_rbbgsub"),orig);
              save(steps_path,(tps[i].getName()+"_normalized"),norm);
              save(steps_path,(tps[i].getName()+"_bgmask"), mask);
            }
            norm.flush();
            orig.flush();
            mask.flush();
            which.flush();
          }
        }
        exp.resetWavelength();
        cp = exp.nextPosition();
      }
    } catch (NumberFormatException e) {
      System.err.println(e);
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println(e);
      e.printStackTrace();
    } catch (Exception e) {
      System.err.println(e);
      e.printStackTrace();
    } finally {
      out.close();
    }
  }

  public static void shower(ImagePlus img, int time) {
    img.updateAndDraw();
    img.show();
    IJ.wait(time);
    img.hide();
  }

  public static long getIntDen(ImagePlus img) {
    long res = 0;
    if (img == null) return -1;
    ImageProcessor ip = img.getProcessor();
    for (int i=0; i<img.getWidth(); i++) {
      for (int j=0; j<img.getHeight(); j++) {
        res += ip.get(i,j);
      }
    }
    return res;
  }

  public static ImagePlus makeMask(ImageProcessor ip) {
    ImageProcessor mask = ip.duplicate();
    ImagePlus img = new ImagePlus("mask",mask);

    double blur_radius   = 2.0;
    double blur_accuracy = 0.002; // Shouldn't exceed 0.02 according to 
                                  // source.
    String method = "Li";
    boolean dark_background = true;

    //img.show();
    //IJ.wait(2000);
    gb.blurGaussian(mask,blur_radius,blur_radius,blur_accuracy);
    //img.updateAndDraw();
    //IJ.wait(2000);
    mask.setAutoThreshold(method,dark_background,mask.RED_LUT);
    //img.updateAndDraw();
    //IJ.wait(2000);
                               

    IJ.run(img, "Convert to Mask","");
    for (int j=0; j<3; j++) IJ.run(img, "Dilate","");
    return img;
  }

  public static boolean
  save(File path, String name, ImageProcessor ip) throws NullPointerException {
    ImagePlus imp = new ImagePlus(name,ip);
    //IJ.saveAs(imp, "tiff",(new File(path,name)).toString());
    return new FileSaver(imp).saveAsTiff((new File(path, (name+".tiff"))).toString());
  }

  public static boolean
  save(File path, String name, ImagePlus ip) throws NullPointerException {
    //IJ.saveAs(ip, "tiff",(new File(path,name)).toString());
    return new FileSaver(ip).saveAsTiff((new File(path, (name+".tiff"))).toString());
  }
}
