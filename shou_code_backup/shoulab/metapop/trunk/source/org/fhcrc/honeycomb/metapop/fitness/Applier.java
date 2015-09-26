package org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator;

/**
 * Applies a {@link Calculator} to a population.
 *
 * Created on 22 Apr, 2013
 * @author Adam Waite
 * @version $Id: Applier.java 1961 2013-04-23 17:11:40Z ajwaite $
 */
public interface Applier {
    abstract public List<Double> calculate(Population pop, int type);
}
