package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;
import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.fitness.SpecifiedCalculator;

import org.junit.*;
import static org.junit.Assert.*;

/** 
 * Tests Subpopulations.
 *
 * Created on 23 Apr, 2013
 * @author Adam Waite
 * @version $Id: SubpopulationTest.java 2240 2013-07-31 22:33:05Z ajwaite $
 *
 */
public class SubpopulationTest {
    private double conf = 0.95;
    private RandomNumberUser rng = new RandomNumberUser(1);
    private FitnessCalculator fc = new SpecifiedCalculator(0.0, 0.0);

    private double initial_resource = 1e7;
    private int initial_size = 1000;
    private double death_rate = 0.1;
    private String id = "test";

    private double unfinished_cells = 0.5;

    private Subpopulation sub;
    private Subpopulation sub_unfinished;

    @Before
    public void setUp() {
        sub = new Subpopulation(initial_size, fc, id, rng);
        sub_unfinished = new Subpopulation(initial_size, unfinished_cells,
                                           fc, id, rng);
    }

    @Test
    public void unfinishedCells() {
        double add = 0.7;
        double remainder = unfinished_cells + add - 1;
        int current_size = sub_unfinished.getSize();

        sub_unfinished.addToUnfinishedCells(add);

        assertEquals("adding to unfinished wrong", current_size+1,
                     sub_unfinished.getSize());

        assertEquals("unfinished remainder wrong", remainder,
                     sub_unfinished.getUnfinishedCells(), 0.0);

    }

    @Test
    public void copy() {
        Subpopulation copy = new Subpopulation(sub);

        assertNotSame("copy is shallow", sub, copy);
        assertNotEquals("copy is equal", sub, copy);

        assertEquals("gamma wrong", sub.getGamma(), copy.getGamma(), 0.0);
        assertEquals("release rate wrong", 
                     sub.getReleaseRate(), copy.getReleaseRate(), 0.0);
        assertEquals("id wrong", sub.getId(), copy.getId());
        assertEquals("size wrong", sub.getSize(), copy.getSize());
        assertEquals("unfinished cells wrong", sub.getUnfinishedCells(),
                     copy.getUnfinishedCells(), 0.0);
    }

    @Test
    public void grow() {
        int tests = 100;
        Estimate expect_fail = 
            IntegerEstimate.binomialEstimate(tests, 1-conf, conf);

        Estimate expected_die = 
            IntegerEstimate.binomialEstimate(initial_size, death_rate, conf);

        int birth_failed = 0;
        int death_failed = 0;
        for (int i=0; i<tests; i++) {
            double growth_rate = i/(double) tests;
            fc = new SpecifiedCalculator(growth_rate, death_rate);
            sub = new Subpopulation(initial_size, fc, id, rng);

            Estimate expected_births = 
                IntegerEstimate.binomialEstimate(initial_size, 
                                                 growth_rate, conf);
            Estimate expected_deaths = 
                IntegerEstimate.binomialEstimate(initial_size, 
                                                 death_rate, conf);


            int births = sub.getBirths(growth_rate);
            int deaths = sub.getDeaths(growth_rate);

            double[] birth_ci = expected_births.getCI();
            if (births < birth_ci[0] || births > birth_ci[1]) {
                birth_failed++;
            }

            double[] death_ci = expected_deaths.getCI();
            if (deaths < death_ci[0] || deaths > death_ci[1]) {
                death_failed++;
            }
        }
        FailReport.report("[SubpopulationTest] grow() births", birth_failed,
                          tests, expect_fail);
        FailReport.report("[SubpopulationTest] grow() deaths", death_failed,
                          tests, expect_fail);
    }


    @Test
    public void migrateExact() {
        double[] test_exact_fractions = {0.0, 1.0};

        for (double frac:test_exact_fractions) {
            Subpopulation copy = new Subpopulation(sub);
            int before_size = copy.getSize();

            double expected_migrants = frac * before_size;
            double expected_remaining = before_size - expected_migrants;

            Subpopulation migrants = copy.retrieveMigrants(frac);

            assertEquals("exact size diluted wrong",
                         expected_migrants, migrants.getSize(), 0.0);

            assertEquals("exact size remaining wrong",
                         expected_remaining, copy.getSize(), 0.0);
        }
    }

    @Test
    public void migrate() {
        double[] test_fractions = {0.1, 0.5, 0.9};

        Estimate expect_fail =
            IntegerEstimate.binomialEstimate(test_fractions.length,
                                             1-conf, conf);
        int failed = 0;
        for (double frac:test_fractions) {
            Subpopulation copy = new Subpopulation(sub);
            int before_size = copy.getSize();

            Estimate expected = 
                IntegerEstimate.binomialEstimate(before_size, frac, conf);

            Subpopulation migrants = copy.retrieveMigrants(frac);
            int migrants_size = migrants.getSize();
            int after_size = copy.getSize();

            assertEquals("wrong total size", before_size,
                         after_size+migrants_size, 0.0);

            double[] ci = expected.getCI();
            if (migrants_size < ci[0] || migrants_size > ci[1]) failed++;
        }
        FailReport.report("[SubpopulationTest] migrate()", failed,
                          test_fractions.length, expect_fail);
    }
}
