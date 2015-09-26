import org.fhcrc.honeycomb.metapop.*;

public class LBLStaticEnvPeriodicDil extends LBLStaticEnv {
    public LBLStaticEnvPeriodicDil(String args[]) { super(args); }

    public void makeDilutionRule() {
        double dilution_fraction = 0.7;
        int dilute_every = 1000 * (int) AdaptiveRace.MIN_PER_HR;
        DilutionRule dr = new PeriodicDilution(dilution_fraction,
                                               dilute_every);

        setDilutionRule(dr);
    }

    public static void main(String args[]) {
        AdaptiveRace ar = new LBLStaticEnvPeriodicDil(args);
        ar.run();
    }
}
