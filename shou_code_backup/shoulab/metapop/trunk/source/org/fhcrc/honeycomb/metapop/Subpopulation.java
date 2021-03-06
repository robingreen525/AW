package org.fhcrc.honeycomb.metapop;

import java.util.List;
import java.math.BigDecimal;

/** 
 * Defines a type present in a {@link Population}.
 *
 * Created on 27 Jan, 2012
 * @author Adam Waite
 * @version $Id: Subpopulation.java 1961 2013-04-23 17:11:40Z ajwaite $
 *
 */
public class Type {
    private int size;
    private FitnessCalculator fc;
    private Population population;
    private final String id = "Type";
    private RandomNumberUser rng;

    public Type(int size, FitnessCalculator fc, String id, 
                Population pop, RandomNumberUser rng)
    {
        this.size = size;
        this.fc   = fc;
        this.id   = id;
        this.population = pop;
        this.rng  = rng;
    }

    public Type(int size, FitnessCalculator fc, String id,
                RandomNumberUser rng)
    {
        this(size, fc, id, null, rng);
    }

    public Type(final Type type) {
        this(type.size(), type.getFitnessCalculator(), 
             type.getId(), type.getPopulation(),
             type.getRNG();)
    }

    public void setPopulation() { this.population = pop }

    public int getSize() { return size; }
    public double getFitnessCalculator() { return fc; }
    public int getId() { return id; }
    public void getPopulation() { return population; }
    public RandomNumberUser getRNG() { return rng; }

    public void grow() {
        double fitness = fc.calculate(population);
        int pick = rng.getNextBinomial(this.size, Math.abs(fitness));
        this.size = (gr >=0) ? size + pick : size - pick;
    }

    public Type dilute(double fraction) {
        double diluted = rng.getNextBinomial(size, fraction);
        size -= diluted;
        return ndiluted;
    }
}
