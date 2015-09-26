package org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.fitness.cheaterloadcalculator.CheaterLoadCalculator;

import java.util.List;
import java.util.Arrays;

/** 
 * Calculates the fitness of the cheater relative to the cooperator 
 * <em>before</em> considering the cheater load.
 *
 * Created on 14 Feb, 2013
 * @author Adam Waite
 * @version $Rev: 1895 $, $Date: 2013-04-08 19:51:54 -0700 (Mon, 08 Apr 2013) $
 *
 */
public class FitnessBeforeLoad implements FitnessCalculator {
    private CheaterLoadCalculator clc;

    public FitnessBeforeLoad(CheaterLoadCalculator clc) { this.clc = clc; }

    public List<Double> calculate(final Population pop,
                                  final double max_fitness)
    {
        double cheat_freq = pop.getCheatFrequency();
        double cheat_adv = 1 + pop.getCheatAdvantage();

        double coop_fitness  = this.clc.calculate(cheat_freq, max_fitness);
        double cheat_fitness = this.clc.calculate(cheat_freq,
                                                  max_fitness*cheat_adv);

        return Arrays.asList(coop_fitness, cheat_fitness);
    }

    public String toString() {
        return "FitnessBeforeLoad with " + clc;
    }
}
