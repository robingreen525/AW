package org.fhcrc.honeycomb.metapop.stop;

import org.fhcrc.honeycomb.metapop.World;

/** 
 * Doesn't stop.
 *
 * Created on 12 May, 2013
 * @author Adam Waite
 * @version $Id: NoStop.java 2046 2013-05-12 19:19:49Z ajwaite $
 *
 */
public class NoStop extends StopCondition {
    public boolean isMet() { return false; }
}
