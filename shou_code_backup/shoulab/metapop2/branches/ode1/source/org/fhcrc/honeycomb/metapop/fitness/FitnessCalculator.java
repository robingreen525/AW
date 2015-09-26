package org.fhcrc.honeycomb.metapop.fitness;

/**
 * Calculates the fitness based on a single parameter.
 *
 * Created on 22 Apr, 2013
 * @author Adam Waite
 * @version $Id: FitnessCalculator.java 2018 2013-05-08 02:50:23Z ajwaite $
 */
public abstract class FitnessCalculator {
    abstract public double calculateGrowthRate(double param);
    abstract public double calculateDeathRate(double param);

    @Override
    public String toString() {
        return new String(this.getClass().getSimpleName());
    }
}
