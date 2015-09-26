package org.fhcrc.honeycomb.metapop.fitness.cheaterloadcalculator;

/** 
 * A <code>CheaterLoadCalculator</code> that returns the fitness given to it.
 *
 * Created on 9 Feb, 2012
 * @author Adam Waite
 * @version $Rev: 1894 $, $Date: 2013-04-08 19:22:19 -0700 (Mon, 08 Apr 2013) $
 *
 */
public class NoCheaterLoad implements CheaterLoadCalculator {
    public NoCheaterLoad() { super(); }
    public double calculate(final double cheater_freq, final double fitness) {
        return fitness;
    }

    @Override
    public String toString() {
        return new String("NoCheaterLoad");
    }
}
