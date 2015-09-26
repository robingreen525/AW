package org.fhcrc.honeycomb.metapop.dilution;

import org.fhcrc.honeycomb.metapop.StepProvider;

/**
 * Rules that dilute all sites by the same amount.
 *
 * Created on 26 Apr, 2013
 * @author Adam Waite
 * @version $Id: ThresholdDilution.java 2177 2013-06-26 22:05:32Z ajwaite $
 */
public abstract class ThresholdDilution implements DilutionRule {
    protected double fraction;
    protected double threshold;

    public ThresholdDilution(double fraction, double threshold) {
        this.fraction = fraction;
        this.threshold = threshold;
    }

    public double getDilutionFraction() { return fraction; }
    public double getThreshold() { return threshold; }

    @Override
    public String toString() {
        return new String(getClass().getSimpleName() + 
                          ", threshold=" + threshold + 
                          ", fraction=" + fraction);
    }

    @Override
    public void setStepProvider(StepProvider sp) { }
}
