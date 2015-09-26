package org.fhcrc.honeycomb.metapop.fitness;

/**
 * Does not alter the number provided.
 *
 * Created on 24 Apr, 2013
 * @author Adam Waite
 * @version $Id: IdentityCalculator.java 2018 2013-05-08 02:50:23Z ajwaite $
 */
public class IdentityCalculator extends FitnessCalculator {
    @Override
    public double calculateGrowthRate(double param) { return param; }

    @Override
    public double calculateDeathRate(double param) { return param; }
}
