package org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator;

/**
 * Calculates the fitness based on the Monod equation.
 *
 * Created on 22 Apr, 2013
 * @author Adam Waite
 * @version $Id: MonodCalculator.java 1961 2013-04-23 17:11:40Z ajwaite $
 */
public class MonodCalculator implements FitnessCalculator {
    private double Vmax;
    private double Km;
    private double d;

    MonodCalculator(double Vmax, double Km, double d) {
        this.Vmax = Vmax;
        this.Km   = Km;
        this.d    = d;
    }

    @Override
    public double calculate(Population pop) {

        return (((Vmax+d)*nutrient_conc / (nutrient_conc + Km)) - d);
    }
}
