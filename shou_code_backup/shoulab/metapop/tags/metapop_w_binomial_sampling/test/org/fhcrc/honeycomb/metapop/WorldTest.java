// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

public class WorldTest {
  private World world;
  private LocationPicker picker;
  private final int rows = 10;
  private final int cols = 10;
  private final double initial_fraction_occupied = 0.1;
  private final long location_seed   = 1L;
  private final long population_seed = 2L;
  private final long migration_seed  = 3L;

  private final InitialLayout initial_layout = 
    new RandomLayout(rows,cols,initial_fraction_occupied,location_seed);



  private final List<Double> fitnesses = Arrays.asList(Math.log(2),
                                                       Math.log(2)*1.1, 
                                                       Math.log(2)*1.2,
                                                       Math.log(2)*1.3);

  private final List<Double> freqs = Arrays.asList(0.25, 0.25, 0.25, 0.25);
  private final Types types = new Types(fitnesses, freqs);
  private final int    initial_size = 800000;
  private final double initial_coop_freq = 0.5;
  private final double cheat_adv = 0.02;

  private final Population initial_population = 
    new Population(initial_size, initial_coop_freq, cheat_adv, types,
                   population_seed);

  @Before
  public void setUp() {
    world = new World(initial_layout, initial_population);
    picker = new LocationPicker(rows, cols, migration_seed);
  }

  @Test
  public void initialPopsUnique() {
    Map<Location,Population> occupied_locations =
      world.getOccupiedLocations();

    for (Map.Entry<Location,Population> ol:occupied_locations.entrySet()) {
      assertNotSame(initial_population, ol.getValue());
      assertTrue("different seeds",
                 initial_population.getSeed() != ol.getValue().getSeed());
    }
  }

  @Test
  public void seedIsConstantForAGivenPop() {
    Location test_location = new Location(6,9);

    final long initial_seed = 
      world.getOccupiedLocations().get(test_location).getSeed();

    int timesteps = 3;
    long step = 1;
    while(world.incrementStep() < timesteps) {
      assertEquals(step, world.getStep());
      Map<Location,Population> occupied_locations =
        world.getOccupiedLocations();
      for (Map.Entry<Location,Population> ol:occupied_locations.entrySet()) {
        Population pop = ol.getValue();
        pop.grow();
      }
      long current_seed = occupied_locations.get(test_location).getSeed();
      assertEquals(initial_seed, current_seed);
      ++step;
    }
  }

  @Test
  public void simpleDistribute() {
    double migration_rate = 0.1;

    double initial = 1e5;
    double nMigrated = migration_rate * initial;
    double after_unoccupied = 2*nMigrated;
    double after_occupied = initial+(2*nMigrated);

    Location unoccupied_location  = new Location(5,5);
    Location occupied_location    = new Location(6,9);

    List<Location> unoccupied_locations = Arrays.asList(unoccupied_location,
                                                        unoccupied_location);

    List<Location> occupied_locations   = Arrays.asList(occupied_location,
                                                        occupied_location);

    Population migrant_pop = 
      initial_population.retrieveMigrants(migration_rate);
    List<Population> migrant_pops = Arrays.asList(migrant_pop, migrant_pop);

    //System.out.println("\n\nBefore distribution");
    //System.out.println("Occupied");
    //System.out.println(world.getOccupiedLocations());

    world.distribute(migrant_pops, unoccupied_locations);
    world.distribute(migrant_pops, occupied_locations);

    Map<Location,Population> ol = world.getOccupiedLocations();
    for (Map.Entry<Location,Population> en:ol.entrySet()) {
      Location loc = en.getKey();
      Population pop = en.getValue();

      for (int i=0; i<pop.getNTypes(); i++) {
        double coop = pop.getCoops().get(i);
        double cheat = pop.getCheats().get(i);
        if (loc.equals(unoccupied_location)) {
          assertEquals(after_unoccupied, coop, 0.0);
          assertEquals(after_unoccupied, cheat, 0.0);
        } else if (loc.equals(occupied_location)) {
          assertEquals(after_occupied, coop, 0.0);
          assertEquals(after_occupied, cheat, 0.0);
        } else {
          assertEquals(initial, coop, 0.0);
          assertEquals(initial, cheat, 0.0);
        }
      }
    }

    //System.out.println("\n\nAfter distribution; ");
    //System.out.println("Occupied");
    //System.out.println(world.getOccupiedLocations());

    //System.out.println("\n\nInitial population structure\n" + 
     //                  world.getInitialPopulations());
  }

  @Test
  public void saveWorld() throws IOException {
    int nLocations = (int) Math.round(rows*cols*initial_fraction_occupied);
    File data_path = new File("world_test.txt");
    if (data_path.exists()) data_path.delete();

    world  = new World(initial_layout, initial_population);

    BufferedReader reader = null;
    int line_count = 0;
    String line = null;
    try {
      world.saveState(data_path);
      world.saveState(data_path);
      reader = new BufferedReader(new FileReader(data_path));
      while ((line=reader.readLine()) != null) ++line_count;

    } catch (IOException e) {
      System.err.println("Couldn't save world!");
      e.printStackTrace();
    } finally {
      reader.close();
    }

    // +1 for header
    assertEquals(2*nLocations+1, line_count);
  }
}

