package org.fhcrc.honeycomb.metapop.environmentchanger;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;

/** 
 * Changes the environment as a Binomial process with a specified probability.
 *
 * Created on 23 Feb, 2013
 * @author Adam Waite
 * @version $Rev: 1895 $, $Date: 2013-04-12 11:26:27 -0700 (Fri, 12 Apr 2013) $
 */
public class BinomialEnvironmentChanger implements EnvironmentChanger {
    private double prob;
    private RandomNumberUser rng;

    /**
     * Constructor.
     * @param prob the probability that the environment changes.
     * @param rng  the <code>RandomNumberUser</code>.
     *
     * @see        RandomNumberUser
     */
    public BinomialEnvironmentChanger(double prob, RandomNumberUser rng) {
        this.prob = prob;
        this.rng = rng;
    }

    public boolean environmentChanged() {
        return rng.getNextBinomial(1, prob) == 1 ? true : false;
    }

    public double getProb() {
        return prob;
    }

    public RandomNumberUser getRNG() {
        return rng;
    }

    @Override
    public String toString() {
        return new String("BinomialEnvironmentChanger, prob=" + prob + 
                          ", seed=" + getRNG());
    }
}
