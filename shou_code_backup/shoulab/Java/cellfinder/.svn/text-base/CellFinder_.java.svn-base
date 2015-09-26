import ij.*;
import ij.io.*;
import ij.IJ;
import ij.process.*;
import ij.gui.*;
import ij.measure.*;
import ij.plugin.*;
import ij.plugin.filter.*;

import java.io.*;
import java.util.*;

import org.fhcrc.honeycomb.mc.MCSperiment;
import org.fhcrc.honeycomb.mc.MCImage;
import org.fhcrc.honeycomb.utils.ij.*;

public class CellFinder_ implements PlugIn, Measurements {
  private File project_path;
  private File results_path;
  private File selection_path;

  private Opener opener      = new Opener();
  private Calibration  calib = new Calibration();
  private PrintWriter out;

  public CellFinder_() {
    IJ.run("Options...", "iterations=1 black edm=Overwrite count=1");
    calib.setUnit("pixel");
  }
  public void run(String arg) {
    if (arg=="") {
      DirectoryChooser dc = new DirectoryChooser("Choose a directory.");
      project_path = new File(dc.getDirectory());
    } else {
      project_path = new File(arg);
    }
    results_path = new File(project_path,"cellfinder_results");
    selection_path = new File(results_path,"cellfinder_selections");
    results_path.mkdir();
    selection_path.mkdir();

    MCSperiment mcs = new MCSperiment(project_path);
    cellFinder(mcs);
  }

  private void cellFinder(MCSperiment mcs) {
    //for (String pos : mcs.getPositions()) {
      List<MCImage> bf_tps = mcs.getTimepoints("A02","WL1");
      ImagePlus processed = preprocess(bf_tps);
    }
  }

  public ImagePlus preprocess(List<MCImage> mcis) {
    ImagePlus stack = new AWImagePlus(mcis);
    IJ.run(stack, "Gaussian Blur...", "sigma=2 stack");
    return stack;
  }

  public static void main(String[] args) {
    CellFinder_ cf = new CellFinder_();
    if (args.length==1) {
      cf.run(args[0]);
    } else {
      cf.run("");
    }
  }
}
