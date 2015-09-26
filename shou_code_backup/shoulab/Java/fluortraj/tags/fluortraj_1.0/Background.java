import ij.IJ;
import ij.ImagePlus;
import ij.plugin.frame.RoiManager;
import ij.process.ImageStatistics;
import ij.gui.Roi;

class Background {
  private ImagePlus bs;
  private RoiManager rm = new RoiManager(false);

  private double f_mean;
  private double f_sd;
  private double f_area;

  private double b_mean;
  private double b_sd;
  private double b_area;

  public double fMean() { return f_mean; }
  public double fSd()   { return f_sd; }
  public double fArea() { return f_area; }

  public double bMean() { return b_mean; }
  public double bSd()   { return b_sd; }
  public double bArea() { return b_area; }

  public Background(ImagePlus im, ImagePlus bgmask) {
    bs = new ImagePlus("bs", im.getProcessor().duplicate());
    bgmask.show();
    IJ.run(bgmask, "Create Selection","");
    //IJ.wait(2000);
    rm.runCommand("Add");
    bgmask.hide();

    Roi[] roi = rm.getRoisAsArray();
    if (roi.length != 1) {
      System.err.println("There are " + roi.length + " selections.");
      System.exit(1);
    }

    bs.setRoi(roi[0]);
    ImageStatistics stats = bs.getStatistics();
    IJ.run(bs, "Make Inverse","");
    ImageStatistics inv_stats = bs.getStatistics();

    //System.out.println("sd: " + stats.stdDev);
    //System.out.println("inv sd: " + inv_stats.stdDev);
    //System.out.println("area: " + stats.area);
    //System.out.println("inv area: " + inv_stats.area);
    if (stats.stdDev < inv_stats.stdDev) {
      // First selection is background.
      b_mean = stats.mean;
      b_sd   = stats.stdDev;
      b_area = stats.area;

      f_mean = inv_stats.mean;
      f_sd   = inv_stats.stdDev;
      f_area = inv_stats.area;
    } else {
      // Inverse selection is background.
      f_mean = stats.mean;
      f_sd   = stats.stdDev;
      f_area = stats.area;

      b_mean = inv_stats.mean;
      b_sd   = inv_stats.stdDev;
      b_area = inv_stats.area;
    }

    //bs.show();
    //IJ.wait(2000);
    //bs.hide();
    bs.flush();
    rm.close();
  }
}
