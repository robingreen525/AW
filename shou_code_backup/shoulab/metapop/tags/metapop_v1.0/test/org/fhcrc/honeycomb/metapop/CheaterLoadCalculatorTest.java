package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.fitness.cheaterloadcalculator.*;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.Arrays;

public class CheaterLoadCalculatorTest {
    double initial_fitness = StrictMath.log(2);
    List<Double> cheater_loads = Arrays.asList(0.0, 0.5, 1.0);

    @Test
    public void noLoad() {
        CheaterLoadCalculator nlc = new NoCheaterLoad();

        for (double load:cheater_loads) {
            assertEquals(initial_fitness,
                         nlc.calculate(load, initial_fitness), 0.0);
        }
    }

    @Test
    public void linearLoad() {
        double max_death_rate = -0.3;
        CheaterLoadCalculator llc = new LinearCheaterLoad(max_death_rate);

        assertEquals("no load", initial_fitness, 
                llc.calculate(cheater_loads.get(0), initial_fitness), 
                1e-15);

        double half_load_expected =
            (max_death_rate-initial_fitness) * cheater_loads.get(1) +
            initial_fitness;

        assertEquals("half load", half_load_expected, 
                llc.calculate(cheater_loads.get(1), initial_fitness),
                1e-15);

        assertEquals("full load", max_death_rate, 
                llc.calculate(cheater_loads.get(2), initial_fitness),
                1e-15);
    }

    @Test
    public void relativeLinearLoad() {
        double max_reduction = 0.1;
        initial_fitness = 0.5;
        CheaterLoadCalculator rlc =
            new RelativeLinearCheaterLoad(max_reduction);

        assertEquals("no load", initial_fitness,
                rlc.calculate(cheater_loads.get(0), initial_fitness), 1e-15);

        assertEquals("half load", 0.475,
                rlc.calculate(cheater_loads.get(1), initial_fitness), 1e-15);

        assertEquals("full load", 0.45,
                rlc.calculate(cheater_loads.get(2), initial_fitness), 1e-15);

        max_reduction = 1.1;
        rlc = new RelativeLinearCheaterLoad(max_reduction);

        assertEquals("no load", initial_fitness,
                rlc.calculate(cheater_loads.get(0), initial_fitness), 1e-15);

        assertEquals("half load", 0.225,
                rlc.calculate(cheater_loads.get(1), initial_fitness), 1e-15);

        assertEquals("full load", -0.05,
                rlc.calculate(cheater_loads.get(2), initial_fitness), 1e-15);
    }
}
