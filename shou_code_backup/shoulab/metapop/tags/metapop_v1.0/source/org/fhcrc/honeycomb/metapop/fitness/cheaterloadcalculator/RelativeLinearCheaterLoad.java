package org.fhcrc.honeycomb.metapop.fitness.cheaterloadcalculator;

/**
 * Calculates a linear reduction in fitness, as a fraction of the maximum
 * growth rate, based on the current cheater frequency. 
 *
 * Created on 8 Feb, 2013
 * @param max_reduction the fractional reduction in growth rate when cheater 
 *                      frequency is 1. Values greater than 1 will result in 
 *                      negative fitness values.
 * @author Adam Waite
 * @version $Rev: 1894 $, $Date: 2013-04-08 19:22:19 -0700 (Mon, 08 Apr 2013) $
 *
 */
public class RelativeLinearCheaterLoad implements CheaterLoadCalculator {
    private final double max_reduction;

    public RelativeLinearCheaterLoad(final double max_reduction) {
        super();
        this.max_reduction = max_reduction;
    }

    public double calculate(final double cheater_freq, final double fitness) {
        if (Double.isNaN(cheater_freq) || Double.isNaN(fitness))
            throw new IllegalArgumentException("Arg was NaN.");

        return -max_reduction*fitness*cheater_freq + fitness;
    }

    @Override
    public String toString() {
        return String.format(
                "Relative linear cheater load, max reduction=%.2e",
                max_reduction);
    }
}
