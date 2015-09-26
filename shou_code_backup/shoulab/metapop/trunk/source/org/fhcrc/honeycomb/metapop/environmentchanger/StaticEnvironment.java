package org.fhcrc.honeycomb.metapop.environmentchanger;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;

/** 
 * An unchanging environment.
 *
 * Created on 20 Mar, 2013
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 */
public class StaticEnvironment implements EnvironmentChanger {
    public StaticEnvironment() { super(); }

    public boolean environmentChanged() { return false; }
    public RandomNumberUser getRNG() { return null; }
    public double getProb() { return 0.0; }

    @Override
    public String toString() {
        return new String("StaticEnvironment");
    }
}
