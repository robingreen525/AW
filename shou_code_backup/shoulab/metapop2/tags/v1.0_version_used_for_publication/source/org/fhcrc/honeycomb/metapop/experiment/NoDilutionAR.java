package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilution.NoDilution;

/** 
 * An AdaptiveRace with no dilution.
 * Created on 25 Jun, 2013.
 *
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public class NoDilutionAR extends AdaptiveRace {
    public NoDilutionAR(String args[]) { super(args); }

    @Override
    public DilutionRule makeDilutionRule() {
        return new NoDilution();
    }

    public static void main(String args[]) {
        AdaptiveRace ar = new NoDilutionAR(args);
        ar.run();
    }
}
