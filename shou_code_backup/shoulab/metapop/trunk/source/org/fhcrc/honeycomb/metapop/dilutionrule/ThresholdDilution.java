package org.fhcrc.honeycomb.metapop.dilutionrule;

import org.fhcrc.honeycomb.metapop.Location;
import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.World;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/** Dilution occurs if a population hits a maximum size.
 *
 * Created on 03 April, 2013
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 */
public class ThresholdDilution extends DilutionRule {
    protected double max_pop;

    public ThresholdDilution(double dilution_fraction) {
        super(dilution_fraction);
    }

    @Override
    public Map<Location, Boolean> generate() {
        checkWorld();

        List<Location> ols = world.getOccupiedLocations();

        Map<Location, Boolean> dilution_map = 
            new HashMap<Location, Boolean>(ols.size());

        for (Location ol:ols) {
            if (ol.populationSize() >= max_pop) {
                dilution_map.put(ol, true);
            } else {
                dilution_map.put(ol, false);
            }
        }
        return dilution_map;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
        this.max_pop = this.world.getMaxPop();
    }

    @Override
    public String toString() {
        return new String("ThresholdDilution");
    }
}
