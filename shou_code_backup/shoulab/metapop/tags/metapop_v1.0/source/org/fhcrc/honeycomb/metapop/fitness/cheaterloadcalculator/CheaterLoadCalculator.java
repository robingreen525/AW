package org.fhcrc.honeycomb.metapop.fitness.cheaterloadcalculator;

/**
 * Calculates the load imposed on a population due to the presence of
 * cheaters.
 * Created on 7 Feb, 2012
 * @author Adam Waite
 * @version $Rev: 1894 $, $Date: 2013-04-08 19:22:19 -0700 (Mon, 08 Apr 2013) $
 *
 */
public interface CheaterLoadCalculator {

    /** calculates the change in fitness due to cheater frequency.
     * @param cheater_freq the current cheater frequency.
     * @param max_fitness  the fitness when cheater frequency = 0.
     *
     * @return             the adjusted fitness.
     *
     */
    abstract public double calculate(double cheater_freq, double max_fitness);
}
