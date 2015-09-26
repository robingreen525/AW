package org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator;

import org.fhcrc.honeycomb.metapop.Population;
import java.util.List;

/** 
 * Calculates fitnesses.
 *
 * Created on 14 Feb, 2013
 * @author Adam Waite
 * @version $Rev: 1894 $, $Date: 2013-04-08 19:22:19 -0700 (Mon, 08 Apr 2013) $
 *
 */
public interface FitnessCalculator {
    /**
     * Calculates fitnesses of cooperators and cheaters based on cooperator
     * fitness.
     *
     * @param pop             a <code>Population</code> object.
     * @param maximum_fitness the maximum fitness of the cooperator.
     * @return                a <code>List</code> containing the fitness of
     *                        the cooperator (first) and cheater (second).
     *
     */
    abstract public List<Double> calculate(Population pop,
                                           double maximum_fitness);
}
