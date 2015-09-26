import org.fhcrc.honeycomb.metapop.*;

public class LBLStaticEnvGlobalDil extends LBLStaticEnv {
    public LBLStaticEnvGlobalDil(String args[]) { super(args); }

    public void makeDilutionRule() {
        double dilution_fraction = 0.5;
        DilutionRule dr = new GlobalThresholdDilution(dilution_fraction);

        setDilutionRule(dr);
    }

    public static void main(String args[]) {
        AdaptiveRace ar = new LBLStaticEnvGlobalDil(args);
        ar.run();
    }
}
