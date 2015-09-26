package org.fhcrc.honeycomb.metapop;

/** 
 * Calculates errors.
 *
 * Created on 23 Apr, 2013
 * @author Adam Waite
 * @version $Id: Estimate.java 2018 2013-05-08 02:50:23Z ajwaite $
 *
 */
public abstract class Estimate {
    protected double alpha;
    protected double[] ci;

    protected abstract double[] calculateCI();
    public abstract double getEstimate();
    public abstract double getVariance();

    public void setAlpha(double alpha) { 
        this.alpha = alpha;
        this.ci = calculateCI();
    }

    public double[] getCI() { return ci; }

}
