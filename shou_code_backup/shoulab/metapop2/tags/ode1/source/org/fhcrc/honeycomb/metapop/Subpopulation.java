package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;

/** 
 * Defines the types of agents present in a {@link Population}.
 *
 * Created on 23 Apr, 2013
 * @author Adam Waite
 * @version $Id: Subpopulation.java 2241 2013-08-01 22:02:26Z ajwaite $
 *
 */
public class Subpopulation {
    private String id = "Unspecified";

    private int size;
    private double gamma; // amount of resource required to make a new cell
    private double release_rate; // amount of resource released /cell/timestep.
    private double unfinished_cells;
    private FitnessCalculator fc;
    private RandomNumberUser rng;

    public Subpopulation(final int size,
                         final double unfinished_cells,
                         final double gamma, 
                         final double release_rate,
                         final FitnessCalculator fc,
                         final String id,
                         final RandomNumberUser rng)
    {
        this.size = size;
        this.unfinished_cells = unfinished_cells;
        this.gamma = gamma;
        this.release_rate = release_rate;
        this.fc   = fc;
        this.id   = id;
        this.rng  = rng;
    }

    public Subpopulation(final int size,
                         final double gamma, 
                         final double release_rate,
                         final FitnessCalculator fc,
                         final String id,
                         final RandomNumberUser rng)
    {
        this(size, 0, gamma, release_rate, fc, id, rng);
    }

    /** constructs a {@code Subpopulation} that does not release resource and
     * requires a unit of resource to double. */
    public Subpopulation(final int size, final FitnessCalculator fc,
                         final String id, final RandomNumberUser rng)
    {
        this(size, 0, 1, 0, fc, id, rng);
    }

    /** constructs a {@code Subpopulation} that does not release resource and
     * requires a unit of resource to double. */
    public Subpopulation(final int size, final double unfinished,
                         final FitnessCalculator fc,
                         final String id, final RandomNumberUser rng)
    {
        this(size, unfinished, 1, 0, fc, id, rng);
    }

    public Subpopulation(final Subpopulation subpop) {
        this(subpop.getSize(), subpop.getUnfinishedCells(), subpop.getGamma(), 
             subpop.getReleaseRate(), subpop.getFitnessCalculator(),
             subpop.getId(), subpop.getRNG());
    }

    public String getId() { return id; }
    public int getSize() { return size; }
    public double getUnfinishedCells() { return unfinished_cells; }
    public void addToUnfinishedCells(double amount) {
        unfinished_cells += amount;
        if (unfinished_cells > 1) {
            int new_cells = (int) StrictMath.floor(unfinished_cells);
            size += new_cells;
            unfinished_cells -= new_cells;
        }
    }

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
                "'%s', size=%d, unfinished_cells=%.2e, gamma=%.2e, " + 
                "release_rate=%.2e. %s, seed=%d",
                id, size, unfinished_cells, gamma, release_rate, fc,
                rng.getSeed());
    }
}
