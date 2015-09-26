package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;
import org.fhcrc.honeycomb.metapop.Subpopulation;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.coordinate.picker.CoordinatePicker;
import org.fhcrc.honeycomb.metapop.coordinate.picker.SpecifiedPicker;

import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.fitness.SpecifiedCalculator;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

/** 
 * Tests Populations.
 *
 * Created on 24 Apr, 2013
 * @author Adam Waite
 * @version $Id: PopulationTest.java 2247 2013-08-04 23:53:00Z ajwaite $
 *
 */
public class PopulationTest {
    private double conf = 0.95;
    private RandomNumberUser rng = new RandomNumberUser(1);
    private Coordinate coord     = new Coordinate(1,1);
    private Coordinate coord2    = new Coordinate(2,3);
    private CoordinatePicker cp  = 
                new SpecifiedPicker(Arrays.asList(coord2));

    private int initial_size1 = 1000;
    private int initial_size2 = 30;
    private int initial_total_size = 0;

    private double initial_resource = 1e10;
    private double low_resource = 1000;
    private double very_low_resource = 20;
    private double no_death_rate = 0.0;
    private double death_rate = 0.1;
    private FitnessCalculator zero_fitness = 
        new SpecifiedCalculator(0.0, no_death_rate);
    private FitnessCalculator halving_fitness = 
        new SpecifiedCalculator(0.5, no_death_rate);
    private FitnessCalculator doubling_fitness =
        new SpecifiedCalculator(1.0, no_death_rate);

    private FitnessCalculator doubling_with_death =
        new SpecifiedCalculator(1.0, death_rate);

    private int n_subs;
    private Subpopulation doubling_sub1a;
    private Subpopulation doubling_sub1b;
    private Subpopulation doubling_sub2a;
    private Subpopulation doubling_sub2b;
    private Subpopulation doubling_death_sub1a;
    private Subpopulation doubling_death_sub1b;
    private Subpopulation doubling_death_sub2a;
    private Subpopulation doubling_death_sub2b;
    private Subpopulation halving_sub1a;
    private Subpopulation halving_sub1b;
    private Subpopulation halving_sub2a;
    private Subpopulation halving_sub2b;
    private Subpopulation static_sub1a;
    private Subpopulation static_sub1b;
    private Subpopulation static_sub2a;
    private Subpopulation static_sub2b;
    private List<Subpopulation> static_subs;
    private List<Subpopulation> halving_subs;
    private List<Subpopulation> doubling_subs;
    private List<Subpopulation> doubling_death_subs;

    private double capacity = initial_size1;
    private Population static_pop;
    private Population doubling_pop;
    private Population doubling_death_pop;
    private Population limited_half_pop;
    private Population halving_pop;
    private Population decreasing_resource_pop;

    @Before
    public void setUp() {
        static_sub1a = new Subpopulation(initial_size1, zero_fitness,
                                        "static1a", rng);
        static_sub1b = new Subpopulation(initial_size1, zero_fitness,
                                        "static1b", rng);
        static_sub2a = new Subpopulation(initial_size2, zero_fitness,
                                        "static2a", rng);
        static_sub2b = new Subpopulation(initial_size2, zero_fitness,
                                        "static2b", rng);
        halving_sub1a = new Subpopulation(initial_size1, halving_fitness,
                                         "halving1a", rng);
        halving_sub1b = new Subpopulation(initial_size1, halving_fitness,
                                         "halving1b", rng);
        halving_sub2a = new Subpopulation(initial_size2, halving_fitness,
                                         "halving2a", rng);
        halving_sub2b = new Subpopulation(initial_size2, halving_fitness,
                                         "halving2b", rng);
        doubling_sub1a = new Subpopulation(initial_size1, doubling_fitness,
                                          "doubling1a", rng);
        doubling_sub1b = new Subpopulation(initial_size1, doubling_fitness,
                                          "doubling1b", rng);
        doubling_sub2a = new Subpopulation(initial_size2, doubling_fitness,
                                          "doubling2a", rng);
        doubling_sub2b = new Subpopulation(initial_size2, doubling_fitness,
                                          "doubling2b", rng);

        doubling_death_sub1a = new Subpopulation(initial_size1,
                                                 doubling_with_death,
                                                 "doubling_death1a", rng);
        doubling_death_sub1b = new Subpopulation(initial_size1,
                                                 doubling_with_death,
                                                 "doubling_death1b", rng);
        doubling_death_sub2a = new Subpopulation(initial_size2,
                                                 doubling_with_death,
                                                 "doubling_death2a", rng);
        doubling_death_sub2b = new Subpopulation(initial_size2,
                                                 doubling_with_death,
                                                 "doubling_death2b", rng);

        static_subs = new ArrayList<Subpopulation>(
                Arrays.asList(static_sub1a, static_sub1b,
                              static_sub2a, static_sub2b));

        doubling_subs = new ArrayList<Subpopulation>(
                Arrays.asList(doubling_sub1a, doubling_sub1b,
                              doubling_sub2a, doubling_sub2b));

        doubling_death_subs = new ArrayList<Subpopulation>(
                Arrays.asList(doubling_death_sub1a, doubling_death_sub1b,
                              doubling_death_sub2a, doubling_death_sub2b));

        halving_subs = new ArrayList<Subpopulation>(
                Arrays.asList(halving_sub1a, halving_sub1b,
                              halving_sub2a, halving_sub2b));

        static_pop = new Population(static_subs, coord, initial_resource, rng);

        doubling_pop = new Population(doubling_subs, coord, initial_resource,
                                      rng);

        doubling_death_pop = new Population(doubling_death_subs, coord,
                                            initial_resource, capacity,
                                            rng);

        limited_half_pop = new Population(halving_subs, coord,
                                          low_resource, rng);

        halving_pop = new Population(halving_subs, coord, 
                                     initial_resource, rng);

        for (Subpopulation sub:static_subs) {
            initial_total_size += sub.getSize();
        }
        n_subs = static_subs.size();
    }

    @Test
    public void coordinate() {
        assertEquals("coordinate is not equal",
                     coord, static_pop.getCoordinate());
        assertNotSame("coordinate is same",
                      coord, static_pop.getCoordinate());
    }

    @Test
    public void numberOfSubpopulations() {
        assertEquals("wrong number of subpops", 
                     n_subs,
                     static_pop.getNSubpopulations());
    }

    @Test
    public void copy() {
        assertNotSame("copy is same reference", static_subs.get(0),
                      static_pop.getSubpopulations().get(0));
    }

    @Test
    public void sizeBeforeGrowth() {
        assertEquals("wrong size before growth",
                     initial_total_size,
                     doubling_pop.getSize());
    }

    @Test
    public void getSizeById() {
        Map<String, Integer> sbid = static_pop.getSizeById();
        assertEquals("large pop wrong", initial_size1,
                     sbid.get("static1a"), 0.0);
        assertEquals("small pop wrong", initial_size2,
                     sbid.get("static2a"), 0.0);
    }

    @Test
    public void getSpecificSizeById() {
        assertEquals("large pop wrong",
                     initial_size1,
                     static_pop.getSizeById("static1a"),
                     0.0);
        assertEquals("small pop wrong",
                     initial_size2,
                     static_pop.getSizeById("static2a"),
                     0.0);
    }

    @Test
    public void getNonExistentId() {
        assertEquals("non-existent size wrong", 0.0,
                     static_pop.getSizeById("null"), 0.0);
    }

    @Test
    public void collectMigrantsExact() {
        double[] exact_migration_rates = {0.0, 1.0};
        for (double mr:exact_migration_rates) {
            Population copy = new Population(static_pop);
            int before_size = copy.getSize();

            double expected_migrated = mr*before_size;
            double expected_remain = before_size - expected_migrated;

            Population migrants = copy.collectMigrants(mr);

            assertEquals("exact size migrants wrong",
                         expected_migrated,
                         migrants.getSize(), 0.0);

            assertEquals("exact size migrants wrong",
                         expected_remain,
                         copy.getSize(), 0.0);
        }
    }

    @Test
    public void collectMigrants() {
        double[] migration_rates = {0.1, 0.25, 0.5, 0.75, 0.9};
        Estimate expect_fail =
            IntegerEstimate.binomialEstimate(migration_rates.length,
                                             1-conf, conf);

        int failed = 0;
        for (double mr:migration_rates) {
            Population copy = new Population(static_pop);
            int before_size = copy.getSize();

            Estimate expected = IntegerEstimate.binomialEstimate(before_size,
                                                                 mr, conf);
            double expected_remain = before_size - expected.getEstimate();

            Population migrants = copy.collectMigrants(mr);
            int migrant_size = migrants.getSize();
            int after_size = copy.getSize();

            assertEquals("wrong total size",
                         before_size,
                         after_size+migrant_size, 0.0);

            double[] ci = expected.getCI();
            if (migrant_size < ci[0] || migrant_size > ci[1]) failed++;
        }
        FailReport.report("[PopulationTest] collectMigrants()", failed,
                          migration_rates.length, expect_fail);
    }

    @Test
    public void dilute() {
        double [] dilutions = {0.0, 0.1, 0.25, 0.5, 0.75, 0.9, 1.0};
        for (double dilution:dilutions) {
            Population copy = new Population(static_pop);
            double resource_before = copy.getResource();
            copy.dilute(dilution);
            assertEquals("wrong resource dilution",
                         resource_before*(1-dilution), copy.getResource(),
                         0.0);
        }
    }

    @Test
    public void mix() {
        Population copy = new Population(static_pop);
        static_pop.mix(copy);
        for (int i=0; i<copy.getNSubpopulations(); i++) {
            assertEquals("new pop size wrong",
                         copy.getSubpopulations().get(i).getSize()*2,
                         static_pop.getSubpopulations().get(i).getSize());
        }
    }

    @Test
    public void mixNew() {
        Subpopulation new_sub = 
            new Subpopulation(initial_size1, zero_fitness, "new", rng);
        Population new_pop = 
            new Population(Arrays.asList(new_sub), coord, 0.0, rng);
        int nsubs_before = static_pop.getSubpopulations().size();

        static_pop.mix(new_pop);
        assertEquals("wrong subpop size", nsubs_before+1,
                     static_pop.getSubpopulations().size());
        assertEquals("n_supopulations not updated", 
                     static_pop.getSubpopulations().size(),
                     static_pop.getNSubpopulations());
    }

    @Test
    public void generator() {
        int number = 10;
        List<Coordinate> coords = Arrays.asList(new Coordinate(1,1),
                                                new Coordinate(2,2),
                                                new Coordinate(3,3),
                                                new Coordinate(4,4),
                                                new Coordinate(5,5),
                                                new Coordinate(6,6),
                                                new Coordinate(7,7),
                                                new Coordinate(8,8),
                                                new Coordinate(9,9),
                                                new Coordinate(10,10));

        cp = new SpecifiedPicker(coords);
  
        List<Population> pops = Population.generate(number, static_subs,
                                                    initial_resource, cp, rng);
        assertEquals("pops wrong size", number, pops.size());
    }
}
