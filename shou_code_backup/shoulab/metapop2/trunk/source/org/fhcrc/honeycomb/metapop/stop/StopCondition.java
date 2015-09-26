package org.fhcrc.honeycomb.metapop.stop;

import org.fhcrc.honeycomb.metapop.World;

/** 
 * Determines when a {@link World} should stop.
 *
 * Created on 12 May, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public abstract class StopCondition {
    protected World world;

    public void setWorld(final World world) {
        this.world = world;
    }

    public abstract boolean isMet();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
