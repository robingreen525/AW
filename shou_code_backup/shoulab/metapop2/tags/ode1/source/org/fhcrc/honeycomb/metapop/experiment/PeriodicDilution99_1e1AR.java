package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilution.PeriodicDilution;

/** 
 * Infrequent but extreme dilution
 * Created on 24 Jun, 2013.
 *
 * @author Adam Waite
 * @version $Id: PeriodicDilution99_1e1AR.java 2237 2013-07-31 17:23:04Z ajwaite $
 *
 */
public class PeriodicDilution99_1e1AR extends AdaptiveRace {

    public PeriodicDilution99_1e1AR(String args[]) { super(args); }

    @Override
    public DilutionRule makeDilutionRule() {
        double dilution_fraction = 0.99;
        int dilute_every = (int) 1e1 * TIMESTEP_SCALE;
        return new PeriodicDilution(dilution_fraction, dilute_every);
    }

    public static void main(String args[]) {
        AdaptiveRace ar = new ExtremePeriodicDilutionAR(args);
        ar.run();
    }
}
