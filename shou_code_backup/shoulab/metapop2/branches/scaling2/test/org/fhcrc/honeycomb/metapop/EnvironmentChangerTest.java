package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.StepProvider;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.RandomNumberUser;

import org.fhcrc.honeycomb.metapop.environment.EnvironmentChanger;
import org.fhcrc.honeycomb.metapop.environment.BinomialEnvironmentChanger;
import org.fhcrc.honeycomb.metapop.environment.SpecifiedEnvironmentChanger;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * A simple implementation of a {@code StepProvider} for testing.
 * Created on 26 Apr, 2013
 *
 * @version $Id: EnvironmentChangerTest.java 1990 2013-04-26 21:39:23Z ajwaite $
 *
 */
public class EnvironmentChangerTest {
    private double   conf = 0.95;
    private double[] probs = {0.1, 0.25, 0.5, 0.75, 0.9};

    private RandomNumberUser rng = new RandomNumberUser(1);
    private StepProvider sp;

    @Before
    public void setUp() {
        sp = new SimpleStep();
    }

    @Test
    public void binomialChanger() {
        int tests = 1000;

        Estimate expect_fail =
            IntegerEstimate.binomialEstimate(probs.length, 1-conf, conf);

        int failed = 0;
        for (double prob:probs) {
            Estimate expected =
                IntegerEstimate.binomialEstimate(tests, prob, conf);

            EnvironmentChanger binom =
                new BinomialEnvironmentChanger(prob, rng);

            assertEquals("prob is wrong", prob, binom.getProb(), 0.0);

            int change_count = 0;
            for (int i=0; i<tests; i++) {
                if (binom.environmentChanged()) change_count++;
            }

            double[] ci = expected.getCI();
            if (change_count < ci[0] || change_count > ci[1]) failed++;
        }
        FailReport.report("[EnvironmentChangerTest] binomialChanger()",
                          failed, probs.length, expect_fail);

    }

    @Test(expected=IllegalArgumentException.class)
    public void emptyList() {
        List<Integer> empty = new ArrayList<Integer>();
        EnvironmentChanger specified = 
            new SpecifiedEnvironmentChanger(sp, empty);
    }

    @Test
    public void specifiedChanger() {
        List<Integer> change_list = Arrays.asList(10, 23, 109);
        Map<Integer, Boolean> change_map = 
            new HashMap<Integer, Boolean>(change_list.size());

        for (Integer i:change_list) {
            change_map.put(i, true);
        }

        EnvironmentChanger specified = 
            new SpecifiedEnvironmentChanger(sp, change_list);

        while (sp.getStep() < 110) {
            boolean env_changed = specified.environmentChanged();
            if (change_map.containsKey(sp.getStep())) {
                assertTrue("should have changed", env_changed);
            } else {
                assertFalse("should not have changed", env_changed);
            }
            sp.incrementStep();
        }
    }
}
