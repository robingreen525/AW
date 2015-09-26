package org.fhcrc.honeycomb.metapop.dilution;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/** Dilution occurs if a population hits a maximum size.
 *
 * Created on 26 April, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 */
public class IndividualThresholdDilution extends ThresholdDilution  {
    public IndividualThresholdDilution(double fraction, int threshold) {
        super(fraction, threshold);
    }

    @Override
    public Map<Coordinate, Double> generate(List<Population> pops) {
        Map<Coordinate, Double> dilution_map = 
            new HashMap<Coordinate, Double>(pops.size());

        for (Population pop:pops) {
            if (pop.getSize() >= threshold) {
                dilution_map.put(pop.getCoordinate(), fraction);
            }
        }
        return dilution_map;
    }
}
