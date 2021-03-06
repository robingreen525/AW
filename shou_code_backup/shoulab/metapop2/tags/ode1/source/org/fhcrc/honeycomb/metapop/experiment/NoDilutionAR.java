package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.dilution.NoDilution;

/** 
 * An AdaptiveRace with no dilution.
 * Created on 25 Jun, 2013.
 *
 * @author Adam Waite
 * @version $Id: NoDilutionAR.java 2171 2013-06-26 01:56:17Z ajwaite $
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
