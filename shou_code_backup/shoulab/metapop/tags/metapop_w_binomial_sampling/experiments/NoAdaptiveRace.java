// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
import org.fhcrc.honeycomb.metapop.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.io.IOException;
import java.io.File;

/** A simple test of the Metapop program.
 * Created on 27 Jan, 2012
@author Adam Waite
@version $Rev: 1201 $, $Date: 2012-02-03 14:33:54 -0800 (Fri, 03 Feb 2012) $
 */
public class NoAdaptiveRace {
  private final static int MIN_PER_HR = 60;
  public static void main(String args[]) {
    final File data_path = new File("dat/no_adaptive_race_test.tab");
    final int rows = 10;
    final int cols = 10;
    final double initial_fraction_occupied = 1;
    final long initial_location_seed = 1L;
    final long population_seed = 2L;
    final long migration_seed = 3L;

    final InitialLayout initial_layout = 
      new RandomLayout(rows,cols,initial_fraction_occupied,
                       initial_location_seed);

    double anc_gr = Math.log(2)/(1*MIN_PER_HR);
    final List<Double> fitnesses = Arrays.asList(anc_gr);
    final List<Double> freqs = Arrays.asList(1.0);
    final Types types = new Types(fitnesses, freqs);
    final int initial_size = 1;
    final double initial_coop_freq = 0.5;
    final double cheat_adv = 0.02;

    final Population initial_population =
      new Population(initial_size, initial_coop_freq, cheat_adv, types);

    final double migration_rate = 0.1;
    final int timesteps = 51*MIN_PER_HR;

    LocationPicker picker = new LocationPicker(rows, cols, migration_seed);
    World world = new World(initial_layout, initial_population);

    try {
      if (data_path.exists()) data_path.delete();
      world.saveState(data_path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    while(world.incrementStep() < timesteps) {
      Map<Location,Population> occupied_locations =
        world.getOccupiedLocations();
      
      List<Location> new_locations = new ArrayList<Location>();
      List<Population> migrants    = new ArrayList<Population>();

      for (Map.Entry<Location, Population> ol:occupied_locations.entrySet())
      {
        Location location = ol.getKey();
        Population pop    = ol.getValue();

        pop.grow();
        migrants.add(pop.retrieveMigrants(migration_rate));
        new_locations.add(picker.pickNonIdRandom(location));
      }
      world.distribute(migrants, new_locations);

      if (world.getStep() % MIN_PER_HR == 0) {
        try {
          world.saveState(data_path);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
