package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilution.PeriodicDilution;

/** 
 * Infrequent but extreme dilution
 * Created on 24 Jun, 2013.
 *
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public class PeriodicDilution99_1e4AR extends AdaptiveRace {

    public PeriodicDilution99_1e4AR(String args[]) { super(args); }

    @Override
    public DilutionRule makeDilutionRule() {
        double dilution_fraction = 0.99;
        int dilute_every = (int) 1e4 * TIMESTEP_SCALE;
        return new PeriodicDilution(dilution_fraction, dilute_every);
    }

    public static void main(String args[]) {
        AdaptiveRace ar = new ExtremePeriodicDilutionAR(args);
        ar.run();
    }
}
