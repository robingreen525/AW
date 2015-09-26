package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilution.GlobalThresholdDilution;

/** 
 * Implements AdaptiveRace with global dilution
 * Created on 24 Jun, 2013.
 *
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
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
