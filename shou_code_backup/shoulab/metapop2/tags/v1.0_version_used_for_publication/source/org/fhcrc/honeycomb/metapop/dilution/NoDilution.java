package org.fhcrc.honeycomb.metapop.dilution;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.StepProvider;
import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/** 
 * No dilution.
 * Created on 25 Jun, 2013.
 *
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public class NoDilution implements DilutionRule {

    @Override
    public Map<Coordinate, Double> generate(List<Population> pops) {
        return null;
    }

    @Override
    public void setStepProvider(StepProvider sp) { }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
