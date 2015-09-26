package org.fhcrc.honeycomb.metapop.environment;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;

/** 
 * An unchanging environment.
 *
 * Created on 26 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
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
    public String toString() { return "StaticEnvironment"; }
}
