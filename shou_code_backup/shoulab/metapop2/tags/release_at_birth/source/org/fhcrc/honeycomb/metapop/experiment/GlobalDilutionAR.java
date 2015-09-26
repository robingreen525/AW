package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilution.GlobalThresholdDilution;

/** 
 * Implements AdaptiveRace with global dilution
 * Created on 24 Jun, 2013.
 *
 * @author Adam Waite
 * @version $Id: GlobalDilutionAR.java 2177 2013-06-26 22:05:32Z ajwaite $
 *
 */
public class GlobalDilutionAR extends AdaptiveRace {

    public GlobalDilutionAR(String args[]) { super(args); }
    
    @Override
    public DilutionRule makeDilutionRule() {
        double dilution_fraction = 0.5;
        double threshold = 1e7;
        return new GlobalThresholdDilution(dilution_fraction, threshold);
    }

    public static void main(String args[]) {
        AdaptiveRace ar = new GlobalDilutionAR(args);
        ar.run();
    }
}
