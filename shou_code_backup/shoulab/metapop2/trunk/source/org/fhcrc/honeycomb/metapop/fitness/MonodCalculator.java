package org.fhcrc.honeycomb.metapop.fitness;

/**
 * Calculates the fitness based on the Monod equation.
 *
 * Created on 22 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 */
public class MonodCalculator extends FitnessCalculator {
    private double vmax;
    private double km;
    private double d;

    public MonodCalculator(double vmax, double km, double d, double scale) {
        this.vmax = (vmax+d)/scale;
        this.km   = km;
        this.d    = d/scale;
    }

    public MonodCalculator(double vmax, double km, double d) {
        this(vmax, km, d, 1);
    }

    @Override
    public double calculateGrowthRate(double nutrient_conc) {
        return (vmax*nutrient_conc) / (km+nutrient_conc);
    }

    @Override
    public double calculateDeathRate(double nutrient_conc) {
        return d;
    }

    @Override
    public String toString() {
        return String.format("%s, Vmax=%.3e, Km=%.3e, d=%.3e",
                             super.toString(), vmax, km, d);
    }
}
