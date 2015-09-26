package org.fhcrc.honeycomb.metapop.dilutionrule;

import org.fhcrc.honeycomb.metapop.World;
import org.fhcrc.honeycomb.metapop.Location;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/** 
 * Dilutes a world.
 *
 * Created on 20 Mar, 2013
 *
 * @author Adam Waite
 * @version $Rev: 1895 $, $Date: 2013-04-12 11:26:27 -0700 (Fri, 12 Apr 2013) $
 *
 */
public abstract class DilutionRule {
    protected World world;
    protected double dilution_fraction;

    public DilutionRule(double dilution_fraction) {
        this.dilution_fraction = dilution_fraction;
    }

    public void checkWorld() {
        if (world == null) {
            throw 
                new RuntimeException("[WorldDiluter] World has not been set!");
        }
    }

    public void setWorld(World world) { this.world = world; }
    public double getDilutionFraction() { return dilution_fraction; }

    abstract public Map<Location, Boolean> generate();
}
