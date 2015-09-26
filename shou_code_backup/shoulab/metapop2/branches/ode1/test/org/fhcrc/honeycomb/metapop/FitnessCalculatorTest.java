package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.fitness.MonodCalculator;

import org.junit.*;
import static org.junit.Assert.*;

/** 
 * Tests FitnessCalculators.
 *
 * Created on 23 Apr, 2013
 * @author Adam Waite
 * @version $Id: FitnessCalculatorTest.java 2037 2013-05-10 02:51:54Z ajwaite $
 *
 */
public class FitnessCalculatorTest {
    private double vmax = StrictMath.log(2)/2;
    private double km   = 1.0;
    private double d    = 1.0;

    private FitnessCalculator monod_calculator;

    private double small_max = 1.0;
    private double large_max = 20.0;

    @Before
    public void setUp() {
        monod_calculator = new MonodCalculator(vmax, km, d);
    }

    @Test
    public void monodCalculator() {
        double min_growth = 0.0;

        assertEquals("min growth incorrect", min_growth, 
                     monod_calculator.calculateGrowthRate(0.0), 0.0);
        assertEquals("vmax incorrect", vmax+d, 
                     monod_calculator.calculateGrowthRate(1e9),
                     0.001);

        for (int i=0; i<100; i++) {
            double expected = monod(vmax, km, i);

            assertEquals("v incorrect", expected,
                         monod_calculator.calculateGrowthRate(i), 0.0);
        }

        vmax = 0.99;
        km   = 10.0;
        d    = 2.0;
        monod_calculator = new MonodCalculator(vmax, km, d);

        assertEquals("min growth incorrect", min_growth,
                     monod_calculator.calculateGrowthRate(0.0), 0.0);
        assertEquals("vmax incorrect", vmax+d, 
                     monod_calculator.calculateGrowthRate(1e9), 0.001);

        for (int i=0; i<100; i++) {
            double expected = monod(vmax, km, i);

            assertEquals("v incorrect", expected, 
                         monod_calculator.calculateGrowthRate(i), 0.0);
        }
    }

    public double monod(double vmax, double km, double s)
    {
        return ((vmax+d)*s) / (km+s);
    }
}

