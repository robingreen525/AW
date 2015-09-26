import java.io.File;
import org.fhcrc.honeycomb.nfpath.NFPath;
import org.fhcrc.honeycomb.nfpath.NFObject;

public class testNFPath {
  public static void main(String[] args) {
    try {
      NFPath experiment = new NFPath(new File(args[0]));
      System.out.println(
          "The project path is:\n\t" + 
          experiment.projectPath().toString()
      );

      /*
      System.out.println("Getting all objects:\n\t");
      NFObject[] all = experiment.getAll();
      for (int i=0; i<all.length; i++) {
        System.out.println("'" + all[i] + "'");
      }
      System.out.println("\n");
      */

      /*
      System.out.println("Getting all positions:\n\t");
      String[] positions = experiment.getAllPositions();
      for (int i=0; i<positions.length; i++) {
        System.out.println("'" + positions[i] + "'");
      }
      System.out.println("\n");


      System.out.println("The paths to the images are:\n\t");
      experiment.listAllPaths();
      System.out.println("\n");

      System.out.println("The path to picture information is:\n\t");
      experiment.listAllInfoPaths();
      System.out.println("\n");
      */

      System.out.println("Setting timepoint to 0...");
      experiment.setTimepoint();
      System.out.println(
          "Timepoint is now '" + experiment.currentTimepoint() + "'.");
      System.out.println("\n");

      System.out.println("Setting experiment to initial position...");
      experiment.setPosition(0);
      System.out.println(
          "Position is now '" + experiment.currentPosition() + "'.");
      System.out.println("\n");

      System.out.println("Getting next position...");
      System.out.println(
          "Position is now '" + experiment.currentPosition()  + "'.");
      System.out.println(
          "Next position is '" + experiment.nextPosition() + "'.");
      System.out.println("\n");



      System.out.println("Setting experiment to initial position...");
      experiment.setPosition(0);
      System.out.println(
          "Position is now '" + experiment.currentPosition() + "'.");

      System.out.println(
          "This experiment started on " + experiment.getStartDate());
      System.out.println(
          "Stepping through each timepoint of this position...\n");


      // Step through available positions.
      String cp = experiment.currentPosition();
      while (cp != null) {
        System.out.println(
            "Position is now '" + cp + "'.");
        // Step through timepoints of the current position.
        NFObject[] all_wls;
        System.out.println("Timepoint is: " + experiment.currentTimepoint());
        while((all_wls = experiment.nextTimepoint()) != null) {
          // Step through the wavelengths of each timepoint.
          for (int i=0; i<all_wls.length; i++) {
            //System.out.println(all_wls[i]);
            System.out.println(
                "This experiment started on " + experiment.getStartDate());
            System.out.println(
                "Wavelength " + all_wls[i].wavelength() + 
                ", timepoint " + (experiment.currentTimepoint()-1) + 
                " was taken at " + all_wls[i].aquisitionDate());
            System.out.println(
                experiment.elapsedTimeHr(all_wls[i]) +
                " hours after the experiment began.");
          }
          System.out.println();
          System.out.println(
              "Timepoint is: " +experiment.currentTimepoint());
        }
        System.out.println("\n\n");
        // Reset timepoint.
        experiment.setTimepoint(0);
        // Get next position.
        cp = experiment.nextPosition();
      }
     /* 

      System.out.println("Find all WL0s");
      NFObject[] wls = experiment.nfMatchWl(0);
      for (int i=0; i<wls.length; i++) {
        System.out.println(wls[i]);
      }

      System.out.println("Get timepoint 1");
      NFObject[] tps = experiment.nfMatchTp(1);
      for (int i=0; i<tps.length; i++) {
        System.out.println(tps[i]);
      }

      System.out.println("Get position B02a, timepoint 1");
      NFObject[] ptp = experiment.nfMatchPosTp("B02a",1);
      for (int i=0; i<ptp.length; i++) {
        System.out.println(ptp[i]);
      }
      */
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
  }
}


