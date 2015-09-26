package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.Subpopulation;
import org.fhcrc.honeycomb.metapop.StepProvider;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.RandomNumberUser;
import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.fitness.IdentityCalculator;

import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilution.ThresholdDilution;
import org.fhcrc.honeycomb.metapop.dilution.IndividualThresholdDilution;
import org.fhcrc.honeycomb.metapop.dilution.GlobalThresholdDilution;
import org.fhcrc.honeycomb.metapop.dilution.PeriodicDilution;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

/** 
 * Tests DilutionRules.
 *
 * Created on 24 Apr, 2013
 * @author Adam Waite
 * @version $Id: DilutionRuleTest.java 2177 2013-06-26 22:05:32Z ajwaite $
 *
 */
public class DilutionRuleTest {
    private double conf = 0.95;

    private RandomNumberUser rng = new RandomNumberUser(1);
    private FitnessCalculator fc = new IdentityCalculator();
    private Coordinate large_coordinate = new Coordinate(1,1);
    private Coordinate small_coordinate = new Coordinate(3,2);

    private double resource = 0.0;
    private int large_subpop_size = 1000;
    private int small_subpop_size = 30;
    private String large_id  = "large";
    private String small_id = "small";
    private Subpopulation large_subpop = 
                new Subpopulation(large_subpop_size, fc, large_id, rng);
    private Subpopulation small_subpop = 
                new Subpopulation(small_subpop_size, fc, small_id, rng);

    private int small_rep = 1;
    private int large_rep = 2;

    private List<Subpopulation> small_subpop_list = 
                new ArrayList<Subpopulation>();
    private List<Subpopulation> large_subpop_list =
                new ArrayList<Subpopulation>();

    private Population large_pop;
    private Population small_pop;
    private int small_pop_size;
    private int large_pop_size;
    private List<Population> pops = new ArrayList<Population>();

    private int max_pop_size;
    private double[] test_fractions = {0.1, 0.9};

    @Before
    public void setUp() {

        for (int i=0; i<small_rep; i++) {
            small_subpop_list.add(large_subpop);
            small_subpop_list.add(small_subpop);
        }

        for (int i=0; i<large_rep; i++) {
            large_subpop_list.add(large_subpop);
            large_subpop_list.add(small_subpop);
        }

        small_pop = new Population(small_subpop_list, small_coordinate,
                                   resource, rng);
        large_pop = new Population(large_subpop_list, large_coordinate,
                                   resource, rng);

        small_pop_size = small_pop.getSize();
        large_pop_size = large_pop.getSize();
        max_pop_size = large_pop_size;

        pops.add(small_pop);
        pops.add(large_pop);
    }

    @Test 
    public void maxPopCorrect() {
        double frac = 0.5;
        double threshold = 
             new IndividualThresholdDilution(frac, large_pop_size).
                getThreshold();
        assertEquals("max_pop size wrong", large_pop_size, threshold, 0.0);
    }

    @Test
    public void thresholdDilution() {
        for (double frac:test_fractions) {
            DilutionRule thresh = 
                new IndividualThresholdDilution(frac, max_pop_size);
            Map<Coordinate, Double> map = thresh.generate(pops);
            assertEquals("wrong dilution amount",
                         frac, map.get(large_coordinate), 0.0);
            assertNull("diluting wrong coordinate", map.get(small_coordinate));
        }
    }

    @Test
    public void globalThresholdDilution() {
        for (double frac:test_fractions) {
            DilutionRule thresh = new GlobalThresholdDilution(frac,
                                                              max_pop_size);
            Map<Coordinate, Double> map = thresh.generate(pops);
            assertEquals("wrong large dilution amount",
                         frac, map.get(large_coordinate), 0.0);
            assertEquals("wrong dilution amount",
                         frac, map.get(small_coordinate), 0.0);
        }
    }

    @Test
    public void periodicDilution() {
        StepProvider sp = new SimpleStep();
        int dilute_every = 5;

        for (double frac:test_fractions) {
            DilutionRule periodic = new PeriodicDilution(frac,
                                                         dilute_every, sp);
            Map<Coordinate, Double> map = periodic.generate(pops);
            assertTrue("map not empty", map.isEmpty());
        }

        for (int i=0; i<5; i++) sp.incrementStep();

        for (double frac:test_fractions) {
            DilutionRule periodic = new PeriodicDilution(frac,
                                                         dilute_every, sp);

            Map<Coordinate, Double> map = periodic.generate(pops);
            assertEquals("wrong large dilution amount", 
                         frac, map.get(large_coordinate), 0.0);
            assertEquals("wrong small dilution amount", 
                         frac, map.get(small_coordinate), 0.0);
        }
    }
}
