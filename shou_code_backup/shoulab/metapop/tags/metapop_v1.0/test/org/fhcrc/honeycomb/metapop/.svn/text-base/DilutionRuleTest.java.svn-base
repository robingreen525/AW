package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.dilutionrule.*;
import org.fhcrc.honeycomb.metapop.environmentchanger.*;
import org.fhcrc.honeycomb.metapop.coordinate.*;
import org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;

@Ignore
public class DilutionRuleTest {
    /*
    private World world;
    private final int rows = 10;
    private final int cols = 10;
    private final int initial_n_pops = 10;
    private long seed = 12345L;
    private final RandomNumberUser rng = new RandomNumberUser(seed);
    private final double cheat_adv = 0.02;
    private final double no_u = 0;
    private double dilution_fraction = 0.5;
    private EnvironmentChanger ec = new StaticEnvironment();
    private final FitnessCalculator fc = new FitnessUnchanged();
    private LocationPicker lp = new UniqueRandomPicker(rng);
    private LocationPicker mp = new NonIdRandomPicker(rng);

    private final File data_path = new File("test_output/world_test");
    private double max_pop = 1000;

    private Location below_location = new Location(9,7);
    private Location above_location = new Location(5,7);
    private List<Double> below_dilution = Arrays.asList(max_pop/4);
    private List<Double> above_dilution = Arrays.asList(max_pop/2);

    private List<Double> max_fitness = Arrays.asList(Math.log(2));
    private double migration_rate = 0.0;

    private Population low_pop;
    private Population high_pop;
    private List<Population> pops;

    @Before
    public void setUp() {
        low_pop = new Population(below_dilution, below_dilution, max_fitness,
                                 cheat_adv, no_u, fc, rng);
        high_pop = new Population(above_dilution, above_dilution,
                                  max_fitness, cheat_adv, no_u, fc, rng);

        pops = Arrays.asList(low_pop, high_pop);
    }

    @Test
    public void periodicDilution() {
        int dilute_every = 5;
        DilutionRule dilution_rule = new PeriodicDilution(dilution_fraction,
                                                          dilute_every);

        world = new World(rows, cols, ec, pops,
                          lp, mp, migration_rate, dilution_rule,
                          max_pop, data_path);

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

        world = new World(rows, cols, ec, pops,
                          lp, mp, migration_rate, dilution_rule,
                          max_pop, data_path);

        world.iterate(1, 1000);

        Map<Location, Population> ols = world.getOccupiedLocations();

        double small_coop_expected = noDilutionFitness(below_dilution, 1);
        double small_cheat_expected = noDilutionFitness(below_dilution,
                                                        (1+cheat_adv));

        assertEquals("small pop coop", small_coop_expected,
                     ols.get(below_location).getNCoops(), 0.0);
        assertEquals("small pop cheat", small_cheat_expected,
                     ols.get(below_location).getNCheats(), 0.0);

        double large_coop_expected = dilutionFitness(above_dilution, 1);
        double large_cheat_expected = dilutionFitness(above_dilution,
                                                      (1+cheat_adv));

        assertEquals("large pop coop", large_coop_expected,
                     ols.get(above_location).getNCoops(), 0.0);
        assertEquals("large pop cheat", large_cheat_expected,
                     ols.get(above_location).getNCheats(), 0.0);
    }

    @Test
    public void globalThresholdDilution() {
        DilutionRule dilution_rule = 
            new GlobalThresholdDilution(dilution_fraction);

        world = new World(rows, cols, ec, pops,
                          lp, mp, migration_rate, dilution_rule,
                          max_pop, data_path);


        Map<Location, Boolean> rules = dilution_rule.generate();
        for (Map.Entry<Location, Boolean> dr : rules.entrySet()) {
            assertTrue("dilute all", dr.getValue());
        }

        world.iterate(1, 1000);

        double small_coop_expected = dilutionFitness(below_dilution, 1);
        double small_cheat_expected = dilutionFitness(below_dilution,
                                                      (1+cheat_adv));

        Map<Location, Population> ols = world.getOccupiedLocations();

        assertEquals("small pop coop", small_coop_expected,
                     ols.get(below_location).getNCoops(), 0.0);
        assertEquals("small pop cheat", small_cheat_expected,
                     ols.get(below_location).getNCheats(), 0.0);

        double large_coop_expected = dilutionFitness(above_dilution, 1);
        double large_cheat_expected = dilutionFitness(above_dilution,
                                                      (1+cheat_adv));

        assertEquals("large pop coop", large_coop_expected,
                     ols.get(above_location).getNCoops(), 0.0);
        assertEquals("large pop cheat", large_cheat_expected,
                     ols.get(above_location).getNCheats(), 0.0);
    }

    private double dilutionFitness(List<Double> N0, double r) {
        return N0.get(0) * dilution_fraction * Math.exp(max_fitness.get(0)*r);
    }

    private double noDilutionFitness(List<Double> N0, double r) {
        return N0.get(0) * Math.exp(max_fitness.get(0)*r);
    }
    */
}
