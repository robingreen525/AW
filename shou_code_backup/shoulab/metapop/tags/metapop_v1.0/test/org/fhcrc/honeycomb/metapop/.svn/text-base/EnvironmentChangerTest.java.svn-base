package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.environmentchanger.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.junit.*;
import static org.junit.Assert.*;

public class EnvironmentChangerTest {
    private List<Integer> at = Arrays.asList(1, 5, 10);
    private int max = Collections.max(at);
    private SpecifiedEnvironmentChanger sec = 
        new SpecifiedEnvironmentChanger(at);

    @Before
    public void setUp() {
    }

    @Test(expected=UnsupportedOperationException.class)
    public void needWorld() {
        sec.environmentChanged();
    }

    @Test
    public void binomialChanger() {
        RandomNumberUser rng = new RandomNumberUser();
        double prob = 0.2;
        BinomialEnvironmentChanger bec = 
            new BinomialEnvironmentChanger(prob, rng);

        assertEquals("prob is correct", prob, bec.getProb(), 0.0);
    }

    @Test
    public void specifiedChanger() {
        assertEquals("prob is correct", 1.0, sec.getProb(), 0.0);

        List<Boolean> result = new ArrayList<Boolean>(max);
        for (int i=0; i<max+1; i++) {
            result.add(sec.checkEnvironment(i));
        }

        for (int i=0; i<max+1; i++) {
            boolean v = result.get(i);
            boolean in_list = false;
            for (int j : at) {
                if (i == j) {
                    assertEquals(true, v);
                    in_list = true;
                }
            }
            if (!in_list) assertEquals(false, v);
        }
    }
}
