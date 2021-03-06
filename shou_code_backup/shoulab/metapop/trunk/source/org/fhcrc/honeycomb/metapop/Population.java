package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.dilutionrule.*;
import org.fhcrc.honeycomb.metapop.environmentchanger.*;
import org.fhcrc.honeycomb.metapop.fitness.cheaterloadcalculator.*;
import org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator.*;
import org.fhcrc.honeycomb.metapop.resource.Resource;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/** 
 * Contains cheaters and cooperators.
 *
 * Created on 27 Jan, 2012
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 */
public class Population {
    // **********FINAL fields **************************************** /

    /** The random number generator. */
    private final RandomNumberUser rng;

    /** The fractional advantage of cheaters over cooperators. */
    private final double cheat_advantage;

    /** The mutation rate from cooperator to cheater. */
    private final double u;

    /** The maximum fitnesses of each type (when cheat freq. = 0) */
    private final List<Double> max_fitnesses;


    // ********* MUTABLE fields ************************************/

    /** The total population size.  */
    private int size;

    /** The resource available to the population. */
    private Resource resource;

    /** The frequency of cheaters. */
    private double cheat_freq;

    /** The ratio of cooperators to cheaters. */
    private double coop_ratio;

    /** The <code>FitnessCalculator</code> to use */
    private FitnessCalculator fitness;

    /** The number of types. */
    private int nTypes;

    /** The number of each type of cooperator. */
    private List<Integer> coops;

    /** The number of each type of cheater. */
    private List<Integer> cheats;


    /** The current frequency of each type of cooperator. */
    private double[] coop_freqs;

    /** The current frequency of each type of cheater. */
    private double[] cheat_freqs;

    /** The total number of cooperators. */
    private int nCoops;

    /** The total number of cheaters. */
    private int nCheats;

    /** 
     * This constructor explicitly specifies the number of cooperators and
     * cheaters in the simulation.
     *
     * @param coops           the number of each type of cooperator.
     * @param cheats          the number of each type of cheater.
     * @param max_fitnesses   the fitnesses of each type.
     * @param cheat_advantage the fractional advantage of cheaters over
     *                        cooperators.
     * @param u               the mutation rate from cooperator to cheater.
     * @param fc              the {@link FitnessCalculator} to use.
     * @param rng             a random number generator.
     *
     * @see FitnessCalculator
     */
    public Population(final List<Type> types,
                      final List<Integer> cheats, 
                      final List<Double> max_fitnesses, 
                      final double cheat_advantage, 
                      final double u, 
                      final FitnessCalculator fc, 
                      final RandomNumberUser rng) 
    {
        if (fc == null) 
            throw new IllegalArgumentException("FitnessCalculator is null.");

        if (u < 0 || u > 1)
            throw new IllegalArgumentException("u cannot be < 0 or > 1");

        if (coops.size() == 0 && cheats.size() == 0) {
            for (int i=0; i<max_fitnesses.size(); i++) {
                coops.add(0);
                cheats.add(0);
            }
        }

        checkSizes(coops.size(), cheats.size(), max_fitnesses.size());

        this.coops  = Utilities.copyInts(coops);
        this.cheats = Utilities.copyInts(cheats);
        this.cheat_advantage = cheat_advantage;
        this.u = u;
        this.max_fitnesses = Utilities.copyDoubles(max_fitnesses);
        this.fitness = fc;
        this.rng = rng;
        nTypes = max_fitnesses.size();

        this.coop_freqs  = new double[nTypes];
        this.cheat_freqs = new double[nTypes];

        update();
    }

    private Population(final List<Double> max_fitnesses, 
                       final double cheat_advantage,
                       final double u,
                       final FitnessCalculator fc, 
                       final RandomNumberUser rng) 
    {
        this(new ArrayList<Integer>(), new ArrayList<Integer>(),
             max_fitnesses, cheat_advantage, u, fc, rng);
    }

    /**
     * This constructor is intended to be used at the start of a simulation,
     * when it is more convenient to specify population structure in terms of
     * an initial size and frequencies. Just converts frequencies to numbers
     * and calls constructor that explicitly uses numbers of each type.
     *
     * @param size            the initial population size.
     * @param cheat_freq      the initial frequency of cheaters.
     * @param cheat_advantage the fractional advantage of cheaters over
     *                        cooperators.
     * @param types           the fitnesses and initial frequencies of each
     *                        type.
     * @param rng             a random number generator.
     *
     * @see Types
     */
    public Population(final int size, final double cheat_freq,
                      final double cheat_advantage, final double u,
                      final Types types, final FitnessCalculator fc,
                      final RandomNumberUser rng)
    {
        this(types.getMaxFitnesses(), cheat_advantage, u, fc, rng);

        double test = size*cheat_freq;
        if (Math.round(test) != Math.floor(test)) {
            throw new IllegalArgumentException(
                    "Cheater freq does not divide pop size evenly.");
        }

        int n_types = types.size();

        int coop_size  = (int) (size * (1-cheat_freq));
        int cheat_size = (int) (size * cheat_freq);

        coops.set(0, coop_size);
        cheats.set(0, cheat_size);

        int total_coop_muts  = 0;
        int total_cheat_muts = 0;
        for (int j=1; j<n_types; j++) {
            double freq = types.getFrequency(j);

            int coop_muts  = 0;
            int cheat_muts = 0;
            if (freq > 0.0) {
                coop_muts  = rng.getNextBinomial(coop_size, freq);
                cheat_muts = rng.getNextBinomial(cheat_size, freq);

                total_coop_muts += coop_muts;
                total_cheat_muts += cheat_muts;
            }
            coops.set(j, coop_muts);
            cheats.set(j, cheat_muts);
        }
        coops.set(0, coops.get(0) - total_coop_muts);
        cheats.set(0, cheats.get(0) - total_cheat_muts);
        update();
    }

    /**
     * Copy constructor.
     *
     * @param pop the <code>Population</code> to be copied.
     */
    public Population(final Population pop) {
        this(pop.getCoops(), pop.getCheats(), pop.getMaxFitnesses(),
             pop.getCheatAdvantage(), pop.getU(), pop.getFitnessCalculator(),
             pop.getRNG());
    }


    /** growth when no resource is being modeled. */
    public void grow() { grow(null); }

    /** changes population size of each type. 
     *
     * @param resource the amount of {@link Resource} available to the 
     *                 {@link Population}.
     * */
    public void grow() {
        int[] order = rng.getNextPermutation(nTypes, nTypes);
        for (int i : order) {
            Type current = type.get(i);
            type.get(i).grow(this);
            resource.update(this);
        }
        update();
    }

    /** mutates a portion of cooperators to cheaters based on the value of
     * <code>u</code>.  If 0 &gt; mutant &lt; 1 is created, makes a mutant with
     * probability equal to that fraction.
     *
     * @param sizes      the size of the cooperator (index 0) and cheater
     *                   (index 1) populations
     *
     * @return           the modified sizes of cooperators and cheaters of this
     *                   type.  Cooperators are index 0, cheats 1.
     */
    public int[] mutate(final int[] sizes) {
        int[] result = {sizes[0], sizes[1]};
        if (u == 0) return result;

        int mutants = rng.getNextBinomial(sizes[0], u);
        return new int[] {result[0]-mutants, result[1]+mutants};
    }

    /**
     * Dilutes each type by the specified amount and returns a new 
     * {@link Population} of the diluted members. It is a
     * runtime error to try to dilute a population of 0 size.
     *
     * @param fraction the fraction of the <code>Population</code> to dilute as
     *                 a value between 0 and 1.
     *
     * @return         the diluted <code>Population</code>.
     */
    public Population dilute(final double fraction) {
        if (fraction < 0 || fraction > 1) {
            throw new
                IllegalArgumentException("Fraction not between 0 and 1.");
        }

        if (size == 0) {
            throw new IllegalArgumentException(
                    "Attempting to dilute population of 0 size.");
        }

        List<Integer> new_coops  = new ArrayList<Integer>(nTypes);
        List<Integer> new_cheats = new ArrayList<Integer>(nTypes);

        //System.out.println("coops: " + coops);
        //System.out.println("cheats: " + cheats);
        for (int type=0; type<nTypes; type++) {
            int[] diluted = {0, 0};
            int[] coop_cheat = getByType(type);

            for (int i=0; i<coop_cheat.length; i++) {
                int pop_size = coop_cheat[i];
                if (pop_size == 0) continue;
                diluted[i] = rng.getNextBinomial(pop_size, fraction);
            }

            //System.out.println("coops: " + coops.get(type));
            //System.out.println("cheats: " + cheats.get(type));
            coops.set(type, coop_cheat[0]-diluted[0]);
            cheats.set(type, coop_cheat[1]-diluted[1]);

            //System.out.println("diluted coops: " + diluted[0]);
            //System.out.println("diluted cheats: " + diluted[1]);
            new_coops.add(diluted[0]);
            new_cheats.add(diluted[1]);
        }
        update();
        return new Population(new_coops, new_cheats, getMaxFitnesses(),
                              getCheatAdvantage(), getU(),
                              getFitnessCalculator(), getRNG());
    }

    /**
     * Removes lowest fitness type, moves higher fitness types to
     * lowest fitness type, and selects new higher-fitness mutants from 
     * these higher fitness types proportional to their original amounts.
     *
     * @param new_freqs the frequency of each new type.
     *
     */
    public void shiftFitness(final List<Double> new_freqs) {
        if (new_freqs.size() != nTypes) {
            throw new IllegalArgumentException(
                "Number of new frequencies does not match number of types.");
        }

        int old_coop_mutants = 0;
        int old_cheat_mutants = 0;

        for (int i=1; i<nTypes; i++) {
            double freq = new_freqs.get(i);

            int current_coop_muts = coops.get(i);
            int current_cheat_muts = cheats.get(i);
            int new_coop_muts = 0;
            int new_cheat_muts = 0;
            if (freq > 0.0) {
                if (current_coop_muts > 0) {
                    new_coop_muts = 
                        rng.getNextBinomial(current_coop_muts, freq);
                }
                if (current_cheat_muts > 0) {
                    new_cheat_muts =
                        rng.getNextBinomial(current_cheat_muts, freq);
                }
            }
            old_coop_mutants += (current_coop_muts - new_coop_muts);
            old_cheat_mutants += (current_cheat_muts - new_cheat_muts);

            coops.set(i, new_coop_muts);
            cheats.set(i, new_cheat_muts);
        }

        coops.set(0, old_coop_mutants);
        cheats.set(0, old_cheat_mutants);
        update();
    }

    /**
     * a wrapper for <code>dilute</code>, included for semantic clarity.
     *
     * @return the migrating <code>Population</code>.
     */
    public Population retrieveMigrants(final double amount) {
        return dilute(amount);
    }

    /**
     * mixes the passed population with the current one.
     *
     * @param incoming the incoming population to be mixed with the current
     *                 one.
     */
    public void mix(final Population incoming) {
        final List<Integer> incoming_coops  = 
            new ArrayList<Integer>(incoming.getCoops());
        final List<Integer> incoming_cheats = 
            new ArrayList<Integer>(incoming.getCheats());
        //System.out.println("Incoming coops: " + incoming_coops);
        //System.out.println("Current coops: " + coops);
        for (int type=0; type<nTypes; type++) {
            coops.set(type,  coops.get(type)  + incoming_coops.get(type));
            cheats.set(type, cheats.get(type) + incoming_cheats.get(type));
        }
        update();
    }

    public static List<Population> generate(final int n,
                                            final int    initial_size,
                                            final double initial_cheat_freq,
                                            final double cheat_advantage,
                                            final double u,
                                            final Types types,
                                            final FitnessCalculator fc,
                                            final RandomNumberUser rng)
    {
        List<Population> result = new ArrayList<Population>(n);

        for (int i=0; i<n; i++) {
            result.add(new Population(initial_size, initial_cheat_freq,
                                      cheat_advantage, u, types, fc, rng));
        }
        return result;
    }

    @Override
    public String toString() {
        return new String("coops: " + coops + "  cheats: " + cheats + "\n");
    }

    /**
     * the random number generator.
     *
     * @return the random number generator.
     */
    public RandomNumberUser getRNG() { return rng; }

    /**
     * the size of the total <code>Population</code>.
     *
     * @return the size of the <code>Population</code>
     */
    public int getSize() { return size; }

    /**
     * gets the <code>List</code> containing the number of each cooperator
     * type.
     *
     * @return the <code>List</code> containing the number of each type of
     *         cooperator.
     */
    public List<Integer> getCoops() { return Utilities.copyInts(coops); }

    /**
     * gets the <code>List</code> containing the number of each cheater type.  
     *
     * @return the <code>List</code> containing the number of each type of
     *         cheater.
     */
    public List<Integer> getCheats() { return Utilities.copyInts(cheats); }

    /**
     * gets the cooperator/cheater population size by type.  Cooperator
     * population is 0 index.
     *
     * @param t the type to retrieve.
     *
     * @return an array containing the current population size of cooperators 
     *         (0) and cheaters (1).
     */
    public int[] getByType(int t) { 
        return new int[] {coops.get(t), cheats.get(t)};
    }

    /**
     * gets the frequencies of each type of cooperator.
     *
     * @return the frequencies of each type of cooperator.
     */
    public List<Double> getCoopFrequencies() { 
        return Utilities.copyDoubles(coop_freqs);
    }

    /**
     * gets the frequencies of each type of cheater.
     *
     * @return the frequencies of each type of cheater.
     */
    public List<Double> getCheatFrequencies() { 
        return Utilities.copyDoubles(cheat_freqs);
    }

    /**
     * gets current fitnesses ordered by cheater/cooperator instead of type.
     *
     * @param w 0 for cooperators, 1 for cheaters.
     */
    private List<Double> getCurrentFitnesses(int w) {
        List<Double> fitnesses = new ArrayList<Double>(nTypes);
        for (int type=0; type<nTypes; type++) {
            fitnesses.add(
                    fitness.calculate(this, max_fitnesses.get(type)).get(w));
        }
        return fitnesses;
    }

    /**
     * gets a <code>List</code> of the current fitnesses of each type of
     * cooperator.
     *
     * @return the <code>List</code> of cooperator fitnesses.
     */
    public List<Double> getCoopFitnesses() { return getCurrentFitnesses(0); }

    /**
     * gets a <code>List</code> of the current fitnesses of each type of
     * cheater.
     *
     * @return the <code>List</code> of cheater fitnesses.
     */
    public List<Double> getCheatFitnesses() { return getCurrentFitnesses(1); }

    /**
     * gets a <code>List</code> of maximum fitnesses of each type.
     *
     * @return the <code>List</code> of maximum fitnesses of each type.
     */
    public List<Double> getMaxFitnesses() { 
        return Utilities.copyDoubles(max_fitnesses);
    }

    /**
     * gets the mutation rate from cooperator to cheater.
     *
     * @return the mutation rate from cooperator to cheater.
     */
    public double getU() { return u; }
     

    /**
     * gets the number of cooperators.
     *
     * @return the number of cooperators.
     */
    public int getNCoops() { return nCoops; }

    /**
     * gets the number of cheaters.
     *
     * @return the number of cheaters.
     */
    public int getNCheats() { return nCheats; }

    public int getNTypes() { return nTypes; }


    /**
     * gets the current cooperator frequency.
     *
     * @return the current cooperator frequency.
     */
    public double getCheatFrequency() { return cheat_freq; }

    /**
     * gets the current cooperator frequency.
     *
     * @return the current cooperator:cheater ratio.
     */
    public double getCoopRatio() { return coop_ratio; }

    /**
     * gets the cheater advantage.
     *
     * @return the cheater advantage.
     */
    public double getCheatAdvantage() { return cheat_advantage; }

    /**
     * get a reference to the FitnessCalculator.
     * @return the FitnessCalculator.
     */
    public FitnessCalculator getFitnessCalculator() { return fitness; }

    /**
     * calculates new values for the mutable parameters of this
     * <code>Population</code>.
     */
    public void update() {
        int[] sums = coopCheatSums();
        nCoops     = sums[0];
        nCheats    = sums[1];
        size       = nCoops + nCheats;
        if (size == 0) return;

        cheat_freq = nCheats/((double) nCoops+nCheats);
        coop_ratio = nCoops/((double) nCheats);

        for (int type=0; type<nTypes; type++) {
            int[] coop_cheat = getByType(type);
            coop_freqs[type] = coop_cheat[0]/(double) nCoops;
            cheat_freqs[type] = coop_cheat[1]/(double) nCheats;
        }
    }

    private int[] coopCheatSums() {
        int[] sums = {0, 0};
        for (int type=0; type<nTypes; type++) {
            int[] coop_cheat = getByType(type);
            for (int i=0; i<sums.length; i++) {
                sums[i] += coop_cheat[i];
            }
        }
        return sums;
    }

    // Check sizes of coop, cheat, and fitness arrays.
    private void checkSizes(int coop_types, int cheat_types,
                            int fitness_types)
    {
        if (coop_types != cheat_types || coop_types != fitness_types ||
            cheat_types != fitness_types)
        {
            throw new IllegalArgumentException("The lengths of cooperator, "+
                    "cheater, and fitness Lists must be equal.");
        }

    }
}


