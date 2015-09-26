package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;

/** 
 * Defines the types of agents present in a {@link Population}.
 *
 * Created on 23 Apr, 2013
 * @author Adam Waite
 * @version $Id: Subpopulation.java 2060 2013-05-14 01:56:20Z ajwaite $
 *
 */
public class Subpopulation {
    private String id = "Unspecified";

    private int size;
    private double gamma; // amount of resource required to make a new cell
    private double release_rate; // amount of resource released /cell/timestep.
    private FitnessCalculator fc;
    private RandomNumberUser rng;

    public Subpopulation(final int size,
                         final double gamma, 
                         final double release_rate,
                         final FitnessCalculator fc,
                         final String id,
                         final RandomNumberUser rng)
    {
        this.size = size;
        this.gamma = gamma;
        this.release_rate = release_rate;
        this.fc   = fc;
        this.id   = id;
        this.rng  = rng;
    }

    /** constructs a {@code Subpopulation} that does not release resource and
     * requires a unit of resource to double. */
    public Subpopulation(final int size, final FitnessCalculator fc,
                         final String id, final RandomNumberUser rng)
    {
        this(size, 1, 0, fc, id, rng);
    }

    public Subpopulation(final Subpopulation subpop) {
        this(subpop.getSize(), subpop.getGamma(), subpop.getReleaseRate(),
             subpop.getFitnessCalculator(), subpop.getId(), subpop.getRNG());
    }

    public String getId() { return id; }
    public int getSize() { return size; }
    public FitnessCalculator getFitnessCalculator() { return fc; }
    public RandomNumberUser getRNG() { return rng; }

    /** returns the amount of resource consumed to make a new cell. */
    public double getGamma() { return gamma; }

    /** returns the amount of resource released per cell per timestep. */
    public double getReleaseRate() { return release_rate; }

    /** returns the current growth rate. */
    public double getGrowthRate(final double resource) {
        double fitness = fc.calculateGrowthRate(resource);

        if (fitness < 0)
            throw new UnsupportedOperationException("Fitness < 0");

        return fitness;
    }

    /** returns the current death rate. */
    public double getDeathRate(final double resource) {
        return fc.calculateDeathRate(resource);
    }

    public void setSize(int new_size) { this.size = new_size; }

    /** 
     * Generates the number of cells that would be born given the passed
     * resource according to its {@link FitnessCalculator}.
     *
     * @param resource the amount of resource available.
     *
     * @return the number of new cells.
     */
    public int getBirths(final double resource) {
        return rng.getNextBinomial(this.size, getGrowthRate(resource));
    }

    /** 
     * Generates the number of cells that would die given the passed
     * resource according to its {@link FitnessCalculator}.
     *
     * @param resource the amount of resource available.
     *
     * @return the number of dead cells.
     */
    public int getDeaths(final double resource) {
        return rng.getNextBinomial(this.size, getDeathRate(resource));
    }

    /**
     * Dilutes the {@link Subpopulation} by the specified fraction. 
     *
     * @param fraction the fraction to dilute (between 0 and 1).
     */
    public void dilute(final double fraction) {
        retrieveMigrants(fraction);
    }

    /**
     * Retrieves the specified fraction of the Subpopulation and returns it.
     * The new Subpopulation is otherwise identical to the original.
     *
     * @param migration_rate the migration rate (between 0 and 1).
     *
     * @return               the migrating Subpopulation, which is otherwise
     *                       identical to the Subpopulation of origin.
     */
    public Subpopulation retrieveMigrants(final double migration_rate) {
        if (migration_rate < 0 || migration_rate > 1) {
            throw new
                IllegalArgumentException("Fraction not between 0 and 1.");
        }

        int migrants = rng.getNextBinomial(size, migration_rate);
        size -= migrants;
        return new Subpopulation(migrants, fc, id, rng);
    }

    @Override
    public String toString() {
        return String.format(
                "'%s', size=%d, gamma=%.2e, release_rate=%.2e. %s, seed=%d",
                id, size, gamma, release_rate, fc, rng.getSeed());
    }
}
