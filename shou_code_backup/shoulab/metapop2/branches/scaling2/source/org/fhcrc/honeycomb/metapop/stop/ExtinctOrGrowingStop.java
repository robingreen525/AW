package org.fhcrc.honeycomb.metapop.stop;

import org.fhcrc.honeycomb.metapop.World;
import org.fhcrc.honeycomb.metapop.Population;

/** 
 * Stops when Populations are either extinct or have achieved some minimum
 * size.
 *
 * Created on 21 Jun, 2013
 * @author Adam Waite
 * @version $Id: ExtinctOrGrowingStop.java 2155 2013-06-22 01:18:46Z ajwaite $
 *
 */
public class ExtinctOrGrowingStop extends StopCondition {
    private int min_pop_size;

    public ExtinctOrGrowingStop(int min_pop_size) {
        this.min_pop_size = min_pop_size;
    }

    public boolean isMet() {
        for (Population pop:world.getOccupiedLocations().getList()) {
            int size = pop.getSize();
            if (size < min_pop_size && size > 0) return false;
        }
        System.out.println("Extinct or growing at step " + world.getStep());
        return true;
    }
}
