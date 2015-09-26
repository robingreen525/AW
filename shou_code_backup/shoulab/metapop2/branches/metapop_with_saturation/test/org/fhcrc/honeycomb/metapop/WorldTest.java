package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.World;
import org.fhcrc.honeycomb.metapop.StepProvider;
import org.fhcrc.honeycomb.metapop.Saveable;
import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.Subpopulation;
import org.fhcrc.honeycomb.metapop.RandomNumberUser;

import org.fhcrc.honeycomb.metapop.environment.EnvironmentChanger;
import org.fhcrc.honeycomb.metapop.environment.StaticEnvironment;

import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilution.GlobalThresholdDilution;
import org.fhcrc.honeycomb.metapop.dilution.PeriodicDilution;

import org.fhcrc.honeycomb.metapop.migration.MigrationRule;
import org.fhcrc.honeycomb.metapop.migration.NoMigration;

import org.fhcrc.honeycomb.metapop.mutation.MutationRule;
import org.fhcrc.honeycomb.metapop.mutation.NoMutation;

import org.fhcrc.honeycomb.metapop.stop.StopCondition;
import org.fhcrc.honeycomb.metapop.stop.NoStop;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.coordinate.CoordinateProvider;
import org.fhcrc.honeycomb.metapop.coordinate.picker.CoordinatePicker;
import org.fhcrc.honeycomb.metapop.coordinate.picker.SpecifiedPicker;

import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.fitness.IdentityCalculator;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.io.File;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Testing {@ World} class.
 * Created on 26 Apr, 2013
 *
 * @version $Id: WorldTest.java 2177 2013-06-26 22:05:32Z ajwaite $
 *
 */
public class WorldTest {
    private double conf = 0.95;

    private RandomNumberUser rng = new RandomNumberUser(1);
    private FitnessCalculator fc = new IdentityCalculator();
    private Coordinate coord     = new Coordinate(1,1);
    private double no_resource = 0.0;

    // Subpopulations
    private int initial_size1 = 1000;
    private int initial_size2 = 30;
    private int initial_total_size = 0;
    private String id  = "large";
    private String id2 = "small";

    private int n_subs = 4;
    private Subpopulation sub;
    private Subpopulation sub2;
    private List<Subpopulation> subs = new ArrayList<Subpopulation>(n_subs);

    // Populations
    private int initial_n_pops = 10;
    private Population static_pop;
    private List<Population> initial_populations = 
                new ArrayList<Population>(initial_n_pops);


    // World
    private final File data_path = new File("output_test/world_test");
    private int rows = 10;
    private int cols = 10;
    private double capacity = 1e7;
    private double dilution_fraction = 0.5;
    private boolean exclude = true;
    private Coordinate starting_coordinate = new Coordinate(1,1);

    private List<Coordinate> initial_coords =
                Arrays.asList(new Coordinate(1,1), new Coordinate(2,2),
                              new Coordinate(3,3), new Coordinate(4,4),
                              new Coordinate(5,5), new Coordinate(6,6),
                              new Coordinate(7,7), new Coordinate(8,8),
                              new Coordinate(9,9), new Coordinate(10,10));

    private CoordinatePicker cp = new SpecifiedPicker(initial_coords);
    private EnvironmentChanger env_changer = new StaticEnvironment();

    private DilutionRule dilution_rule =
                new GlobalThresholdDilution(dilution_fraction, (int) capacity);
    private MigrationRule migration_rule = new NoMigration();
    private StopCondition stop_condition = new NoStop();
    private MutationRule mutation_rule   = new NoMutation();

    private World world;
    private OccupiedLocations ols;


    @Before
    public void setUp() {
        sub = new Subpopulation(initial_size1, fc, id, rng);
        sub2 = new Subpopulation(initial_size2, fc, id2, rng);

        subs.add(new Subpopulation(sub));
        subs.add(new Subpopulation(sub));
        subs.add(new Subpopulation(sub2));
        subs.add(new Subpopulation(sub2));

        for (Subpopulation sub:subs) initial_total_size += sub.getSize();

        initial_populations = Population.generate(initial_n_pops, subs, 
                                                  no_resource, cp, rng);

        world = new World(rows, cols,
                          initial_populations,
                          env_changer, dilution_rule, mutation_rule,
                          migration_rule, stop_condition, data_path);

        ols = new OccupiedLocations(initial_populations, rows*cols);
    }

    @Test
    public void initialize() {
        assertEquals("wrong number of initial pops",
                     initial_populations.size(),
                     world.getInitialPopulations().size(), 0.0);
    }

    @Test
    public void headers() {
        assertFalse(world.getHeaders().isEmpty());
    }

    @Test
    public void occupiedLocationInitialization() {
        assertEquals("wrong size",
                     initial_populations.size(), ols.getSize(), 0.0);
    }

    @Test
    public void isOccupied() {
        assertTrue("should be occupied",
                   ols.isOccupied(initial_coords.get(0)));
    }

    @Test
    public void mix() {
        Coordinate test_coord = initial_coords.get(0);
        Population incoming = initial_populations.get(0);
        ols.addOrMix(incoming);
        assertEquals("wrong pop size", 
                     incoming.getSize()*2,
                     ols.getPopulationAt(test_coord).getSize(), 0.0);
    }

    @Test
    public void add() {
        Coordinate test_coord = new Coordinate(1,2);
        Population new_pop = initial_populations.get(0);
        new_pop.setCoordinate(test_coord);
        ols.addOrMix(new_pop);

        assertEquals("wrong number of pops", initial_populations.size()+1,
                     ols.getSize());

        assertEquals("wrong pop size", 
                     new_pop.getSize(),
                     ols.getPopulationAt(test_coord).getSize(), 0.0);
    }

    @Test
    public void worldSize() {
        double expected_size = 0;
        for (Population pop:initial_populations) {
            expected_size += pop.getSize();
        }

        assertEquals("wrong world size", expected_size, world.getSize(), 0.0);
    }

    @Test
    public void sizeById() {
        Estimate large = IntegerEstimate.poissonEstimate(initial_size1, conf);
        Estimate small = IntegerEstimate.poissonEstimate(initial_size2, conf);
        double[] large_ci = large.getCI();
        double[] small_ci = small.getCI();

        assertTrue("'large' size out of expected range",
                   world.getSizeById("large") > large_ci[0] && 
                   world.getSizeById("large") > large_ci[1]); 

        assertTrue("'small' size out of expected range",
                   world.getSizeById("small") > small_ci[0] && 
                   world.getSizeById("small") > small_ci[1]); 
    }
}
