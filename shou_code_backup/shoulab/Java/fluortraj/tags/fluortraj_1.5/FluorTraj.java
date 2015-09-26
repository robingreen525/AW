import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.io.FileSaver;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import ij.process.AutoThresholder;
import ij.process.Blitter;
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
  private static boolean SHOW_STEPS = false;
  private static int BGSD = 6;
  private static int WAIT = 2000;
  private static File project_path;
  private static File results_path;
  private static File steps_path;

  // ImageJ objects used by methods
  private static Calibration  calib = new Calibration();
  private static GaussianBlur gb    = new GaussianBlur();
  
  public static String usage() {
    return "\n\nUsage: FluorTraj save show n [path_to_project]\n" +
           "\tsave \tsave intermediate steps.\n" +
           "\tshow\tshow intermediate steps.\n" +
           "\tn\tthe number of SD above mean background to subtract from the image.\n";
  }

  public static void main(String[] args) throws IOException {
    PrintWriter out = null;

    try {
      if (args.length > 4  || args.length == 0)
        throw new Exception( usage() ); 

      for (int i=0; i<args.length-1; i++) {
        if (args[i].equals("save")) SAVE_STEPS = true;
        else if (args[i].equals("show")) SHOW_STEPS = true;
        else if (args[i].matches("[0-9]+")) BGSD = Integer.parseInt(args[i]);
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
            "well\t location\tfilter\texp\ttarea\ttimepoint\ttime\tbmean\tfmean\tbsd\tfsd\tfarea\tbsfmean\tbsfsd\tbsfarea\tintden\tbs_intden");
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
          //if (!fc.equals("RFP")) continue;

          double fluor_per_cell = 0;
          for (int i=0; i<tps.length; i++) {
            //if (i<15 || i>20) continue;
            System.out.println("Processing image: " + tps[i].getName());
            ImagePlus orig = 
              opener.openImage(tps[i].imagePath().toString());
            int area = orig.getWidth() * orig.getHeight();

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

            // Thresholding doesn't work properly without normalizing.
            IJ.run(norm, "Enhance Contrast","saturated=0 normalize");

            // Make initial background mask.
            ImagePlus mask = makeMask(fc, norm.getProcessor(), "Li");
            if (SHOW_STEPS) shower(mask,WAIT);

            Background bg = new Background(norm, mask);

            ImagePlus bgsub = subtractBackground(norm, bg, BGSD);
            if (SHOW_STEPS) shower(bgsub,WAIT);

            // Make second background mask.  This will give a more
            // accurate area estimation.
            ImagePlus bs_mask =
              makeMask(fc, bgsub.getProcessor(), "MaxEntropy");

            if (SHOW_STEPS) shower(bs_mask,WAIT);

            Background bs_bg = new Background(bgsub, bs_mask);
            
            // Measure total intensity of the image. 
            double intden    = (double) getIntDen(norm);
            double bs_intden = (double) getIntDen(bgsub);

            // Print results.
            out.println(tps[i].getWell()      + "\t" +
                        tps[i].getLocation()  + "\t" +
                        fc                    + "\t" +
                        tps[i].exposureTime() + "\t" +
                        area                  + "\t" +
                        i                     + "\t" + 
                        exp.elapsedTime(tps[i],'h') + "\t" +
                        bg.bMean()            + "\t" +
                        bg.fMean()            + "\t" + 
                        bg.bSd()            + "\t" +
                        bg.fSd()            + "\t" + 
                        bg.fArea()            + "\t" + 
                        bs_bg.fMean()         + "\t" +
                        bs_bg.fSd()         + "\t" +
                        bs_bg.fArea()         + "\t" +
                        intden                + "\t" +
                        bs_intden);

            if (SAVE_STEPS) {
              save(steps_path,(tps[i].getName()+"_rbbgsub"),orig);
              save(steps_path,(tps[i].getName()+"_normalized"),norm);
              save(steps_path,(tps[i].getName()+"_bgmask"), mask);
              save(steps_path,(tps[i].getName()+"_bgmask2"), bs_mask);
              save(steps_path,(tps[i].getName()+"_bgsub"), bgsub);
            }
            norm.flush();
            orig.flush();
            mask.flush();
            bgsub.flush();
            bs_mask.flush();
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

  public static ImagePlus subtractBackground(ImagePlus img, Background bg, int bgsd) {
    ImagePlus res = new ImagePlus("bg_sub",img.getProcessor().duplicate());
    ShortProcessor sp = new ShortProcessor(img.getWidth(),img.getHeight());
    sp.setValue(bg.bMean() + bgsd*bg.bSd());
    sp.fill();
    res.getProcessor().copyBits(sp,0,0,Blitter.SUBTRACT);
    return res;
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

  public static ImagePlus makeMask(String filter, ImageProcessor ip,
                                   String method)
  {
    ImageProcessor mask = ip.duplicate();
    ImagePlus img = new ImagePlus("mask",mask);

    double blur_radius   = 2.0;
    double blur_accuracy = 0.002; // Shouldn't exceed 0.02 according to 
                                  // source.
    //if (filter.equals("RFP")) method = "MaxEntropy";
    boolean dark_background = true;

    gb.blurGaussian(mask,blur_radius,blur_radius,blur_accuracy);
    if (SHOW_STEPS) shower(img,WAIT);
    mask.setAutoThreshold(method,dark_background,mask.RED_LUT);
    if (SHOW_STEPS) shower(img,WAIT);

    IJ.run(img, "Convert to Mask","");
    //for (int j=0; j<3; j++) IJ.run(img, "Dilate","");
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
