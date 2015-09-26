package org.fhcrc.honeycomb.metapop;

public class Estimate {
    private double estimate;

    Estimate(double estimate) {
        if (estimate < 0)
            throw new IllegalArgumentException("estimate is negative!");

        this.estimate = estimate;
    }

    public double getEstimate() { return estimate; }
    public double getError(double mult) {
        return mult * StrictMath.sqrt(estimate);
    }
    public double getError() { return getError(1); }

    @Override
    public String toString() {
        return new String("Estimate: " + estimate + " Error: " + getError());
    }

}
