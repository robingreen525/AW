import org.fhcrc.honeycomb.mc.MCSperiment;
import org.fhcrc.honeycomb.mc.MCImage;
import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;

public class MCSperimentTest {
  private MCSperiment mce;
    File project_path = new File("test-files/my_experiment");

  public MCSperimentTest() {}
  public MCSperimentTest(String project_path) {
    this.project_path = new File(project_path);
  }

  @Test
  public void testConstructor() {
    System.out.println("Testing construction...");
    mce = new MCSperiment(project_path);
  }

  @Test
  public void testDataStructure() {
    System.out.println("Testing data structure...");
    System.out.println("Number of positions: " + mce.getNPositions());

    System.out.println("Iterating through data structure...");
    for (String pos : mce.getPositions()) {
      System.out.print("  " + pos);
      System.out.println(
        " has " + mce.getNWavelengths(pos) + " wavelengths:");
      for (String wl : mce.getWavelengths(pos)) {
          System.out.print("    " + wl);
          System.out.println(
            " has " + mce.getNTimepoints(pos,wl) + " timepoints:");
        for (MCImage tp : mce.getTimepoints(pos,wl)) {
          System.out.println("      " + tp);
        }
      }
      System.out.println();
    }
  }

  public static void main (String[] args) {
    if (args.length==1) {
      MCSperiment mcs = new MCSperiment(new File(args[0]));
      if (mcs.getPositions().size() == 0) {
        System.out.println("No positions in this folder.");
      } else {
        for (String pos : mcs.getPositions()) {
          System.out.println(pos);
        }
      }
    } else {
      MCSperimentTest tester = new MCSperimentTest();

      tester.testConstructor();
      tester.testDataStructure();
    }
  }
}

