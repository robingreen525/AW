package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilution.PeriodicDilution;

/** 
 * Infrequent but extreme dilution
 * Created on 24 Jun, 2013.
 *
 * @author Adam Waite
 * @version $Id: ExtremePeriodicDilutionAR.java 2180 2013-06-26 23:59:13Z ajwaite $
 *
 */
public class ExtremePeriodicDilutionAR extends AdaptiveRace {

    public ExtremePeriodicDilutionAR(String args[]) { super(args); }

    @Override
    public DilutionRule makeDilutionRule() {
        double dilution_fraction = 0.99;
        int dilute_every = (int) 1e3 * TIMESTEP_SCALE;
        return new PeriodicDilution(dilution_fraction, dilute_every);
    }

    public static void main(String args[]) {
        AdaptiveRace ar = new ExtremePeriodicDilutionAR(args);
        ar.run();
    }
}
