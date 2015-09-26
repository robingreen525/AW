package org.fhcrc.honeycomb.metapop.fitness;

/**
 * Does not alter the number provided.
 *
 * Created on 24 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 */
public class IdentityCalculator extends FitnessCalculator {
    @Override
    public double calculateGrowthRate(double param) { return param; }

    @Override
    public double calculateDeathRate(double param) { return param; }
}
