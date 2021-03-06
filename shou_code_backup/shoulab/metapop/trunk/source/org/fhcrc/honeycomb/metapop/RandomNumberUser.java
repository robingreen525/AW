package org.fhcrc.honeycomb.metapop;

import java.util.List;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

/** A wrapper class for all random number generation.
 *
 * Created on 12 Feb, 2012
@author Adam Waite
@version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 */
public class RandomNumberUser {
    protected final long DEFAULT_SEED = System.nanoTime();

    protected RandomDataGenerator rng = null;
    protected long seed;

    public RandomNumberUser(long seed) {
        this.seed = seed;
        this.rng = new RandomDataGenerator(new Well19937c(seed));
    }

    public RandomNumberUser() {
        this(System.nanoTime());
    }

    public int getNextInt(int min, int max) {
        return rng.nextInt(min, max);
    }

    public int getNextBinomial(int n, double p) {
        return rng.nextBinomial(n, p);
    }

    public int getNextBinomial(int n, Double p) {
        return rng.nextBinomial(n, p.doubleValue());
    }

    public long getNextPoisson(double m) {
        return rng.nextPoisson(m);
    }

    public int[] getNextPermutation(int n, int k) {
        return rng.nextPermutation(n, k);
    }

    public void reSeed(long seed) { 
        this.seed = seed;
        rng.reSeed(seed);
    }

    public long getSeed() { return seed; }
    public RandomDataGenerator getRNG() { return rng; }
}
