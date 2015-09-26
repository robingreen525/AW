package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.AdaptiveRace;
import org.fhcrc.honeycomb.metapop.dilutionrule.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilutionrule.GlobalThresholdDilution;

/** 
 * Implements global dilution.
 *
 * Created on 8 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 */
public class LBLStaticEnvGlobalDil extends LBLStaticEnv {
    protected double max_dr_per_min;

    public LBLStaticEnvGlobalDil(String args[]) { super(args); }

    @Override
    public DilutionRule makeDilutionRule() {
        double dilution_fraction = 0.5;
        return new GlobalThresholdDilution(dilution_fraction);
    }

    public static void main(String args[]) {
        AdaptiveRace ar = new LBLStaticEnvGlobalDil(args);
        ar.run();
    }
}
