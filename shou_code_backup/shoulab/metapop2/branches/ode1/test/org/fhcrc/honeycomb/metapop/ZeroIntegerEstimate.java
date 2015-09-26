package org.fhcrc.honeycomb.metapop;

public class ZeroIntegerEstimate extends IntegerEstimate {
    public ZeroIntegerEstimate(double alpha) {
        super(null, alpha);
    }

    @Override
    protected double[] calculateCI() {
        return new double[] {0.0, 0.0};
    }
}
