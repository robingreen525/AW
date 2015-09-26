package org.fhcrc.honeycomb.metapop.dilution;

import org.fhcrc.honeycomb.metapop.StepProvider;

/**
 * Rules that dilute all sites by the same amount.
 *
 * Created on 26 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
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
        return getClass().getSimpleName() + 
               ", threshold=" + threshold +
               ", fraction=" + fraction;
    }

    @Override
    public void setStepProvider(StepProvider sp) { }
}
