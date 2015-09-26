package org.fhcrc.honeycomb.metapop.fitness;

/**
 * Returns the value it was initialized with regardless of the resource.
 *
 * Created on 24 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 */
public class SpecifiedCalculator extends FitnessCalculator {
    private double growth_rate;
    private double death_rate;

    public SpecifiedCalculator(double growth_rate, double death_rate) {
        this.growth_rate = growth_rate;
        this.death_rate = death_rate;
    }

    @Override
    public double calculateGrowthRate(double param) { return growth_rate; }

    @Override
    public double calculateDeathRate(double param) { return death_rate; }
}
