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
 * @version $Id: NoDilution.java 2170 2013-06-26 01:55:29Z ajwaite $
 *
 */
public class NoDilution implements DilutionRule {

    @Override
    public Map<Coordinate, Double> generate(List<Population> pops) {
        Map<Coordinate, Double> dilution_map = 
            new HashMap<Coordinate, Double>(pops.size());

        for (Population pop:pops) {
            dilution_map.put(pop.getCoordinate(), 0.0);
        }
        return dilution_map;
    }

    @Override
    public void setStepProvider(StepProvider sp) { }

    @Override
    public String toString() {
        return new String(getClass().getSimpleName());
    }
}
