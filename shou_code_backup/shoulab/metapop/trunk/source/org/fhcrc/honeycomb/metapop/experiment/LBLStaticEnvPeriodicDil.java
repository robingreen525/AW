package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.AdaptiveRace;
import org.fhcrc.honeycomb.metapop.dilutionrule.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilutionrule.PeriodicDilution;

/** 
 * Implements a periodic dilution scheme.
 *
 * Created on 8 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 */
public class LBLStaticEnvPeriodicDil extends LBLStaticEnv {
    public LBLStaticEnvPeriodicDil(String args[]) { super(args); }

    public DilutionRule makeDilutionRule() {
        double dilution_fraction = 0.70;
        int dilute_every = 1000 * (int) AdaptiveRace.MIN_PER_HR;

        return new PeriodicDilution(dilution_fraction, dilute_every);
    }

    public static void main(String args[]) {
        AdaptiveRace ar = new LBLStaticEnvPeriodicDil(args);
        ar.run();
    }
}
