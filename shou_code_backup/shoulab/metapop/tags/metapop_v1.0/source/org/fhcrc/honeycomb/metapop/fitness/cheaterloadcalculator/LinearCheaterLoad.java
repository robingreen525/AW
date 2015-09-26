package org.fhcrc.honeycomb.metapop.fitness.cheaterloadcalculator;

/** 
 * Calculates a linear reduction in fitness based on the current cheater
 * frequency. 
 * Created on 7 Feb, 2012
 * @param max_death_rate the growth rate (or death rate, if negative) when 
 *                       cheater frequency is 1.
 * @author Adam Waite
 * @version $Rev: 1894 $, $Date: 2013-04-08 19:22:19 -0700 (Mon, 08 Apr 2013) $
 *
 */
public class LinearCheaterLoad implements CheaterLoadCalculator {
    private final double max_death_rate;

    public LinearCheaterLoad(final double max_death_rate)
    {
        super();
        this.max_death_rate = max_death_rate;
    }

    public double calculate(final double cheater_freq,
                            final double max_fitness)
    {
        if (Double.isNaN(cheater_freq) || Double.isNaN(max_fitness))
            throw new IllegalArgumentException("Arg was NaN.");

        return (max_death_rate-max_fitness)*cheater_freq + max_fitness;
    }

    public double getMaxDeathRate() { return max_death_rate; }

    @Override
    public String toString() {
        return String.format(
                "LinearCheaterLoad, max death rate=%.2e", max_death_rate);
    }
}
