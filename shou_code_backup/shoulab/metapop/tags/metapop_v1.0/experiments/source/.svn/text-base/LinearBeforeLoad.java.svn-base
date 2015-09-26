import org.fhcrc.honeycomb.metapop.*;

abstract class LinearBeforeLoad extends AdaptiveRace {
    protected double max_dr_per_min;

    public LinearBeforeLoad(String args[]) { super(args); }

    public void makeFitnessCalculator() {
        max_dr_per_min = maxDeathRatePerHr()/AdaptiveRace.MIN_PER_HR;

        CheaterLoadCalculator clc = new LinearCheaterLoad(max_dr_per_min);
        FitnessCalculator fc = new FitnessBeforeLoad(clc);

        setFitnessCalculator(fc);
    }
}
