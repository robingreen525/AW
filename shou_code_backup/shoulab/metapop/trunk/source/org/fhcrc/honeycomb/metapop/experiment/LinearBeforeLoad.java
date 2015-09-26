package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.AdaptiveRace;
import org.fhcrc.honeycomb.metapop.fitness.cheaterloadcalculator.CheaterLoadCalculator;
import org.fhcrc.honeycomb.metapop.fitness.cheaterloadcalculator.LinearCheaterLoad;
import org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator.FitnessBeforeLoad;

/** 
 * Implements a linear cheater load that is calculated before the fitness
 * advantage of cheaters is considered.
 *
 * Created on 8 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 */
public abstract class LinearBeforeLoad extends AdaptiveRace {
    protected double max_dr_per_min;

    public LinearBeforeLoad(String args[]) { super(args); }

    @Override
    public FitnessCalculator makeFitnessCalculator() {
        max_dr_per_min = maxDeathRatePerHr()/AdaptiveRace.MIN_PER_HR;
        CheaterLoadCalculator clc = new LinearCheaterLoad(max_dr_per_min);

        return new FitnessBeforeLoad(clc);
    }
}
