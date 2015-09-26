package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.dilutionrule.*;
import org.fhcrc.honeycomb.metapop.environmentchanger.*;
import org.fhcrc.honeycomb.metapop.coordinate.*;
import org.fhcrc.honeycomb.metapop.resource.Resource;
import org.fhcrc.honeycomb.metapop.resource.NullResource;
import org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;

public class DilutionRuleTest {
    private World world;

    private final int rows = 10;
    private final int cols = 10;
    private final int initial_n_pops = 10;
    private long seed = 12345L;
    private final double cheat_adv = 0.02;
    private final double no_u = 0;
    private double dilution_fraction = 0.5;
    private int max_pop = 1000;
    private double migration_rate = 0.0;
    private boolean exclude = true;
    private boolean include = false;
    private List<Double> max_fitness = Arrays.asList(0.0);

    private Coordinate below_location = new Coordinate(9,7);
    private Coordinate above_location = new Coordinate(5,7);
    private List<Integer> below_thresh = Arrays.asList(max_pop/4);
    private List<Integer> above_thresh = Arrays.asList(max_pop);

    private final RandomNumberUser rng = new RandomNumberUser(seed);
    private Resource resource = new NullResource();
    private EnvironmentChanger ec = new StaticEnvironment();
    private DilutionRule dr = new GlobalThresholdDilution(dilution_fraction);
    private final FitnessCalculator fc = new FitnessUnchanged();
    private CoordinatePicker lp = 
        new SpecifiedCoordinatePicker(Arrays.asList(below_location,
                                                    above_location));
    private CoordinatePicker mp = new RandomPicker(exclude, rng);

    private final File data_path = new File("test_output/world_test");



    private Population low_pop;
    private Population high_pop;
    private List<Population> pops;

    @Before
    public void setUp() {
        low_pop = new Population(below_thresh, below_thresh, max_fitness,
                                 cheat_adv, no_u, fc, rng);
        high_pop = new Population(above_thresh, above_thresh,
                                  max_fitness, cheat_adv, no_u, fc, rng);

        pops = Arrays.asList(low_pop, high_pop);
        world = new World(rows, cols, resource, ec, pops, lp, mp,
                          migration_rate, dr, max_pop, data_path);
    }

    @Test
    public void periodicDilution() {
        int dilute_every = 5;
        DilutionRule dilution_rule = new PeriodicDilution(dilution_fraction,
                                                          dilute_every);
        world.setDilutionRule(dilution_rule);

        for (int i=0; i<dilute_every; i++) {

            Map<Location, Boolean> rules = dilution_rule.generate();
            for (Map.Entry<Location, Boolean> dr : rules.entrySet()) {
                assertFalse("no dilution", dr.getValue());
            }
            world.incrementStep();
        }

        Map<Location, Boolean> rules = dilution_rule.generate();
        for (Map.Entry<Location, Boolean> dr : rules.entrySet()) {
            assertTrue("dilution", dr.getValue());
        }
    }

    @Test
    public void thresholdDilution() {
        DilutionRule dilution_rule = new ThresholdDilution(dilution_fraction);
        world.setDilutionRule(dilution_rule);

        world.iterate(1, 1000);

        double small_coop_expected = below_thresh.get(0);
        double small_cheat_expected = small_coop_expected;

        Population below_pop = 
            world.getLocationAt(below_location).getPopulation();
        assertEquals("small pop coop", small_coop_expected,
                     below_pop.getNCoops(), 0.0);
        assertEquals("small pop cheat", small_cheat_expected,
                     below_pop.getNCheats(), 0.0);

        Estimate large = new Estimate(above_thresh.get(0) * dilution_fraction);

        Population above_pop = 
            world.getLocationAt(above_location).getPopulation();
        assertEquals("large pop coop", 
                     large.getEstimate(),
                     above_pop.getNCoops(),
                     large.getError(2));
        assertEquals("large pop cheat", 
                     large.getEstimate(),
                     above_pop.getNCheats(),
                     large.getError(2));
    }

    @Test
    public void globalThresholdDilution() {
        DilutionRule dilution_rule = 
            new GlobalThresholdDilution(dilution_fraction);
        world.setDilutionRule(dilution_rule);


        Map<Location, Boolean> rules = dilution_rule.generate();
        for (Map.Entry<Location, Boolean> dr : rules.entrySet()) {
            assertTrue("dilute all", dr.getValue());
        }

        world.iterate(1, 1000);

        Estimate small = new Estimate(below_thresh.get(0) * dilution_fraction);


        Population below_pop = 
            world.getLocationAt(below_location).getPopulation();
        assertEquals("small pop coop",
                     small.getEstimate(),
                     below_pop.getNCoops(),
                     small.getError(2));
        assertEquals("small pop cheat", 
                     small.getEstimate(),
                     below_pop.getNCheats(),
                     small.getError(2));

        Estimate large = new Estimate(above_thresh.get(0) * dilution_fraction);

        Population above_pop = 
            world.getLocationAt(above_location).getPopulation();
        assertEquals("large pop coop", 
                     large.getEstimate(),
                     above_pop.getNCoops(),
                     large.getError(2));
        assertEquals("large pop cheat",
                     large.getEstimate(),
                     above_pop.getNCheats(),
                     large.getError(2));
    }
}
