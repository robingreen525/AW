package org.fhcrc.honeycomb.metapop.dilution;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.Population;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/** Dilution occurs globally if a population hits a maximum size.
 *
 * Created on 26 Apr, 2013
 * @author Adam Waite
 * @version $Id: GlobalThresholdDilution.java 2177 2013-06-26 22:05:32Z ajwaite $
 */
public class GlobalThresholdDilution extends ThresholdDilution {
    public GlobalThresholdDilution(double fraction, double threshold) {
        super(fraction, threshold);
    }

    @Override
    public Map<Coordinate, Double> generate(List<Population> pops) {
        Map<Coordinate, Double> dilution_map = 
            new HashMap<Coordinate, Double>(pops.size());

        for (Population pop1:pops) {
            if (pop1.getSize() >= threshold) {
                for (Population pop2:pops) {
                    dilution_map.put(pop2.getCoordinate(), fraction);
                }
                break;
            }
        }
        return dilution_map;
    }
}
