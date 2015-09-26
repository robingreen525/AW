package org.fhcrc.honeycomb.metapop.environment;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;

/** 
 * An unchanging environment.
 *
 * Created on 26 Apr, 2013
 * @author Adam Waite
 * @version $Id: StaticEnvironment.java 1990 2013-04-26 21:39:23Z ajwaite $
 */
public class StaticEnvironment implements EnvironmentChanger {
    public StaticEnvironment() { super(); }

    @Override
    public boolean environmentChanged() { return false; }

    @Override
    public RandomNumberUser getRNG() { return null; }

    @Override
    public double getProb() { return 0.0; }

    @Override
    public String toString() { return new String("StaticEnvironment"); }
}
