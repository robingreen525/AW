package org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator;

/**
 * Calculates the fitness based on a single parameter.
 *
 * Created on 22 Apr, 2013
 * @author Adam Waite
 * @version $Id: Calculator.java 1961 2013-04-23 17:11:40Z ajwaite $
 */
public interface Calculator {
    abstract public List<Double> calculate(double param);
}
