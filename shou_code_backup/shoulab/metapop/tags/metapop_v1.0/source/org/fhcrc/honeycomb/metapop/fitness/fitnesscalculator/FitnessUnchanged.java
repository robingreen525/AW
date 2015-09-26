package org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator;

import org.fhcrc.honeycomb.metapop.Population;

import java.util.List;
import java.util.Arrays;

/**
 * A simple implementation that just returns the fitness unaltered.
 *
 * Created on 15 Feb, 2013
 * @author Adam Waite
 * @version $Rev: 1894 $, $Date: 2013-04-08 19:22:19 -0700 (Mon, 08 Apr 2013) $
 *
 */
public class FitnessUnchanged implements FitnessCalculator {

    public List<Double> calculate(Population pop, double max_fitness) {
        return Arrays.asList(max_fitness,
                             max_fitness*(1+pop.getCheatAdvantage()));
    }

    public String toString() {
        return "FitnessUnchanged.";
    }
}

