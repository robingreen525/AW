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
 * @version $Id: SubpopulationTest.java 2285 2013-08-14 21:12:57Z ajwaite $
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

    private Subpopulation sub;

    @Before
    public void setUp() {
        sub = new Subpopulation(initial_size, fc, id, rng);
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
