package org.fhcrc.honeycomb.metapop;

import java.util.List;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;

/** A wrapper class for all random number generation.
 *
 * Created on 12 Feb, 2012
@author Adam Waite
@version $Rev: 1894 $, $Date: 2013-04-08 19:22:19 -0700 (Mon, 08 Apr 2013) $
 */
public class RandomNumberUser {
    protected final long DEFAULT_SEED = System.nanoTime();

    protected RandomDataGenerator rng = null;
    protected long seed;

    public RandomNumberUser(long seed) {
        this.seed = seed;
        this.rng = new RandomDataGenerator (new MersenneTwister(seed));
    }

    public RandomNumberUser() {
        this.seed = System.nanoTime();
        this.rng = new RandomDataGenerator (new MersenneTwister(seed));
    }

    public int getNextInt(int min, int max) {
        return rng.nextInt(min, max);
    }

    public int getNextBinomial(int n, double p) {
        return rng.nextBinomial(n, p);
    }

    public long getNextPoisson(double m) {
        return rng.nextPoisson(m);
    }

    public void reSeed(long seed) { 
        this.seed = seed;
        rng = new RandomDataGenerator(new MersenneTwister(seed));
    }

    public long getSeed() { return seed; }
    public RandomDataGenerator getRNG() { return rng; }
}
