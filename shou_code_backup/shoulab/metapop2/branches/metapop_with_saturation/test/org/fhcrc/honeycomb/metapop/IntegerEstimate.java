package org.fhcrc.honeycomb.metapop;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;

/** 
 * Calculates confidence intervals for integer distributions.
 *
 * Created on 23 Apr, 2013
 * @author Adam Waite
 * @version $Id: IntegerEstimate.java 2019 2013-05-08 17:40:29Z ajwaite $
 *
 */
public class IntegerEstimate extends Estimate {
    private IntegerDistribution dist;
    private double estimate;
    private double variance;

    IntegerEstimate(IntegerDistribution dist, double alpha) {
        this.dist = dist;
        this.alpha = alpha;
        this.estimate = dist.getNumericalMean();
        this.variance = dist.getNumericalVariance();
        this.ci = calculateCI();
    }

    @Override
    public double getEstimate() { return estimate; }

    @Override
    public double getVariance() { return variance; }

    @Override
    protected double[] calculateCI() { 
        double lower = dist.inverseCumulativeProbability((1-alpha)/2);
        double upper = dist.inverseCumulativeProbability((1+alpha)/2);
        return new double[] {lower, upper}; 
    }

    @Override
    public String toString() {
        double[] ci = calculateCI();
        String ci_text = " (" + 100*alpha + "% CI: " +
                         ci[0] + "-" +  ci[1] + ")";

        return new String(dist.getClass().getSimpleName() +
                          "\n  estimate=" + getEstimate() + ci_text);
    }

    public static Estimate binomialEstimate(int n, double p, double a) {
        return new IntegerEstimate(new BinomialDistribution(n, p), a);
    }

    public static Estimate poissonEstimate(int m, double a) {
        return new IntegerEstimate(new PoissonDistribution(m), a);
    }
}
