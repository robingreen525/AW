package org.fhcrc.honeycomb.metapop.fitness;

/**
 * Calculates the fitness based on a single parameter.
 *
 * Created on 22 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 */
public abstract class FitnessCalculator {
    abstract public double calculateGrowthRate(double param);
    abstract public double calculateDeathRate(double param);

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
