package org.fhcrc.honeycomb.metapop.dilutionrule;

import org.fhcrc.honeycomb.metapop.World;
import org.fhcrc.honeycomb.metapop.Location;
import org.fhcrc.honeycomb.metapop.Population;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/** Dilution occurs globally if a population hits a maximum size.
 *
 * Created on 20 Mar, 2013
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 */
public class GlobalThresholdDilution extends ThresholdDilution {

    public GlobalThresholdDilution(double dilution_fraction) {
        super(dilution_fraction);
    }

    @Override
    public Map<Location, Boolean> generate() {
        checkWorld();

        List<Location> ols = world.getOccupiedLocations();

        Map<Location, Boolean> dilution_map = 
            new HashMap<Location, Boolean>(ols.size());

        boolean dilute = false;
        for (Location ol:ols) {
            if (ol.populationSize() >= max_pop) {
                dilute = true;
                break;
            }
        }

        for (Location ol:ols) {
            dilution_map.put(ol, dilute);
        }
        return dilution_map;
    }

    @Override
    public String toString() {
        return new String("GlobalThresholdDilution");
    }
}
