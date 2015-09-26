package org.fhcrc.honeycomb.metapop.stop;

import org.fhcrc.honeycomb.metapop.World;

/** 
 * Determines when a {@link World} should stop.
 *
 * Created on 12 May, 2013
 * @author Adam Waite
 * @version $Id: StopCondition.java 2046 2013-05-12 19:19:49Z ajwaite $
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
        return new String(this.getClass().getSimpleName());
    }
}
