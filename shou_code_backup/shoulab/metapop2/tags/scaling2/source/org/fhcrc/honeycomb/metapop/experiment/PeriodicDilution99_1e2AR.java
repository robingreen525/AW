package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilution.PeriodicDilution;

/** 
 * Infrequent but extreme dilution
 * Created on 24 Jun, 2013.
 *
 * @author Adam Waite
 * @version $Id: PeriodicDilution99_1e2AR.java 2233 2013-07-24 17:23:41Z ajwaite $
 *
 */
public class PeriodicDilution99_1e2AR extends AdaptiveRace {

    public PeriodicDilution99_1e2AR(String args[]) { super(args); }

    @Override
    public DilutionRule makeDilutionRule() {
        double dilution_fraction = 0.99;
        int dilute_every = (int) 1e2 * TIMESTEP_SCALE;
        return new PeriodicDilution(dilution_fraction, dilute_every);
    }

    public static void main(String args[]) {
        AdaptiveRace ar = new ExtremePeriodicDilutionAR(args);
        ar.run();
    }
}
