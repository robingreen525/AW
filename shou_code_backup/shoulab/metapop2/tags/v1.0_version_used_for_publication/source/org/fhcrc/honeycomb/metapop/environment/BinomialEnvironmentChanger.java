package org.fhcrc.honeycomb.metapop.environment;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;

/** 
 * Changes the environment as a Binomial process with a specified probability.
 *
 * Created on 26 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 */
public class BinomialEnvironmentChanger implements EnvironmentChanger {
    private double prob;
    private RandomNumberUser rng;

    /**
     * Constructor.
     * @param prob the probability that the environment changes.
     * @param rng  the {@link RandomNumberUser}.
     *
     */
    public BinomialEnvironmentChanger(double prob, RandomNumberUser rng) {
        this.prob = prob;
        this.rng = rng;
    }

    @Override
    public boolean environmentChanged() {
        return rng.getNextBinomial(1, prob) == 1 ? true : false;
    }

    @Override
    public double getProb() {
        return prob;
    }

    @Override
    public RandomNumberUser getRNG() {
        return rng;
    }

    @Override
    public String toString() {
        return "BinomialEnvironmentChanger, prob=" + prob + 
               ", seed=" + getRNG();
    }
}
