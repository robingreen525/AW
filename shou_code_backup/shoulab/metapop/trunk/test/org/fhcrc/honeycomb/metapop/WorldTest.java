package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.Location;
import org.fhcrc.honeycomb.metapop.StateSaver;

import org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator.FitnessUnchanged;

import org.fhcrc.honeycomb.metapop.environmentchanger.EnvironmentChanger;
import org.fhcrc.honeycomb.metapop.environmentchanger.StaticEnvironment;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.coordinate.CoordinatePicker;
import org.fhcrc.honeycomb.metapop.coordinate.SpecifiedCoordinatePicker;

import org.fhcrc.honeycomb.metapop.dilutionrule.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilutionrule.GlobalThresholdDilution;

import org.fhcrc.honeycomb.metapop.resource.Resource;
import org.fhcrc.honeycomb.metapop.resource.NullResource;

import org.junit.*;
import static org.junit.Assert.*;

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
import java.io.FilenameFilter;

class Rng {
    private static final long seed = 12345L;
    private static final RandomNumberUser rng = new RandomNumberUser(seed);
    public static RandomNumberUser getRNG() { return rng; }
}

class PopulationGenerator {
    private final List<Double> fitnesses = Arrays.asList(Math.log(2),
                                                         Math.log(2)*1.1,
                                                         Math.log(2)*1.2,
                                                         Math.log(2)*1.3);

    private final List<Double> freqs = Arrays.asList(0.25, 0.25, 0.25, 0.25);
    private final Types types = new Types(fitnesses, freqs);
    private final int    initial_size = 800000;
    private final double initial_coop_freq = 0.5;
    private final double cheat_adv = 0.02;
    private final double no_u = 0;
    private final FitnessCalculator fc = new FitnessUnchanged();
    private final Population population;

    public PopulationGenerator() {
         this.population =  new Population(initial_size, initial_coop_freq,
                                           cheat_adv, no_u, types, fc,
                                           Rng.getRNG());
    }

    public Population getPopulation() {
        return new Population(population);
    }
}

public class WorldTest {
    private World world;
    private final int rows = 10;
    private final int cols = 10;
    private final int initial_n_pops = 10;
    private double migration_rate  = 0.01;
    private double dilution_fraction = 0.5;
    private double max_pop = 1e7;
    private boolean exclude = true;
    private List<Coordinate> initial_coordinates =
        Arrays.asList(new Coordinate(1,1),
                      new Coordinate(2,2),
                      new Coordinate(3,3),
                      new Coordinate(4,4),
                      new Coordinate(5,5),
                      new Coordinate(6,6),
                      new Coordinate(7,7),
                      new Coordinate(8,8),
                      new Coordinate(9,9),
                      new Coordinate(10,10)
                      );

    private final File data_path = new File("test_output/world_test");

    private EnvironmentChanger ec = new StaticEnvironment();
    private Resource resource = new NullResource(100);
    private DilutionRule dilution_rule =
        new GlobalThresholdDilution(dilution_fraction);

    private CoordinatePicker lp = 
        new SpecifiedCoordinatePicker(initial_coordinates);

    private List<Population> initial_populations = 
        new ArrayList<Population>(initial_n_pops);

    private PopulationGenerator popgen = new PopulationGenerator();
    private Population initial_population = popgen.getPopulation();

    @Before
    public void setUp() {
        for (int i=0; i<initial_n_pops; i++) {
            initial_populations.add(popgen.getPopulation());
        }

        world = new World(rows, cols, resource, ec, initial_populations,
                          lp, null, migration_rate, dilution_rule,
                          max_pop, data_path);
    }

    @Test
    public void getLocations() {
        Coordinate occupied   = initial_coordinates.get(0);
        Coordinate unoccupied = new Coordinate(1,2);

        assertEquals("failed - wrong coordinate", 
                     occupied, world.getLocationAt(occupied).getCoordinate());
        assertNull("failed - should be null", world.getLocationAt(unoccupied));
    }

    @Test
    public void unoccupiedLocations() {
        List<Location> occupied = world.getOccupiedLocations();
        List<Coordinate> unoccupied = world.getUnoccupiedLocations();
        int expected_size = rows*cols - initial_n_pops;

        assertEquals("failed - unoccupied is wrong size",
                     expected_size, unoccupied.size());

        for (Coordinate un:unoccupied) {
            assertFalse("failed - contains unoccupied location",
                        occupied.contains(un));
        }
    }

    @Test
    public void initialLocations() {
        List<Location> occupied_locations = world.getOccupiedLocations();
        List<Coordinate> occupied_coordinates = world.getOccupiedCoordinates();

        assertEquals("failed - wrong number of occupied locations",
                     initial_n_pops, occupied_locations.size());
        assertEquals("failed - wrong number of occupied coordinates",
                     initial_n_pops, occupied_coordinates.size());

        for (int i=0; i<initial_n_pops; i++) {
            Coordinate initial_coord = initial_coordinates.get(i);
            Location loc = occupied_locations.get(i);
            Coordinate coord = occupied_coordinates.get(i);

            assertEquals("failed - coordinate mismatch", initial_coord, coord);
            assertNotSame("failed - coordinates are same object",
                          initial_coord, coord);

            assertEquals("failed - coordinate from location mismatch",
                         initial_coord, loc.getCoordinate());

            assertNotSame("failed - populations are same object",
                          initial_population, loc.getPopulation());
        }
    }

    @Test
    public void distributeTest() {
        Coordinate occupied_coordinate   = initial_coordinates.get(0);
        Coordinate occupied_coordinate2  = initial_coordinates.get(1);
        Coordinate unoccupied_coordinate = new Coordinate(1,2);

        double migration_rate = 0.1;
        Estimate migrant_size =
            new Estimate(initial_population.getSize() * migration_rate);
        double remaining = 
            initial_population.getSize() - migrant_size.getEstimate();

        Location occupied_location = world.getLocationAt(occupied_coordinate);

        Population migrants = 
            occupied_location.retrieveMigrants(migration_rate);
        assertEquals("wrong migrant size",
                     migrant_size.getEstimate(), migrants.getSize(),
                     migrant_size.getError(2));

        assertEquals("wrong remaining size", remaining,
                     occupied_location.populationSize(),
                     migrant_size.getError(2));

        // Migrate to empty location.
        List<Location> new_location = 
            Arrays.asList(new Location(unoccupied_coordinate,
                                       resource, migrants));
        Location emigration_location = 
            world.getLocationAt(unoccupied_coordinate);

        assertNull("unoccupied not null",
                   world.getLocationAt(unoccupied_coordinate));
        world.distribute(new_location);
        assertNotNull("newly occupied not null",
                      world.getLocationAt(unoccupied_coordinate));

        Location newly_occupied = world.getLocationAt(unoccupied_coordinate);
        assertNotSame("did not copy location", new_location, newly_occupied);
        assertEquals("wrong new size",
                     migrant_size.getEstimate(),
                     newly_occupied.populationSize(),
                     migrant_size.getError(2));

        // Migrate to occupied location.
        List<Location> new_location2 = 
            Arrays.asList(new Location(occupied_coordinate2,
                                       resource, migrants));

        Location emigration_location2 = 
            world.getLocationAt(occupied_coordinate2);
        assertNotNull("occupied null", emigration_location2);

        double expected_after_migrants = 
            emigration_location2.populationSize() + migrant_size.getEstimate();
        world.distribute(new_location2);
        assertEquals("wrong size after mixing",
                     expected_after_migrants,
                     emigration_location2.populationSize(),
                     migrant_size.getError());
    }

    @Test
    public void saveWorld() throws IOException {
        //System.out.println(world.getEnvChanger().getRNG());

        File[] test_files = world.getDataPath().listFiles();
        if (test_files != null) {
            for (File f : world.getDataPath().listFiles()) {
                f.delete();
            }
        }

        StateSaver ss = new StateSaver(world);
        ss.saveState();
        world.incrementStep();
        ss.saveState();

        String[] files = world.getDataPath().list(new FilenameFilter() {
            public boolean accept(File p, String s) {
                return !s.startsWith(".");
            }
        });

        //for (String file : files) { System.out.println(file); }

        assertEquals("correct number of files", 2, files.length);

        FileReader fr = new FileReader(new File(world.getDataPath(), "0.tab"));
        BufferedReader br = new BufferedReader(fr);

        String comment_line = "#rows:";
        String header_line = "";
        String data_line = "";
        for (int i=0; i<3; i++) {
            String line = br.readLine();
            if (i==0) {
                assertTrue("comment line", line.startsWith(comment_line));
            } else if (i==1) {
                header_line = line;
            } else if (i==2) {
                data_line = line;
            }
        }

        //System.out.println(header_line);
        //System.out.println(data_line);
        assertEquals("header and data have same # of columns",
                     header_line.split("\t").length,
                     data_line.split("\t").length);

    }
}
