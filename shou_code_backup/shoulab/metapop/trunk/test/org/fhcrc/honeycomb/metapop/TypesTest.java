package org.fhcrc.honeycomb.metapop;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;

public class TypesTest {
    Types types;
    List<Double> good_freqs      = Arrays.asList(0.95, 0.03, 0.01, 0.01);
    List<Double> bad_freqs       = Arrays.asList(0.95, 0.07, 0.03, 0.02);
    List<Double> too_many_freqs  = Arrays.asList(0.95, 0.02, 0.02, 0.005,
                                                 0.005);

    List<Double> fitnesses = Arrays.asList(1.0,  1.1,  1.2,  1.3);

    @Test(expected=RuntimeException.class)
    public void wrongFreqs() {
        types = new Types(fitnesses, bad_freqs);
    }

    @Test(expected=RuntimeException.class)
    public void wrongLengths() {
        types = new Types(fitnesses, too_many_freqs);
    }

    @Test 
    public void initialzeTypes() {
        types = new Types(fitnesses, good_freqs);
    }
}
